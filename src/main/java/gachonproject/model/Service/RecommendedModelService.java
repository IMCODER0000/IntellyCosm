package gachonproject.model.Service;


import gachonproject.model.Repository.RecommendedModelRepository;
import gachonproject.model.domain.RecommendedModel;
import gachonproject.model.domain.RecommendedModelHistory;
import gachonproject.web.dto.ModelRateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecommendedModelService {


    @Autowired
    private RecommendedModelRepository recommendedModelRepository;


    public ModelRateDTO getModelRate(Long recommendedmodel_id){

        RecommendedModel recommendedModel = recommendedModelRepository.getModelRate(recommendedmodel_id);

        return new ModelRateDTO(recommendedModel.getOne_rate(), recommendedModel.getTwo_rate(), recommendedModel.getThree_rate(), recommendedModel.getFour_rate(), recommendedModel.getFive_rate());


    }

    @Transactional(readOnly = true)
    public Long getLatestModelId(){
        return recommendedModelRepository.getLatestModel().getId();
    }

    // OCR 최신 모델 버전 조회
    @Transactional(readOnly = true)
    public String getLatestModelVersion(){
        return recommendedModelRepository.getLatestModel().getVersion();
    }

    @Transactional(readOnly = true)
    public RecommendedModel getRecommendedModel(){
        return recommendedModelRepository.getLatestModel();
    }

    @Transactional(readOnly = true)
    public List<RecommendedModelHistory> findAllRecommendedModelHistory(){
        return recommendedModelRepository.findAllRecommendedModelHistory();
    }

    @Transactional(readOnly = true)
    public List<RecommendedModelHistory> findAllRecommendedModelHistoryByModelId(Long recommendedmodel_id){
        return recommendedModelRepository.findAllRecommendedModelHistoryByModelId(recommendedmodel_id);
    }


    



}
