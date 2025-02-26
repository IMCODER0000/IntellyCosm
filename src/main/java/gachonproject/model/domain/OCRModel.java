package gachonproject.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
public class OCRModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocrModel_id")
    private Long id;

    private String version;

    private float learning_rate;

    private float batch_size;

    private float epoch;

    private LocalDate date;

    private int five_rate = 0;
    private int four_rate = 0;
    private int three_rate = 0;
    private int two_rate = 0;
    private int one_rate = 0;
    private boolean latest = true;

    @JsonIgnore
    @OneToMany(mappedBy = "ocrModel", cascade = CascadeType.ALL)
    private List<OCRModelHistory> ocrModelHistory = new ArrayList<>();






}
