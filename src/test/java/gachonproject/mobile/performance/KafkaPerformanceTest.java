package gachonproject.mobile.performance;

import gachonproject.mobile.service.AnalysisService;
import gachonproject.mobile.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.group-id=test-group"
})
public class KafkaPerformanceTest {

    @Autowired
    private AnalysisService analysisService;
    
    @MockBean
    private KafkaProducerService kafkaProducerService;
    
    private static final int[] CONCURRENT_USERS_ARRAY = {10, 50, 100};
    private static final int REQUESTS_PER_USER = 10;
    private static final int WARMUP_REQUESTS = 5;

    private static class PerformanceResult {
        private final int totalRequests;
        private final int successCount;
        private final int errorCount;
        private final long totalTime;
        private final double throughput;
        private final double avgResponseTime;
        private final long minResponseTime;
        private final long maxResponseTime;
        private final boolean completed;
        
        public PerformanceResult(
                int totalRequests, 
                int successCount, 
                int errorCount, 
                long totalTime, 
                double throughput, 
                double avgResponseTime,
                long minResponseTime,
                long maxResponseTime,
                boolean completed) {
            this.totalRequests = totalRequests;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.totalTime = totalTime;
            this.throughput = throughput;
            this.avgResponseTime = avgResponseTime;
            this.minResponseTime = minResponseTime;
            this.maxResponseTime = maxResponseTime;
            this.completed = completed;
        }
        
        public int getTotalRequests() {
            return totalRequests;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getErrorCount() {
            return errorCount;
        }
        
        public long getTotalTime() {
            return totalTime;
        }
        
        public double getThroughput() {
            return throughput;
        }
        
        public double getAvgResponseTime() {
            return avgResponseTime;
        }
        
        public long getMinResponseTime() {
            return minResponseTime;
        }
        
        public long getMaxResponseTime() {
            return maxResponseTime;
        }
        
        public boolean isCompleted() {
            return completed;
        }
    }

    @BeforeEach
    public void setup() {
        // 테스트 실행 전 워밍업
        for (int i = 0; i < WARMUP_REQUESTS; i++) {
            analysisService.requestAnalysis(1L, "test-image.jpg", "SKIN_ANALYSIS");
        }
    }

    /**
     * 카프카를 사용하는 비동기 방식 성능 테스트
     */
    @Test
    public void testKafkaPerformance() throws InterruptedException {
        log.info("===== 카프카 비동기 방식 성능 테스트 시작 =====");
        
        Map<Integer, PerformanceResult> kafkaResults = new ConcurrentHashMap<>();
        
        for (int concurrentUsers : CONCURRENT_USERS_ARRAY) {
            PerformanceResult result = runPerformanceTest(concurrentUsers, REQUESTS_PER_USER, 
                () -> analysisService.requestAnalysis(1L, "test-image.jpg", "SKIN_ANALYSIS"));
            
            kafkaResults.put(concurrentUsers, result);
            
            log.info("[카프카] 동시 사용자 {}: 초당 처리량={} req/s, 평균 응답 시간={} ms", 
                    concurrentUsers, 
                    String.format("%.2f", result.getThroughput()),
                    String.format("%.2f", result.getAvgResponseTime()));
            
            // 테스트 간 간격을 두어 시스템 안정화
            Thread.sleep(2000);
        }
        
        log.info("===== 카프카 비동기 방식 성능 테스트 완료 =====");
    }
    
    /**
     * 카프카를 사용하지 않는 동기 방식 성능 테스트
     */
    @Test
    public void testSynchronousPerformance() throws InterruptedException {
        log.info("===== 동기 방식 성능 테스트 시작 =====");
        
        Map<Integer, PerformanceResult> syncResults = new ConcurrentHashMap<>();
        
        for (int concurrentUsers : CONCURRENT_USERS_ARRAY) {
            PerformanceResult result = runPerformanceTest(concurrentUsers, REQUESTS_PER_USER, 
                () -> analysisService.processSynchronously(1L, "test-image.jpg", "SKIN_ANALYSIS"));
            
            syncResults.put(concurrentUsers, result);
            
            log.info("[동기] 동시 사용자 {}: 초당 처리량={} req/s, 평균 응답 시간={} ms", 
                    concurrentUsers, 
                    String.format("%.2f", result.getThroughput()),
                    String.format("%.2f", result.getAvgResponseTime()));
            
            // 테스트 간 간격을 두어 시스템 안정화
            Thread.sleep(2000);
        }
        
        log.info("===== 동기 방식 성능 테스트 완료 =====");
    }
    
