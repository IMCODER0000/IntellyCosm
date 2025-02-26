package gachonproject.web.repository;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.OCRModelHistory;
import gachonproject.model.domain.RecommendedModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminAIRepository {


    @PersistenceContext
    private EntityManager em;


    public OCRModel latestOcr() {
        try {
            return em.createQuery("SELECT o FROM OCRModel o WHERE o.latest = true", OCRModel.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }



    public RecommendedModel latestReco(){
        return em.createQuery("SELECT r FROM RecommendedModel r WHERE r.latest = true", RecommendedModel.class).getSingleResult();
    }

    public List<RecommendedModel> latestRecoList(){
        return em.createQuery("SELECT r FROM RecommendedModel r", RecommendedModel.class).getResultList();
    }

    public List<OCRModel> latestOcrList(){
        return em.createQuery("SELECT r FROM OCRModel r", OCRModel.class).getResultList();
    }

    public void latestOcrChange(OCRModel ocrModel, String version) {
        String version1 = ocrModel.getVersion();

        // 기존 latest 값을 false로 변경
        em.createQuery("UPDATE OCRModel SET latest = false WHERE version = :version1")
                .setParameter("version1", version1)
                .executeUpdate();

        // 새 버전을 latest로 설정
        em.createQuery("UPDATE OCRModel SET latest = true WHERE version = :version")
                .setParameter("version", version)
                .executeUpdate();
    }

    public void latestRecoChange(RecommendedModel recommendedModel, String version) {
        String version1 = recommendedModel.getVersion();

        // 기존 latest 값을 false로 변경
        em.createQuery("UPDATE RecommendedModel SET latest = false WHERE version = :version1")
                .setParameter("version1", version1)
                .executeUpdate();

        // 새 버전을 latest로 설정
        em.createQuery("UPDATE RecommendedModel SET latest = true WHERE version = :version")
                .setParameter("version", version)
                .executeUpdate();
    }

    public void OcrChange(OCRModel ocrModel, OCRModel ocrModel1) {

        String version1 = ocrModel.getVersion();

        // 기존 latest 값을 false로 변경
        em.createQuery("UPDATE OCRModel SET latest = false WHERE version = :version1")
                .setParameter("version1", version1)
                .executeUpdate();



        em.persist(ocrModel1);




    }

    public void RecoChange(RecommendedModel recommendedMode, RecommendedModel recommendedModel) {

        String version1 = recommendedMode.getVersion();

        // 기존 latest 값을 false로 변경
        em.createQuery("UPDATE RecommendedModel SET latest = false WHERE version = :version1")
                .setParameter("version1", version1)
                .executeUpdate();



        em.persist(recommendedModel);




    }






}
