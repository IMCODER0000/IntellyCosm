package gachonproject.mobile.service;

import gachonproject.mobile.domain.Ingredient;
import gachonproject.mobile.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkProcessingService {

    private final IngredientRepository ingredientRepository;
    private final CacheManager cacheManager;

    @Value("${bulk.processing.batch-size:1000}")
    private int defaultBatchSize;

    @Value("${bulk.processing.thread-pool-size:4}")
    private int threadPoolSize;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 배치 크기로 분할하여 벌크 업데이트 수행
     */
    @Transactional
    public void bulkUpdateInBatches(List<Ingredient> ingredients, int batchSize) {
        List<List<Ingredient>> batches = new ArrayList<>();
        for (int i = 0; i < ingredients.size(); i += batchSize) {
            batches.add(ingredients.subList(i, Math.min(i + batchSize, ingredients.size())));
        }

        batches.forEach(batch -> {
            List<Long> ids = batch.stream()
                    .map(Ingredient::getId)
                    .collect(Collectors.toList());
            
            // 벌크 업데이트 실행
            ingredientRepository.bulkUpdateSafetyGrade(ids, "A");
            
            // 캐시 갱신
            evictCacheForIngredients(ids);
        });
    }

    /**
     * 병렬 처리를 통한 대량 데이터 분석
     */
    public void parallelProcessIngredients(List<Ingredient> ingredients) {
        int batchSize = ingredients.size() / threadPoolSize;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < threadPoolSize; i++) {
            int start = i * batchSize;
            int end = (i == threadPoolSize - 1) ? ingredients.size() : (i + 1) * batchSize;
            List<Ingredient> batch = ingredients.subList(start, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> 
                processBatch(batch), executorService);
            futures.add(future);
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 스트리밍 방식의 대량 데이터 처리
     */
    @Transactional(readOnly = true)
    public void streamProcessIngredients() {
        long lastId = 0;
        List<Ingredient> batch;
        
        while (!(batch = ingredientRepository.findIngredientsInBatch(lastId, defaultBatchSize)).isEmpty()) {
            processBatch(batch);
            lastId = batch.get(batch.size() - 1).getId();
        }
    }

    /**
     * 개별 배치 처리 로직
     */
    private void processBatch(List<Ingredient> batch) {
        batch.forEach(ingredient -> {
            try {
                // 분석 로직 수행
                String analysisResult = analyzeIngredient(ingredient);
                
                // 분석 결과 저장
                ingredientRepository.saveAnalysisResult(
                    ingredient.getId(), 
                    analysisResult
                );
            } catch (Exception e) {
                log.error("Error processing ingredient {}: {}", ingredient.getId(), e.getMessage());
            }
        });
    }

    /**
     * 성분 분석 로직
     */
    private String analyzeIngredient(Ingredient ingredient) {
        // 실제 분석 로직 구현
        return "Analysis result for " + ingredient.getId();
    }

    /**
     * 캐시 삭제
     */
    private void evictCacheForIngredients(List<Long> ingredientIds) {
        ingredientIds.forEach(id -> {
            cacheManager.getCache("ingredient").evict(id);
        });
        cacheManager.getCache("ingredients").clear();
    }

    /**
     * 리소스 정리
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
