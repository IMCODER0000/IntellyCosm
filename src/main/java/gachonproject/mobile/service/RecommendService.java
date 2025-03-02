package gachonproject.mobile.service;


import gachonproject.mobile.domain.recommend.RecommendEvaluation;
import gachonproject.mobile.repository.RecommendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RecommendService {

    @Autowired
    private RecommendRepository recommendRepository;


    public void createRecommendEvaluation(RecommendEvaluation recommendEvaluation) {
        recommendRepository.createRecommendEvaluation(recommendEvaluation);
    }


    public List<RecommendEvaluation> getAllRecommendEvaluation(LocalDate day) {
        return recommendRepository.getAllRecommendEvaluation(day);
    }

    public void saveEvaluation(RecommendEvaluation recommendEvaluation) {
        recommendRepository.saveEvaluation(recommendEvaluation);
    }


    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void executeTask() {

        LocalDate day = LocalDate.now().minusDays(1);
        List<RecommendEvaluation> allRecommendEvaluation = recommendRepository.getAllRecommendEvaluation(day);

    }


}
