package gachonproject.web.api;


import gachonproject.web.domain.PromotionList;
import gachonproject.web.domain.PromotionMain;
import gachonproject.web.dto.CosmeticAddDTO;
import gachonproject.web.service.PromotionService;
import jakarta.validation.Valid;
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

@RestController
public class PromotionApiContrlloer {

    @Autowired
    protected PromotionService promotionService;


    private static final String UPLOAD_DIR = "/home/t24106/v1.0src/Image/Admin/";


    @GetMapping("/api/admin/promotion/all")
    public ResponseEntity promotionAll() {

        List<PromotionMain> result = promotionService.getAllPromotionMain();

        return new ResponseEntity(result,HttpStatus.OK);

    }

    @GetMapping("/api/admin/promotion/List")
    public ResponseEntity promotionList() {

        List<Long> mainids = new ArrayList<>();
        List<PromotionList> result = promotionService.getAllPromotionList();
        for (PromotionList promotionList : result) {
            Long mainId = promotionList.getMain_id();
            mainids.add(mainId);
        }

        List<PromotionMain> Real = promotionService.findAllPromotionMain(mainids);


        return new ResponseEntity(Real, HttpStatus.OK);
    }

    @PostMapping("/api/admin/promotion/List/update")
    public ResponseEntity promotionListUpdate(@Valid @RequestBody List<PromotionList> promotionLists) {

        List<PromotionList> result = promotionService.getAllPromotionList();

        if(!result.isEmpty()) {
            promotionService.promotionListDel();
            promotionService.promotionListAdd(promotionLists);
        }
        else {
            promotionService.promotionListAdd(promotionLists);
        }


        return new ResponseEntity(HttpStatus.OK);

    }


    @PostMapping("/api/admin/promotion/add")
    public ResponseEntity promotionAdd(@RequestPart(value = "request") @Valid PromotionMain promotionMain,
                                       @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {


        // 현재 시간을 기반으로 파일 이름 생성 (member_id_YYYYMMDDHHMMSS.jpg)
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String fileName = promotionMain.getName() + "_" + now.format(formatter) + ".png";
        String imagePath = "http://ceprj.gachon.ac.kr:60006/admin_image/" +fileName;

        // 파일 저장 경로 생성
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // 이미지 파일 저장
        Files.copy(file.getInputStream(), filePath);

        promotionMain.setImage(imagePath);

        promotionService.promotionAdd(promotionMain);






        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/api/admin/promotion/delete/{id}")
    public ResponseEntity promotionDelete(@PathVariable Long id) {


        promotionService.promotionDel(id);

        return new ResponseEntity(HttpStatus.OK);
    }






}
