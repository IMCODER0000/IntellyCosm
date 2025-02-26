package gachonproject.mobile.api.dto;


import lombok.Data;

@Data
public class ComparisonDTO {


    private int ratio1;
    private int ratio2;
    private String total_ai_description;


    public ComparisonDTO(int ratio1, int ratio2, String total_ai_description) {
        this.ratio1 = ratio1;
        this.ratio2 = ratio2;
        this.total_ai_description = total_ai_description;
    }
}
