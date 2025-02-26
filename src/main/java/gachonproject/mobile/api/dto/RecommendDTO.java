package gachonproject.mobile.api.dto;


import gachonproject.mobile.domain.em.Skintype;
import lombok.Data;

import java.util.List;

@Data
public class RecommendDTO {


    private Long id;
    private String name;
    private int compatibilityScore;
    private Skintype skintype;
    private List<String> skinTypeDescriptions;
    private List<String> keyIngredient;
    private String image;


    public RecommendDTO(Long id, int compatibilityScore, List<String> keyIngredient, String name, Skintype skintype, List<String> skinTypeDescriptions, String image) {
        this.id = id;
        this.compatibilityScore = compatibilityScore;
        this.keyIngredient = keyIngredient;
        this.name = name;
        this.skintype = skintype;
        this.skinTypeDescriptions = skinTypeDescriptions;
        this.image = image;
    }
}
