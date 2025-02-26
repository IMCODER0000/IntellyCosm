package gachonproject.web.api;


import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.IngredientFeature;
import gachonproject.mobile.domain.ingredient.IngredientPurpose;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class IngredientManageApiController {

    @Autowired
    private IngredientService ingredientService;






    @GetMapping("/api/amin/ingredient")
    public ResponseEntity getIngredient() {

        List<Ingredient> allIngredients = ingredientService.getAllIngredients();


        return new ResponseEntity(allIngredients, HttpStatus.OK);

    }

    @PostMapping("/api/admin/ingredient/add")
    public ResponseEntity addIngredient(@RequestBody Ingredient ingredient) {

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName(ingredient.getName());
        ingredientService.createIngredient(ingredient1);

        List<IngredientPurpose> purposes = new ArrayList<>();
        for (IngredientPurpose purpose : ingredient.getPurposes()) {
            IngredientPurpose newPurpose = new IngredientPurpose(purpose.getPurpose(), ingredient1);
            purposes.add(newPurpose);
        }
        ingredientService.createIngredientPurpose(purposes);

        List<IngredientFeature> features = new ArrayList<>();
        for (IngredientFeature feature : ingredient.getFeatures()) {
            IngredientFeature newFeature = new IngredientFeature(feature.getFeature(), ingredient1);
            features.add(newFeature);
        }
        ingredientService.createIngredientFeatures(features);

        List<SkinTypeFeature> skinTypeFeatures = new ArrayList<>();
        for (SkinTypeFeature skinTypeFeature : ingredient.getSkinTypeFeatures()) {
            SkinTypeFeature newSkinTypeFeature = new SkinTypeFeature();
            newSkinTypeFeature.setSkin_type(skinTypeFeature.getSkin_type());
            newSkinTypeFeature.setSkinDescription(skinTypeFeature.getSkinDescription());
            newSkinTypeFeature.setPositivity_status(skinTypeFeature.isPositivity_status());
            newSkinTypeFeature.setIngredient(ingredient1);
            skinTypeFeatures.add(newSkinTypeFeature);
        }
        ingredientService.createIngredientSkin(skinTypeFeatures);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/api/admin/ingredient/update/{ingredient_id}")
    public ResponseEntity updateIngredient(@PathVariable Long ingredient_id,
                                           @RequestBody Ingredient updatedIngredient) {
        try {
            // 기존의 엔티티를 데이터베이스에서 가져옴
            Ingredient existingIngredient = ingredientService.getIngredient(ingredient_id);

            // 업데이트할 필드 설정
            existingIngredient.setName(updatedIngredient.getName());
            existingIngredient.setGrade(updatedIngredient.getGrade());
            existingIngredient.setDanger_status(updatedIngredient.isDanger_status());
            existingIngredient.setAllergy_status(updatedIngredient.isAllergy_status());

            // 기존의 목적 리스트 삭제
            existingIngredient.getPurposes().clear();
            // 새로운 목적 리스트 추가
            for (IngredientPurpose purpose : updatedIngredient.getPurposes()) {
                IngredientPurpose newPurpose = new IngredientPurpose(purpose.getPurpose(), existingIngredient);
                existingIngredient.getPurposes().add(newPurpose);
            }

            // 기존의 특징 리스트 삭제
            existingIngredient.getFeatures().clear();
            // 새로운 특징 리스트 추가
            for (IngredientFeature feature : updatedIngredient.getFeatures()) {
                IngredientFeature newFeature = new IngredientFeature(feature.getFeature(), existingIngredient);
                existingIngredient.getFeatures().add(newFeature);
            }

            // 기존의 피부유형 특성 리스트 삭제
            existingIngredient.getSkinTypeFeatures().clear();
            // 새로운 피부유형 특성 리스트 추가
            for (SkinTypeFeature skinTypeFeature : updatedIngredient.getSkinTypeFeatures()) {
                SkinTypeFeature newSkinTypeFeature = new SkinTypeFeature();
                newSkinTypeFeature.setSkin_type(skinTypeFeature.getSkin_type());
                newSkinTypeFeature.setSkinDescription(skinTypeFeature.getSkinDescription());
                newSkinTypeFeature.setPositivity_status(skinTypeFeature.isPositivity_status());
                newSkinTypeFeature.setIngredient(existingIngredient);
                existingIngredient.getSkinTypeFeatures().add(newSkinTypeFeature);
            }

            // 엔티티 저장
            ingredientService.updateIngredient(existingIngredient);

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/api/admin/ingredient/delete/{ingredient_id}")
    public ResponseEntity deleteIngredient(@PathVariable Long ingredient_id) {
        ingredientService.deleteIngredientById(ingredient_id);

        return new ResponseEntity(HttpStatus.OK);
    }



}
