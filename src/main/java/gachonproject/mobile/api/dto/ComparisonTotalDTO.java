package gachonproject.mobile.api.dto;


import lombok.Data;

@Data
public class ComparisonTotalDTO {


    private AnalysisDTO analysis1;
    private AnalysisDTO analysis2;
    private ComparisonDTO comparison;

    public ComparisonTotalDTO(AnalysisDTO analysis1, AnalysisDTO analysis2, ComparisonDTO comparison) {
        this.analysis1 = analysis1;
        this.analysis2 = analysis2;
        this.comparison = comparison;
    }
}
