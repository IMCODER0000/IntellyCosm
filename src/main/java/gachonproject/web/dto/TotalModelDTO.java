package gachonproject.web.dto;


import gachonproject.model.domain.OCRModel;
import gachonproject.model.domain.RecommendedModel;
import lombok.Data;

@Data
public class TotalModelDTO {

    private String model;

    private int five_rate;

    private int four_rate;

    private int three_rate;

    private int two_rate;

    private int one_rate;

    public TotalModelDTO(String model,int five_rate, int four_rate, int one_rate, int three_rate, int two_rate) {
        this.model = model;
        this.five_rate = five_rate;
        this.four_rate = four_rate;
        this.one_rate = one_rate;
        this.three_rate = three_rate;
        this.two_rate = two_rate;
    }
}
