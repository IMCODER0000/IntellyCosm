package gachonproject.mobile.service;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gachonproject.mobile.api.dto.OcrDTO;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;



@Service
public class AiService {




    public ScoreData Scoring(Long id) {
        try {
            // 파이썬 스크립트 실행 경로 설정
            String pythonScriptPath = "/home/t24106/v1.0src/ai/analysis/analysis.py";



            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, id.toString());
            Process process = pb.start();

            // 파이썬 스크립트의 출력을 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder jsonResults = new StringBuilder();
            String line;


//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//                jsonResults.append(line); // 결과를 StringBuilder에 추가
//            }



            String R = reader.readLine();
            System.out.println(R);

            if (R == null || R.isEmpty()) {
                // 결과가 null이거나 비어있을 경우 처리
                return null; // 또는 다른 적절한 값으로 처리
            }

            // JSON 결과를 파싱하여 ScoreData 객체로 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Integer> scoreDataMap  = mapper.readValue(R, new TypeReference<Map<String,Integer>>(){});

            ScoreData scoreData = new ScoreData(scoreDataMap.get("score"), scoreDataMap.get("pos_len"), scoreDataMap.get("neg_len"));
            System.out.println(scoreData);


            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

            return scoreData;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return (ScoreData) Collections.emptyList();
        }
    }

    @Data
    public static class ScoreData {
        @JsonProperty("score")
        private int score;

        @JsonProperty("positive_cnt")
        private int positiveCount;

        @JsonProperty("negative_cnt")
        private int negativeCount;

        // 생성자 완성
        public ScoreData(@JsonProperty("score") int score, @JsonProperty("positive_cnt") int positiveCount, @JsonProperty("negative_cnt") int negativeCount) {
            this.score = score;
            this.positiveCount = positiveCount;
            this.negativeCount = negativeCount;
        }
    }



    public List<Integer> recommend(Long analysis_id, Long member_id) {
        try {
            // 파이썬 스크립트 실행 경로 설정
            String pythonScriptPath = "/home/t24106/v1.0src/ai/recommend/recommend/recomend_backup.py";

            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, analysis_id.toString(), member_id.toString());
            Process process = pb.start();

            // 파이썬 스크립트의 출력을 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Integer> recommendations = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                // 대괄호와 쉼표 제거 후 공백을 기준으로 문자열을 나누어 정수 리스트에 추가
                String[] parts = line.replaceAll("[\\[\\],]", "").split("\\s+");
                for (String part : parts) {
                    recommendations.add(Integer.parseInt(part.trim()));
                }
            }

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 오류가 발생한 경우
                System.err.println("파이썬 스크립트 실행 중 오류 발생: " + exitCode);
                return null;
            }

            return recommendations;
        } catch (IOException | InterruptedException e) {
            // 예외 발생 시
            e.printStackTrace();
            return null;
        }
    }

