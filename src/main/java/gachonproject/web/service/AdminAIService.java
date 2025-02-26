package gachonproject.web.service;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.RecommendedModel;
import gachonproject.web.repository.AdminAIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminAIService {

    @Autowired
    AdminAIRepository adminAIRepository;


    public OCRModel latestOcr(){
        return adminAIRepository.latestOcr();
    }

    public RecommendedModel latestReco(){
        return adminAIRepository.latestReco();
    }


    public List<OCRModel> latestOcrList(){
        return adminAIRepository.latestOcrList();
    }
    public List<RecommendedModel> latestRecoList(){
        return adminAIRepository.latestRecoList();
    }


    public void latestOcrChange(String version){

        OCRModel ocrModel = adminAIRepository.latestOcr();

        adminAIRepository.latestOcrChange(ocrModel, version);



    }

    public void latestRecoChange(String version){

        RecommendedModel recommendedModel = adminAIRepository.latestReco();

        adminAIRepository.latestRecoChange(recommendedModel, version);



    }

    public void OcrChange(OCRModel ocrModel){

        OCRModel ocrModel1 = adminAIRepository.latestOcr();

        adminAIRepository.OcrChange(ocrModel1, ocrModel);


    }

    public void RecoChange(RecommendedModel recommendedModel){

        RecommendedModel recommendedModel1 = adminAIRepository.latestReco();

        adminAIRepository.RecoChange(recommendedModel1, recommendedModel);


    }

    public String incrementVersion(String version) {
        // 'v.' 제거 후 '.'으로 나눔
        String[] parts = version.substring(2).split("\\.");

        // 문자열을 정수로 변환
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        // 패치 버전 증가
        patch++;

        // 패치 버전이 10이 되면 마이너 버전을 증가시키고 패치 버전을 0으로 초기화
        if (patch == 10) {
            patch = 0;
            minor++;
        }

        // 마이너 버전이 10이 되면 메이저 버전을 증가시키고 마이너 버전을 0으로 초기화
        if (minor == 10) {
            minor = 0;
            major++;
        }

        // 새로운 버전 문자열 생성
        return String.format("v%d.%d.%d", major, minor, patch);
    }

    public String incrementVersion2(List<OCRModel> ocrModels) {
        int maxMajor = 0;
        int maxMinor = 0;
        int maxPatch = 0;

        // 가장 큰 버전을 찾는 과정
        for (OCRModel ocrModel : ocrModels) {
            String version = ocrModel.getVersion();

            System.out.println("00000000000000");
            System.out.println(version);
            System.out.println("00000000000000");

            // 'v.' 제거 후 '.'으로 나눔
            String[] parts = version.replaceFirst("v", "").split("\\.");


            // 문자열을 정수로 변환
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);

            // 현재 버전이 가장 큰 버전인지 확인
            if (major > maxMajor ||
                    (major == maxMajor && minor > maxMinor) ||
                    (major == maxMajor && minor == maxMinor && patch > maxPatch)) {
                maxMajor = major;
                maxMinor = minor;
                maxPatch = patch;
            }
        }

        // 패치 버전 증가
        maxPatch++;

        // 패치 버전이 10이 되면 마이너 버전을 증가시키고 패치 버전을 0으로 초기화
        if (maxPatch == 10) {
            maxPatch = 0;
            maxMinor++;
        }

        // 마이너 버전이 10이 되면 메이저 버전을 증가시키고 마이너 버전을 0으로 초기화
        if (maxMinor == 10) {
            maxMinor = 0;
            maxMajor++;
        }

        // 새로운 버전 문자열 생성
        return String.format("v%d.%d.%d", maxMajor, maxMinor, maxPatch);
    }

    public String incrementVersion3(List<RecommendedModel> recommendedModels) {
        int maxMajor = 0;
        int maxMinor = 0;
        int maxPatch = 0;

        // 가장 큰 버전을 찾는 과정
        for (RecommendedModel ocrModel : recommendedModels) {
            String version = ocrModel.getVersion();

            System.out.println("00000000000000");
            System.out.println(version);
            System.out.println("00000000000000");

            // 'v.' 제거 후 '.'으로 나눔
            String[] parts = version.replaceFirst("v", "").split("\\.");


            // 문자열을 정수로 변환
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);

            // 현재 버전이 가장 큰 버전인지 확인
            if (major > maxMajor ||
                    (major == maxMajor && minor > maxMinor) ||
                    (major == maxMajor && minor == maxMinor && patch > maxPatch)) {
                maxMajor = major;
                maxMinor = minor;
                maxPatch = patch;
            }
        }

        // 패치 버전 증가
        maxPatch++;

        // 패치 버전이 10이 되면 마이너 버전을 증가시키고 패치 버전을 0으로 초기화
        if (maxPatch == 10) {
            maxPatch = 0;
            maxMinor++;
        }

        // 마이너 버전이 10이 되면 메이저 버전을 증가시키고 마이너 버전을 0으로 초기화
        if (maxMinor == 10) {
            maxMinor = 0;
            maxMajor++;
        }

        // 새로운 버전 문자열 생성
        return String.format("v%d.%d.%d", maxMajor, maxMinor, maxPatch);
    }






}
