package gachonproject.mobile.service;


import gachonproject.mobile.api.em.LoginResponse;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.repository.dto.MemberSkinDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;


    //회원 가입
    @Transactional
    public boolean signup1(Member member) {
        try {
            for (SkinConcern skinConcern : member.getSkin_concern()) {
                skinConcern.setMember(member);
            }
            memberRepository.signup1(member);
            return true;
        } catch (Exception e) {

            return false;
        }
    }


    // 피부 고민 등록
    @Transactional
    public boolean signup2(List<SkinConcern> skinConcerns) {
        try {
            for (SkinConcern skinConcern : skinConcerns){
                memberRepository.signup2(skinConcern);
            }
            return true;
        } catch (Exception e) {

            return false;
        }
    }
    @Transactional
    public void createSkinConcern(SkinConcern skinConcern){
        memberRepository.signup2(skinConcern);
    }

    @Transactional
    public void updateSkinConcern(SkinConcern skinConcern){
        memberRepository.updateSkinConcern(skinConcern);
    }


    // 피부 타입 가져오기
    public List<SkinConcern> getSkinConcerns(String member_login_id) {
        List<SkinConcern> skinConcerns = memberRepository.findSkinConcernByLogin_id(member_login_id);
        return skinConcerns;
    }

    // 피부타입 변경
    public void deleteSkinConcern(String member_login_id) {
        List<SkinConcern> skinConcerns = memberRepository.findSkinConcernByLogin_id(member_login_id);
        for (SkinConcern skinConcern : skinConcerns) {
            memberRepository.deleteSkinConcern(skinConcern);
        }


    }





    //로그인
    public MemberResponse login(String login_id, String password){
        Member findMember = memberRepository.findByLogin_id(login_id);
        if(findMember != null){
            if(findMember.getPassword().equals(password)){
                return new MemberResponse(LoginResponse.SUCCESS, findMember);
            }
            else{
                return new MemberResponse(LoginResponse.INCORRECT_PASSWORD, null);

            }

        }
        else{
            return new MemberResponse(LoginResponse.INCORRECT_LOGI_ID, null);

        }


    }




    //아이디 찾기
    public String findId(Member member) {

        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember == null) {
            return null;
        }
        else {
            if (findMember.getName().equals(member.getName())) {
                return findMember.getLogin_id();
            }
            else {
                return null;
            }
        }
    }

    @Getter
    public class MemberResponse {
        private Member member;
        private LoginResponse loginResponse;

        public MemberResponse(LoginResponse loginResponse, Member member) {
            this.loginResponse = loginResponse;
            this.member = member;
        }
    }


    //비밀번호 찾기
    public String findPassword(Member member) {
        Member findMember = memberRepository.findByLogin_id(member.getLogin_id());
        if (findMember == null) {
            return null;
        } else {
            if (findMember.getName().equals(member.getName()) || findMember.getEmail().equals(member.getEmail())) {
                return findMember.getPassword();
            } else {
                return null;
            }
        }
    }


    // 개인정보수정
    @Transactional
    public boolean updateProfile(Member member){
        memberRepository.update(member);
        return true;
    }

    public Member findMemberByLogin_id(String login_id) {
        return memberRepository.findByLogin_id(login_id);
    }







    //회원 탈퇴
    @Transactional
    public boolean withdraw(String member_login_id) {
        try {
            memberRepository.delete(member_login_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //    public void deleteMember(String login_id){
//        memberRepository.delete(login_id);
//    }





    //아이디 중복 확인
    public boolean exist(String login_id) {
        return memberRepository.existsByLoginId(login_id);
    }


    //모든 회원
    public List<Member> findAll(){
        List<Member> all = memberRepository.findAll();
        return all;
    }

    //회원 피부 정보 가져오기
    public MemberSkinDTO getMemberSkinDTO(Long member_id){
        return memberRepository.getMemberSkinDTO(member_id);
    }



    //id로 멤버 찾기
    public Member findMemberById(Long member_id){
        return memberRepository.findByid(member_id);

    }






}
