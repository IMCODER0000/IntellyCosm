package gachonproject.mobile.service;
import gachonproject.mobile.api.OpenAIController;
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
import java.util.ArrayList;
import java.util.List;


@Service
public class OpenAiService {

    @Autowired
    private MemberService memberService;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getCompletion(String prompt) {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        List<OpenAIController.Message> messages = new ArrayList<>();
        OpenAIController.Message message = new OpenAIController.Message("user", prompt);
        System.out.println("message = " + message.getRole() + message.getContent());
        messages.add(message);

        OpenAIController.OpenAIRequest requestBody = new OpenAIController.OpenAIRequest();
        requestBody.setMessages(messages);
        HttpEntity<OpenAIController.OpenAIRequest> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        OpenAIController.OpenAIResponse response = restTemplate.postForObject(OPENAI_API_URL, request, OpenAIController.OpenAIResponse.class);

        return response.getChoices().get(0).getMessage().getContent();


    }

    @Data
    public static class OpenAIRequest {
        private String model = "gpt-3.5-turbo";
        private List<OpenAIController.Message> messages;
    }

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role; // user, system
        private String content; // text
        /***
         *
         * 회원 정보와 화장품 성분을 제공받아 회원에게 적합한 화장품인지 판단해주는 시스템
         * 회원 정보:
         *   피부타입:
         *
         *  성분리스트: [ㅇㄹㄴㄹ ㄹㄴㅇㄹ ㅇㄴㄹ ]
         *
         *  결과:
         */
    }

    @Data
    public static class OpenAIResponse {
        private List<OpenAIController.Choice> choices;
    }

    @Data
    @AllArgsConstructor
    private static class Choice {
        private int index;
        private OpenAIController.Message message;
    }



    public String promptAnalysis(Long member_id, List<String> ingredient) {


        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);


        String ingredients = String.join(", ", ingredient);

        return ingredients+"로 구성된 화장품이 있어" + memberSkinDTO.toString() + "의 회원에게 맞는 60자의 분석요약을 해줘";
//        String prompt = "약산성 어성초 레드스팟 미스트라는 화장품이 병풀추출물, 약모밀추출물, 펜틸렌글라이콜, 부틸렌글라이콜, 클리세린, 나이아신아마이드, 에탄올, 1,2-헥산다이올, 글루코노락톤, 락토바이오닉애씨드, 타타릭애씨드, 정제수, 알란토인, 파스향나무잎추출물, 트레할로오스, 티트리잎추출물, 폴리글리세릴-10라우레이트, 폴리글리세릴-10미리스테이트, 에틸헥실글리세린, 프로판다이올, 에난티아클로란타껍질추출물, 알로에베라잎가루, 소듐글루코네이트, 유칼립투스잎오일, 쇠비름추출물, 유자추출물, 소나무잎추출물, 개똥쑥추출뭋, 올레아놀릭애씨드로 구성되어있어, 건성인 회원에게 이 화장품에 맞는 분석과 평가를 120자로 해줘";

    }

    public String promptComparison(Long member_id, List<String> ingredient1, List<String> ingredient2) {


        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);


        String ingredients1 = String.join(", ", ingredient1);
        String ingredients2 = String.join(", ", ingredient2);

        return ingredients1+" 이 성분들로 구성된 화장품과" + ingredients2+"이 성분들로 구성된 화장품이있어" + memberSkinDTO.toString() + "의 회원에게 맞는 80자의 비교분석요약을 해줘";
//        String prompt = "약산성 어성초 레드스팟 미스트라는 화장품이 병풀추출물, 약모밀추출물, 펜틸렌글라이콜, 부틸렌글라이콜, 클리세린, 나이아신아마이드, 에탄올, 1,2-헥산다이올, 글루코노락톤, 락토바이오닉애씨드, 타타릭애씨드, 정제수, 알란토인, 파스향나무잎추출물, 트레할로오스, 티트리잎추출물, 폴리글리세릴-10라우레이트, 폴리글리세릴-10미리스테이트, 에틸헥실글리세린, 프로판다이올, 에난티아클로란타껍질추출물, 알로에베라잎가루, 소듐글루코네이트, 유칼립투스잎오일, 쇠비름추출물, 유자추출물, 소나무잎추출물, 개똥쑥추출뭋, 올레아놀릭애씨드로 구성되어있어, 건성인 회원에게 이 화장품에 맞는 분석과 평가를 120자로 해줘";


    }
    public String[] promptRatio(Long member_id, List<String> ingredient1, List<String> ingredient2) {
        MemberSkinDTO memberSkinDTO = memberService.getMemberSkinDTO(member_id);


        String ingredients1 = String.join(", ", ingredient1);
        String ingredients2 = String.join(", ", ingredient2);

        String prompt = "회원정보와 화장품1의 성분, 화장품2의 성분으로 각 화장품이 회원에게 얼마나 적합한지를 0~100의 점수로 출력해줘\n (출력 예시): 95, 80\n회원정보: " + memberSkinDTO.toString() + "\n화장품 성분1: " + ingredients1 + "\n 화장품 성분2: " + ingredients2 + "\n";

        String completion = getCompletion(prompt);
        
        String[] ratios = completion.split(",");
        if (ratios.length == 2) {
            return ratios;
        } else {
            String[] ratios2 = {"70", "85"};
            return ratios2;
        }


        // return ingredients1+" 이 성분들로 구성된 화장품1과" + ingredients2+"이 성분들로 구성된 화장품2가있어" + memberSkinDTO.toString() + "의 회원에게 맞게 총 100점을 화장품1,화장품2를 각각 분배해주고 화장품 1부터 화장품2 이런 순서로 각각 숫자만줘 ";
//        String prompt = "약산성 어성초 레드스팟 미스트라는 화장품이 병풀추출물, 약모밀추출물, 펜틸렌글라이콜, 부틸렌글라이콜, 클리세린, 나이아신아마이드, 에탄올, 1,2-헥산다이올, 글루코노락톤, 락토바이오닉애씨드, 타타릭애씨드, 정제수, 알란토인, 파스향나무잎추출물, 트레할로오스, 티트리잎추출물, 폴리글리세릴-10라우레이트, 폴리글리세릴-10미리스테이트, 에틸헥실글리세린, 프로판다이올, 에난티아클로란타껍질추출물, 알로에베라잎가루, 소듐글루코네이트, 유칼립투스잎오일, 쇠비름추출물, 유자추출물, 소나무잎추출물, 개똥쑥추출뭋, 올레아놀릭애씨드로 구성되어있어, 건성인 회원에게 이 화장품에 맞는 분석과 평가를 120자로 해줘";


    }

}
