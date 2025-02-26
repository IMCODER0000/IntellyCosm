package gachonproject.mobile.service;

import com.mysql.cj.log.Log;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.cosmetic.CosmeticPurchaseLink;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.repository.CosmeticRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CosmeticService {
    private final CosmeticRepository cosmeticRepository;


    public void creatCosmetic(Cosmetic cosmetic){
        cosmeticRepository.createCosmetic(cosmetic);
    }

    public void createCosmeticPurchaseLink(CosmeticPurchaseLink cosmeticPurchaseLink){
        cosmeticRepository.createCosmeticPurchaseLink(cosmeticPurchaseLink);
    }

    public Cosmetic findCosmeticById(Long id) {
        return cosmeticRepository.findById(id);
    }



    public List<gachonproject.mobile.service.CosmeticService.CosmeticListData> getCosmeticList() {
        List<gachonproject.mobile.service.CosmeticService.CosmeticListData>cosmeticList = new ArrayList<>();
        List<Cosmetic> cosmetics = cosmeticRepository.findAll();
        for (Cosmetic cosmetic : cosmetics) {
            cosmeticList.add(new gachonproject.mobile.service.CosmeticService.CosmeticListData(cosmetic.getId(), cosmetic.getName(), cosmetic.getImage_path(), cosmetic.getLowestPrice()));
        }
        return cosmeticList;
    }

    public List<Cosmetic> findAllCosmetic() {
        return cosmeticRepository.findAll();
    }

    public void updateCosmetic(Cosmetic cosmetic){
        cosmeticRepository.updateCosmetic(cosmetic);
    }

    public void updateCosmeticPurchaseUpdate(List<CosmeticPurchaseLink> cosmeticPurchaseLink){
        cosmeticRepository.updateCosmeticPurchaseUpdate(cosmeticPurchaseLink);
    }
    public void cosmeticPurchaseDeleteById(Long cosmetic_id){
        List<CosmeticPurchaseLink> allCosmeticPurchaseLinkByCosmeticId = cosmeticRepository.findAllCosmeticPurchaseLinkByCosmetic_id(cosmetic_id);
        System.out.println(allCosmeticPurchaseLinkByCosmeticId);

        for (CosmeticPurchaseLink cosmeticPurchaseLink : allCosmeticPurchaseLinkByCosmeticId) {
            System.out.println(cosmeticPurchaseLink.getPurchaseSite());
            cosmeticRepository.deleteCosmeticPurchase(cosmeticPurchaseLink);
        }
    }

    public void cosmeticIngredientDeleteById(Long cosmetic_id){
        List<CosmeticIngredient> allCosmeticIngredientByCosmeticId = cosmeticRepository.findAllCosmeticIngredientByCosmeticId(cosmetic_id);

        for (CosmeticIngredient cosmeticIngredient : allCosmeticIngredientByCosmeticId) {
            cosmeticRepository.deleteCosmeticIngredient(cosmeticIngredient);
        }
    }

    public void cosmeticDeleteById(Long cosmetic_id){
        Cosmetic findCosmetic = cosmeticRepository.findById(cosmetic_id);
        cosmeticRepository.deleteCosmetic(findCosmetic);
    }

    public void updateCosmeticPurchaseUpdate(CosmeticPurchaseLink cosmeticPurchaseLink){
        cosmeticRepository.updateCosmeticPurchaseUpdate2(cosmeticPurchaseLink);
    }

    public void createCosmeticIngredient(CosmeticIngredient cosmeticIngredient){
        cosmeticRepository.createCosmeticIngredient(cosmeticIngredient);
    }


    public Cosmetic getCosmeticById(Long id) {
        return cosmeticRepository.findById(id);
    }



    public gachonproject.mobile.service.CosmeticService.CosmeticDetailData getCosmeticDetail(Long id) {
        Cosmetic cosmetic = cosmeticRepository.findById(id);
        if (cosmetic == null) {
            return null;
        }
        else {
            List<CosmeticPurchaseLink> cosmeticPurchaseLinks = cosmetic.getCosmeticPurchaseLinks();
            return new gachonproject.mobile.service.CosmeticService.CosmeticDetailData(cosmetic.getId(), cosmetic.getName(), cosmetic.getImage_path(), cosmetic.getLowestPrice(), cosmeticPurchaseLinks);
        }
    }

    @Data
    public static class CosmeticListData {
        private final Long id;
        private final String name;
        private final String imagePath;
        private final int lowestPrice;
    }

    @Data
    public static class CosmeticDetailData {
        private final Long id;
        private final String name;
        private final String imagePath;
        private final int lowestPrice;
        private final List<CosmeticPurchaseLink> cosmeticPurchaseLinks;
    }


}
