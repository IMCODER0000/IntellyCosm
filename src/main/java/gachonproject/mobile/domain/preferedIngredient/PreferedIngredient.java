package gachonproject.mobile.domain.preferedIngredient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class PreferedIngredient {


    @Id
    @GeneratedValue
    @Column(name = "prefferrend_ingredient_id")
    private Long id;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;







}
