package gachonproject.mobile.repository;

import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.IngredientFeature;
import gachonproject.mobile.domain.ingredient.IngredientPurpose;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class IngredientRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 전체 성분 목록을 배치 단위로 조회
     */
    @Query(value = "SELECT i FROM Ingredient i WHERE i.id > :lastId ORDER BY i.id ASC",
           nativeQuery = true)
    public List<Ingredient> findAllInBatch(@Param("lastId") Long lastId, @Param("batchSize") int batchSize) {
        return em.createQuery("SELECT i FROM Ingredient i WHERE i.id > :lastId ORDER BY i.id ASC", Ingredient.class)
                .setParameter("lastId", lastId)
                .setMaxResults(batchSize)
                .getResultList();
    }

    /**
     * ID 목록으로 성분 일괄 조회
     */
    @Query("SELECT i FROM Ingredient i WHERE i.id IN :ids")
    public List<Ingredient> findAllByIds(@Param("ids") List<Long> ids) {
        return em.createQuery("SELECT i FROM Ingredient i WHERE i.id IN :ids", Ingredient.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public List<Ingredient> findAll(){
        return em.createQuery("select i from Ingredient i", Ingredient.class)
                .getResultList();

    }

    public Ingredient findById(Long id){
        return em.find(Ingredient.class, id);
    }


    public List<PreferedIngredient> findByIngredient(List<Ingredient> ingredients) {
        return em.createQuery("select p from PreferedIngredient p where p.ingredient in :ingredients", PreferedIngredient.class)
                .setParameter("ingredients", ingredients)
                .getResultList();
    }


    public List<PreferedIngredient> findByMemberId(Long member_id) {
        Member findMember = em.find(Member.class, member_id);
        return em.createQuery("select p from PreferedIngredient p where p.member = :member", PreferedIngredient.class)
                .setParameter("member", findMember)
                .getResultList();
    }

    public Ingredient update(Ingredient ingredient) {
        return em.merge(ingredient);
    }

    public Ingredient ingredientfindByName(String name) {
        try {
            return em.createQuery("select i from Ingredient i where i.name = :name", Ingredient.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            // 성분을 찾을 수 없을 때의 처리
            return null;
        }
    }

    public List<IngredientPurpose> findIngredientPurpose(Long ingredient_id) {
        return em.createQuery("SELECT i FROM IngredientPurpose i WHERE  i.ingredient.id = :ingredient_id", IngredientPurpose.class)
                .setParameter("ingredient_id", ingredient_id)
                .getResultList();
    }
    public List<IngredientFeature> findIngredientFeatures(Long ingredient_id) {
        return em.createQuery("SELECT i FROM IngredientFeature i WHERE  i.ingredient.id = :ingredient_id", IngredientFeature.class)
                .setParameter("ingredient_id", ingredient_id)
                .getResultList();
    }
    public List<SkinTypeFeature> findSkin(Long ingredient_id) {
        return em.createQuery("SELECT s FROM SkinTypeFeature s WHERE s.ingredient.id = :ingredient_id", SkinTypeFeature.class)
                .setParameter("ingredient_id", ingredient_id)
                .getResultList();
    }

    public void deleteSkin(List<SkinTypeFeature> skinTypeFeatures) {
        for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
            em.remove(skinTypeFeature);
        }
    }
    public void deleteIngredientPurpose(List<IngredientPurpose> purposes) {
        for (IngredientPurpose purpose : purposes) {
            em.remove(purpose);
        }
    }
    public void deleteIngredientFeature(List<IngredientFeature> features) {
        for (IngredientFeature feature : features) {
            em.remove(feature);
        }
    }

    public void createIngredient(Ingredient ingredient){

        em.persist(ingredient);

    }

    public void updateIngredient(Ingredient ingredient){

        em.merge(ingredient);

    }


    public void deleteIngredient(Ingredient ingredient){
        em.remove(ingredient);
    }

    public void createIngredientPurpose(List<IngredientPurpose> purposes) {
        for (IngredientPurpose purpose : purposes) {
            em.persist(purpose);
        }

    }
    public void createIngredientFeatures(List<IngredientFeature> features) {
        for (IngredientFeature feature : features) {
            em.persist(feature);
        }

    }
    public void createIngredientSkin(List<SkinTypeFeature> skinTypeFeatures) {
        for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
            em.persist(skinTypeFeature);
        }

    }

    @Modifying
    @Query(value = "UPDATE Ingredient i SET i.safety_grade = :safetyGrade WHERE i.id IN :ids")
    int bulkUpdateSafetyGrade(@Param("ids") List<Long> ids, @Param("safetyGrade") String safetyGrade);

    @Query(value = "SELECT i FROM Ingredient i WHERE i.id > :lastId ORDER BY i.id ASC LIMIT :batchSize", 
           nativeQuery = true)
    List<Ingredient> findIngredientsInBatch(@Param("lastId") Long lastId, @Param("batchSize") int batchSize);

    @Query("SELECT i FROM Ingredient i WHERE i.updated_at IS NULL")
    List<Ingredient> findIngredientsNeedingUpdate();

    @Modifying
    @Query(value = "INSERT INTO ingredient_analysis_result (ingredient_id, analysis_data) " +
           "VALUES (:ingredientId, :analysisData) " +
           "ON DUPLICATE KEY UPDATE analysis_data = :analysisData", 
           nativeQuery = true)
    void saveAnalysisResult(@Param("ingredientId") Long ingredientId, 
                           @Param("analysisData") String analysisData);
}
