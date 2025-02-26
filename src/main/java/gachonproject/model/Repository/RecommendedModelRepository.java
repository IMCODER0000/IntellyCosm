package gachonproject.model.Repository;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.RecommendedModel;
import gachonproject.model.domain.RecommendedModelHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecommendedModelRepository {

    @PersistenceContext
    private EntityManager em;


    public RecommendedModel getModelRate(Long recommendedmodel_id){
        return em.find(RecommendedModel.class, recommendedmodel_id);
    }

    public RecommendedModel getLatestModel(){
        return em.createQuery("SELECT r FROM RecommendedModel r WHERE r.latest = :isLatest", RecommendedModel.class)
                .setParameter("isLatest", true)
                .getSingleResult();
    }
    public List<RecommendedModelHistory> findAllRecommendedModelHistory(){
        return em.createQuery("SELECT r FROM RecommendedModelHistory r", RecommendedModelHistory.class)
                .getResultList();
    }

    public List<RecommendedModelHistory> findAllRecommendedModelHistoryByModelId(Long recommendedmodel_id){
        return em.createQuery("SELECT r FROM RecommendedModelHistory r WHERE r.recommendedModel.id = :recommendedmodel_id", RecommendedModelHistory.class)
                .setParameter("recommendedmodel_id", recommendedmodel_id)
                .getResultList();
    }

}
