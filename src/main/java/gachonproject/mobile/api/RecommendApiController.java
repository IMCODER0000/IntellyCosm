package gachonproject.mobile.api;

import gachonproject.mobile.domain.recommend.RecommendEvaluation;
import gachonproject.mobile.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendApiController {

    private final RecommendService recommendService;

    @PostMapping("/api/user/recommend/evaluation/")
    public ResponseEntity<Void> evaluation(@RequestBody RecommendEvaluation recommendEvaluation) {
        recommendService.saveEvaluation(recommendEvaluation);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
