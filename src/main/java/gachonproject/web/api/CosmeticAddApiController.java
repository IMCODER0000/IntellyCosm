package gachonproject.web.api;


import jakarta.servlet.http.HttpServletRequest;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.repository.IngredientRepository;
import gachonproject.mobile.service.AiService;
import gachonproject.mobile.service.AnalysisService;
import gachonproject.mobile.service.CosmeticService;
import gachonproject.mobile.service.MemberService;
import gachonproject.web.dto.CosmeticAddDTO;
import gachonproject.web.dto.CosmeticAddInfoDTO;
import gachonproject.web.dto.CosmteticAddFinalDTO;
import gachonproject.web.dto.CrawlingResultDTO;
import gachonproject.web.service.CrawlingService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequiredArgsConstructor
public class CosmeticAddApiController {

    @Autowired
    private final AnalysisService analysisService;
    @Autowired
    private final CosmeticService cosmeticService;
    @Autowired
    private final CrawlingService crawlingService;
    @Autowired
    private final IngredientRepository ingredientRepository;
    @Autowired
    private final AiService aiService;
    @Autowired
    private final MemberService memberService;


    private static final String UPLOAD_DIR = "/home/t24106/v1.0src/Image/Admin/";








    // 화장품 등록

    //화장품 등록 정보 데이터 전달
    @GetMapping("/api/admin/cosmetics/add/info")
    public ResponseEntity<List<CosmeticAddInfoDTO>> cosmeticsAddInfo() {

        List<AnalysisCosmeticRegistration> analysisCosmeticRegistrations = analysisService.analysisCosmeticRegistrationsFindAll();

        List<CosmeticAddInfoDTO> cosmeticAddInfos = new ArrayList<>();
        for (AnalysisCosmeticRegistration acr : analysisCosmeticRegistrations) {
            Member findMember = memberService.findMemberById(acr.getMember().getId());
            CosmeticAddInfoDTO cosmeticAddInfo = new CosmeticAddInfoDTO(acr.getCosmetic_name(), acr.getDate(), acr.getId(), acr.getImage_path(), findMember.getLogin_id(), acr.getScore());
            cosmeticAddInfos.add(cosmeticAddInfo);
        }


        return new ResponseEntity(cosmeticAddInfos, HttpStatus.OK);

    }

//    //화장품 크롤링 등록
//    @PostMapping("/api/admin/cosmetics/add/Auto/{analysis_cosmetic_registration_id}")
//    public ResponseEntity cosmeticsAddAuto(@PathVariable("analysis_cosmetic_registration_id") Long analysis_cosmetic_registration_id){
//
//        AnalysisCosmeticRegistration find = analysisService.findAnalysisCosmeticRegistrationById(analysis_cosmetic_registration_id);
//
//        LocalDate date = LocalDate.now();
//        String cosmeticName = find.getCosmetic_name();
//        String image_path = null;
//        List<CosmeticPurchaseLink> cosmeticPurchaseLinks;
//
//        //자동 크롤링
//        List<Object> objects = crawlingService.crawling2(cosmeticName);
//
//
//        for (Object object : objects) {
//            String string = object.toString();
//            System.out.println(string);
//        }
//
//
//
//        List<CrawlingResultDTO> result = crawlingService.result(objects);
//        String cosmeticImage = result.get(0).getCosmetic_image();
//        result.remove(0);
//        System.out.println(cosmeticImage);
//
//
//
//
//        String image; //상품 이미지 링크
//        List<List<String>> purchaseLinks; // 판매처 링크, 판매처 로고, 판매처 이름, 가격
//
//
//
//        Long analysisId = find.getAnalysis_id();
//        Analysis findAnalysis = analysisService.findById(analysisId);
//        List<AnalysisIngredient> analysisIngredient = findAnalysis.getAnalysisIngredient();
//
//        Cosmetic cosmetic = new Cosmetic();
//
//
//        List<CosmeticIngredient> cosmeticIngredients= new ArrayList<>();
//        CosmeticIngredient cosmeticIngredient = new CosmeticIngredient();
//        for (AnalysisIngredient ingredient : analysisIngredient) {
//            Ingredient ingredient1 = ingredient.getIngredient();
//            cosmeticIngredient.setCosmetic(cosmetic);
//            cosmeticIngredient.setIngredient(ingredient1);
//            cosmeticIngredients.add(cosmeticIngredient);
//        }
//
//
//        cosmetic.setCosmeticIngredients(cosmeticIngredients);
//        cosmetic.setName(cosmeticName);
//        cosmetic.setImage_path(cosmeticImage);
//        cosmetic.setLatest_update_date(date);
//        cosmeticService.creatCosmetic(cosmetic);
//
//        List<CosmeticPurchaseLink> cosmeticPurchaseLinks1 = new ArrayList<>();
//        for (CrawlingResultDTO crawlingResultDTO : result) {
//            String purchaseName = crawlingResultDTO.getPurchase_name();
//            String purchasePrice = crawlingResultDTO.getPurchase_price();
//            String purchaseLogoImage = crawlingResultDTO.getPurchase_logo_image();
//            String purchaseUrl = crawlingResultDTO.getPurchase_url();
//
//            CosmeticPurchaseLink cosmeticPurchaseLink = new CosmeticPurchaseLink();
//            cosmeticPurchaseLink.setPurchaseSite(purchaseName);
//            cosmeticPurchaseLink.setUrl(purchaseUrl);
//            cosmeticPurchaseLink.setPrice(purchasePrice);
//            cosmeticPurchaseLink.setCosmetic(cosmetic);
//            cosmeticPurchaseLink.setRogoImage(purchaseLogoImage);
//
//            cosmeticService.createCosmeticPurchaseLink(cosmeticPurchaseLink);
//            cosmeticPurchaseLinks1.add(cosmeticPurchaseLink);
//        }
//
//
//        cosmetic.setCosmeticPurchaseLinks(cosmeticPurchaseLinks1);
//
//        cosmeticService.updateCosmetic(cosmetic);
//
//        Long id = cosmetic.getId();
//
//
//        return new ResponseEntity(id,HttpStatus.OK);
//
//    }




