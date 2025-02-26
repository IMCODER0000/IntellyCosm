package gachonproject.mobile.domain.recommend;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class RecommendEvaluationAverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendevaluationaverage_id")
    private Long id;

    private LocalDate date;

    private float evaluationAverage;

}
