package gachonproject.mobile.repository;

import com.mysql.cj.log.Log;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CosmeticRepository {
    @PersistenceContext
    private EntityManager em;

    public void createCosmetic(Cosmetic cosmetic){
        em.persist(cosmetic);
    }

    public void createCosmeticPurchaseLink(CosmeticPurchaseLink cosmeticPurchaseLink){
        em.persist(cosmeticPurchaseLink);
    }

    public void updateCosmetic(Cosmetic cosmetic){
        try {
            em.merge(cosmetic);
        } catch (Exception e) {
            // 예외 처리 코드
            e.printStackTrace(); // 또는 로그 기록 등을 수행할 수 있습니다.
        }
    }

    public Cosmetic findById(Long id) {
        return em.createQuery(
                "SELECT c FROM Cosmetic c " +
                "LEFT JOIN FETCH c.cosmeticPurchaseLinks " +
                "LEFT JOIN FETCH c.cosmeticIngredients " +
                "WHERE c.id = :id", Cosmetic.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Cosmetic> findAll() {
        return em.createQuery(
                "SELECT DISTINCT c FROM Cosmetic c " +
                "LEFT JOIN FETCH c.cosmeticPurchaseLinks " +
                "LEFT JOIN FETCH c.cosmeticIngredients", Cosmetic.class)
                .getResultList();
    }

    public void updateCosmeticPurchaseUpdate(List<CosmeticPurchaseLink> cosmeticPurchaseLink) {
        try {
            for (CosmeticPurchaseLink purchaseLink : cosmeticPurchaseLink) {
                em.merge(purchaseLink);
            }
        } catch (Exception e) {
            // 예외 처리 코드
            e.printStackTrace(); // 또는 로그 기록 등을 수행할 수 있습니다.
        }
    }

    public List<CosmeticPurchaseLink> findAllCosmeticPurchaseLinkByCosmetic_id(Long cosmetic_id) {
        return em.createQuery("SELECT c FROM CosmeticPurchaseLink c WHERE c.cosmetic.id = :cosmetic_id", CosmeticPurchaseLink.class)
                .setParameter("cosmetic_id", cosmetic_id)
                .getResultList();
    }

    public void deleteCosmeticIngredient(CosmeticIngredient cosmeticIngredient) {
        em.remove(cosmeticIngredient);
    }

    public void deleteCosmeticPurchase(CosmeticPurchaseLink cosmeticPurchaseLink) {
        em.remove(cosmeticPurchaseLink);
    }

    public void deleteCosmetic(Cosmetic cosmetic){
        em.remove(cosmetic);
    }

    public void updateCosmeticPurchaseUpdate2(CosmeticPurchaseLink cosmeticPurchaseLink) {

        em.merge(cosmeticPurchaseLink);

    }

    public void cosmeticPurchaseDeleteById(Long id) {
        em.remove(em.find(Cosmetic.class, id));
    }

    public void createCosmeticIngredient(CosmeticIngredient cosmeticIngredient){
        em.persist(cosmeticIngredient);
    }

    public List<CosmeticIngredient> findAllCosmeticIngredientByCosmeticId(Long cosmetic_id) {
        return em.createQuery("SELECT ci FROM CosmeticIngredient ci WHERE ci.cosmetic.id = :cosmetic_id", CosmeticIngredient.class)
                .setParameter("cosmetic_id", cosmetic_id)
                .getResultList();
    }
}
