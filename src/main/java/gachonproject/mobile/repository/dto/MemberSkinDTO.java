package gachonproject.mobile.repository.dto;


import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MemberSkinDTO {

    private Skintype skin_type;
    private List<String> skin_concern;
    private Gender gender;
    private LocalDate birth;
    private String allergy;

    public MemberSkinDTO(String allergy, LocalDate birth, Gender gender, List<String> skin_concern, Skintype skin_type) {
        this.allergy = allergy;
        this.birth = birth;
        this.gender = gender;
        this.skin_concern = skin_concern;
        this.skin_type = skin_type;
    }
    @Override
    public String toString() {
        return
                "회원의 피부 타입은" + skin_type +
                        ", 피부 고민은 " + skin_concern+
                        ", 성별" + gender +
                        ", 생년월일은" + birth +
                        ", 알레르기는'" + allergy + '\'';
    }
//    @Override
//    public String toString() {
//        return
//                "회원의 피부 타입은" + this.skin_type +
//                        ", 피부 고민은 " + this.skin_concern+
//                        ", 성별" + this.gender +
//                        ", 생년월일은" + this.birth +
//                        ", 알레르기는'" + this.allergy + '\'';
//    }
}
