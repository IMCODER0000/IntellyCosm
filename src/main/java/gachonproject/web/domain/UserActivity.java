package gachonproject.web.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class UserActivity {



    @Id
    @GeneratedValue
    @Column(name = "userActivity_id")
    private Long id;

    private int activity;

    private LocalDate date;


    public UserActivity(int activity, LocalDate date) {
        this.activity = activity;
        this.date = date;
    }

    public UserActivity() {

    }
}
