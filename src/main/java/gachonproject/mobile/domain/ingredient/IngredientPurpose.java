package gachonproject.mobile.domain.ingredient;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class IngredientPurpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_purpose_id")
    private Long id;

    private String purpose;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public IngredientPurpose(String purpose, Ingredient ingredient) {
        this.purpose = purpose;
        this.ingredient = ingredient;
    }

    public IngredientPurpose() {
    }

    public IngredientPurpose(String purpose) {
        this.purpose = purpose;
    }
}
