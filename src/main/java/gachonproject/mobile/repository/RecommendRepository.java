package gachonproject.mobile.repository;


import gachonproject.mobile.domain.recommend.RecommendEvaluation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class RecommendRepository {

    @PersistenceContext
    private EntityManager em;


    public void saveEvaluation(RecommendEvaluation recommendEvaluation) {
        em.persist(recommendEvaluation);
    }

    public void createRecommendEvaluation(RecommendEvaluation recommendEvaluation) {
        em.persist(recommendEvaluation);
    }

    public List<RecommendEvaluation> getAllRecommendEvaluation(LocalDate date) {

        return em.createQuery("SELECT r FROM RecommendEvaluation r WHERE r.date = :date", RecommendEvaluation.class)
                .setParameter("date", date)
                .getResultList();

    }


}
