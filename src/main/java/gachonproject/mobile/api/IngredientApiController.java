package gachonproject.mobile.api;


import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.ingredient.IngredientFeature;
import gachonproject.mobile.domain.ingredient.IngredientPurpose;
import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import gachonproject.mobile.service.IngredientService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class IngredientApiController {


    private final IngredientService ingredientService;

    // 전체 성분 리스트
    @GetMapping("/api/user/ingredient/list/{member_id}")
    public ResponseEntity<List<IngredientDTO>> getAllIngredients(@PathVariable("member_id") Long member_id) {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        if (ingredients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            List<IngredientDTO> collect = ingredients.stream()
                    .map(i -> new IngredientDTO(i, member_id))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(collect, HttpStatus.OK);
        }
    }



    @Data
    static class IngredientDTO{
        private String name;
        private int grade;
        private boolean preference;
        private List<String> purpose;
        private List<String> features;

        public IngredientDTO(Ingredient ingredient, Long member_id) {
            this.name = ingredient.getName();
            this.grade = ingredient.getGrade();
            // 선호하는 성분 리스트에서 성분 id만 추출하여 리스트로 만듦
            List<Long> preferedIngredientIds = ingredient.getPreferedIngredientsIds(member_id);
            // 선호하는 성분 리스트에 현재 성분의 id가 포함되어 있는지 확인
            this.preference = preferedIngredientIds.contains(ingredient.getId());
            this.purpose = new ArrayList<>();
            this.features = new ArrayList<>();

            for(IngredientPurpose purpose : ingredient.getPurposes()) {
                this.purpose.add(purpose.getPurpose());
            }
            for(IngredientFeature feature : ingredient.getFeatures()) {
                this.features.add(feature.getFeature());
            }


        }


        




    }





    // 나의 성분 리스트
    @GetMapping("/api/user/preference/ingredient/list/{member_id}")
    public ResponseEntity<List<IngredientDTO>> getIngredientsByPreference(@PathVariable("member_id") Long member_id) {
        List<Ingredient> prefferredIngredient = ingredientService.getPrefferredIngredient(member_id);
        if (prefferredIngredient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            List<IngredientDTO> collect = prefferredIngredient.stream()
                    .map(i -> new IngredientDTO(i, member_id))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(collect, HttpStatus.OK);
        }
    }


    // 선호성분 변경
    @PutMapping("/api/user/ingredient/preferenve/{member_id}")
    public ResponseEntity updatePreference(@PathVariable("member_id") Long member_id, @RequestBody @Valid List<String> preferences){
        boolean result = ingredientService.updatePrefferredIngredient(member_id, preferences);
        if (result){
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }














//
//    @GetMapping("/api/user/ingredient/list")
//    public ListResponse Ingredientlist() {
//        List<Ingredient> allIngredient = ingredientService.getAllIngredient();
//        if(allIngredient != null) {
//            return new ListResponse(200, allIngredient);
//        }
//        else {
//            return new ListResponse(404, null);
//        }
//
//    }
//
//    @Data
//    public class ListResponse {
//        private int resultcode;
//        private List<Ingredient> ingredients;
//
//
//        public ListResponse(int resultcode, List<Ingredient> ingredients) {
//            this.resultcode = resultcode;
//            this.ingredients = ingredients;
//        }
//    }



    @GetMapping("/qwer")
    public ResponseEntity A(){

        int count = 0;
        List<Ingredient> allIngredients = ingredientService.getAllIngredients();
        for (Ingredient allIngredient : allIngredients) {
            List<SkinTypeFeature> skinTypeFeatures = allIngredient.getSkinTypeFeatures();
            for (SkinTypeFeature skinTypeFeature : skinTypeFeatures) {
                if(skinTypeFeature.getId().equals(888)) {
                    boolean positivityStatus = skinTypeFeature.isPositivity_status();
                    if (positivityStatus) {
                        count++;
                    }
                    else{
                        count += 2;
                    }
                }
                else if (skinTypeFeature.getId().equals(9999)) {
                    count++;
                }
                else{
                    count += 2;
                }
                
                    System.out.println(skinTypeFeature.isPositivity_status());

            }
        }


        return new ResponseEntity(count,HttpStatus.OK);
    }








}
