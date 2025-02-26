package gachonproject.mobile.api;

import gachonproject.mobile.api.dto.AnalysisDTO;
import gachonproject.mobile.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnalysisApiController {

    private final AnalysisService analysisService;

    @PostMapping("/api/user/analysis/{member_id}")
    public ResponseEntity<Long> analysis(
            @PathVariable("member_id") Long memberId,
            @RequestParam("file") MultipartFile file) {
        try {
            Long analysisId = analysisService.performAnalysis(memberId, file);
            return ResponseEntity.ok(analysisId);
        } catch (Exception e) {
            log.error("분석 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/user/{member_id}/analysis/result/{analysis_id}")
    public ResponseEntity<AnalysisDTO> analysisResult(
            @PathVariable("member_id") Long memberId,
            @PathVariable("analysis_id") Long analysisId) {
        try {
            AnalysisDTO result = analysisService.getAnalysisResult(analysisId, memberId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("분석 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
