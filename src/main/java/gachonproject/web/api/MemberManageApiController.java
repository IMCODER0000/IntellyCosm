package gachonproject.web.api;


import gachonproject.mobile.api.MemberApiController;
import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.service.MemberService;
import gachonproject.web.dto.AdminMemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class MemberManageApiController {


    @Autowired
    private final MemberService memberService;
    @Autowired
    private final MemberRepository memberRepository;




    //사옹자 관리


    //사용자 데이터 전달
    @GetMapping("/api/admin/member/info")
    public ResponseEntity<List<AdminMemberDTO>> info() {

        List<Member> all = memberService.findAll();

        List<AdminMemberDTO> Members = new ArrayList<>();
        for (Member member : all) {
            List<String> skinconcerns = new ArrayList<>();
            List<SkinConcern> skinConcern = member.getSkin_concern();
            for (SkinConcern concern : skinConcern) {
                String skinConcern1 = concern.getSkin_concern();
                skinconcerns.add(skinConcern1);
            }
            AdminMemberDTO members = new AdminMemberDTO(member.getAllergy(), member.getBirth(), member.getEmail(), member.getGender(),
                    member.getLogin_id(), member.getName(), member.getNickname(), member.getPassword(), skinconcerns, member.getSkin_type());
            Members.add(members);
        }


        return new ResponseEntity<>(Members, HttpStatus.OK);
    }


    // 개인정보수정
    @PutMapping("/api/admin/memeber/udate/{member_login_id}")
    public ResponseEntity updateProfile(@PathVariable("member_login_id") String member_login_id, @RequestBody @Valid AdminMemberDTO memberRequest) {



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
            exMember.setBirth(memberRequest.getBirth());
        }

        if (memberRequest.getGender() != null) {
            exMember.setGender(memberRequest.getGender());
        }

        if (memberRequest.getEmail() != null) {
            exMember.setEmail(memberRequest.getEmail());
        }

        if (memberRequest.getSkin_type() != null) {
            exMember.setSkin_type(memberRequest.getSkin_type());
        }
        if (memberRequest.getPassword() != null) {
            exMember.setPassword(memberRequest.getPassword());
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

    //사용자 데이터 수정
//    @PutMapping("/api/admin/memeber/udate")
//    public ResponseEntity updateMember(@RequestBody Member member) {
//
//
//        List<SkinConcern> skinConcern = member.getSkin_concern();
//        for (SkinConcern concern : skinConcern) {
//            int index = 0;
//            if(concern.getId()==null){
//                if(concern.getMember() == null){
//                    concern.setMember(member);
//                    memberService.createSkinConcern(concern);
//                }
//                else{
//                    concern.setMember(member);
//                    memberService.updateSkinConcern(concern);
//                }
//
//
//            }
//            else{
//                concern.setMember(member);
//                memberService.updateSkinConcern(concern);
//            }
//
//
//
//
//
//        }
//
//        System.out.println("냐옹");
//        boolean result = memberService.updateProfile(member);
//
//        if (result) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//        else{
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//
//    }

    //사용자 데이터 삭제
    @DeleteMapping("/api/admin/member/delete/{member_login_id}")
    public ResponseEntity deleteMember(@PathVariable("member_login_id") String member_login_id){
        boolean result = memberService.withdraw(member_login_id);

        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



}
