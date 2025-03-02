package gachonproject.mobile.domain.ingredient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.analysis.AnalysisIngredient;
import gachonproject.mobile.domain.cosmeticIngredient.CosmeticIngredient;
import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    private String name;

    private int grade;

//    private String purpose;
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientPurpose> purposes = new ArrayList<>();

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientFeature> Features = new ArrayList<>();

    private boolean danger_status;

    private boolean allergy_status;

    @JsonIgnore
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CosmeticIngredient> cosmeticIngredients = new ArrayList<>();


    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkinTypeFeature> skinTypeFeatures = new ArrayList<>();



    @JsonIgnore
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisIngredient> analysisIngredient = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreferedIngredient> preferedIngredient =  new ArrayList<>();

    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getPreferedIngredientsIds(Long memberId) {
        List<Long> preferedIngredients = new ArrayList<>();
        for (PreferedIngredient preferedIngredient : preferedIngredient) {
            if (preferedIngredient.getMember().getId().equals(memberId)) {
                preferedIngredients.add(preferedIngredient.getIngredient().getId());
            }
        }
        return preferedIngredients;
    }
}
