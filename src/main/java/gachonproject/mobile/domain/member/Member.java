package gachonproject.mobile.domain.member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.analysis.Analysis;
import gachonproject.mobile.domain.analysis.AnalysisCosmeticRegistration;
import gachonproject.mobile.domain.comparison.ComparisonAnalysis;
import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.preferedIngredient.PreferedIngredient;
import gachonproject.mobile.domain.qna.Qna;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    private String login_id;

    private String password;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;  // [MALE, FEMALE]

    private String email;

    @Enumerated(EnumType.STRING)
    private Skintype skin_type;



    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<SkinConcern> skin_concern;

    private String allergy;

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Analysis> analysis = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Qna> qnaList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PreferedIngredient> preferedIngredient = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComparisonAnalysis> comparisonAnalyses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisCosmeticRegistration> analysisCosmeticRegistrations = new ArrayList<>();




    public Member updateFrom(Member member) {
        if (member.getName() != null) {
            this.name = member.getName();
        }
        if (member.getNickname() != null) {
            this.nickname = member.getNickname();
        }
        if (member.getGender() != null) {
            this.gender = member.getGender();
        }
        if (member.getSkin_type() != null) {
            this.skin_type = member.getSkin_type();
        }
        if (member.getSkin_concern() != null) {
            this.skin_concern = member.getSkin_concern();
        }
        if (member.getAllergy() != null) {
            this.allergy = member.getAllergy();
        }
        return this;
    }




}
