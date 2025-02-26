package gachonproject.web.api;


import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.service.CosmeticService;
import gachonproject.web.dto.CosmeticLinkDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CosmeticLinkApiController {


    @Autowired
    private final CosmeticService cosmeticService;


    @GetMapping("/api/admin/link")
    public ResponseEntity<List<Cosmetic>> getCosmeticLink() {


        System.out.println("전");
        List<Cosmetic> allCosmetic = cosmeticService.findAllCosmetic();
        for (Cosmetic cosmetic : allCosmetic) {
            System.out.println("00000000000000000000");
            System.out.println(cosmetic.getId());
            System.out.println("----------------------------");
            List<CosmeticPurchaseLink> cosmeticPurchaseLinks = cosmetic.getCosmeticPurchaseLinks();
            for (CosmeticPurchaseLink cosmeticPurchaseLink : cosmeticPurchaseLinks) {
                System.out.println(cosmeticPurchaseLink.getPrice());
                System.out.println(cosmeticPurchaseLink.getPrice());
            }
            System.out.println("00000000000000000000");

        }
        System.out.println("후");

        return new ResponseEntity<>(allCosmetic, HttpStatus.OK);


    }

//    @PostMapping("/api/admin/link/update/{cosmetic_id}")
//    private ResponseEntity updateCosmeticLink(@RequestBody List<CosmeticLinkDTO> cosmeticLinkDTO,
//                                              @PathVariable("cosmetic_id") Long cosmetic_id) {
//        // 요청 데이터에서 id와 p_id 추출
//
//        Cosmetic cosmetic = cosmeticService.getCosmeticById(cosmetic_id);
//
//        for (CosmeticLinkDTO linkDTO : cosmeticLinkDTO) {
//            int count = 0;
//            System.out.println(linkDTO.getPurchaseSite());
//            System.out.println(linkDTO.getP_id());
//            System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getId());
//            System.out.println("0000000000000000000000000000000000000");
//            if (cosmetic.getCosmeticPurchaseLinks().get(count).getId().equals(linkDTO.getP_id())) {
//                cosmetic.getCosmeticPurchaseLinks().get(count).setPurchaseSite(linkDTO.getPurchaseSite());
//                cosmetic.getCosmeticPurchaseLinks().get(count).setUrl(linkDTO.getUrl());
//                cosmetic.getCosmeticPurchaseLinks().get(count).setPrice(linkDTO.getPrice());
//                System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getId());
//                System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getPrice());
//                System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getUrl());
//                System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getCosmetic());
//                System.out.println(cosmetic.getCosmeticPurchaseLinks().get(count).getPurchaseSite());
//                cosmeticService.updateCosmeticPurchaseUpdate(cosmetic.getCosmeticPurchaseLinks().get(count));
//                cosmeticService.updateCosmetic(cosmetic);
//            }
//            count++;
//
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


    //화장품 구매처 정보 변경
    @PostMapping("/api/admin/link/update/{cosmetic_id}")
    private ResponseEntity updateCosmeticLink(@RequestBody List<CosmeticPurchaseLink> cosmeticLinkDTO,
                                              @PathVariable("cosmetic_id") Long cosmetic_id) {




        Cosmetic cosmetic = cosmeticService.getCosmeticById(cosmetic_id);
        System.out.println(cosmetic_id);
        cosmeticService.cosmeticPurchaseDeleteById(cosmetic_id);

        for (CosmeticPurchaseLink cosmeticPurchaseLink : cosmeticLinkDTO) {
            cosmeticPurchaseLink.setCosmetic(cosmetic);
        }


        cosmeticService.updateCosmeticPurchaseUpdate(cosmeticLinkDTO);



        return new ResponseEntity<>(HttpStatus.OK);

    }




    @DeleteMapping("/api/admin/cosmetic/delete/{cosmetic_id}")
    private ResponseEntity deleteCosmteic(@PathVariable Long cosmetic_id){

        cosmeticService.cosmeticIngredientDeleteById(cosmetic_id);
        cosmeticService.cosmeticPurchaseDeleteById(cosmetic_id);
        cosmeticService.cosmeticDeleteById(cosmetic_id);

        return new ResponseEntity<>(HttpStatus.OK);


    }


}
