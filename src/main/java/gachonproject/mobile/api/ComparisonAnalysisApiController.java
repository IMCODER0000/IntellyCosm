package gachonproject.mobile.api;

import gachonproject.mobile.service.ComparisonAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ComparisonAnalysisApiController {

    private final ComparisonAnalysisService comparisonAnalysisService;

    @PostMapping("/api/user/comparison/analysis/{member_id}")
    public ResponseEntity<Long> comparisonAnalysis(
            @PathVariable("member_id") Long memberId,
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        try {
            Long comparisonAnalysisId = comparisonAnalysisService.performComparisonAnalysis(memberId, file1, file2);
            return ResponseEntity.ok(comparisonAnalysisId);
        } catch (Exception e) {
            log.error("비교 분석 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/user/{member_id}/comparisonAnalysis/result/{comparisonAnalysis_id}")
    public ResponseEntity<?> comparisonAnalysisResult(
            @PathVariable("member_id") Long memberId,
            @PathVariable("comparisonAnalysis_id") Long comparisonAnalysisId) {
        try {
            Map<String, Object> result = comparisonAnalysisService.getComparisonAnalysisResult(memberId, comparisonAnalysisId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("비교 분석 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
