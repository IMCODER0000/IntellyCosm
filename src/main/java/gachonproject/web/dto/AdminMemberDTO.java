package gachonproject.web.dto;


import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AdminMemberDTO {

    private String name;
    private String nickname;
    private String login_id;
    private String password;
    private LocalDate birth;
    private Gender gender;
    private String email;
    private Skintype skin_type;
    private List<String> skin_concern;
    private String allergy;

    public AdminMemberDTO(String allergy, LocalDate birth, String email, Gender gender, String login_id, String name, String nickname,
                          String password, List<String> skin_concern, Skintype skin_type) {
        this.allergy = allergy;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
        this.login_id = login_id;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.skin_concern = skin_concern;
        this.skin_type = skin_type;
    }
}
