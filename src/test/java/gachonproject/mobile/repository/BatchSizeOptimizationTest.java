package gachonproject.mobile.repository;

import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.ingredient.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingDouble;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.hikari.maximum-pool-size=5",
        "spring.datasource.hikari.minimum-idle=2",
        "spring.datasource.hikari.idle-timeout=300000",
        "spring.datasource.hikari.connection-timeout=20000",
        "spring.jpa.properties.hibernate.jdbc.batch_size=50",
        "spring.jpa.properties.hibernate.order_inserts=true",
        "spring.jpa.properties.hibernate.batch_versioned_data=true",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.format_sql=false"
})
public class BatchSizeOptimizationTest {

    private static final int TOTAL_RECORDS = 1000;     // 총 레코드 수
    private static final int[] BATCH_SIZES = {10, 20, 50};  // 테스트할 배치 크기
    private static final int[] THREAD_COUNTS = {1, 2};   // 테스트할 스레드 수
    private static final int TEST_RUNS = 2;  // 각 조합당 테스트 실행 횟수

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Ingredient testIngredient;

    private JdbcMetrics jdbcMetrics = new JdbcMetrics();

    @Data
    private static class JdbcMetrics {
        private long connectionAcquisitionTime;
        private long sqlPrepareTime;
        private long sqlExecutionTime;
        private long flushTime;
        private int statementCount;
        private int queryCount;
        private int flushCount;
    }

    private void measureJdbcMetrics(Runnable operation) {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear(); // 이전 통계 초기화

        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();

        jdbcMetrics.setConnectionAcquisitionTime(stats.getConnectCount() > 0 ? 
            (endTime - startTime) / stats.getConnectCount() / 1_000_000 : 0);
        jdbcMetrics.setSqlPrepareTime(stats.getPrepareStatementCount() > 0 ? 
            (endTime - startTime) / stats.getPrepareStatementCount() / 1_000_000 : 0);
        jdbcMetrics.setSqlExecutionTime(stats.getQueryExecutionCount() > 0 ? 
            (endTime - startTime) / stats.getQueryExecutionCount() / 1_000_000 : 0);
        jdbcMetrics.setFlushTime(stats.getFlushCount() > 0 ? 
            (endTime - startTime) / stats.getFlushCount() / 1_000_000 : 0);
        
        jdbcMetrics.setStatementCount((int) stats.getPrepareStatementCount());
        jdbcMetrics.setQueryCount((int) stats.getQueryExecutionCount());
        jdbcMetrics.setFlushCount((int) stats.getFlushCount());
    }

    private void printJdbcMetrics() {
        log.info("\n[ JDBC 성능 메트릭스 ]");
        log.info("• 연결 획득 시간: {} ms", jdbcMetrics.getConnectionAcquisitionTime());
        log.info("• SQL 준비 시간: {} ms (총 {} 개의 statement)", 
            jdbcMetrics.getSqlPrepareTime(), jdbcMetrics.getStatementCount());
        log.info("• SQL 실행 시간: {} ms (총 {} 개의 쿼리)", 
            jdbcMetrics.getSqlExecutionTime(), jdbcMetrics.getQueryCount());
        log.info("• 플러시 작업 시간: {} ms (총 {} 회)", 
            jdbcMetrics.getFlushTime(), jdbcMetrics.getFlushCount());
    }

    @Data
    private static class SystemMetrics {
        private long peakMemoryUsage;
        private long averageMemoryUsage;
        private double gcFrequency;
        private List<Long> memoryMeasurements = new ArrayList<>();
        private int gcCount;
        private long startTime;

        public void startMeasuring() {
            startTime = System.currentTimeMillis();
            gcCount = getGcCount();
        }

        public void measure() {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory());
            memoryMeasurements.add(usedMemory);
            peakMemoryUsage = Math.max(peakMemoryUsage, usedMemory);
        }

        public void calculateMetrics() {
            // 평균 메모리 사용량 계산
            averageMemoryUsage = (long) memoryMeasurements.stream()
                    .mapToLong(Long::valueOf)
                    .average()
                    .orElse(0.0);

            // GC 빈도 계산 (초당)
            long duration = System.currentTimeMillis() - startTime;
            int currentGcCount = getGcCount() - gcCount;
            gcFrequency = duration > 0 ? (currentGcCount * 1000.0 / duration) : 0;
        }

