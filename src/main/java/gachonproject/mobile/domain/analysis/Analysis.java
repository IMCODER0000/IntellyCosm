package gachonproject.mobile.domain.analysis;


import gachonproject.mobile.domain.comparison.ComparisonAnalysis;
import gachonproject.mobile.domain.comparison.ComparisonAnalysisMaping;
import gachonproject.mobile.domain.em.AnalysisStatus;
import gachonproject.mobile.domain.ingredient.Ingredient;
import gachonproject.mobile.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    private String code;

    @Column(name = "cosmetic_name", length = 255)
    private String cosmetic_name;

    private String image_path;

    private LocalDate date;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private int score;

    private int evaluation;

    private Long model_id;

    private boolean registration_status;


    @OneToMany(mappedBy = "analysis")
    private List<ComparisonAnalysisMaping> comparisonAnalysisMapings;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL)
    private List<AnalysisIngredient> analysisIngredient = new ArrayList<>();


    public Analysis(String code, String cosmetic_name, String image_path, LocalDate date, String description,
                    int score, long model_id, boolean registration_status, List<ComparisonAnalysisMaping> comparisonAnalysisMapings,
                    Member member, List<AnalysisIngredient> analysisIngredient) {


        this.code = code;
        this.cosmetic_name = cosmetic_name;
        this.image_path = image_path;
        this.date = date;
        this.description = description;
        this.score = score;
        this.model_id = model_id;
        this.registration_status = registration_status;
        this.comparisonAnalysisMapings = comparisonAnalysisMapings;
        this.member = member;
        this.analysisIngredient = analysisIngredient;
    }

    public Analysis() {
    }

    public int countDanger(List<Ingredient> ingredients) {
        int count = 0;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isDanger_status()) {
                count++;
            }
        }
        return count;
    }

    public int countAllergy(List<Ingredient> ingredients) {
        int count = 0;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isAllergy_status()) {
                count++;
            }
        }
        return count;
    }




    public int final_grade(int score) {
        int a = 0;
        if(score <= 100 && score >= 80){
            a = 6;
        }
        else if(score <= 80 && score >= 60){
            a = 5;
        }
        else if(score <= 60 && score >= 40){
            a = 4;
        }
        else if(score <= 40 && score >= 30){
            a = 3;
        }
        else if(score <= 30 && score >= 20){
            a = 2;
        }
        else if(score <= 20 && score >= 10){
            a = 1;
        }
        return a;
    }


}