    //화장품 크롤링 등록
    @PostMapping("/api/admin/cosmetics/add/Auto/{analysis_cosmetic_registration_id}")
    public ResponseEntity cosmeticsAddAuto(@PathVariable("analysis_cosmetic_registration_id") Long analysis_cosmetic_registration_id){

        AnalysisCosmeticRegistration find = analysisService.findAnalysisCosmeticRegistrationById(analysis_cosmetic_registration_id);

        LocalDate date = LocalDate.now();
        String cosmeticName = find.getCosmetic_name();
        String image_path = find.getImage_path();
        List<CosmeticPurchaseLink> cosmeticPurchaseLinks;

        //자동 크롤링
        List<Object> crawling = crawlingService.crawling2(cosmeticName);


        List<CrawlingResultDTO> result = crawlingService.result(crawling);


        String cosmeticImage = result.get(0).getCosmetic_image();
        result.remove(0);

        for (CrawlingResultDTO crawlingResultDTO : result) {
            String replace = crawlingResultDTO.getPurchase_price().replace(",", "");
            crawlingResultDTO.setPurchase_price(replace);
        }








        Long analysisId = find.getAnalysis_id();
        Analysis findAnalysis = analysisService.findById(analysisId);
        List<AnalysisIngredient> analysisIngredient = findAnalysis.getAnalysisIngredient();

        Cosmetic cosmetic = new Cosmetic();

        cosmetic.setName(cosmeticName);
        cosmetic.setImage_path(cosmeticImage);
        cosmetic.setLatest_update_date(date);
        cosmeticService.creatCosmetic(cosmetic);

        List<CosmeticIngredient> cosmeticIngredients= new ArrayList<>();
        CosmeticIngredient cosmeticIngredient = new CosmeticIngredient();
        for (AnalysisIngredient ingredient : analysisIngredient) {
            Ingredient ingredient1 = new Ingredient();
            ingredient1 = ingredient.getIngredient();
            cosmeticIngredient.setCosmetic(cosmetic);
            cosmeticIngredient.setIngredient(ingredient1);
            cosmeticIngredients.add(cosmeticIngredient);
            cosmeticService.createCosmeticIngredient(cosmeticIngredient);
            System.out.println("화장품 성분 등록 완료");
        }



        cosmetic.setCosmeticIngredients(cosmeticIngredients);


        List<CosmeticPurchaseLink> cosmeticPurchaseLinks1 = new ArrayList<>();
        for (CrawlingResultDTO crawlingResultDTO : result) {
            String purchaseName = crawlingResultDTO.getPurchase_name();
            String purchasePrice = crawlingResultDTO.getPurchase_price();
            String purchaseLogoImage = crawlingResultDTO.getPurchase_logo_image();
            String purchaseUrl = crawlingResultDTO.getPurchase_url();

            CosmeticPurchaseLink cosmeticPurchaseLink = new CosmeticPurchaseLink();
            cosmeticPurchaseLink.setPurchaseSite(purchaseName);
            cosmeticPurchaseLink.setUrl(purchaseUrl);
            cosmeticPurchaseLink.setPrice(purchasePrice);
            cosmeticPurchaseLink.setCosmetic(cosmetic);
            cosmeticPurchaseLink.setRogoImage(purchaseLogoImage);

            cosmeticService.createCosmeticPurchaseLink(cosmeticPurchaseLink);
            cosmeticPurchaseLinks1.add(cosmeticPurchaseLink);
        }


        cosmetic.setCosmeticPurchaseLinks(cosmeticPurchaseLinks1);

        cosmeticService.updateCosmetic(cosmetic);


        analysisService.deleteAnalysisCosmeticRegistration(cosmeticName);

        Long id = cosmetic.getId();


        return new ResponseEntity(id,HttpStatus.OK);

    }


