package gachonproject.mobile.api;

import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.service.MemberService;
import gachonproject.mobile.service.MemberService.MemberResponse;
import gachonproject.mobile.service.MemberService.MemberSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/user/signup")
    public ResponseEntity<?> memberSignup(@RequestBody @Valid MemberSignupRequest request) {
        MemberService.SignupResult result = memberService.processSignup(request);
        return result.isSuccess() 
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<MemberResponse> login(@RequestBody @Valid Member member) {
        MemberResponse response = memberService.login(member.getLogin_id(), member.getPassword());
        return response.getId() != null
            ? new ResponseEntity<>(response, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/user/find-id")
    public ResponseEntity<String> findId(@RequestBody @Valid Member member) {
        String findId = memberService.findId(member);
        return findId != null
            ? new ResponseEntity<>(findId, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/user/find_password")
    public ResponseEntity<String> findPassword(@RequestBody @Valid Member member) {
        String findPassword = memberService.findPassword(member);
        return findPassword != null
            ? new ResponseEntity<>(findPassword, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 개인정보수정
    @PutMapping("/api/user/update_profile/{member_login_id}")
    public ResponseEntity updateProfile(@PathVariable("member_login_id") String member_login_id, @RequestBody @Valid MemberSignupRequest memberRequest) {
        boolean result = memberService.updateProfile(member_login_id, memberRequest);
        return result
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //비밀번호수정
    @PutMapping("/api/user/update/password/{member_login_id}")
    public ResponseEntity<String> updatePassword(
            @PathVariable("member_login_id") String member_login_id,
            @RequestBody MemberService.UpdatePasswordRequest updatePasswordRequest) {
        boolean isSuccess = memberService.updatePassword(member_login_id, updatePasswordRequest);
        return isSuccess
            ? new ResponseEntity<>("비밀번호가 성공적으로 업데이트되었습니다.", HttpStatus.OK)
            : new ResponseEntity<>("비밀번호 업데이트에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //회원 탈퇴
    @DeleteMapping("/api/user/userDelete/{member_login_id}")
    public ResponseEntity deleteUser(@PathVariable("member_login_id") Long member_login_id) {
        boolean result = memberService.withdraw(member_login_id);
        return result
            ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //아이디 중복 확인
    @GetMapping("/api/user/existLoginId/{check_id}")
    public ResponseEntity existLoginId(@PathVariable("check_id") String check_id) {
        boolean result = memberService.exist(check_id);
        return result
            ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
            : new ResponseEntity<>(HttpStatus.OK);
    }
}
