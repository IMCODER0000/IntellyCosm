package gachonproject.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
public class RecommendedModelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendedmodelhistory_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recommendedModel_id")
    private RecommendedModel recommendedModel;

    private LocalDate startDate;
    private LocalDate date;
    private LocalDate endDate;

    private float average;

}
