package gachonproject.mobile.service;

import gachonproject.mobile.api.dto.AnalysisRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final AnalysisService analysisService;
    private final AiService aiService;

    @KafkaListener(topics = "analysis-requests", groupId = "analysis-group")
    public void processAnalysisRequest(AnalysisRequestDTO request) {
        try {
            // AI 분석 처리
            aiService.processAnalysis(request.getImageUrl(), request.getAnalysisId());
        } catch (Exception e) {
            // 에러 처리 로직
            // TODO: 에러 로깅 및 알림
        }
    }
}
