package gachonproject.web.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_count")
    private Long id;

    private Long count;


    public UserCount(Long count) {
        this.count = count;
    }

    public UserCount() {
    }
}
