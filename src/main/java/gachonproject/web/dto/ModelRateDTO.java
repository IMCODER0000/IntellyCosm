package gachonproject.web.dto;


import lombok.Data;

@Data
public class ModelRateDTO {

    private int oneRate;
    private int twoRate;
    private int threeRate;
    private int fourRate;
    private int fiveRate;

    public ModelRateDTO(int oneRate, int twoRate, int threeRate, int fourRate, int fiveRate) {
        this.oneRate = oneRate;
        this.twoRate = twoRate;
        this.threeRate = threeRate;
        this.fourRate = fourRate;
        this.fiveRate = fiveRate;
    }
}
