package gachonproject.model.Repository;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.OCRModelHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OcrModelRepository {

    @PersistenceContext
    private EntityManager em;


    public OCRModel getLatestModel(){
        return em.createQuery("SELECT o FROM OCRModel o WHERE o.latest = :isLatest", OCRModel.class)
                .setParameter("isLatest", true)
                .getSingleResult();
    }

    public OCRModel getModelRate(Long ocrModel_id){
        return em.find(OCRModel.class, ocrModel_id);
    }


    public List<OCRModelHistory> findALLOCRModelHistory(){
        return em.createQuery("SELECT o FROM OCRModelHistory o", OCRModelHistory.class)
                .getResultList();
    }

    public List<OCRModelHistory> findALLOCRModelHistoryByModelId(Long model_id){
        return em.createQuery("SELECT o FROM OCRModelHistory o WHERE o.ocrModel.id = :model_id", OCRModelHistory.class)
                .setParameter("model_id", model_id)
                .getResultList();
    }




}
