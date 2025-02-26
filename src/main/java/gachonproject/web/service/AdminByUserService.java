package gachonproject.web.service;


import gachonproject.web.domain.AnalysisActivity;
import gachonproject.web.domain.UserActivity;
import gachonproject.web.repository.AnalysisActivityRepository;
import gachonproject.web.repository.UserActivityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class AdminByUserService {


    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private AnalysisActivityRepository analysisActivityRepository;


    public List<UserActivity> findUserActivitiesLast30Days() {
        return userActivityRepository.findUserActivitiesLast30Days();
    }

    public List<AnalysisActivity> findAnalysisActivitiesLast30Days(){
        return analysisActivityRepository.findAnalysisActivitiesLast30Days();
    }





}
