package gachonproject.web.api;


import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminIngredientApiController {


    @Autowired
    private final IngredientService ingredientService;


    @GetMapping("/api/admin/ingredient")
    public ResponseEntity AllIngredient(){

        List<Ingredient> allIngredients = ingredientService.getAllIngredients();


        return new ResponseEntity(allIngredients, HttpStatus.OK);
    }

    @PostMapping("/api/admin/ingredient/create")
    public ResponseEntity createIngredient(@RequestBody Ingredient ingredient){


        System.out.println(ingredient.getAnalysisIngredient());
        System.out.println(ingredient.getFeatures());
        System.out.println(ingredient.getName());
        System.out.println(ingredient.getCosmeticIngredients());
        System.out.println(ingredient.getPreferedIngredient());



        ingredientService.createIngredient(ingredient);


        return new ResponseEntity(HttpStatus.OK);

    }



}
