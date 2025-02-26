package gachonproject.mobile.domain.cosmeticIngredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.cosmetic.Cosmetic;
import gachonproject.mobile.domain.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cosmetic_ingredient")
public class CosmeticIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cosmetic_ingredient_id", nullable = false, updatable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cosmetic_id")
    private Cosmetic cosmetic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}
