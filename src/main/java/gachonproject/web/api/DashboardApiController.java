package gachonproject.web.api;


import gachonproject.All.service.UserCountService;
import gachonproject.mobile.service.AnalysisService;
import gachonproject.mobile.service.QnaService;
import gachonproject.model.Service.OcrModelService;
import gachonproject.model.Service.RecommendedModelService;
import gachonproject.web.domain.Admin;
import gachonproject.web.domain.AnalysisActivity;
import gachonproject.web.domain.UserActivity;
import gachonproject.web.domain.UserCount;
import gachonproject.web.dto.DashboardDTO;
import gachonproject.web.dto.ModelRateDTO;
import gachonproject.web.service.AdminByUserService;
import gachonproject.web.service.AdminService;
import gachonproject.web.service.AnalysisCountService;
import gachonproject.web.service.UserCountMainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DashboardApiController {

    @Autowired
    private final UserCountService userCountService;
    @Autowired
    private final AnalysisCountService analysisCountService;
    @Autowired
    private final QnaService qnaService;
    @Autowired
    private final AnalysisService analysisService;
    @Autowired
    private final AdminByUserService adminByUserService;
    @Autowired
    private final AdminService adminService;
    @Autowired
    private final OcrModelService ocrModelService;
    @Autowired
    private final RecommendedModelService recommendedModelService;
    @Autowired
    private final UserCountMainService userCountMainService;




    // 대쉬보드
    @GetMapping("/api/admin/dashboard/{admin_id}")
    public ResponseEntity<DashboardDTO> dashboard(@PathVariable("admin_id") Long admin_id) {


        //대쉬보드 맨 위
        int userCount = userCountService.getUserCount();
        int totalUserCount = userCountService.getTotalUserCount();
        //분석 이용자 수
        int totalRequestCount = analysisCountService.getTotalRequestCount();
        int noAnswerQnaCount = qnaService.noAnswerQnaCount();
        //화장품 분석 가능 수
        int analysisCount = analysisService.AnalysisRCount();

        List<UserActivity> userActivitiesLast30Days = adminByUserService.findUserActivitiesLast30Days();

        List<AnalysisActivity> analysisActivitiesLast30Days = adminByUserService.findAnalysisActivitiesLast30Days();

        Admin findAdmin = adminService.findById(admin_id);
        ModelRateDTO ocrModelRate = ocrModelService.getModelRate(findAdmin.getOcrModel());
        ModelRateDTO RecommendedModelRate = recommendedModelService.getModelRate(findAdmin.getRecommendedModel());

        DashboardDTO dashboard = new DashboardDTO(analysisActivitiesLast30Days, ocrModelRate, analysisCount, userCount, noAnswerQnaCount, totalRequestCount,
                totalUserCount, RecommendedModelRate, userActivitiesLast30Days);


        return new ResponseEntity<>(dashboard, HttpStatus.OK);

    }

    @PostMapping("/api/admin/userCount/{stauts}")
    public ResponseEntity userCount(@PathVariable Long stauts){

        //1이 접속, 0이 아웃

        if(stauts == 1){
            UserCount userCount = new UserCount(0L);
            userCountMainService.Connection(userCount);
        }
        else if(stauts == 0){
            userCountMainService.Out();
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }



}
