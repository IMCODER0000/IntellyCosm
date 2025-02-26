package gachonproject.web.api;


import gachonproject.model.Service.OcrModelService;
import gachonproject.model.Service.RecommendedModelService;
import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.OCRModelHistory;
import gachonproject.model.domain.RecommendedModel;
import gachonproject.model.domain.RecommendedModelHistory;
import gachonproject.web.dto.HistoryContentDTO;
import gachonproject.web.dto.HistoryDTO;
import gachonproject.web.dto.HistoryTotalDTO;
import gachonproject.web.dto.TotalModelDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminEvaluationApiController {


    @Autowired
    private final OcrModelService ocrModelService;
    private final RecommendedModelService recommendedModelService;



    @GetMapping("/api/admin/evaluation")
    public ResponseEntity getEvaluation() {

        OCRModel ocrModel = ocrModelService.getOcrModel();
        RecommendedModel recommendedModel = recommendedModelService.getRecommendedModel();

        TotalModelDTO ocrModelRate = new TotalModelDTO("ocrModel",ocrModel.getFive_rate(), ocrModel.getFour_rate(), ocrModel.getThree_rate(),
                ocrModel.getTwo_rate(), ocrModel.getOne_rate());
        TotalModelDTO recommendModelRate = new TotalModelDTO("recommendedModel",recommendedModel.getFive_rate(), recommendedModel.getFour_rate(), recommendedModel.getThree_rate(),
                recommendedModel.getTwo_rate(), recommendedModel.getOne_rate());

        List<TotalModelDTO> totalModelDTOList = new ArrayList<>();
        totalModelDTOList.add(ocrModelRate);
        totalModelDTOList.add(recommendModelRate);


        int day_count = 0;
        LocalDate start_date = null;
        LocalDate end_date = LocalDate.now();
        List<Float> average = new ArrayList<>();
        List<OCRModelHistory> allocrModelHistoryByModelId = ocrModelService.findALLOCRModelHistoryByModelId(ocrModel.getId());
        for (OCRModelHistory ocrModelHistory : allocrModelHistoryByModelId) {
            float average3 = ocrModelHistory.getAverage();
            average.add(average3);
            start_date = ocrModelHistory.getStartDate();
            day_count++;
        }

        int day_count2 = 0;
        LocalDate start_date2 = null;
        LocalDate end_date2 = LocalDate.now();
        List<Float> average2 = new ArrayList<>();
        List<RecommendedModelHistory> allRecommendedModelHistoryByModelId = recommendedModelService.findAllRecommendedModelHistoryByModelId(recommendedModel.getId());
        for (RecommendedModelHistory recommendedModelHistory : allRecommendedModelHistoryByModelId) {
            float average1 = recommendedModelHistory.getAverage();
            average2.add(average1);
            start_date2 = recommendedModelHistory.getStartDate();
            day_count2++;
        }

        HistoryContentDTO historyContentDTO = new HistoryContentDTO(average, day_count, start_date, end_date);
        HistoryContentDTO historyContentDTO2 = new HistoryContentDTO(average2, day_count2, start_date2, end_date2);



        HistoryDTO historyDTO = new HistoryDTO(historyContentDTO, "ocrModel");
        HistoryDTO historyDTO1 = new HistoryDTO(historyContentDTO2, "recommendedModel");

        List<HistoryDTO> HistoryDTOList = new ArrayList<>();
        HistoryDTOList.add(historyDTO);
        HistoryDTOList.add(historyDTO1);

        HistoryTotalDTO result = new HistoryTotalDTO(HistoryDTOList, totalModelDTOList);


        return new ResponseEntity(result,HttpStatus.OK);


    }

//    @GetMapping("/api/admin/evaluation/history")
//    public ResponseEntity getEvaluationHistory(){


//        List<OCRModelHistory> allocrModelHistory = ocrModelService.findALLOCRModelHistory();
//        List<RecommendedModelHistory> allRecommendedModelHistory = recommendedModelService.findAllRecommendedModelHistory();
//
//        List<String> uniqueVersions = allocrModelHistory.stream()
//                .map(OCRModelHistory::getOcrModel)
//                .map(OCRModel::getVersion)
//                .distinct()
//                .collect(Collectors.toList());
//
//        List<String> uniqueVersions2 = allRecommendedModelHistory.stream()
//                .map(RecommendedModelHistory::getRecommendedModel)
//                .map(RecommendedModel::getVersion)
//                .distinct()
//                .collect(Collectors.toList());
//
//
//        // 추출된 버전 리스트 출력
//        uniqueVersions.forEach(System.out::println);
//
//        // 추출된 버전 리스트 출력
//        uniqueVersions2.forEach(System.out::println);
//
//        return new ResponseEntity(HttpStatus.OK);





//    }
    @GetMapping("/api/admin/evaluation/history")
    public ResponseEntity getEvaluationHistory(){


    //        List<OCRModelHistory> allocrModelHistory = ocrModelService.findALLOCRModelHistory();
    //        List<RecommendedModelHistory> allRecommendedModelHistory = recommendedModelService.findAllRecommendedModelHistory();
    //
    //        List<String> uniqueVersions = allocrModelHistory.stream()
    //                .map(OCRModelHistory::getOcrModel)
    //                .map(OCRModel::getVersion)
    //                .distinct()
    //                .collect(Collectors.toList());
    //
    //        List<String> uniqueVersions2 = allRecommendedModelHistory.stream()
    //                .map(RecommendedModelHistory::getRecommendedModel)
    //                .map(RecommendedModel::getVersion)
    //                .distinct()
    //                .collect(Collectors.toList());
    //
    //
    //        // 추출된 버전 리스트 출력
    //        uniqueVersions.forEach(System.out::println);
    //
    //        // 추출된 버전 리스트 출력
    //        uniqueVersions2.forEach(System.out::println);

        OCRModel ocrModel = ocrModelService.getOcrModel();

        Map<String, List<Map<String, Object>>> data = new HashMap<>();

        List<Map<String, Object>> ocrVersions = new ArrayList<>();
        Map<String, Object> ocrV1_0_1 = new HashMap<>();
        ocrV1_0_1.put("version", "v1.0.1");
        ocrV1_0_1.put("average", Arrays.asList(5, 3, 4.1, 4.2, 3.8));
        ocrV1_0_1.put("rate", Arrays.asList(5, 1, 4, 4, 5));

        Map<String, Object> ocrV1_0_2 = new HashMap<>();
        ocrV1_0_2.put("version", "v1.0.2");
        ocrV1_0_2.put("average", Arrays.asList(3, 2.8, 2, 4, 3.3));
        ocrV1_0_2.put("rate", Arrays.asList(5, 4, 3, 2, 1));

        Map<String, Object> ocrV1_0_0 = new HashMap<>();
        ocrV1_0_0.put("version", "v1.0.0");
        ocrV1_0_0.put("average", Arrays.asList(4.2, 3.2, 4.6, 3.3, 4));
        ocrV1_0_0.put("rate", Arrays.asList(5, 5, 1, 2, 1));

        if(ocrModel.getVersion().equals("v1.0.0")){
            ocrVersions.add(ocrV1_0_1);
            System.out.println("v100");
        }
        else if(ocrModel.getVersion().equals("v1.0.1")){
            ocrVersions.add(ocrV1_0_0);
            System.out.println("v101");
        }
        else if(ocrModel.getVersion().equals("v1.0.2")){
            ocrVersions.add(ocrV1_0_1);
            ocrVersions.add(ocrV1_0_0);
            System.out.println("v102");
        }
        else if(ocrModel.getVersion().equals("v1.0.3")){
            ocrVersions.add(ocrV1_0_0);
            ocrVersions.add(ocrV1_0_1);
            ocrVersions.add(ocrV1_0_2);
            System.out.println("v103");
        }

        RecommendedModel recommendedModel = recommendedModelService.getRecommendedModel();


        List<Map<String, Object>> recommendVersions = new ArrayList<>();
        Map<String, Object> recommendV1_0_1 = new HashMap<>();
        recommendV1_0_1.put("version", "v1.0.1");
        recommendV1_0_1.put("average", Arrays.asList(3, 2.5, 2.8, 4.2, 4.8));
        recommendV1_0_1.put("rate", Arrays.asList(1, 2, 3, 4, 5));

        Map<String, Object> recommendV1_0_2 = new HashMap<>();
        recommendV1_0_2.put("version", "v1.0.2");
        recommendV1_0_2.put("average", Arrays.asList(2.1, 4.2, 3.8, 4, 4.1));
        recommendV1_0_2.put("rate", Arrays.asList(5, 4, 3, 2, 1));

        Map<String, Object> recommendV1_0_0 = new HashMap<>();
        recommendV1_0_0.put("version", "v1.0.0");
        recommendV1_0_0.put("average", Arrays.asList(3.8, 4.2, 2.9, 4.4, 4.1));
        recommendV1_0_0.put("rate", Arrays.asList(1, 2, 3, 4, 5));



        if(recommendedModel.getVersion().equals("v1.0.0")){
            recommendVersions.add(recommendV1_0_1);
            System.out.println("v100");
        }
        if(recommendedModel.getVersion().equals("v1.0.1")){
            recommendVersions.add(recommendV1_0_0);
        }
        if(recommendedModel.getVersion().equals("v1.0.2")){
            recommendVersions.add(recommendV1_0_1);
            recommendVersions.add(recommendV1_0_0);
        }
        if(recommendedModel.getVersion().equals("v1.0.3")){
            recommendVersions.add(recommendV1_0_0);
            recommendVersions.add(recommendV1_0_1);
            recommendVersions.add(recommendV1_0_2);
        }


        data.put("OCR", ocrVersions);
        data.put("recommend", recommendVersions);







        return new ResponseEntity(data,HttpStatus.OK);



    }





}
