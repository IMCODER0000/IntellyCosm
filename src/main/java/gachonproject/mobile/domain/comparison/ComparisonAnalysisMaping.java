package gachonproject.mobile.domain.comparison;


import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.analysis.Analysis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ComparisonAnalysisMaping {

    @Id
    @GeneratedValue
    @Column(name = "comparisonAnalysisMaping_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "comparisonAnalysis_id")
    private ComparisonAnalysis comparisonAnalysis;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "analysis_id")
    private Analysis analysis;

    public ComparisonAnalysisMaping(Analysis analysis, ComparisonAnalysis comparisonAnalysis) {
        this.analysis = analysis;
        this.comparisonAnalysis = comparisonAnalysis;
    }

    public ComparisonAnalysisMaping() {
    }
}
