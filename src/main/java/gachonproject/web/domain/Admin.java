package gachonproject.web.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "admin_id")
    private Long id;

    private String name;
    private String email;
    private String password;
    private String code;

    private Long ocrModel;
    private String ocrModelVersion;

    private Long recommendedModel;
    private String recommendedModelVersion;


}
