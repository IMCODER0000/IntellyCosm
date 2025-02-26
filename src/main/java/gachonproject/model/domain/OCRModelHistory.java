package gachonproject.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
public class OCRModelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocrModelHistory_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ocrModel_id")
    private OCRModel ocrModel;

    private LocalDate startDate;
    private LocalDate date;
    private LocalDate endDate;

    private float average;



}
