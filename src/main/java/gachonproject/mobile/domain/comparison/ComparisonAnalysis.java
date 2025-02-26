package gachonproject.mobile.domain.comparison;


import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ComparisonAnalysis {

    @Id
    @GeneratedValue
    @Column(name = "comparisonAnalysis_id")
    private Long id;


    @OneToMany(mappedBy = "comparisonAnalysis")
    private List<ComparisonAnalysisMaping> comparisonAnalysisMapings;

    @Column(columnDefinition = "LONGTEXT")
    private String total_ai_description;

    private int ratio0;

    private int ratio1;

    private int evaluation;


    private Long model_id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;







}
