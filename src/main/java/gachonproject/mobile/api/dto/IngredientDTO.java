package gachonproject.mobile.api.dto;

import gachonproject.mobile.domain.ingredient.SkinTypeFeature;
import lombok.Data;

import java.util.List;

@Data
public class IngredientDTO {

    private Long id;
    private String name;
    private int grade;
    private boolean preference;
    private List<String> purpose;
    private List<SkinTypeFeature> skin_type;
    private List<String> features;
    private String description;

    public IngredientDTO(String name, int grade, boolean preference, List<String> purposeDTO, List<SkinTypeFeature> skin_typeDTO, List<String> featuresDTO) {
        this.name = name;
        this.grade = grade;
        this.preference = preference;
        this.purpose = purposeDTO;
        this.skin_type = skin_typeDTO;
        this.features = featuresDTO;
    }

    public IngredientDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
