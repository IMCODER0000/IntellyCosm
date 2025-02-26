package gachonproject.web.service;


import gachonproject.All.service.UserCountService;
import gachonproject.web.domain.UserActivity;
import gachonproject.web.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

@Service
@Transactional
public class MemberCountService {

    @Autowired
    private UserCountService userCountService;
    @Autowired
    private UserActivityRepository userActivityRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void saveTotalUserCountToDatabase() {
        LocalDate currentDate = LocalDate.now();
        LocalDate date = currentDate.minusDays(1);
        int totalUserCount = userCountService.getTotalUserCount();
        UserActivity userActivity = new UserActivity(totalUserCount, date);
        userActivityRepository.save(userActivity);

        // totalUserCount를 DB에 저장하는 로직을 여기에 구현
        // 예를 들어 userRepository.saveTotalUserCount(totalUserCount);
    }

}