        private int getGcCount() {
            int count = 0;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                count += garbageCollectorMXBean.getCollectionCount();
            }
            return count;
        }
    }

    private SystemMetrics systemMetrics = new SystemMetrics();

    private ScheduledExecutorService monitorSystemMetrics() {
        ScheduledExecutorService scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            systemMetrics.measure();
        }, 0, 100, TimeUnit.MILLISECONDS);
        return scheduler;
    }

    @BeforeEach
    void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        testIngredient = transactionTemplate.execute(status -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setName("Test Ingredient");
            ingredient.setGrade(1);
            ingredient.setDanger_status(false);
            ingredient.setAllergy_status(false);
            em.persist(ingredient);
            return ingredient;
        });
    }

    @Test
    void compareBatchSizePerformance() throws InterruptedException {
        systemMetrics.startMeasuring();
        ScheduledExecutorService scheduler = monitorSystemMetrics();
        
        try {
            log.info("\n============= 대량 데이터 배치 처리 성능 테스트 =============");
            log.info("총 레코드 수: {}", TOTAL_RECORDS);
            log.info("테스트 실행 횟수: {}/조합", TEST_RUNS);

            Map<String, List<PerformanceResult>> allResults = new HashMap<>();

            // 각 배치 크기와 스레드 수 조합 테스트
            for (int batchSize : BATCH_SIZES) {
                for (int threadCount : THREAD_COUNTS) {
                    // 스레드당 레코드 수 계산
                    int recordsPerThread = TOTAL_RECORDS / threadCount;

                    // 배치 크기가 스레드당 레코드 수를 초과하지 않도록
                    if (batchSize > recordsPerThread) {
                        continue;
                    }

                    String key = String.format("batch_%d_thread_%d", batchSize, threadCount);
                    List<PerformanceResult> runResults = new ArrayList<>();

                    log.info("\n테스트 실행: 배치 크기={}, 스레드={}", batchSize, threadCount);
                    
                    // 여러 번 실행하여 평균 성능 측정
                    for (int run = 0; run < TEST_RUNS; run++) {
                        runResults.add(runBatchTest(threadCount, batchSize));
                        Thread.sleep(2000); // DB 안정화 대기
                        System.gc(); // 명시적 GC 호출
                    }
                    
                    allResults.put(key, runResults);
                }
            }

            // 평균 성능 결과 계산
            Map<String, PerformanceResult> averageResults = calculateAverageResults(allResults);

            // 최적의 조합 찾기
            Map.Entry<String, PerformanceResult> optimal = averageResults.entrySet().stream()
                    .max(comparingDouble(e -> e.getValue().getRecordsPerSecond()))
                    .orElseThrow();

            printDetailedReport(averageResults, optimal.getKey());
        } finally {
            scheduler.shutdown();
            systemMetrics.calculateMetrics();
        }
    }

    private Map<String, PerformanceResult> calculateAverageResults(Map<String, List<PerformanceResult>> allResults) {
        Map<String, PerformanceResult> averageResults = new HashMap<>();

        allResults.forEach((key, results) -> {
            double avgTotalTime = results.stream()
                    .mapToLong(PerformanceResult::getTotalTime)
                    .average()
                    .orElse(0.0);

            double avgRecordsPerSecond = results.stream()
                    .mapToDouble(PerformanceResult::getRecordsPerSecond)
                    .average()
                    .orElse(0.0);

            double avgThreadTime = results.stream()
                    .mapToDouble(PerformanceResult::getAvgThreadTime)
                    .average()
                    .orElse(0.0);

            double avgVariance = results.stream()
                    .mapToLong(PerformanceResult::getThreadTimeVariance)
                    .average()
                    .orElse(0.0);

            String[] params = key.split("_");
            int batchSize = Integer.parseInt(params[1]);
            int threadCount = Integer.parseInt(params[3]);

            averageResults.put(key, new PerformanceResult(
                    threadCount,
                    (long) avgTotalTime,
                    Collections.singletonList((long) avgThreadTime),
                    batchSize,
                    avgRecordsPerSecond,
                    (long) avgVariance
            ));
        });

        return averageResults;
    }

    private void printDetailedReport(Map<String, PerformanceResult> results, String optimalKey) {
        PerformanceResult optimal = results.get(optimalKey);
        String[] optimalParams = optimalKey.split("_");
        int optimalBatch = Integer.parseInt(optimalParams[1]);
        int optimalThreads = Integer.parseInt(optimalParams[3]);

        // 단일 스레드, 최소 배치 크기를 기준으로 사용
        String baselineKey = String.format("batch_%d_thread_1", BATCH_SIZES[0]);
        PerformanceResult baseline = results.get(baselineKey);

        log.info("=== Batch Processing Performance (배치 처리 성능) ===");
        log.info("Optimal Configuration (최적 구성):");
        log.info("Batch Size (배치 크기): {} records", optimalBatch);
        log.info("Thread Count (스레드 수): {} threads", optimalThreads);
        log.info("Performance Improvement (성능 향상): {}x", String.format("%.2f", optimal.getRecordsPerSecond() / baseline.getRecordsPerSecond()));
        log.info("");

        log.info("=== Performance by Batch Size (배치 크기별 성능) ===");
        for (int batchSize : BATCH_SIZES) {
            log.info("Batch Size (배치 크기): {}", batchSize);
            for (int threadCount : THREAD_COUNTS) {
                String key = String.format("batch_%d_thread_%d", batchSize, threadCount);
                PerformanceResult result = results.get(key);
                if (result != null) {
                    log.info("Thread Count (스레드 수): {}", threadCount);
                    log.info("Execution Time (실행 시간): {} s", String.format("%.2f", result.getTotalTime() / 1000.0));
                    log.info("Throughput (처리량): {} records/s", String.format("%.0f", result.getRecordsPerSecond()));
                    log.info("Time Variance (시간 편차): {} ms", result.getThreadTimeVariance());
                    log.info("");
                }
            }
        }

        printJdbcMetrics(); // JDBC 메트릭스 출력 추가
        
        printSystemMetrics(optimal, baseline);
    }

    private void printSystemMetrics(PerformanceResult optimal, PerformanceResult baseline) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);

        log.info("=== Memory Usage (메모리 사용량) ===");
        log.info("Current Memory (현재 메모리): {} MB", usedMemory);
        log.info("Estimated Peak (예상 피크): {} MB", usedMemory * 1.4);
        log.info("Peak Memory (피크 메모리): {} MB", systemMetrics.getPeakMemoryUsage() / (1024 * 1024));
        log.info("Average Memory (평균 메모리): {} MB", systemMetrics.getAverageMemoryUsage() / (1024 * 1024));
        log.info("");

        log.info("=== Performance Metrics (성능 지표) ===");
        log.info("Average Throughput (평균 처리량): {} records/s", String.format("%.0f", optimal.getRecordsPerSecond()));
        log.info("Response Time (응답 시간): {} s", String.format("%.2f", optimal.getTotalTime() / 1000.0));
        log.info("Performance Improvement (성능 향상): {}%", 
                String.format("%.1f", (optimal.getRecordsPerSecond() - baseline.getRecordsPerSecond()) 
                / baseline.getRecordsPerSecond() * 100));
        log.info("");

        log.info("=== Processing Efficiency (처리 효율성) ===");
        log.info("Time per Record (레코드당 시간): {} ms", 
                String.format("%.2f", optimal.getTotalTime() / (double) TOTAL_RECORDS));
        log.info("Thread Load (스레드 부하): {}%", 
                String.format("%.1f", 100.0 / optimal.getThreadCount()));
        log.info("");

        log.info("=== Garbage Collection (가비지 컬렉션) ===");
        log.info("GC Frequency (GC 빈도): {}/s", String.format("%.2f", systemMetrics.getGcFrequency()));
        log.info("");
    }

    private PerformanceResult runBatchTest(int threadCount, int batchSize) throws InterruptedException {
        List<Cosmetic> testData = IntStream.range(0, TOTAL_RECORDS)
                .mapToObj(this::createTestCosmetic)
                .collect(Collectors.toList());

        // 작업 큐 생성
        BlockingQueue<List<Cosmetic>> workQueue = new LinkedBlockingQueue<>();
        int totalBatches = (int) Math.ceil((double) TOTAL_RECORDS / batchSize);
        
        // 데이터를 배치 단위로 분할하여 큐에 추가
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, TOTAL_RECORDS);
            workQueue.add(testData.subList(start, end));
        }

        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Long> threadTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger processedBatches = new AtomicInteger(0);

        // 트랜잭션 템플릿 설정
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setTimeout(10); // 10초 타임아웃 설정

        long startTime = System.currentTimeMillis();

        // 워커 스레드 생성
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Thread worker = new Thread(() -> {
                long threadStart = System.currentTimeMillis();
                List<Cosmetic> batch = null;

                try {
                    while ((batch = workQueue.poll()) != null) {
                        final List<Cosmetic> currentBatch = batch;
                        
                        // 트랜잭션 내에서 배치 처리
                        transactionTemplate.execute(status -> {
                            try {
                                measureJdbcMetrics(() -> processBatch(currentBatch));
                                processedBatches.incrementAndGet();
                                return null;
                            } catch (Exception e) {
                                status.setRollbackOnly();
                                log.error("배치 처리 중 오류 발생", e);
                                return null;
                            }
                        });

                        // 배치 처리 후 짧은 지연으로 리소스 경합 감소
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    log.error("워커 스레드 실행 중 오류", e);
                } finally {
                    long threadTime = System.currentTimeMillis() - threadStart;
                    threadTimes.add(threadTime);
                    latch.countDown();
                }
            });
            workers.add(worker);
            worker.start();
        }

        // 모든 워커 스레드 완료 대기
        latch.await();
        long totalTime = System.currentTimeMillis() - startTime;

        // 처리된 배치 수 확인
        int expectedBatches = (int) Math.ceil((double) TOTAL_RECORDS / batchSize);
        int actualBatches = processedBatches.get();
        if (actualBatches != expectedBatches) {
            log.warn("일부 배치가 처리되지 않음. 예상: {}, 실제: {}", expectedBatches, actualBatches);
        }

        return new PerformanceResult(threadCount, totalTime, threadTimes, batchSize);
    }

    private void processBatch(List<Cosmetic> batch) {
        for (Cosmetic cosmetic : batch) {
            CosmeticIngredient ingredient = new CosmeticIngredient();
            ingredient.setCosmetic(cosmetic);
            ingredient.setIngredient(testIngredient);
            cosmetic.getCosmeticIngredients().add(ingredient);
            em.persist(cosmetic);
        }
        em.flush();
        em.clear();
    }

    private Cosmetic createTestCosmetic(int index) {
        Cosmetic cosmetic = new Cosmetic();
        cosmetic.setName("Test Cosmetic " + index);
        cosmetic.setImage_path("/test/image" + index + ".jpg");

        CosmeticPurchaseLink link = new CosmeticPurchaseLink();
        link.setCosmetic(cosmetic);
        link.setPurchaseSite("Test Site");
        link.setPrice("10000");
        link.setUrl("http://test.com/" + index);
        cosmetic.getCosmeticPurchaseLinks().add(link);

        CosmeticIngredient ingredient = new CosmeticIngredient();
        ingredient.setCosmetic(cosmetic);
        ingredient.setIngredient(testIngredient);
        cosmetic.getCosmeticIngredients().add(ingredient);

        return cosmetic;
    }

    @lombok.Getter
    private static class PerformanceResult {
        private final int threadCount;
        private final int batchSize;
        private final long totalTime;
        private final double recordsPerSecond;
        private final double avgThreadTime;
        private final long threadTimeVariance;

        public PerformanceResult(int threadCount, long totalTime, List<Long> threadTimes, 
                               int batchSize, double recordsPerSecond, long variance) {
            this.threadCount = threadCount;
            this.batchSize = batchSize;
            this.totalTime = totalTime;
            this.recordsPerSecond = recordsPerSecond > 0 ? recordsPerSecond 
                    : (TOTAL_RECORDS * 1000.0) / totalTime;
            this.avgThreadTime = threadTimes.stream()
                    .mapToLong(Long::valueOf)
                    .average()
                    .orElse(0.0);
            this.threadTimeVariance = variance;
        }

        public PerformanceResult(int threadCount, long totalTime, List<Long> threadTimes, int batchSize) {
            this(threadCount, totalTime, threadTimes, batchSize, 
                 (TOTAL_RECORDS * 1000.0) / totalTime,
                 calculateVariance(threadTimes));
        }

        private static long calculateVariance(List<Long> threadTimes) {
            if (threadTimes.isEmpty()) return 0;
            long maxTime = threadTimes.stream().max(Long::compareTo).orElse(0L);
            long minTime = threadTimes.stream().min(Long::compareTo).orElse(0L);
            return maxTime - minTime;
        }
    }
}