     //화장품 수동 최종 등록
    @PostMapping("/api/admin/cosmetics/add/passive")
    public ResponseEntity cosmeticsAddPassive(@RequestPart(value = "request") @Valid CosmeticAddDTO cosmeticAddDTO,
                                              @RequestPart(value = "file", required = false) MultipartFile file) {



        try {


        
        
            // 현재 시간을 기반으로 파일 이름 생성 (member_id_YYYYMMDDHHMMSS.jpg)
            LocalDateTime now = LocalDateTime.now();
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String fileName = cosmeticAddDTO.getCosmetic_name() + "_" + now.format(formatter) + ".png";
            String imagePath = "http://ceprj.gachon.ac.kr:60006/admin_image/" +fileName;

            // 파일 저장 경로 생성
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 이미지 파일 저장
            Files.copy(file.getInputStream(), filePath);





            Cosmetic cosmetic = new Cosmetic();

            cosmetic.setLatest_update_date(currentDate);
            cosmetic.setName(cosmeticAddDTO.getCosmetic_name());
            cosmetic.setImage_path(imagePath);

            cosmeticService.creatCosmetic(cosmetic);

            List<String> cosmeticIngredient = cosmeticAddDTO.getCosmetic_ingredient();

            List<CosmeticIngredient> cosmeticIngredients = new ArrayList<>();

            for (String ingredient : cosmeticIngredient) {

                Ingredient ingredient1 = ingredientRepository.ingredientfindByName(ingredient);
                if (ingredient1 != null) {
                    CosmeticIngredient cosmeticIngredient1 = new CosmeticIngredient();
                    System.out.println(ingredient1.getName());
                    // 각 성분에 대해 새로운 CosmeticIngredient 객체를 생성합니다.
                    cosmeticIngredient1.setCosmetic(cosmetic);
                    cosmeticIngredient1.setIngredient(ingredient1);
                    cosmetic.setCosmeticIngredients(cosmeticIngredients);
                    // 리스트에 새로운 CosmeticIngredient 객체를 추가합니다.
                    cosmeticIngredients.add(cosmeticIngredient1);
                }
            }


            Map<String, List<String>> cosmeticPurchase = cosmeticAddDTO.getCosmetic_purchase();
            List<CosmeticPurchaseLink> cosmeticPurchaseLinks = new ArrayList<>();

            for (String purchase : cosmeticPurchase.keySet()) {
                String purchaseSite = cosmeticPurchase.get(purchase).get(0); // url
                String price = cosmeticPurchase.get(purchase).get(1); // 가격

                CosmeticPurchaseLink cosmeticPurchaseLink = new CosmeticPurchaseLink();



                cosmeticPurchaseLink.setCosmetic(cosmetic);
                cosmeticPurchaseLink.setUrl(purchaseSite);
                cosmeticPurchaseLink.setPrice(price);
                cosmeticPurchaseLink.setPurchaseSite(purchase);
                cosmeticService.createCosmeticPurchaseLink(cosmeticPurchaseLink);

                cosmeticPurchaseLinks.add(cosmeticPurchaseLink);
            }


            cosmetic.setCosmeticIngredients(cosmeticIngredients);
            cosmetic.setCosmeticPurchaseLinks(cosmeticPurchaseLinks);

            cosmeticService.updateCosmetic(cosmetic);




            Long id = cosmetic.getId();





            return new ResponseEntity(id, HttpStatus.OK);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //화장품등록(수동) 성분 자동 추출
    @PostMapping("/api/admin/cosmetic/ingredient/ocr")
    public ResponseEntity<List<String>> cosmeticIngredient(@RequestParam(value="file",required = false)  MultipartFile file){


        try {
            // 현재 시간을 기반으로 파일 이름 생성 (member_id_YYYYMMDDHHMMSS.jpg)
            LocalDateTime now = LocalDateTime.now();
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String fileName =  "admin_" + now.format(formatter) + ".jpg";

            // 파일 저장 경로 생성
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 이미지 파일 저장
            Files.copy(file.getInputStream(), filePath);

            System.out.println(fileName.toString());

            //OCR
            List<String> result = aiService.analyzeImage2(filePath);
            for (String s : result) {
                System.out.println(s);
            }
            String[] tokens = result.get(0).split(","); // 첫 번째 요소를 공백으로 분할하여 문자열 배열로 가져옴

            

            result.remove(0);

            return new ResponseEntity(result, HttpStatus.OK);




        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }



    //화장품등록(수동) 구매처 크롤링
    @GetMapping("/api/admin/cosmetic/crawling/{cosmetic_name}")
    public ResponseEntity crawling(@PathVariable("cosmetic_name") String cosmetic_name){


        List<Object> crawling = crawlingService.crawling2(cosmetic_name);
        List<CrawlingResultDTO> crawlings = crawlingService.result(crawling);


        String cosmeticImage = crawlings.get(0).getCosmetic_image();
        crawlings.remove(0);

        for (CrawlingResultDTO crawlingResultDTO : crawlings) {
            String replace = crawlingResultDTO.getPurchase_price().replace(",", "");
            crawlingResultDTO.setPurchase_price(replace);
        }

        ResultDTO result = new ResultDTO(crawlings, cosmeticImage);





        return new ResponseEntity(result,HttpStatus.OK);

    }

    @Data
    public class ResultDTO{
        List<CrawlingResultDTO> listCrawling;
        String cosmeticImage;

        public ResultDTO(List<CrawlingResultDTO> listCrawling, String cosmeticImage) {
            this.listCrawling = listCrawling;
            this.cosmeticImage = cosmeticImage;
        }
    }


    @DeleteMapping("/api/admin/analysis/cosmetic/delete/{analysis_cosmetic_registration_id}")
    public ResponseEntity deleteACosmetic(@PathVariable("analysis_cosmetic_registration_id") Long analysis_cosmetic_registration_id){

        analysisService.deleteAnalysisCosmeticRegistrationById(analysis_cosmetic_registration_id);

        return new ResponseEntity(HttpStatus.OK);

    }






}
