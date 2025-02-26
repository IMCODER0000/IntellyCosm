package gachonproject.mobile.domain.qna;


import com.fasterxml.jackson.annotation.JsonIgnore;
import gachonproject.mobile.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Qna {

    @Id
    @GeneratedValue
    @Column(name = "qna_id")
    private Long id;

    private String title;

    private String description;

    private boolean answer_status;

    private String answer;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    public void setMember(Member member){
        this.member = member;
    }

}
