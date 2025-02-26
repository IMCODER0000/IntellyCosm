package gachonproject.mobile.service;

import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.IngredientFeature;
import gachonproject.mobile.domain.ingredient.IngredientPurpose;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import gachonproject.mobile.repository.IngredientRepository;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.repository.PreferenceIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private PreferenceIngredientRepository preferedIngredientRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Value("${ingredient.batch-size:1000}")
    private int batchSize;

    // 전체 성분
    @Transactional(readOnly = true)
    @Cacheable(value = "ingredients", key = "'all'")
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    // 선호 성분
    @Transactional(readOnly = true)
    @Cacheable(value = "preferred_ingredients", key = "#member_id")
    public List<Ingredient> getPrefferredIngredient(Long member_id) {
        List<PreferedIngredient> ingredients = ingredientRepository.findByMemberId(member_id);
        return ingredients.stream()
                .map(PreferedIngredient::getIngredient)
                .collect(Collectors.toList());
    }

    // 선호 성분 변경
    @Transactional
    public boolean updatePrefferredIngredient(Long member_id, List<String> preference) {

        Member findMemeber = memberRepository.findByid(member_id);
        List<PreferedIngredient> preferedIngredients = new ArrayList<>();
        List<PreferedIngredient> preferedIngredients2 = new ArrayList<>();
        List<PreferedIngredient> allPreference = preferedIngredientRepository.findAll(member_id);
        for (PreferedIngredient preferedIngredient : allPreference) {
            String ingredientName = preferedIngredient.getIngredient().getName();
            if (preference.contains(ingredientName)) {
                preferedIngredients.add(preferedIngredient);
                preference.remove(ingredientName); // preference에서 해당 성분 제거
            }
        }

        for (String prefer : preference) {
            PreferedIngredient newPreferedIngredient = new PreferedIngredient();
            newPreferedIngredient.setMember(findMemeber);
            Ingredient ingredient = ingredientRepository.ingredientfindByName(prefer); // 예시 코드
            newPreferedIngredient.setIngredient(ingredient);
            preferedIngredients2.add(newPreferedIngredient);
        }

        for (PreferedIngredient preferedIngredient : preferedIngredients) {
            System.out.println(preferedIngredient.getIngredient().getName());
        }

        for (PreferedIngredient preferedIngredient : preferedIngredients2) {
            System.out.println(preferedIngredient.getIngredient().getName());
        }

        boolean result = preferedIngredientRepository.deletePreference(preferedIngredients);
        boolean result2 = preferedIngredientRepository.createPreference(preferedIngredients2);
        if (result && result2){
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "ingredient", key = "#id")
    public Ingredient getIngredient(Long id) {
        return ingredientRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void deleteIngredientPurpose(Long ingredient_id) {
        List<IngredientPurpose> ingredientPurpose = ingredientRepository.findIngredientPurpose(ingredient_id);
        ingredientRepository.deleteIngredientPurpose(ingredientPurpose);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void deleteIngredientFeatures(Long ingredient_id) {
        List<IngredientFeature> ingredientFeatures = ingredientRepository.findIngredientFeatures(ingredient_id);
        ingredientRepository.deleteIngredientFeature(ingredientFeatures);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void deleteSkin(Long ingredient_id){
        List<SkinTypeFeature> skin = ingredientRepository.findSkin(ingredient_id);
        ingredientRepository.deleteSkin(skin);
    }

    @Transactional
    @CachePut(value = "ingredient", key = "#ingredient.id")
    @CacheEvict(value = "ingredients", allEntries = true)
    public Ingredient updateIngredient(Ingredient ingredient){
        ingredientRepository.updateIngredient(ingredient);
        return ingredient;
    }

    @Transactional
    public void createIngredientPurpose(List<IngredientPurpose> purposes){
        ingredientRepository.createIngredientPurpose(purposes);
    }

    @Transactional
    public void createIngredientFeatures(List<IngredientFeature> features){
        ingredientRepository.createIngredientFeatures(features);
    }

    @Transactional
    public void createIngredientSkin(List<SkinTypeFeature> skinTypeFeatures1 ){
        ingredientRepository.createIngredientSkin(skinTypeFeatures1);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void createIngredient(Ingredient ingredient){
        ingredientRepository.createIngredient(ingredient);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void deleteIngredient(Ingredient ingredient){
        ingredientRepository.deleteIngredient(ingredient);
    }

    @Transactional
    @CacheEvict(value = {"ingredient", "ingredients"}, allEntries = true)
    public void deleteIngredientById(Long ingredient_id){
        Ingredient findIngredient = ingredientRepository.findById(ingredient_id);
        ingredientRepository.deleteIngredient(findIngredient);
    }

    /**
     * 전체 성분 목록을 배치 단위로 조회
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ingredients", key = "'batch:' + #lastId")
    public List<Ingredient> getIngredientsInBatch(Long lastId) {
        return ingredientRepository.findAllInBatch(lastId, batchSize);
    }

    /**
     * ID 목록으로 성분 일괄 조회
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ingredients", key = "'ids:' + #ids")
    public List<Ingredient> getIngredientsByIds(List<Long> ids) {
        return ingredientRepository.findAllByIds(ids);
    }

    /**
     * 스트림 방식으로 전체 성분 처리
     */
    @Transactional(readOnly = true)
    public void processAllIngredients(Consumer<List<Ingredient>> processor) {
        long lastId = 0;
        List<Ingredient> batch;
        
        while (!(batch = getIngredientsInBatch(lastId)).isEmpty()) {
            processor.accept(batch);
            lastId = batch.get(batch.size() - 1).getId();
        }
    }
}
