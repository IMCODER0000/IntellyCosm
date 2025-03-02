package gachonproject.mobile.service;

import gachonproject.mobile.api.dto.AnalysisDTO;
import gachonproject.mobile.api.dto.AnalysisRequestDTO;
import gachonproject.mobile.api.dto.IngredientDTO;
import gachonproject.mobile.api.dto.ScoreDataDTO;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.em.AnalysisStatus;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.IngredientFeature;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.repository.AnalysisRepository;
import gachonproject.mobile.service.AiService.AnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalysisService {

    @Autowired
    private final IngredientService ingredientService;
    @Autowired
    private final MemberService memberService;
    @Autowired
    private final AnalysisRepository analysisRepository;
    @Autowired
    private final AiService ai;
    @Autowired
    private final KafkaProducerService kafkaProducerService;

    // 분석 전체 조회
    public List<Analysis> findAll(){
        return analysisRepository.findAll();
    }

    // 분석 성분 조회
    public List<Ingredient> findByNames(List<String> ingredientNames) {
        return analysisRepository.findByNameIn(ingredientNames);
    }

    public int AnalysisRCount() {
        return analysisRepository.findAllCosmeticRegistration().size();
    }

    public Analysis findById(Long id) {
        return analysisRepository.findById(id);
    }

    public List<AnalysisIngredient> createAnalysisIngredient(List<Ingredient> ingredients, Analysis analysis) {
        return ingredients.stream()
                .map(ingredient -> {
                    AnalysisIngredient analysisIngredient = new AnalysisIngredient(ingredient, analysis);
                    analysisRepository.createAnalysisIngredient(analysisIngredient);
                    return analysisIngredient;
                })
                .collect(Collectors.toList());
    }

    public void createAnalysis(Analysis analysis) {
        analysisRepository.createAnalysis(analysis);
    }

    public void updateAnalysis(Analysis analysis) {
        analysisRepository.updateAnalysis(analysis);
    }

    public void createAnalysisCosmeticRegistration(AnalysisCosmeticRegistration analysisCosmeticRegistration){
        analysisRepository.createAnalysisCosmeticRegistration(analysisCosmeticRegistration);
    }

    public void updateAnalysisCosmeticRegistration(AnalysisCosmeticRegistration analysisCosmeticRegistration){
        analysisRepository.updateAnalysisCosmeticRegistration(analysisCosmeticRegistration);
    }

    public AnalysisCosmeticRegistration findAnalysisCosmeticRegistrationByAnalysisId(Long analysis_id){
        return analysisRepository.findAnalysisCosmeticRegistrationByAnalysisId(analysis_id);
    }

    public AnalysisCosmeticRegistration findAnalysisCosmeticRegistrationById(Long analysis_cosmetic_registration_id){
        return analysisRepository.findAnalysisCosmeticRegistrationById(analysis_cosmetic_registration_id);
    }

    public List<AnalysisCosmeticRegistration> findAnalysisCosmeticRegistrationByName(String name){
        return analysisRepository.findAnalysisCosmeticRegistrationByAnalysisName(name);
    }

    public List<AnalysisCosmeticRegistration> analysisCosmeticRegistrationsFindAll(){
        return analysisRepository.analysisCosmeticRegistrationsFindAll();
    }

    public void deleteAnalysisCosmeticRegistration(String name){
        List<AnalysisCosmeticRegistration> analysisCosmeticRegistrationByAnalysisName = analysisRepository.findAnalysisCosmeticRegistrationByAnalysisName(name);
        for (AnalysisCosmeticRegistration analysisCosmeticRegistration : analysisCosmeticRegistrationByAnalysisName) {
            analysisRepository.deleteAnalysisCosmeticRegistration(analysisCosmeticRegistration);
        }
    }

    public void deleteAnalysisCosmeticRegistrationById(Long id){
        analysisRepository.deleteAnalysisCosmeticRegistrationById(id);
    }

    public int AnalysisCount(){
        int count = 0;
        List<Analysis> allNoRegistration = analysisRepository.findAllNoRegistration();
        for (Analysis analysis : allNoRegistration) {
            count++;
        }

        return count;
    }

    public ScoreDataDTO scoring(Long analysis_id, List<Ingredient> ingredientList) {
        int Score = 0;
        int N_ingredientCount = 0;
        int AllergyCount = 0;
        int DangerCount = 0;
        int P_ingredientCount = 0;
        int P1 = 0;
        int N1 = 0;
        int ingredientCount = ingredientList.size();
        int PreferenceCount = 0;
        int GradeCount = 0;
        int AttentionCount = 0;

        Member member = findAnalysisById(analysis_id).getMember();
        
        for (Ingredient ingredient : ingredientList) {
            List<SkinTypeFeature> skinTypeFeatures = ingredient.getSkinTypeFeatures();
            for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
                if (skinTypeFeature.getSkin_type().equals(member.getSkin_type())) {
                    if (skinTypeFeature.isPositivity_status()) {
                        P_ingredientCount++;
                    } else {
                        N_ingredientCount++;
                    }
                }
            }

            if (ingredient.isAllergy_status()) {
                AllergyCount++;
            }
            if (ingredient.isDanger_status()) {
                DangerCount++;
            }
        }

        int GradePercent = (GradeCount / ingredientCount) * 100;

        if(N_ingredientCount==0 && AllergyCount == 0 && DangerCount == 0 && PreferenceCount != 0 && GradePercent>=80){
            Score = 1;
        }
        else if(N_ingredientCount==0 && AllergyCount == 0 && DangerCount == 0 && PreferenceCount != 0){
            Score = 2;
        }
        else if(N_ingredientCount==0 && AllergyCount == 0 && DangerCount == 0){
            Score = 3;
        }
        else if((N_ingredientCount!=0 || AllergyCount != 0) && DangerCount == 0){
            Score = 4;
        }
        else{
            Score = 5;
        }

        P1 = type_grade(P_ingredientCount);
        N1 = type_grade(N_ingredientCount);
        AttentionCount = type_grade(AttentionCount);

        return new ScoreDataDTO(Score, P1, N1, AllergyCount, DangerCount, AttentionCount);
    }

    private Analysis findAnalysisById(Long analysisId) {
        return analysisRepository.findById(analysisId);
    }

    public int type_grade(int count){

        int grade = 0;

        if(count <= 10){
            grade = 3;
        }
        else if(count <= 6){
            grade = 2;
        }
        else if(count <= 3){
            grade = 1;
        }
        else if(count == 0){
            grade = 0;
        }
        return grade;
    }

    private static final String UPLOAD_DIR = "/home/t24106/v1.0src/Image/User/";
    private static final String User_analysis_image_UPLOAD_DIR = "http://ceprj.gachon.ac.kr:60006/User_analysis_image/";

    /**
     * 이미지 파일을 저장하고 경로를 반환합니다.
     */
    public String saveImageFile(MultipartFile file, Long memberId) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String fileName = memberId + "_" + now.format(formatter) + ".jpg";

        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    /**
     * OCR을 통해 화장품 이름과 성분을 분석합니다.
     * @param filePath 이미지 파일 경로
     * @return 분석 결과 (화장품 이름과 성분 목록)
     */
    private AiService.AnalysisResult processOcrAnalysis(Path filePath) {
        try {
            // 이미지 분석 (OCR)
            List<String> ingredients = ai.analyzeImage3(filePath);
            
            // 화장품 이름은 파일 이름에서 추출 (실제 구현에서는 OCR 결과에서 추출)
            String cosmeticName = filePath.getFileName().toString();
            if (cosmeticName.contains(".")) {
                cosmeticName = cosmeticName.substring(0, cosmeticName.lastIndexOf("."));
            }
            
            return new AiService.AnalysisResult(cosmeticName, ingredients);
        } catch (Exception e) {
            throw new RuntimeException("OCR 분석 중 오류 발생", e);
        }
    }

    /**
     * Analysis 엔티티를 생성하고 저장합니다.
     */
    public Analysis createAndSaveAnalysis(
            String cosmeticName,
            String imagePath,
            Member member,
            List<Ingredient> ingredients,
            Long modelId,
            String description) {
        Analysis analysis = new Analysis();
        analysis.setCosmetic_name(cosmeticName);
        analysis.setImage_path(imagePath);
        analysis.setDate(LocalDate.now());
        analysis.setDescription(description);
        analysis.setModel_id(modelId);
        analysis.setMember(member);
        analysis.setRegistration_status(false);

        createAnalysis(analysis);

        List<AnalysisIngredient> analysisIngredients = createAnalysisIngredient(ingredients, analysis);
        analysis.setAnalysisIngredient(analysisIngredients);

        updateAnalysis(analysis);
        
        return analysis;
    }

    @Getter
    @AllArgsConstructor
    public static class AnalysisResult {
        private final String cosmeticName;
        private final List<String> ingredients;
    }

    public Long performAnalysis(Long memberId, MultipartFile file) throws IOException {
        // 분석 요청 카운트 증가
        // analysisCountService.upRequestCount();

        // 이미지 파일 저장
        String imagePath = saveImageFile(file, memberId);
        Path filePath = Paths.get(imagePath);

        // OCR 분석 수행
        AiService.AnalysisResult ocrResult = processOcrAnalysis(filePath);
            
        // 성분 조회
        List<Ingredient> ingredients = findByNames(ocrResult.getIngredients());
            
        // 회원 조회
        Member member = memberService.findById(memberId);
            
        // 최신 모델 ID 조회
        // Long latestModelId = ocrModelService.getLatestModelId();

        // AI 분석 수행
        String aiDescription = "";
        // if (!memberId.equals(999L)) {
        //     String prompt = openAiService.promptAnalysis(memberId, ocrResult.getIngredients());
        //     aiDescription = openAiService.getCompletion(prompt);
        // }
            
        // Analysis 생성 및 저장
        Analysis analysis = createAndSaveAnalysis(
            ocrResult.getCosmeticName(),
            imagePath,
            member,
            ingredients,
            0L,
            aiDescription
        );

        // 일반 회원인 경우 화장품 등록 데이터도 저장
        // if (!memberId.equals(999L)) {
        //     AnalysisCosmeticRegistration registration = new AnalysisCosmeticRegistration(
        //         ocrResult.getCosmeticName(),
        //         LocalDate.now(),
        //         User_analysis_image_UPLOAD_DIR + filePath.getFileName().toString(),
        //         member,
        //         0,
        //         analysis.getId()
        //     );
        //     createAnalysisCosmeticRegistration(registration);
        // }

        return analysis.getId();
    }

    public AnalysisDTO getAnalysisResult(Long analysisId, Long memberId) {
        Analysis analysis = findById(analysisId);
        List<AnalysisIngredient> analysisIngredients = analysis.getAnalysisIngredient();
        
        List<Ingredient> ingredients = analysisIngredients.stream()
                .map(AnalysisIngredient::getIngredient)
                .collect(Collectors.toList());

        List<IngredientDTO> ingredientDTOs = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            IngredientDTO dto = new IngredientDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getName()
            );
            ingredientDTOs.add(dto);
        }

        ScoreDataDTO scoring = scoring(analysisId, ingredients);

        return new AnalysisDTO(
            analysisId,
            analysis.getDescription(),
            scoring.getScore() - 1,
            scoring.getType_posit(),
            scoring.getType_nega(),
            scoring.getAttentionCount(),
            scoring.getAllergyCount(),
            scoring.getDangerCount(),
            ingredientDTOs
        );
    }

    // 분석 요청을 Kafka로 전송
    public void requestAnalysis(Long memberId, String imageUrl, String requestType) {
        // 분석 엔티티 생성
        Analysis analysis = new Analysis();
        analysis.setStatus(AnalysisStatus.PENDING);
        createAnalysis(analysis);

        // Kafka로 분석 요청 전송
        AnalysisRequestDTO request = new AnalysisRequestDTO(memberId, imageUrl, requestType, analysis.getId());
        kafkaProducerService.sendAnalysisRequest(request);
    }
    
    /**
     * 동기 방식으로 분석을 수행합니다 (Kafka를 사용하지 않음)
     * 성능 비교 테스트를 위한 메서드입니다.
     */
    public Analysis processSynchronously(Long memberId, String imageUrl, String requestType) {
        long startTime = System.currentTimeMillis();
        
        // 분석 엔티티 생성
        Analysis analysis = new Analysis();
        analysis.setStatus(AnalysisStatus.PROCESSING);
        createAnalysis(analysis);
        
        try {
            // 멤버 조회
            Member member = memberService.findById(memberId);
            if (member == null) {
                throw new RuntimeException("멤버를 찾을 수 없습니다: " + memberId);
            }
            
            // 분석 유형에 따른 처리
            if ("SKIN_ANALYSIS".equals(requestType)) {
                // 피부 분석 로직 수행
                List<Ingredient> ingredients = ai.performSkinAnalysis(imageUrl);
                analysis.setDescription("피부 분석 완료");
                createAnalysisIngredient(ingredients, analysis);
            } else if ("COSMETIC_ANALYSIS".equals(requestType)) {
                // 화장품 분석 로직 수행
                Path filePath = Paths.get(imageUrl);
                AiService.AnalysisResult ocrResult = processOcrAnalysis(filePath);
                
                // 성분 분석
                List<Ingredient> ingredients = findByNames(ocrResult.getIngredients());
                analysis.setDescription("화장품 분석 완료: " + ocrResult.getCosmeticName());
                createAnalysisIngredient(ingredients, analysis);
            } else {
                throw new IllegalArgumentException("지원하지 않는 분석 유형: " + requestType);
            }
            
            // 분석 완료 상태로 업데이트
            analysis.setStatus(AnalysisStatus.COMPLETED);
            analysis.setMember(member);
            analysis.setAnalysisDate(LocalDateTime.now());
            updateAnalysis(analysis);
            
            long endTime = System.currentTimeMillis();
            System.out.println("동기 처리 소요 시간: " + (endTime - startTime) + "ms");
            
            return analysis;
        } catch (Exception e) {
            // 오류 발생 시 상태 업데이트
            analysis.setStatus(AnalysisStatus.FAILED);
            analysis.setDescription("분석 중 오류 발생: " + e.getMessage());
            updateAnalysis(analysis);
            throw new RuntimeException("분석 처리 중 오류 발생", e);
        }
    }
}
