package gachonproject.mobile.domain.member;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SkinConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skinConcern_id")
    private Long id;

    private String skin_concern;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

//    @Column(insertable=false, updatable=false) // 추가
//    private Long member_id;


}
