package gachonproject.mobile.api;

import gachonproject.mobile.api.dto.RecommendDTO;
import gachonproject.mobile.service.EvaluationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EvaluationApiController {

    private final EvaluationService evaluationService;

    @PostMapping("/api/user/analysis/evaluation/{member_id}")
    public ResponseEntity<RecommendDTO> analysisEvaluation(
            @PathVariable("member_id") Long memberId,
            @RequestBody Evaluation evaluation) {
        RecommendDTO result = evaluationService.processAnalysisEvaluation(
                memberId,
                evaluation.getAnalysis_id(),
                evaluation.getCosmetic_score()
        );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/api/user/conparison/evaluation/{member_id}")
    public ResponseEntity<RecommendDTO> comparisonAnalysisEvaluation(
            @PathVariable("member_id") Long memberId,
            @RequestBody Evaluation2 evaluation) {
        RecommendDTO result = evaluationService.processComparisonAnalysisEvaluation(
                memberId,
                evaluation.getAnalysis_id(),
                evaluation.getCosmetic_score(),
                evaluation.getCosmetic_score2()
        );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Data
    static class Evaluation {
        private Long analysis_id;
        private int cosmetic_score;
    }

    @Data
    static class Evaluation2 {
        private Long analysis_id;
        private int cosmetic_score;
        private int cosmetic_score2;
    }
}
