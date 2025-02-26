package gachonproject.web.service;

import gachonproject.web.dto.CrawlingResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Transactional
public class CrawlingService {

//    public Map<String, List<String>> crawling(String cosmeticName) {
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("python", "D\\a.py", "인셀덤 비에톤 오일 미스트 50ml");
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//
//            process.waitFor();
//
//            String resultString = stringBuilder.toString();
//            return parsePythonOutput(resultString);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public List<Object> crawling2(String cosmeticName) {
        List<Object> result = new ArrayList<>();

        try {
            // 파이썬 실행하여 결과 가져오기
            ProcessBuilder processBuilder = new ProcessBuilder("python", "/home/t24106/v1.0src/BackEnd/Service/src/main/resources/static/python/a.py", cosmeticName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 파이썬 실행 결과 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder pythonOutput = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                pythonOutput.append(line).append("\n");
                System.out.println(line);
            }

            System.out.println(pythonOutput);
            // 파이썬 결과 파싱
            result.addAll(parsePythonOutput(pythonOutput.toString()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }





    private List<Object> parsePythonOutput(String output) {
        List<Object> result = new ArrayList<>();

        // 첫 번째 상품 이미지 링크 파싱
        int firstImageLinkStartIndex = output.indexOf("상품 이미지 링크: ");
        if (firstImageLinkStartIndex != -1) {
            int firstImageLinkEndIndex = output.indexOf("\n", firstImageLinkStartIndex);
            if (firstImageLinkEndIndex != -1) {
                String firstImageLink = output.substring(firstImageLinkStartIndex + "상품 이미지 링크: ".length(), firstImageLinkEndIndex).trim();

                // 결과에 첫 번째 상품 이미지 링크 추가
                Map<String, String> firstItem = new HashMap<>();
                firstItem.put("imageLinks", firstImageLink);
                result.add(firstItem);
            }
        }

        // "판매처 정보" 파싱
        int sellerInfoStartIndex = output.indexOf("판매처 링크: ");
        while (sellerInfoStartIndex != -1) {
            int sellerInfoEndIndex = output.indexOf("------------------", sellerInfoStartIndex);
            if (sellerInfoEndIndex != -1) {
                String sellerInfoSection = output.substring(sellerInfoStartIndex, sellerInfoEndIndex);
                Map<String, String> sellerInfoMap = new HashMap<>();
                String[] sellerInfoLines = sellerInfoSection.split("\n");
                for (String line : sellerInfoLines) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        sellerInfoMap.put(parts[0].trim(), parts[1].trim());
                    }
                }
                // 결과에 판매처 정보 추가
                result.add(sellerInfoMap);
                sellerInfoStartIndex = output.indexOf("판매처 링크: ", sellerInfoEndIndex);
            } else {
                break;
            }
        }

        return result;
    }





    public List<CrawlingResultDTO> result(List<Object> objects){
        List<CrawlingResultDTO> result = new ArrayList<>();


        int count = 0;
        for (Object object : objects) {
            Object firstObject = objects.get(count);
            Map<String, String> firstMap = (Map<String, String>) firstObject;
            String purchase_name = firstMap.get("판매처 이름");
            String cosmetic_image = firstMap.get("imageLinks");
            String purchase_logo_image = firstMap.get("판매처 로고");
            String purchase_price = firstMap.get("가격");
            String purchase_url = firstMap.get("판매처 링크");
            CrawlingResultDTO crawlingResultDTO = new CrawlingResultDTO(purchase_price, purchase_name, purchase_logo_image, cosmetic_image, purchase_url);
            result.add(crawlingResultDTO);
            count ++;
        }

//        Object firstObject = objects.get(0);
//        // 첫 번째 오브젝트를 Map<String, String>으로 캐스팅
//        Map<String, String> firstMap = (Map<String, String>) firstObject;
//
//        // "판매처 이름"에 해당하는 값을 가져옴
//        String sellerName = firstMap.get("판매처 이름");
//        System.out.println("첫 번째 판매처 이름: " + sellerName);


        return result;

    }




//    private List<Object> parsePythonOutput(String output) {
//        List<Object> result = new ArrayList<>();
//
//
//        // "판매처 정보" 파싱
//        int sellerInfoStartIndex = output.indexOf("판매처 링크: ");
//        while (sellerInfoStartIndex != -1) {
//            int sellerInfoEndIndex = output.indexOf("------------------", sellerInfoStartIndex);
//            if (sellerInfoEndIndex != -1) {
//                String sellerInfoSection = output.substring(sellerInfoStartIndex, sellerInfoEndIndex);
//                Map<String, String> sellerInfoMap = new HashMap<>();
//                String[] sellerInfoLines = sellerInfoSection.split("\n");
//                for (String line : sellerInfoLines) {
//                    String[] parts = line.split(": ");
//                    if (parts.length == 2) {
//                        sellerInfoMap.put(parts[0].trim(), parts[1].trim());
//                    }
//                }
//                // 이미지 링크 목록을 문자열로 결합하여 맵에 추가
//                List<String> imageLinks = new ArrayList<>();
//                int imageLinkStartIndex = sellerInfoSection.indexOf("상품 이미지 링크: ");
//                if (imageLinkStartIndex != -1) {
//                    int imageLinkEndIndex = sellerInfoSection.indexOf("\n", imageLinkStartIndex);
//                    if (imageLinkEndIndex != -1) {
//                        String imageLinkSection = sellerInfoSection.substring(imageLinkStartIndex, imageLinkEndIndex);
//                        String[] imageLinkLines = imageLinkSection.split("\n");
//                        for (String line : imageLinkLines) {
//                            if (line.startsWith("상품 이미지 링크: ")) {
//                                imageLinks.add(line.substring("상품 이미지 링크: ".length()).trim());
//                            }
//                        }
//                    }
//                }
//                sellerInfoMap.put("imageLinks", String.join("\n", imageLinks));
//                result.add(sellerInfoMap);
//                sellerInfoStartIndex = output.indexOf("판매처 링크: ", sellerInfoEndIndex);
//            } else {
//                break;
//            }
//        }
//
//        return result;
//    }








}
