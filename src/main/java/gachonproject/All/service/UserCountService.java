package gachonproject.All.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class UserCountService {

    private int userCount = 0;
    private int totalUserCount = 0;

    public synchronized int incrementAndGetUserCount() {
        userCount++;
        totalUserCount++;
        return userCount;
    }

    public synchronized int decrementAndGetUserCount() {
        userCount--;
        return userCount;
    }


}