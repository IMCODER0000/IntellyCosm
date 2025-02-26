package gachonproject.mobile.domain.qna;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Answer {

    @Id
    @GeneratedValue
    @Column(name = "answer_id")
    private Long id;

    private String answerText;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qna_id")
    private Qna qna;

}
