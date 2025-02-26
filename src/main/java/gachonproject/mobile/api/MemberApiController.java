package gachonproject.mobile.api;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.member.SkinConcern;
import gachonproject.mobile.service.MemberService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class MemberApiController {


    private final MemberService memberService;




    // 회원가입
//    @PostMapping("/api/user/signup")
//    public ResponseEntity memberSignup(@RequestBody @Valid Member member) {
//
//
//        boolean signupResult = memberService.signup1(member);
//        if (signupResult == true) {
//            return new ResponseEntity(HttpStatus.OK);
//        } else {
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }


    // 회원가입
    @PostMapping("/api/user/signup")
    public ResponseEntity<?> memberSignup(@RequestBody @Valid memberDTO memberRequest) {
        Member member = new Member();
        member.setName(memberRequest.getName());
        member.setNickname(memberRequest.getNickname());
        member.setLogin_id(memberRequest.getLogin_id());
        member.setPassword(memberRequest.getPassword());
        member.setBirth(LocalDate.parse(memberRequest.getBirth()));
        member.setGender(Gender.valueOf(memberRequest.getGender()));
        member.setEmail(memberRequest.getEmail());
        member.setSkin_type(Skintype.valueOf(memberRequest.getSkin_type()));

        // SkinConcern 정보 설정
        List<SkinConcern> skinConcernList = new ArrayList<>();
        for (String skinConcernName : memberRequest.getSkin_concern()) {
            SkinConcern skinConcern = new SkinConcern();
            skinConcern.setSkin_concern(skinConcernName);
            skinConcern.setMember(member);
            skinConcernList.add(skinConcern);
        }
        member.setSkin_concern(skinConcernList);

        member.setAllergy(memberRequest.getAllergy());
        boolean signupResult = memberService.signup1(member);
        boolean skinconcernResult = memberService.signup2(skinConcernList);

        if (signupResult && skinconcernResult) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @Data
    public static class memberDTO {
        private String name;
        private String nickname;
        private String login_id;
        private String password;
        private String birth;
        private String gender;
        private String email;
        private String skin_type;
        private List<String> skin_concern;
        private String allergy;

        public memberDTO(String allergy, String birth, String email, String gender, String login_id, String name, String nickname, String password, List<String> skin_concern, String skin_type) {
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


    //로그인
    @PostMapping("/api/user/login")
    public ResponseEntity<MemberResponse> login(@RequestBody @Valid Member member) {
        List<SkinConcern> skinConcerns = memberService.getSkinConcerns(member.getLogin_id());
        List<String> skinConcernNames = skinConcerns.stream()
                .map(SkinConcern::getSkin_concern)
                .collect(Collectors.toList());

        MemberService.MemberResponse loginResult = memberService.login(member.getLogin_id(), member.getPassword());
        if (loginResult.getMember() != null) {
            SkinConcernDTO skinConcernDTO = new SkinConcernDTO(skinConcernNames); // SkinConcernDTO 객체 생성
            MemberResponse response = new MemberResponse(
                    loginResult.getMember().getId(),
                    loginResult.getMember().getName(),
                    loginResult.getMember().getNickname(),
                    loginResult.getMember().getLogin_id(),
                    loginResult.getMember().getBirth(),
                    loginResult.getMember().getGender(),
                    loginResult.getMember().getEmail(),
                    loginResult.getMember().getSkin_type(),
                    skinConcernNames,
                    loginResult.getMember().getAllergy()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Data
    static class MemberResponse {
        private Long id;
        private String name;
        private String nickname;
        private String login_id;
        private LocalDate birth;
        private Gender gender;
        private String email;
        private Skintype skin_type;
        private List<String> skin_concern;
        private String allergy;

        public MemberResponse(Long id, String name, String nickname, String login_id, LocalDate birth, Gender gender, String email, Skintype skin_type, List<String> skin_concern, String allergy) {
            this.id = id;
            this.name = name;
            this.nickname = nickname;
            this.login_id = login_id;
            this.birth = birth;
            this.gender = gender;
            this.email = email;
            this.skin_type = skin_type;
            this.skin_concern = skin_concern;
            this.allergy = allergy;
        }
    }
    @Data
    public class SkinConcernDTO {
        private List<String> skin_concern;

        public SkinConcernDTO(List<String> skin_concerns) {
            this.skin_concern = skin_concern;
        }
    }




    // 아이디찾기
    @PostMapping("/api/user/find-id")
    public ResponseEntity<String> findId(@RequestBody @Valid Member member) {
        String findId = memberService.findId(member);
        if (findId != null) {
            return new ResponseEntity<>(findId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // 비밀번호 찾기
    @PostMapping("/api/user/find_password")
    public ResponseEntity<String> findPassword(@RequestBody @Valid Member member) {

        String findPassword = memberService.findPassword(member);
        if (findPassword != null) {
            return new ResponseEntity<>(findPassword, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






    // 개인정보수정
    @PutMapping("/api/user/update_profile/{member_login_id}")
    public ResponseEntity updateProfile(@PathVariable("member_login_id") String member_login_id, @RequestBody @Valid memberDTO memberRequest) {



        Member exMember = memberService.findMemberByLogin_id(member_login_id);
        if (exMember == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (memberRequest.getName() != null) {
            exMember.setName(memberRequest.getName());
        }

        if (memberRequest.getNickname() != null) {
            exMember.setNickname(memberRequest.getNickname());
        }

        if (memberRequest.getLogin_id() != null) {
            exMember.setLogin_id(memberRequest.getLogin_id());
        }

        if (memberRequest.getPassword() != null) {
            exMember.setPassword(memberRequest.getPassword());
        }

        if (memberRequest.getBirth() != null) {
            exMember.setBirth(LocalDate.parse(memberRequest.getBirth()));
        }

        if (memberRequest.getGender() != null) {
            exMember.setGender(Gender.valueOf(memberRequest.getGender()));
        }

        if (memberRequest.getEmail() != null) {
            exMember.setEmail(memberRequest.getEmail());
        }

        if (memberRequest.getSkin_type() != null) {
            exMember.setSkin_type(Skintype.valueOf(memberRequest.getSkin_type()));
        }

        if (memberRequest.getSkin_concern() != null) {
            List<SkinConcern> skinConcernList = new ArrayList<>();
            for (String skinConcernName : memberRequest.getSkin_concern()) {
                SkinConcern skinConcern = new SkinConcern();
                skinConcern.setSkin_concern(skinConcernName);
                skinConcern.setMember(exMember);
                skinConcernList.add(skinConcern);
            }
            exMember.setSkin_concern(skinConcernList);
            memberService.deleteSkinConcern(member_login_id);
            boolean result = memberService.signup2(skinConcernList);
            if (result) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (memberRequest.getAllergy() != null) {
            exMember.setAllergy(memberRequest.getAllergy());
        }




        boolean result2 = memberService.updateProfile(exMember);
        if (result2) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); // 예기치 않은 오류 발생 시 500 에러 반환
        }
    }


    @Data
    public class UpdatePasswordRequest {

        private String currentPassword;
        private String newPassword;

        // 생성자, 게터, 세터
    }

    //비밀번호수정
    @PutMapping("/api/user/update/password/{member_login_id}")
    public ResponseEntity<String> updatePassword(
            @PathVariable("member_login_id") String member_login_id,
            @RequestBody UpdatePasswordRequestDTO  updatePasswordRequest) {



        String nowPassword = updatePasswordRequest.getNowpassword();
        String newPassword = updatePasswordRequest.getNewpassword();


        Member findMember = memberService.findMemberByLogin_id(member_login_id);



        if (!findMember.getPassword().equals(nowPassword)) {
            return new ResponseEntity<>("현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }


        findMember.setPassword(newPassword);
        boolean isSuccess = memberService.updateProfile(findMember);
        if (isSuccess) {
            return new ResponseEntity<>("비밀번호가 성공적으로 업데이트되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("비밀번호 업데이트에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Data
    @NoArgsConstructor
    public static class UpdatePasswordRequestDTO {
        private String nowpassword;
        private String newpassword;

        public UpdatePasswordRequestDTO(String nowpassword, String newpassword) {
            this.nowpassword = nowpassword;
            this.newpassword = newpassword;
        }
    }







    //회원 탈퇴
    @DeleteMapping("/api/user/userDelete/{member_login_id}")
    public ResponseEntity deleteUser(@PathVariable("member_login_id") String member_login_id) {
        boolean result = memberService.withdraw(member_login_id);
        if (result) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }


    //아이디 중복 확인
    @GetMapping("/api/user/existLoginId/{check_id}")
    public ResponseEntity existLoginId(@PathVariable("check_id") String check_id) {
        boolean result = memberService.exist(check_id);
        if (result) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }





}

