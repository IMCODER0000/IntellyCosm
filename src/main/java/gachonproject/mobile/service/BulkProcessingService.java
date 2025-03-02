package gachonproject.mobile.service;

import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.repository.IngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BulkProcessingService {

    private final IngredientRepository ingredientRepository;
    private final CacheManager cacheManager;

    @Value("${bulk.processing.batch-size:1000}")
    private int defaultBatchSize;

    @Value("${bulk.processing.thread-pool-size:4}")
    private int threadPoolSize;

    private ExecutorService executorService;
    
    @Autowired
    public BulkProcessingService(IngredientRepository ingredientRepository, CacheManager cacheManager) {
        this.ingredientRepository = ingredientRepository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

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
        if (batch == null || batch.isEmpty()) {
            log.warn("빈 배치가 처리되었습니다.");
            return;
        }
        
        List<Long> processedIds = new ArrayList<>();
        
        batch.forEach(ingredient -> {
            try {
                // 성분 분석 수행
                String analysisResult = analyzeIngredient(ingredient);
                
                // 분석 결과 저장
                if (analysisResult != null) {
                    ingredientRepository.saveAnalysisResult(
                        ingredient.getId(), 
                        analysisResult
                    );
                    processedIds.add(ingredient.getId());
                }
            } catch (Exception e) {
                log.error("성분 처리 중 오류 발생 ID {}: {}", ingredient.getId(), e.getMessage());
            }
        });
        
        // 처리된 항목에 대한 캐시 갱신
        if (!processedIds.isEmpty()) {
            evictCacheForIngredients(processedIds);
        }
    }

    /**
     * 성분 분석 로직
     */
    private String analyzeIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            log.warn("분석을 위한 성분이 null입니다.");
            return null;
        }
        
        try {
            // 실제 분석 로직 구현 (현재는 예시)
            log.info("성분 분석 중: {}", ingredient.getId());
            return "성분 " + ingredient.getId() + "에 대한 분석 결과: " + 
                   (ingredient.getName() != null ? ingredient.getName() : "이름 없음");
        } catch (Exception e) {
            log.error("성분 분석 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 캐시 삭제
     */
    private void evictCacheForIngredients(List<Long> ingredientIds) {
        if (cacheManager.getCache("ingredient") != null) {
            ingredientIds.forEach(id -> {
                cacheManager.getCache("ingredient").evict(id);
            });
        }
        
        if (cacheManager.getCache("ingredients") != null) {
            cacheManager.getCache("ingredients").clear();
        }
    }

    /**
     * 리소스 정리
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
