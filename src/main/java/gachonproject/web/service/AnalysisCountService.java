package gachonproject.web.service;


import gachonproject.web.repository.AnalysisActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@Transactional
public class AnalysisCountService {

    @Autowired
    private AnalysisActivityRepository analysisActivityRepository;

    private int totalCount = 0;

    // 요청을 카운팅하는 메서드
    public void countRequest() {
        totalCount++;
    }

    // 매일 자정에 카운트 초기화하는 작업
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetCounts() {
        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 전날의 날짜 계산하기
        LocalDate yesterday = today.minusDays(1);

        analysisActivityRepository.saveAnalysisActivity(totalCount, yesterday);

        totalCount = 0; // 변수를 초기화하여 카운트를 리셋
    }

    // 총 요청 수를 반환하는 메서드
    public int getTotalRequestCount() {
        return totalCount;
    }


    public void upRequestCount() {
        totalCount++;
    }




}
