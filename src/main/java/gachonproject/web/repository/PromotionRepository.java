package gachonproject.web.repository;


import gachonproject.web.domain.PromotionList;
import gachonproject.web.domain.PromotionMain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PromotionRepository {


    @PersistenceContext
    private EntityManager em;



    public void promotionAdd(PromotionMain promotionMain){
        em.persist(promotionMain);
    }
    public void promotionAdd2(PromotionList promotionList){
        em.persist(promotionList);
    }


    public void promotionDel(Long id){
        PromotionMain findPro = em.createQuery("SELECT p FROM PromotionMain p WHERE p.id = :id", PromotionMain.class)
                .setParameter("id", id)
                .getSingleResult();
        em.remove(findPro);
    }

    public void promotionListDel(Long id){
        em.createQuery("DELETE FROM PromotionList p WHERE p.main_id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void promotionListAdd(PromotionList promotionList){

        em.persist(promotionList);
    }
    public void promotionDelAll() {
        em.createQuery("DELETE FROM PromotionList").executeUpdate();
    }



    public List<PromotionMain> getAllPromotionMain(){

        return em.createQuery("SELECT p FROM PromotionMain p", PromotionMain.class).getResultList();

    }

    public List<PromotionList> getAllPromotionList(){

        return em.createQuery("SELECT p FROM PromotionList p", PromotionList.class).getResultList();
    }

    public PromotionMain findAllPromotionMain(Long id){
        return em.createQuery("SELECT p FROM PromotionMain p WHERE p.id = :id", PromotionMain.class )
                .setParameter("id", id)
                .getSingleResult();

    }

}
