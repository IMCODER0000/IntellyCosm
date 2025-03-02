package gachonproject.mobile.service;

import gachonproject.mobile.api.dto.AnalysisRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "analysis-requests";

    public void sendAnalysisRequest(AnalysisRequestDTO request) {
        kafkaTemplate.send(TOPIC, request);
    }
}
