package gachonproject.mobile.api.dto;


import gachonproject.mobile.api.AnalysisApiController;
import lombok.Data;

import java.util.List;

@Data
public class AnalysisDTO {

    private Long analysis_id;
    private String Ai_description;
    private int score;
    private int type_posit;
    private int type_nega;
    private int type_danger;
    private int AllArg_danger;
    private int danger;
    private List<IngredientDTO> ingredient;




    public AnalysisDTO(Long analysis_id, String Ai_description, int score, int type_posit, int type_nega, int type_danger, int AllArg_danger, int danger, List<IngredientDTO> ingredientDTO) {

        this.analysis_id = analysis_id;
        this.Ai_description = Ai_description;
        this.score = score;
        this.type_posit = type_posit;
        this.type_nega = type_nega;
        this.type_danger = type_danger;
        this.AllArg_danger = AllArg_danger;
        this.danger = danger;
        this.ingredient = ingredientDTO;

    }

}
