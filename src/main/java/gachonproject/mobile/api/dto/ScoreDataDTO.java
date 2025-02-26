package gachonproject.mobile.api.dto;


import lombok.Data;

@Data
public class ScoreDataDTO {

    private int score;
    private int type_posit;
    private int type_nega;
    private int AllergyCount;
    private int DangerCount;
    private int AttentionCount;


    public ScoreDataDTO(int score, int type_posit, int type_nega, int AllergyCount, int DangerCount, int AttentionCount) {
        this.score = score;
        this.type_posit = type_posit;
        this.type_nega = type_nega;
        this.AllergyCount = AllergyCount;
        this.DangerCount = DangerCount;
        this.AttentionCount = AttentionCount;
    }
}
