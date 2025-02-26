package gachonproject.model.Service;


import gachonproject.model.Repository.OcrModelRepository;
import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.OCRModelHistory;
import gachonproject.web.dto.ModelRateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OcrModelService {

    @Autowired
    private final OcrModelRepository ocrModelRepository;



    // OCR 최신 모델 id 가져오기
    @Transactional(readOnly = true)
    public Long getLatestModelId(){
        return ocrModelRepository.getLatestModel().getId();
    }

    // OCR 최신 모델 버전 조회
    @Transactional(readOnly = true)
    public String getLatestModelVersion(){
        return ocrModelRepository.getLatestModel().getVersion();
    }

    // OCR모델 점수들 가져오기
    @Transactional(readOnly = true)
    public ModelRateDTO getModelRate(Long model_id){
        OCRModel ocrModel = ocrModelRepository.getModelRate(model_id);

        return new ModelRateDTO(ocrModel.getOne_rate(), ocrModel.getTwo_rate(), ocrModel.getThree_rate(), ocrModel.getFour_rate(), ocrModel.getFive_rate());

    }

    //OCR 모델 데이터 가져오기
    @Transactional(readOnly = true)
    public OCRModel getOcrModel() {
        return ocrModelRepository.getLatestModel();
    }

    //OCR history
    @Transactional(readOnly = true)
    public List<OCRModelHistory> findALLOCRModelHistory(){
        return ocrModelRepository.findALLOCRModelHistory();
    }

    //OCR history by id
    @Transactional(readOnly = true)
    public List<OCRModelHistory> findALLOCRModelHistoryByModelId(Long model_id){
        return ocrModelRepository.findALLOCRModelHistoryByModelId(model_id);
    }

}
