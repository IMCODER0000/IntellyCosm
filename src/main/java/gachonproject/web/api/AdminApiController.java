package gachonproject.web.api;


import gachonproject.All.service.UserCountService;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.repository.IngredientRepository;
import gachonproject.mobile.service.*;
import gachonproject.model.Service.OcrModelService;
import gachonproject.model.Service.RecommendedModelService;
import gachonproject.web.domain.Admin;
import gachonproject.web.domain.AnalysisActivity;
import gachonproject.web.domain.UserActivity;
import gachonproject.web.dto.*;
import gachonproject.web.service.AdminByUserService;
import gachonproject.web.service.AdminService;
import gachonproject.web.service.AnalysisCountService;
import gachonproject.web.service.CrawlingService;
import gachonproject.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class AdminApiController {

    @Autowired
    private final AdminService adminService;
    @Autowired
    private final OcrModelService ocrModelService;
    @Autowired
    private final RecommendedModelService recommendedModelService;
    @Autowired
    private final SessionManager sessionManager;






    // 관리자 로그인
    @PostMapping("/api/admin/login")
    public ResponseEntity login(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response){

        // 관리자 정보가 올바른지 여부를 확인하는 코드를 추가
        Admin findMember = adminService.findAdminByEmail(admin.getEmail());
        if(findMember == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else{
            if(findMember.getPassword().equals(admin.getPassword())){
                Cookie session = sessionManager.createSession(admin, response);
                session.setHttpOnly(true);
                System.out.println(session.getName());
                System.out.println(session.getValue());
                response.addCookie(session);
                SessionDTO session1 = new SessionDTO(session.getValue(), session.getName());
                return new ResponseEntity(session1, HttpStatus.OK);
            }
            else{
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }


    }

    // 로그아웃
    @PostMapping("/api/admin/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        // 세션 만료를 통해 로그아웃을 처리
        sessionManager.expire(request);
        // 로그아웃 후 적절한 응답을 반환
        return new ResponseEntity(HttpStatus.OK);
    }


    // 관리자 회원 가입
    @PostMapping("/api/admin/signup")
    public ResponseEntity signup(@RequestBody Admin admin){

        System.out.println(admin.getCode());

        if (!"gachon".equals(admin.getCode())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


        Long latestModelId_O = ocrModelService.getLatestModelId();
        String latestModelVersion_O = ocrModelService.getLatestModelVersion();

        Long latestModelId_R = recommendedModelService.getLatestModelId();
        String latestModelVersion_R = recommendedModelService.getLatestModelVersion();

        admin.setOcrModel(latestModelId_O);
        admin.setRecommendedModel(latestModelId_R);
        admin.setOcrModelVersion(latestModelVersion_O);
        admin.setRecommendedModelVersion(latestModelVersion_R);


        adminService.signUp(admin);




        return new ResponseEntity(HttpStatus.OK);
    }

    // 비밀번호 찾기(변경)
    @PostMapping("/api/admin/update/password")
    public ResponseEntity updatePassword(@RequestBody Admin_updatePwDTO adminUpdatePwDTO){
        System.out.println(adminUpdatePwDTO.getEmail());
        System.out.println(adminUpdatePwDTO.getNew_password());

        Admin findAdmin = adminService.findAdminByEmail(adminUpdatePwDTO.getEmail());

        System.out.println(findAdmin.getPassword());
        System.out.println(findAdmin.getEmail());




        if(findAdmin == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else{
            if(adminUpdatePwDTO.getPassword().equals(findAdmin.getPassword())){
                findAdmin.setPassword(adminUpdatePwDTO.getNew_password());
                System.out.println(findAdmin.getPassword());
                System.out.println(findAdmin.getEmail());

                adminService.updateAdmin(findAdmin);
            }
            else{
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity(HttpStatus.OK);

    }













}
