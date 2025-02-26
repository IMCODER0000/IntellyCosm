package gachonproject.mobile.domain.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_ingredient_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    public AnalysisIngredient(Ingredient ingredient, Analysis analysis) {
        this.ingredient = ingredient;
        this.analysis = analysis;
    }
}
