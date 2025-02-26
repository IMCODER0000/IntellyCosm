package gachonproject.mobile.api;


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
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.service.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EvaluationApiController {

    @Autowired
    private final MemberService memberService;
    @Autowired
    private final AnalysisService analysisService;
    @Autowired
    private final CosmeticService cosmeticService;
    @Autowired
    private final ComparisonAnalysisService comparisonAnalysisService;
    @Autowired
    private final AiService aiService;





    @GetMapping("/vvv")
    public ResponseEntity<Object> getVvv() {

        Long analysis_id = 490L;
        Long member_id = 352L;

        System.out.println("파이썬 시작 전");
        List<Integer> recommend = aiService.recommend(analysis_id, member_id);
        for (Integer i : recommend) {
            System.out.println(i);
        }
        System.out.println("파이썬 시작 후");



        if (recommend != null && !recommend.isEmpty()) {
            // 리스트가 비어있지 않은 경우에만 접근
            for (int i = 0; i < recommend.size(); i++) {
                System.out.println(recommend.get(i));
            }
        } else {
            System.out.println("추천 리스트가 비어 있습니다.");
        }

        return ResponseEntity.ok("vvv");
    }



    @PostMapping("/api/user/analysis/evaluation/{member_id}")
    public ResponseEntity<RecommendDTO> analysisEvaluation(@PathVariable("member_id") Long member_id,
                                             @RequestBody Evaluation evaluation ){



        Long analysis_id = evaluation.analysis_id;
        int Analysis_score = evaluation.cosmetic_score;

        //화장품 등록 데이터 업데이트
        AnalysisCosmeticRegistration findAnalysisCosmeticRegistrationByAnalysisId = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysis_id);
        findAnalysisCosmeticRegistrationByAnalysisId.setScore(Analysis_score);
        analysisService.updateAnalysisCosmeticRegistration(findAnalysisCosmeticRegistrationByAnalysisId);

        Member findMember = memberService.findMemberById(member_id);
        Analysis findAnalysis = analysisService.findById(analysis_id);
        findAnalysis.setEvaluation(Analysis_score);
        analysisService.updateAnalysis(findAnalysis);

        //Cosmetic_score를 사용한 추천모델

        List<Integer> recommend = aiService.recommend(analysis_id, member_id);
        Long cosmetir_id = Long.valueOf(recommend.get(0));
        int socre = recommend.get(1);

        System.out.println("00000000000000000000000000000");
        System.out.println(cosmetir_id);
        System.out.println(socre);
        


        Cosmetic cosmetic = cosmeticService.findCosmeticById(cosmetir_id);


        String image = cosmetic.getImage_path();
        String cosmeticName = cosmetic.getName();
        Skintype skinType = findMember.getSkin_type();
        List<PreferedIngredient> preferedIngredient = findMember.getPreferedIngredient();
        List<Ingredient> ingredients = new ArrayList<>();
        for (PreferedIngredient ingredient : preferedIngredient) {
            ingredients.add(ingredient.getIngredient());
        }
        List<CosmeticIngredient> cosmeticIngredients = cosmetic.getCosmeticIngredients();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            System.out.println(cosmeticIngredient);
        }

        // 화장품 성분에서 긍적적인 피부타입 효과 저장, 선호 성분 리스트 저장

        List<String> positive_skin_type_features = new ArrayList<>();
        List<String> prefference_ingredient = new ArrayList<String>();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            if(cosmeticIngredient.getIngredient().getSkinTypeFeatures() != null) {
                List<SkinTypeFeature> skinTypeFeatures = cosmeticIngredient.getIngredient().getSkinTypeFeatures();
                for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
                    if (skinTypeFeature.getSkin_type() == findMember.getSkin_type()) {
                        if (skinTypeFeature.isPositivity_status()) {
                            positive_skin_type_features.add(skinTypeFeature.getSkinDescription());
                        }
                    }
                }
            }
            if(ingredients.contains(cosmeticIngredient.getIngredient())){
                prefference_ingredient.add(cosmeticIngredient.getIngredient().getName());
            }

        }

        RecommendDTO recommendDTO = new RecommendDTO(cosmetic.getId(),socre, prefference_ingredient, cosmeticName, skinType, positive_skin_type_features, image);


        return new ResponseEntity(recommendDTO,HttpStatus.OK);
    }



    @PostMapping("/api/user/conparison/evaluation/{member_id}")
    public ResponseEntity<RecommendDTO> comparisonAnalysisEvaluation(@PathVariable("member_id") Long member_id,
                                                       @RequestBody Evaluation2 evaluation2){


        Long comparison_id = evaluation2.analysis_id;
        List<ComparisonAnalysisMaping> comparisonAnalysisMaping = comparisonAnalysisService.findComparisonAnalysisMaping(comparison_id);

        System.out.println(comparisonAnalysisMaping);

        Long analysis_id1 = comparisonAnalysisMaping.get(0).getAnalysis().getId();
        Long analysis_id2 = comparisonAnalysisMaping.get(1).getAnalysis().getId();

        System.out.println(analysis_id1);
        System.out.println(analysis_id2);

        int cosmetic_score = evaluation2.cosmetic_score;
        int cosmetic_score2 = evaluation2.cosmetic_score2;

        AnalysisCosmeticRegistration analysisCosmeticRegistration1 = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysis_id1);
        AnalysisCosmeticRegistration analysisCosmeticRegistration2 = analysisService.findAnalysisCosmeticRegistrationByAnalysisId(analysis_id2);

        analysisCosmeticRegistration1.setScore(cosmetic_score);
        analysisCosmeticRegistration2.setScore(cosmetic_score2);

        analysisService.updateAnalysisCosmeticRegistration(analysisCosmeticRegistration1);
        analysisService.updateAnalysisCosmeticRegistration(analysisCosmeticRegistration2);

        Member findMember = memberService.findMemberById(member_id);
        Analysis findAnalysis1 = analysisService.findById(analysis_id1);
        Analysis findAnalysis2 = analysisService.findById(analysis_id2);
        findAnalysis1.setEvaluation(cosmetic_score);
        findAnalysis2.setEvaluation(cosmetic_score2);
        analysisService.updateAnalysis(findAnalysis1);
        analysisService.updateAnalysis(findAnalysis2);


        Cosmetic cosmetic = cosmeticService.getCosmeticById(1L);

        String image = cosmetic.getImage_path();
        String cosmeticName = cosmetic.getName();
        Skintype skinType = findMember.getSkin_type();
        List<PreferedIngredient> preferedIngredient = findMember.getPreferedIngredient();
        List<Ingredient> ingredients = new ArrayList<>();
        for (PreferedIngredient ingredient : preferedIngredient) {
            ingredients.add(ingredient.getIngredient());
        }
        List<CosmeticIngredient> cosmeticIngredients = cosmetic.getCosmeticIngredients();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            System.out.println(cosmeticIngredient);
        }

        // 화장품 성분에서 긍적적인 피부타입 효과 저장, 선호 성분 리스트 저장

        List<String> positive_skin_type_features = new ArrayList<>();
        List<String> prefference_ingredient = new ArrayList<String>();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            List<SkinTypeFeature> skinTypeFeatures = cosmeticIngredient.getIngredient().getSkinTypeFeatures();
            for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
                if(skinTypeFeature.getSkin_type() == findMember.getSkin_type()){
                    if(skinTypeFeature.isPositivity_status()){
                        positive_skin_type_features.add(skinTypeFeature.getSkinDescription());
                    }
                }
            }
            if(ingredients.contains(cosmeticIngredient.getIngredient())){
                prefference_ingredient.add(cosmeticIngredient.getIngredient().getName());
            }

        }

        RecommendDTO recommendDTO = new RecommendDTO(cosmetic.getId(),82, prefference_ingredient, cosmeticName, skinType, positive_skin_type_features, image);



        return new ResponseEntity(recommendDTO ,HttpStatus.OK);
    }

    @Data
    public static class Evaluation{
        private Long analysis_id;
        private int analysis_score;
        private int cosmetic_score;

    }
    @Data
    public static class Evaluation2{
        private Long analysis_id;
        private int analysis_score;
        private int cosmetic_score;
        private int cosmetic_score2;

    }


    //추천
    @GetMapping("/api/user/ai/recommend/{member_id}")
    public ResponseEntity<RecommendDTO> recommend(@PathVariable("member_id") Long member_id){

        Member findMember = memberService.findMemberById(member_id);

        Cosmetic cosmetic = cosmeticService.getCosmeticById(41L);

        String image = cosmetic.getImage_path();
        String cosmeticName = cosmetic.getName();
        Skintype skinType = findMember.getSkin_type();
        List<PreferedIngredient> preferedIngredient = findMember.getPreferedIngredient();
        List<Ingredient> ingredients = new ArrayList<>();
        for (PreferedIngredient ingredient : preferedIngredient) {
            ingredients.add(ingredient.getIngredient());
        }
        List<CosmeticIngredient> cosmeticIngredients = cosmetic.getCosmeticIngredients();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            System.out.println(cosmeticIngredient);
        }

        // 화장품 성분에서 긍적적인 피부타입 효과 저장, 선호 성분 리스트 저장

        List<String> positive_skin_type_features = new ArrayList<>();
        List<String> prefference_ingredient = new ArrayList<String>();
        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
            List<SkinTypeFeature> skinTypeFeatures = cosmeticIngredient.getIngredient().getSkinTypeFeatures();
            for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
                if(skinTypeFeature.getSkin_type() == findMember.getSkin_type()){
                    if(skinTypeFeature.isPositivity_status()){
                        positive_skin_type_features.add(skinTypeFeature.getSkinDescription());
                    }
                }
            }
            if(ingredients.contains(cosmeticIngredient.getIngredient())){
                prefference_ingredient.add(cosmeticIngredient.getIngredient().getName());
            }

        }

        RecommendDTO recommendDTO = new RecommendDTO(cosmetic.getId(),82, prefference_ingredient, cosmeticName, skinType, positive_skin_type_features, image);

        return new ResponseEntity(recommendDTO,HttpStatus.OK);

    }




}
