package gachonproject.mobile.performance;

import gachonproject.mobile.service.AiService;
import gachonproject.mobile.service.AnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ActiveProfiles("test")
public class DirectAnalysisPerformanceTest {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AiService aiService;

    @Test
    public void testDirectAnalysisPerformance() throws InterruptedException {
        int CONCURRENT_USERS = 10;
        int REQUESTS_PER_USER = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS * REQUESTS_PER_USER);
        List<Long> responseTimes = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_USER; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();

                        // 직접 AI 서비스 호출
                        aiService.processAnalysis("test-image.jpg", 1L);

                        long requestEnd = System.currentTimeMillis();
                        responseTimes.add(requestEnd - requestStart);

                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        latch.await(5, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        double avgResponseTime = responseTimes.stream().mapToLong(Long::valueOf).average().orElse(0.0);
        double requestsPerSecond = (CONCURRENT_USERS * REQUESTS_PER_USER) / ((endTime - startTime) / 1000.0);

        System.out.println("\n직접 호출 성능 테스트 결과:");
        System.out.println("총 요청 수: " + (CONCURRENT_USERS * REQUESTS_PER_USER));
        System.out.println("총 소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("초당 처리량: " + String.format("%.2f", requestsPerSecond) + " requests/second");
        System.out.println("평균 응답 시간: " + String.format("%.2f", avgResponseTime) + "ms");

        executorService.shutdown();
    }
}