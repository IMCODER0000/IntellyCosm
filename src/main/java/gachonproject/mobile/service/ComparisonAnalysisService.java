package gachonproject.mobile.service;

import gachonproject.mobile.api.dto.*;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.comparison.ComparisonAnalysis;
import gachonproject.mobile.domain.comparison.ComparisonAnalysisMaping;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.repository.ComparisonAnalysisRepository;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.model.Service.OcrModelService;
import gachonproject.web.service.AnalysisCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComparisonAnalysisService {

    private static final String UPLOAD_DIR = "/home/t24106/v1.0src/Image/User/";

    @Autowired
    private ComparisonAnalysisRepository comparisonAnalysisRepository;
    @Autowired
    private AnalysisCountService analysisCountService;
    @Autowired
    private AiService aiService;
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private OcrModelService ocrModelService;
    @Autowired
    private MemberRepository memberRepository;

    public Long performComparisonAnalysis(Long memberId, MultipartFile file1, MultipartFile file2) throws IOException {
        analysisCountService.countRequest();

        // 현재 시간을 기반으로 파일 이름 생성
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String fileName1 = "C_1" + memberId + "_" + now.format(formatter) + ".png";
        String fileName2 = "C_2" + memberId + "_" + now.format(formatter) + ".png";

        // 파일 저장 경로 생성 및 저장
        Path filePath1 = Paths.get(UPLOAD_DIR + fileName1);
        Path filePath2 = Paths.get(UPLOAD_DIR + fileName2);
        Files.copy(file1.getInputStream(), filePath1);
        Files.copy(file2.getInputStream(), filePath2);

        // 이미지 분석
        List<String> result1 = aiService.analyzeImage2(filePath1);
        String cosmeticName1 = result1.get(0).split(",")[0];
        result1.remove(0);

        List<String> result2 = aiService.analyzeImage2(filePath2);
        String cosmeticName2 = result2.get(0).split(",")[0];
        result2.remove(0);

        // 성분 분석
        List<Ingredient> analysisIngredients1 = analysisService.findByNames(result1);
        List<Ingredient> analysisIngredients2 = analysisService.findByNames(result2);

        // AI 분석
        String comparisonDescription = openAiService.getCompletion(
            openAiService.promptComparison(memberId, result1, result2)
        );
        String description1 = openAiService.getCompletion(
            openAiService.promptAnalysis(memberId, result1)
        );
        String description2 = openAiService.getCompletion(
            openAiService.promptAnalysis(memberId, result2)
        );

        // 분석 객체 생성 및 저장
        Long latestModelId = ocrModelService.getLatestModelId();
        Member member = memberRepository.findByid(memberId);

        Analysis analysis1 = createAnalysis(cosmeticName1, filePath1.toString(), currentDate, 
            description1, latestModelId, member, analysisIngredients1);
        Analysis analysis2 = createAnalysis(cosmeticName2, filePath2.toString(), currentDate, 
            description2, latestModelId, member, analysisIngredients2);

        // 비교 분석 생성
        ComparisonAnalysis comparisonAnalysis = new ComparisonAnalysis();
        comparisonAnalysis.setDate(currentDate);
        comparisonAnalysis.setMember(member);
        comparisonAnalysis.setModel_id(latestModelId);
        comparisonAnalysis.setTotal_ai_description(comparisonDescription);
        comparisonAnalysis.setRatio0(78);  // TODO: 실제 계산된 값으로 대체
        comparisonAnalysis.setRatio1(83);  // TODO: 실제 계산된 값으로 대체
        createComparisonAnalysis(comparisonAnalysis);

        // 비교 분석 매핑 생성
        List<Analysis> analyses = List.of(analysis1, analysis2);
        for (Analysis analysis : analyses) {
            ComparisonAnalysisMaping mapping = new ComparisonAnalysisMaping();
            mapping.setAnalysis(analysis);
            mapping.setComparisonAnalysis(comparisonAnalysis);
            createComparisonAnalysisMaping(mapping);
        }

        // 화장품 등록 데이터 생성
        String imgPath1 = filePath1.getFileName().toString();
        String imgPath2 = filePath2.getFileName().toString();

        String ip1 = "http://ceprj.gachon.ac.kr:60006/User_analysis_image/" + imgPath1;
        String ip2 = "http://ceprj.gachon.ac.kr:60006/User_analysis_image/" + imgPath2;

        AnalysisCosmeticRegistration registration1 = new AnalysisCosmeticRegistration(
            analysis1.getCosmetic_name(), analysis1.getDate(), ip1, analysis1.getMember(), 0, analysis1.getId()
        );
        AnalysisCosmeticRegistration registration2 = new AnalysisCosmeticRegistration(
            analysis2.getCosmetic_name(), analysis2.getDate(), ip2, analysis2.getMember(), 0, analysis2.getId()
        );

        analysisService.createAnalysisCosmeticRegistration(registration1);
        analysisService.createAnalysisCosmeticRegistration(registration2);

        return comparisonAnalysis.getId();
    }

    public Map<String, Object> getComparisonAnalysisResult(Long memberId, Long comparisonAnalysisId) {
        ComparisonAnalysis comparisonAnalysis = findAnalysisByIds(comparisonAnalysisId);
        if (comparisonAnalysis == null) {
            throw new RuntimeException("Comparison analysis not found");
        }

        List<ComparisonAnalysisMaping> mappings = findComparisonAnalysisMaping(comparisonAnalysisId);
        List<Analysis> analyses = mappings.stream()
                .map(ComparisonAnalysisMaping::getAnalysis)
                .collect(Collectors.toList());

        List<AnalysisDTO> analysisDTOs = new ArrayList<>();
        for (Analysis analysis : analyses) {
            List<Ingredient> ingredients = analysis.getAnalysisIngredient().stream()
                    .map(AnalysisIngredient::getIngredient)
                    .collect(Collectors.toList());

            ScoreDataDTO scoring = analysisService.scoring(analysis.getId(), ingredients);

            List<IngredientDTO> ingredientDTOs = ingredients.stream()
                    .map(i -> new IngredientDTO(i.getId(), i.getName(), i.getName()))
                    .collect(Collectors.toList());

            AnalysisDTO analysisDTO = new AnalysisDTO(
                analysis.getId(),
                analysis.getDescription(),
                scoring.getScore(),
                scoring.getType_posit(),
                scoring.getType_nega(),
                scoring.getAttentionCount(),
                scoring.getAllergyCount(),
                scoring.getDangerCount(),
                ingredientDTOs
            );
            analysisDTOs.add(analysisDTO);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("analysis", analysisDTOs);
        response.put("AI_total_description", comparisonAnalysis.getTotal_ai_description());
        return response;
    }

    private Analysis createAnalysis(String cosmeticName, String imagePath, LocalDate date, 
            String description, Long modelId, Member member, List<Ingredient> ingredients) {
        Analysis analysis = new Analysis();
        analysis.setCosmetic_name(cosmeticName);
        analysis.setImage_path(imagePath);
        analysis.setDate(date);
        analysis.setDescription(description);
        analysis.setModel_id(modelId);
        analysis.setMember(member);

        analysisService.createAnalysis(analysis);

        List<AnalysisIngredient> analysisIngredients = analysisService.createAnalysisIngredient(ingredients, analysis);
        analysis.setAnalysisIngredient(analysisIngredients);
        analysisService.updateAnalysis(analysis);

        return analysis;
    }

    public void createComparisonAnalysis(ComparisonAnalysis comparisonAnalysis) {
        comparisonAnalysisRepository.createComparisonAnalysis(comparisonAnalysis);
    }

    public ComparisonAnalysis findAnalysisByIds(Long comparisonAnalysisId) {
        return comparisonAnalysisRepository.findAnalysisByIds(comparisonAnalysisId);
    }

    public void createComparisonAnalysisMaping(ComparisonAnalysisMaping comparisonAnalysisMaping) {
        comparisonAnalysisRepository.createComparisonAnalysisMaping(comparisonAnalysisMaping);
    }

    public List<ComparisonAnalysisMaping> findComparisonAnalysisMaping(Long comparisonAnalysisId) {
        return comparisonAnalysisRepository.findComparisonAnalysisMaping(comparisonAnalysisId);
    }
}
