package gachonproject.web.api;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.RecommendedModel;
import gachonproject.web.service.AdminAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class AdminAIApiController {

    @Autowired
    private AdminAIService aiService;


    @GetMapping("/api/admin/AI/latest/OCR")
    public ResponseEntity OCR() {

        OCRModel ocrModel = aiService.latestOcr();

        return new ResponseEntity(ocrModel, HttpStatus.OK);

    }

    @GetMapping("/api/admin/AI/latest/reco")
    public ResponseEntity Reco() {

        RecommendedModel recommendedModel = aiService.latestReco();

        return new ResponseEntity(recommendedModel, HttpStatus.OK);

    }


    @GetMapping("/api/admin/AI/latest/OCR/List")
    public ResponseEntity OCRList() {

        List<OCRModel> ocrModels = aiService.latestOcrList();

        return new ResponseEntity(ocrModels, HttpStatus.OK);

    }



    @GetMapping("/api/admin/AI/latest/reco/List")
    public ResponseEntity RecoList() {

        List<RecommendedModel> recommendedModels = aiService.latestRecoList();

        return new ResponseEntity(recommendedModels, HttpStatus.OK);

    }


    @PostMapping("/api/admin/AI/latest/change/OCR")
    public ResponseEntity changeOCR(@RequestBody OCRModel ocrModel) {

        String version = ocrModel.getVersion();
        aiService.latestOcrChange(version);


        return new ResponseEntity(HttpStatus.OK);


    }

    @PostMapping("/api/admin/AI/latest/change/Reco")
    public ResponseEntity changeReco(@RequestBody RecommendedModel recommendedModel) {

        String version = recommendedModel.getVersion();
        aiService.latestRecoChange(version);


        return new ResponseEntity(HttpStatus.OK);


    }

    @PostMapping("/api/admin/AI/OCR/change")
    public ResponseEntity changeAI(@RequestBody OCRModel ocrModel) {

        List<OCRModel> ocrModels = aiService.latestOcrList();



        LocalDate currentDate = LocalDate.now();
        ocrModel.setDate(currentDate);

        String newVersion = aiService.incrementVersion2(ocrModels);
        ocrModel.setVersion(newVersion);


        aiService.OcrChange(ocrModel);


        return new ResponseEntity(HttpStatus.OK);


    }

    @PostMapping("/api/admin/AI/reco/change")
    public ResponseEntity changeAIreco(@RequestBody RecommendedModel recommendedModel) {

        List<RecommendedModel> recommendedModels = aiService.latestRecoList();



        LocalDate currentDate = LocalDate.now();
        recommendedModel.setDate(currentDate);

        String newVersion = aiService.incrementVersion3(recommendedModels);
        recommendedModel.setVersion(newVersion);


        aiService.RecoChange(recommendedModel);


        return new ResponseEntity(HttpStatus.OK);


    }








}
