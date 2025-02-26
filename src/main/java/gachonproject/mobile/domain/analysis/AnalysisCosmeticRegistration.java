package gachonproject.mobile.domain.analysis;


import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class AnalysisCosmeticRegistration {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_cosmetic_registration_id")
    private Long id;

    private String cosmetic_name;

    private String image_path;

    private LocalDate date;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private int score;

    private Long analysis_id;

    public AnalysisCosmeticRegistration(String cosmetic_name, LocalDate date, String image_path, Member member, int score, Long analysis_id) {
        this.cosmetic_name = cosmetic_name;
        this.date = date;
        this.image_path = image_path;
        this.member = member;
        this.score = score;
        this.analysis_id = analysis_id;
    }

    public AnalysisCosmeticRegistration() {
    }
}
