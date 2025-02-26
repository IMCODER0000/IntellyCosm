package gachonproject.mobile.service;

import gachonproject.mobile.api.dto.RecommendDTO;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.comparison.ComparisonAnalysisMaping;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EvaluationService {

    private final MemberService memberService;
    private final AnalysisService analysisService;
    private final CosmeticService cosmeticService;
    private final ComparisonAnalysisService comparisonAnalysisService;
    private final AiService aiService;

    public RecommendDTO processAnalysisEvaluation(Long memberId, Long analysisId, int cosmeticScore) {
        // 화장품 등록 데이터 업데이트
        AnalysisCosmeticRegistration registration = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysisId);
        registration.setScore(cosmeticScore);
        analysisService.updateAnalysisCosmeticRegistration(registration);

        // 분석 데이터 업데이트
        Member member = memberService.findMemberById(memberId);
        Analysis analysis = analysisService.findById(analysisId);
        analysis.setEvaluation(cosmeticScore);
        analysisService.updateAnalysis(analysis);

        // AI 추천 처리
        List<Integer> recommend = aiService.recommend(analysisId, memberId);
        Long cosmeticId = Long.valueOf(recommend.get(0));
        int score = recommend.get(1);

        // 화장품 정보 조회
        Cosmetic cosmetic = cosmeticService.findCosmeticById(cosmeticId);
        
        // 회원 선호 성분 정보 조회
        List<Ingredient> preferredIngredients = member.getPreferedIngredient().stream()
                .map(PreferedIngredient::getIngredient)
                .toList();

        // 화장품 성분 분석
        List<String> positiveSkinTypeFeatures = new ArrayList<>();
        List<String> preferenceIngredients = new ArrayList<>();
        
        for (CosmeticIngredient cosmeticIngredient : cosmetic.getCosmeticIngredients()) {
            // 피부 타입 효과 분석
            if (cosmeticIngredient.getIngredient().getSkinTypeFeatures() != null) {
                for (SkinTypeFeature feature : cosmeticIngredient.getIngredient().getSkinTypeFeatures()) {
                    if (feature.getSkin_type() == member.getSkin_type() && feature.isPositivity_status()) {
                        positiveSkinTypeFeatures.add(feature.getSkinDescription());
                    }
                }
            }
            
            // 선호 성분 체크
            if (preferredIngredients.contains(cosmeticIngredient.getIngredient())) {
                preferenceIngredients.add(cosmeticIngredient.getIngredient().getName());
            }
        }

        // DTO 생성 및 반환
        return new RecommendDTO(
            cosmetic.getId(),
            score,
            preferenceIngredients,
            cosmetic.getName(),
            member.getSkin_type(),
            positiveSkinTypeFeatures,
            cosmetic.getImage_path()
        );
    }

    public RecommendDTO processComparisonAnalysisEvaluation(Long memberId, Long comparisonId, 
            int cosmeticScore1, int cosmeticScore2) {
        // 비교 분석 매핑 정보 조회
        List<ComparisonAnalysisMaping> mappings = comparisonAnalysisService.findComparisonAnalysisMaping(comparisonId);
        Long analysisId1 = mappings.get(0).getAnalysis().getId();
        Long analysisId2 = mappings.get(1).getAnalysis().getId();

        // 화장품 등록 데이터 업데이트
        AnalysisCosmeticRegistration registration1 = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysisId1);
        AnalysisCosmeticRegistration registration2 = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysisId2);
        registration1.setScore(cosmeticScore1);
        registration2.setScore(cosmeticScore2);
        analysisService.updateAnalysisCosmeticRegistration(registration1);
        analysisService.updateAnalysisCosmeticRegistration(registration2);

        // 분석 데이터 업데이트
        Member member = memberService.findMemberById(memberId);
        Analysis analysis1 = analysisService.findById(analysisId1);
        Analysis analysis2 = analysisService.findById(analysisId2);
        analysis1.setEvaluation(cosmeticScore1);
        analysis2.setEvaluation(cosmeticScore2);
        analysisService.updateAnalysis(analysis1);
        analysisService.updateAnalysis(analysis2);

        // 임시로 첫 번째 화장품 정보 반환 (실제로는 AI 모델 기반 추천 로직 필요)
        Cosmetic cosmetic = cosmeticService.getCosmeticById(1L);
        
        // 회원 선호 성분 정보 조회
        List<Ingredient> preferredIngredients = member.getPreferedIngredient().stream()
                .map(PreferedIngredient::getIngredient)
                .toList();

        // 화장품 성분 분석
        List<String> positiveSkinTypeFeatures = new ArrayList<>();
        List<String> preferenceIngredients = new ArrayList<>();
        
        for (CosmeticIngredient cosmeticIngredient : cosmetic.getCosmeticIngredients()) {
            // 피부 타입 효과 분석
            if (cosmeticIngredient.getIngredient().getSkinTypeFeatures() != null) {
                for (SkinTypeFeature feature : cosmeticIngredient.getIngredient().getSkinTypeFeatures()) {
                    if (feature.getSkin_type() == member.getSkin_type() && feature.isPositivity_status()) {
                        positiveSkinTypeFeatures.add(feature.getSkinDescription());
                    }
                }
            }
            
            // 선호 성분 체크
            if (preferredIngredients.contains(cosmeticIngredient.getIngredient())) {
                preferenceIngredients.add(cosmeticIngredient.getIngredient().getName());
            }
        }

        // DTO 생성 및 반환
        return new RecommendDTO(
            cosmetic.getId(),
            85, // 임시 점수
            preferenceIngredients,
            cosmetic.getName(),
            member.getSkin_type(),
            positiveSkinTypeFeatures,
            cosmetic.getImage_path()
        );
    }
}
