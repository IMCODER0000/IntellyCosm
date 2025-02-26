package gachonproject.mobile.domain.ingredient;


import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.em.Skintype;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SkinTypeFeature {

    @Id
    @GeneratedValue
    @Column(name = "skin_type_ingredient_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Skintype skin_type;

    private String skinDescription;

    private boolean positivity_status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;




}
