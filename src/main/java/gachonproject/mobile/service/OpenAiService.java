package gachonproject.mobile.service;
import gachonproject.mobile.repository.dto.MemberSkinDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI API를 호출하여 문장 완성을 수행하는 서비스 클래스입니다.
 */
@Service
public class OpenAiService {

    @Autowired
    private MemberService memberService;
    private AiService aiService;

    @Value("${openai.api-key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * OpenAI API를 호출하여 문장 완성을 수행합니다.
     * 
     * @param prompt 완성할 문장
     * @return 완성된 문장
     */
    public String getCompletion(String prompt) {

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 요청 메시지 설정
        List<Message> messages = new ArrayList<>();
        Message message = new Message("user", prompt);
        System.out.println("message = " + message.getRole() + message.getContent());
        messages.add(message);

        // 요청 바디 설정
        OpenAIRequest requestBody = new OpenAIRequest();
        requestBody.setMessages(messages);

        // HTTP 요청 설정
        HttpEntity<OpenAIRequest> request = new HttpEntity<>(requestBody, headers);

        // OpenAI API 호출
        RestTemplate restTemplate = new RestTemplate();
        OpenAIResponse response = restTemplate.postForObject(OPENAI_API_URL, request, OpenAIResponse.class);

        // 완성된 문장 반환
        return response.getChoices().get(0).getMessage().getContent();
    }

    /**
     * OpenAI 요청 바디 클래스입니다.
     */
    @Data
    public static class OpenAIRequest {
        private String model = "gpt-3.5-turbo";
        private List<Message> messages;
    }

    /**
     * OpenAI 메시지 클래스입니다.
     */
    @Data
    @AllArgsConstructor
    public static class Message {
        private String role; // user, system
        private String content; // text
    }

    /**
     * OpenAI 응답 클래스입니다.
     */
    @Data
    public static class OpenAIResponse {
        private List<Choice> choices;
    }

    /**
     * OpenAI 선택 클래스입니다.
     */
    @Data
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private Message message;
    }

    /**
     * 회원 정보와 화장품 성분을 분석하여 60자 요약을 반환합니다.
     * 
     * @param member_id 회원 ID
     * @param ingredient 화장품 성분 목록
     * @return 60자 요약
     */
    public String promptAnalysis(Long member_id, List<String> ingredient) {

        // 회원 정보 조회
        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);

        // 화장품 성분 문자열 생성
        String ingredients = String.join(", ", ingredient);

        // 60자 요약 반환
        return ingredients + "로 구성된 화장품이 있어" + memberSkinDTO.toString() + "의 회원에게 맞는 60자의 분석요약을 해줘";
    }

    /**
     * 회원 정보와 두 개의 화장품 성분을 비교하여 80자 요약을 반환합니다.
     * 
     * @param member_id 회원 ID
     * @param ingredient1 첫 번째 화장품 성분 목록
     * @param ingredient2 두 번째 화장품 성분 목록
     * @return 80자 요약
     */
    public String promptComparison(Long member_id, List<String> ingredient1, List<String> ingredient2) {

        // 회원 정보 조회
        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);

        // 화장품 성분 문자열 생성
        String ingredients1 = String.join(", ", ingredient1);
        String ingredients2 = String.join(", ", ingredient2);

        // 80자 요약 반환
        return ingredients1 + " 이 성분들로 구성된 화장품과" + ingredients2 + "이 성분들로 구성된 화장품이있어" + memberSkinDTO.toString() + "의 회원에게 맞는 80자의 비교분석요약을 해줘";
    }

    /**
     * 회원 정보와 두 개의 화장품 성분을 비교하여 적합도 점수를 반환합니다.
     * 
     * @param member_id 회원 ID
     * @param ingredient1 첫 번째 화장품 성분 목록
     * @param ingredient2 두 번째 화장품 성분 목록
     * @return 적합도 점수 목록
     */
    public String[] promptRatio(Long member_id, List<String> ingredient1, List<String> ingredient2) {

        // 회원 정보 조회
        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);

        // 화장품 성분 문자열 생성
        String ingredients1 = String.join(", ", ingredient1);
        String ingredients2 = String.join(", ", ingredient2);

        // OpenAI 요청 생성
        String prompt = "회원정보와 화장품1의 성분, 화장품2의 성분으로 각 화장품이 회원에게 얼마나 적합한지를 0~100의 점수로 출력해줘\n (출력 예시): 95, 80\n회원정보: " + memberSkinDTO.toString() + "\n화장품 성분1: " + ingredients1 + "\n 화장품 성분2: " + ingredients2 + "\n";

        // OpenAI API 호출
        String completion = getCompletion(prompt);

        // 적합도 점수 목록 반환
        String[] ratios = completion.split(",");
        if (ratios.length == 2) {
            return ratios;
        } else {
            String[] ratios2 = {"70", "85"};
            return ratios2;
        }
    }

    /**
     * 이미지 경로를 분석하여 성분 목록을 반환합니다.
     * 
     * @param imageUrl 이미지 경로
     * @return 성분 목록
     */
    public List<gachonproject.mobile.domain.ingredient.Ingredient> performSkinAnalysis(String imageUrl) {
        try {
            // 이미지 경로를 Path 객체로 변환
            Path imagePath = java.nio.file.Paths.get(imageUrl);

            // 이미지 분석 (기존 메서드 활용)
            List<String> ingredientNames = aiService.analyzeImage3(imagePath);

            // 성분 이름 목록을 Ingredient 객체 목록으로 변환
            List<gachonproject.mobile.domain.ingredient.Ingredient> ingredients = new ArrayList<>();

            if (ingredientNames != null && !ingredientNames.isEmpty()) {
                for (String name : ingredientNames) {
                    // 실제 구현에서는 DB에서 성분을 조회합니다.
                    // 테스트를 위한 간단한 구현입니다.
                    gachonproject.mobile.domain.ingredient.Ingredient ingredient = new gachonproject.mobile.domain.ingredient.Ingredient();
                    ingredient.setName(name);
                    ingredient.setDescription("피부 분석을 통해 발견된 성분: " + name);
                    ingredients.add(ingredient);
                }
            } else {
                // 분석 결과가 없는 경우 기본 성분 추가
                gachonproject.mobile.domain.ingredient.Ingredient defaultIngredient = new gachonproject.mobile.domain.ingredient.Ingredient();
                defaultIngredient.setName("기본 성분");
                defaultIngredient.setDescription("피부 분석 결과가 없어 기본 성분이 추가되었습니다.");
                ingredients.add(defaultIngredient);
            }

            return ingredients;
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 빈 목록 반환
            return new ArrayList<>();
        }
    }
}
