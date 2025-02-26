package gachonproject.mobile.domain.recommend;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Getter
@RequiredArgsConstructor
public class RecommendEvaluation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendEvaluation_id")
    private Long id;

    private LocalDate date;

    private int Evaluation;

    public RecommendEvaluation(LocalDate date, int evaluation) {
        this.date = date;
        Evaluation = evaluation;
    }
}
