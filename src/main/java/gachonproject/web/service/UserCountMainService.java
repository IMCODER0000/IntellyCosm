package gachonproject.web.service;


import gachonproject.web.domain.UserCount;
import gachonproject.web.repository.UserCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCountMainService {

    @Autowired
    private UserCountRepository userCountRepository;



    public void Connection(UserCount userCount){
        userCountRepository.Connection(userCount);
    }

    public void Out(){
        userCountRepository.Out();
    }



}
