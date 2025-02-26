package gachonproject.mobile.domain.cosmetic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cosmetic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cosmetic_id")
    private Long id;

    private String name;

    private String image_path;

    private LocalDate latest_update_date;

    @OneToMany(mappedBy = "cosmetic", cascade = CascadeType.ALL)
    private List<CosmeticPurchaseLink> cosmeticPurchaseLinks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "cosmetic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CosmeticIngredient> cosmeticIngredients = new ArrayList<>();


//    public List<Ingredient> getIngredients() {
//        List<Ingredient> ingredients = new ArrayList<>();
//        for (CosmeticIngredient cosmeticIngredient : cosmeticIngredients) {
//            ingredients.add(cosmeticIngredient.getIngredient());
//        }
//        return ingredients;
//    }

    public int getLowestPrice() {
        int lowestPrice = Integer.MAX_VALUE;
        for (CosmeticPurchaseLink cosmeticPurchaseLink : cosmeticPurchaseLinks) {
            int price = Integer.parseInt(cosmeticPurchaseLink.getPrice());
            if (price < lowestPrice) {
                lowestPrice = price;
            }
        }
        return lowestPrice;
    }

    public Cosmetic(String name, String image_path, List<CosmeticIngredient> cosmeticIngredients) {
        this.name = name;
        this.image_path = image_path;
        this.cosmeticIngredients = cosmeticIngredients;
    }

    public Cosmetic(String name, String image_path, List<CosmeticPurchaseLink> cosmeticPurchaseLinks, List<CosmeticIngredient> cosmeticIngredients) {
        this.name = name;
        this.image_path = image_path;
        this.cosmeticPurchaseLinks = cosmeticPurchaseLinks;
        this.cosmeticIngredients = cosmeticIngredients;
    }

    public Cosmetic() {
    }
}