    /**
     * 카프카와 동기 방식의 성능 비교 테스트
     */
    @Test
    public void comparePerformance() throws InterruptedException {
        log.info("===== 카프카 vs 동기 방식 성능 비교 테스트 시작 =====");
        
        // 중간 규모의 부하로 테스트
        int concurrentUsers = 50;
        
        // 카프카 성능 측정
        PerformanceResult kafkaResult = runPerformanceTest(concurrentUsers, REQUESTS_PER_USER, 
            () -> analysisService);
        
        // 시스템 안정화를 위한 대기
        Thread.sleep(3000);
        
        // 동기 방식 성능 측정
        PerformanceResult syncResult = runPerformanceTest(concurrentUsers, REQUESTS_PER_USER, 
            () -> analysisService.processSynchronously(1L, "test-image.jpg", "SKIN_ANALYSIS"));
        
        // 성능 비교 결과 출력
        log.info("\n성능 비교 결과 (동시 사용자: {}):", concurrentUsers);
        log.info("카프카 비동기 방식:");
        log.info("  - 총 요청 수: {}", concurrentUsers * REQUESTS_PER_USER);
        log.info("  - 총 소요 시간: {} ms", kafkaResult.getTotalTime());
        log.info("  - 초당 처리량: {} requests/second", String.format("%.2f", kafkaResult.getThroughput()));
        log.info("  - 평균 응답 시간: {} ms", String.format("%.2f", kafkaResult.getAvgResponseTime()));
        log.info("  - 최소 응답 시간: {} ms", kafkaResult.getMinResponseTime());
        log.info("  - 최대 응답 시간: {} ms", kafkaResult.getMaxResponseTime());
        
        log.info("동기 방식:");
        log.info("  - 총 요청 수: {}", concurrentUsers * REQUESTS_PER_USER);
        log.info("  - 총 소요 시간: {} ms", syncResult.getTotalTime());
        log.info("  - 초당 처리량: {} requests/second", String.format("%.2f", syncResult.getThroughput()));
        log.info("  - 평균 응답 시간: {} ms", String.format("%.2f", syncResult.getAvgResponseTime()));
        log.info("  - 최소 응답 시간: {} ms", syncResult.getMinResponseTime());
        log.info("  - 최대 응답 시간: {} ms", syncResult.getMaxResponseTime());
        
        // 성능 향상 비율 계산
        double throughputImprovement = (kafkaResult.getThroughput() / syncResult.getThroughput() - 1) * 100;
        double responseTimeImprovement = (1 - kafkaResult.getAvgResponseTime() / syncResult.getAvgResponseTime()) * 100;
        
        log.info("성능 향상:");
        log.info("  - 처리량 향상: {}%", String.format("%.2f", throughputImprovement));
        log.info("  - 응답 시간 개선: {}%", String.format("%.2f", responseTimeImprovement));
        
        log.info("===== 카프카 vs 동기 방식 성능 비교 테스트 완료 =====");
    }
    
    /**
     * 성능 테스트 실행 유틸리티 메서드
     */
    private PerformanceResult runPerformanceTest(int concurrentUsers, int requestsPerUser, Supplier<Object> operation) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch latch = new CountDownLatch(concurrentUsers * requestsPerUser);
        List<Long> responseTimes = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();

        // 동시 사용자 시뮬레이션
        for (int i = 0; i < concurrentUsers; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < requestsPerUser; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        // 테스트 대상 작업 실행
                        operation.get();
                        
                        long requestEnd = System.currentTimeMillis();
                        responseTimes.add(requestEnd - requestStart);
                        
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        log.error("요청 처리 중 오류 발생: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        // 모든 요청이 완료될 때까지 대기 (최대 5분)
        boolean completed = latch.await(5, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();
        
        // 결과 계산
        long totalTime = endTime - startTime;
        int totalRequests = concurrentUsers * requestsPerUser;
        double throughput = totalRequests / (totalTime / 1000.0);
        
        double avgResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = 0;
        
        if (!responseTimes.isEmpty()) {
            avgResponseTime = responseTimes.stream().mapToLong(Long::valueOf).average().orElse(0.0);
            minResponseTime = responseTimes.stream().min(Long::compare).orElse(0L);
            maxResponseTime = responseTimes.stream().max(Long::compare).orElse(0L);
        }
        
        return new PerformanceResult(
            totalRequests,
            successCount.get(),
            errorCount.get(),
            totalTime,
            throughput,
            avgResponseTime,
            minResponseTime,
            maxResponseTime,
            completed
        );
    }
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        
        try {
            testKafkaPerformance();
            result.setSuccessful(true);
        } catch (Exception e) {
            result.setSuccessful(false);
            result.setResponseMessage("Error: " + e.getMessage());
        } finally {
            result.sampleEnd();
        }
        
        return result;
    }
}