//    public List<Integer> recommend(Long analysis_id, Long member_id) {
//        try {
//            // 파이썬 스크립트 실행 경로 설정
//            String pythonScriptPath = "/home/t24106/v0.5src/ai/recommend/recommend/recomend_backup.py";
//
//            // 파이썬 스크립트 실행
//            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, analysis_id.toString(), member_id.toString());
//            Process process = pb.start();
//
//            // 파이썬 스크립트의 출력을 읽어옴
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            List<Integer> recommendations = new ArrayList<>();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                recommendations.add(Integer.parseInt(line.trim()));
//            }
//
//            // 프로세스 종료 대기
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                // 오류가 발생한 경우
//                System.err.println("파이썬 스크립트 실행 중 오류 발생: " + exitCode);
//                return null;
//            }
//
//            return recommendations;
//        } catch (IOException | InterruptedException e) {
//            // 예외 발생 시
//            e.printStackTrace();
//            return null;
//        }
//    }










    public List<String> analyzeImage(String imagePath) {


        try {

            // 파이썬 스크립트 실행 경로 설정
            String pythonScriptPath = "${user.dir}/src/main/resources/static/python/analysis2.py";


            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, imagePath);
            Process process = pb.start();


            // 파이썬 스크립트의 출력을 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> outputLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line); // 결과를 리스트에 추가
            }

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

            return outputLines;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> analyzeImage2(Path imagePath) {
        try {
            // 파이썬 스크립트 실행 경로 설정
            String pythonScriptPath = "/home/t24106/v1.0src/ai/OCR_api/gpt_api_demo.py";
//            String pythonScriptPath = "D:\\7777\\service\\Service\\src\\main\\resources\\static\\python\\OCR_api\\gpt_api.py";
//            String pythonScriptPath = "/home/t24106/ocr/EasyOCR/test.py";

            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, imagePath.toString());
            Process process = pb.start();

            // 파이썬 스크립트의 출력을 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line); // 결과를 StringBuilder에 추가
            }

            // 파이썬 스크립트에서 이미 JSON 형식으로 출력되었으므로, 추가 변환 작업이 필요하지 않음
            String outputJson = outputBuilder.toString();
            System.out.println("파이썬 출력: " + outputJson);

            // 파이썬 출력을 직접 파싱하여 필요한 데이터를 추출
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> dataMap = mapper.readValue(outputJson, new TypeReference<Map<String, Object>>() {});

            // 화장품 이름과 성분 추출
            String cosmeticName = (String) dataMap.get("화장품 이름");
            List<String> result = (List<String>) dataMap.get("화장품 성분");

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

            // 화장품 이름과 성분 반환
            System.out.println("화장품 이름: " + cosmeticName);
            System.out.println("화장품 성분: " + result);
            result.add(0, cosmeticName);
            return result;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

//
//    public OcrDTO analyzeImage4(Path imagePath, String code) {
//        try {
//            // 파이썬 스크립트 실행 경로 설정
//            String pythonScriptPath = "/home/t24106/v0.5src/ai/OCR_api/gpt_api.py";
////            String pythonScriptPath = "D:\\7777\\service\\Service\\src\\main\\resources\\static\\python\\OCR_api\\gpt_api.py";
////            String pythonScriptPath = "/home/t24106/ocr/EasyOCR/test.py";
//
//            // 파이썬 스크립트 실행
//            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, imagePath.toString(), code);
//            Process process = pb.start();
//
//            // 파이썬 스크립트의 출력을 읽어옴
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder outputBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                outputBuilder.append(line); // 결과를 StringBuilder에 추가
//            }
//
//            // 파이썬 스크립트에서 이미 JSON 형식으로 출력되었으므로, 추가 변환 작업이 필요하지 않음
//            String outputJson = outputBuilder.toString();
//            System.out.println("파이썬 출력: " + outputJson);
//
//            // 파이썬 출력을 직접 파싱하여 필요한 데이터를 추출
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> dataMap = mapper.readValue(outputJson, new TypeReference<Map<String, Object>>() {});
//
//            // 화장품 이름과 성분 추출
//            int code = (int) dataMap.get("code");
//            String cosmeticName = (String) dataMap.get("화장품 이름");
//            List<String> result = (List<String>) dataMap.get("화장품 성분");
//            OcrDTO ocrDTO = new OcrDTO(code, cosmeticName, result);
//
//            // 프로세스 종료 대기
//            int exitCode = process.waitFor();
//            System.out.println("Exited with error code: " + exitCode);
//
//            // 화장품 이름과 성분 반환
//            System.out.println("화장품 이름: " + cosmeticName);
//            System.out.println("화장품 성분: " + result);
//            return ocrDTO;
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    public List<String> analyzeImage3(Path imagePath) {

//
//         이름들로 DB에서 이름이 같은 성분 리스트
//         LLM 컴마// 자바 컴마기준으로
        try {
            // 파이썬 스크립트 실행 경로 설정
            String pythonScriptPath = "/home/t24106/v1.0src/ai/OCR/test_gpt.py";

            // 파이썬 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, imagePath.toString());
            Process process = pb.start();

            // 파이썬 스크립트의 출력을 읽어옴
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> outputLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line); // 결과를 리스트에 추가
            }

            // ObjectMapper를 사용하여 JSON 형식으로 변환
            ObjectMapper mapper = new ObjectMapper();
            List<String> dataList = mapper.readValue(outputLines.get(0), new TypeReference<List<String>>() {});

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

            return dataList;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }











    // 추천

    // 분석 알고리즘






}
