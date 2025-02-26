package gachonproject.web.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;


import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
public class AnalysisActivity {


    @Id
    @GeneratedValue
    @Column(name = "analysis_activity_id")
    private Long id;


    private int Activity;

    private LocalDate date;

    public AnalysisActivity(int activity, LocalDate date) {
        Activity = activity;
        this.date = date;
    }

    public AnalysisActivity() {
    }
}
