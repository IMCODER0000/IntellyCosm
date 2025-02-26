package gachonproject.mobile.domain.ingredient;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class IngredientFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_feature_id")
    private Long id;

    private String Feature;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public IngredientFeature(Long id, String feature, Ingredient ingredient) {
        this.id = id;
        Feature = feature;
        this.ingredient = ingredient;
    }

    public IngredientFeature() {
    }

    public IngredientFeature(String feature) {
        Feature = feature;
    }

    public IngredientFeature(String feature, Ingredient ingredient) {
        Feature = feature;
        this.ingredient = ingredient;
    }
}
