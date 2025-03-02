package gachonproject.mobile.service;

import gachonproject.mobile.domain.em.Gender;
import gachonproject.mobile.domain.em.Skintype;
import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.repository.SkinConcernRepository;
import gachonproject.mobile.repository.dto.MemberSkinDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final SkinConcernRepository skinConcernRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public MemberService(MemberRepository memberRepository, 
                         SkinConcernRepository skinConcernRepository, 
                         PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.skinConcernRepository = skinConcernRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResult processSignup(MemberSignupRequest request) {
        validateSignupRequest(request);
        
        try {
            Member member = createMember(request);
            memberRepository.signup1(member);
            for (SkinConcern skinConcern : member.getSkin_concern()) {
                skinConcernRepository.save(skinConcern);
            }
            
            log.info("회원가입 성공: {}", member.getLogin_id());
            return new SignupResult(true);
        } catch (Exception e) {
            log.error("회원가입 실패: {}", request.getLogin_id(), e);
            return new SignupResult(false);
        }
    }

    private void validateSignupRequest(MemberSignupRequest request) {
        if (!StringUtils.hasText(request.getLogin_id())) {
            throw new IllegalArgumentException("아이디는 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        if (memberRepository.existsByLoginId(request.getLogin_id())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
    }

    private Member createMember(MemberSignupRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setLogin_id(request.getLogin_id());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setBirth(LocalDate.parse(request.getBirth()));
        member.setGender(Gender.valueOf(request.getGender()));
        member.setEmail(request.getEmail());
        member.setSkin_type(Skintype.valueOf(request.getSkin_type()));
        member.setAllergy(request.getAllergy());

        List<SkinConcern> skinConcernList = request.getSkin_concern().stream()
                .map(skinConcernName -> createSkinConcern(skinConcernName, member))
                .collect(Collectors.toList());
        member.setSkin_concern(skinConcernList);

        return member;
    }


    private SkinConcern createSkinConcern(String skinConcernName, Member member) {
        SkinConcern skinConcern = new SkinConcern();
        skinConcern.setSkin_concern(skinConcernName);
        skinConcern.setMember(member);
        return skinConcern;
    }

    @Transactional(readOnly = true)
    public MemberResponse login(String loginId, String password) {
        Optional<Member> memberOpt = memberRepository.findByLoginId(loginId);
        
        if (memberOpt.isEmpty()) {
            log.info("로그인 실패: 존재하지 않는 아이디 - {}", loginId);
            return null;
        }

        Member member = memberOpt.get();
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("로그인 실패: 비밀번호 불일치 - {}", loginId);
            return null;
        }

        log.info("로그인 성공: {}", loginId);
        return new MemberResponse(member);
    }

    @Transactional(readOnly = true)
    public List<SkinConcern> getSkinConcerns(String loginId) {
        return skinConcernRepository.findByMemberLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public String findId(Member member) {
        return memberRepository.findLoginIdByNameAndEmail(member.getName(), member.getEmail());
    }

    @Transactional(readOnly = true)
    public String findPassword(Member member) {
        return memberRepository.findPasswordByLoginIdAndNameAndEmail(
                member.getLogin_id(), member.getName(), member.getEmail());
    }

    @Transactional
    public boolean updateProfile(String loginId, MemberSignupRequest request) {
        try {
            Optional<Member> memberOpt = memberRepository.findByLoginId(loginId);
            if (memberOpt.isEmpty()) {
                return false;
            }

            Member member = memberOpt.get();
            updateMemberInfo(member, request);
            
            if (request.getSkin_concern() != null) {
                updateSkinConcerns(member, request.getSkin_concern());
            }

            log.info("프로필 업데이트 성공: {}", loginId);
            return true;
        } catch (Exception e) {
            log.error("프로필 업데이트 실패: {}", loginId, e);
            return false;
        }
    }

    @Transactional
    public boolean updateProfile(Member member) {
        try {
            memberRepository.update(member);
            log.info("회원 정보 업데이트 성공: {}", member.getLogin_id());
            return true;
        } catch (Exception e) {
            log.error("회원 정보 업데이트 실패: {}", member.getLogin_id(), e);
            return false;
        }
    }

    private void updateMemberInfo(Member member, MemberSignupRequest request) {
        if (StringUtils.hasText(request.getName())) {
            member.setName(request.getName());
        }
        if (StringUtils.hasText(request.getNickname())) {
            member.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getPassword())) {
            member.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (StringUtils.hasText(request.getBirth())) {
            member.setBirth(LocalDate.parse(request.getBirth()));
        }
        if (StringUtils.hasText(request.getGender())) {
            member.setGender(Gender.valueOf(request.getGender()));
        }
        if (StringUtils.hasText(request.getEmail())) {
            member.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getSkin_type())) {
            member.setSkin_type(Skintype.valueOf(request.getSkin_type()));
        }
        if (StringUtils.hasText(request.getAllergy())) {
            member.setAllergy(request.getAllergy());
        }
    }

    private void updateSkinConcerns(Member member, List<String> newSkinConcerns) {
        skinConcernRepository.deleteByMember(member);
        List<SkinConcern> skinConcernList = newSkinConcerns.stream()
                .map(name -> createSkinConcern(name, member))
                .collect(Collectors.toList());
        skinConcernRepository.saveAll(skinConcernList);
        member.setSkin_concern(skinConcernList);
    }

    @Transactional
    public boolean updatePassword(String loginId, UpdatePasswordRequest request) {
        try {
            Optional<Member> memberOpt = memberRepository.findByLoginId(loginId);
            if (memberOpt.isEmpty()) {
                return false;
            }

            Member member = memberOpt.get();
            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                return false;
            }

            member.setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("비밀번호 변경 성공: {}", loginId);
            return true;
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: {}", loginId, e);
            return false;
        }
    }

    @Transactional
    public boolean withdraw(Long memberId) {
        try {
            Member member = findById(memberId);
            if (member != null) {
                // 회원 관련 스킨 컨선 삭제
                skinConcernRepository.deleteByMember(member);
                // 회원 삭제
                memberRepository.delete(member.getLogin_id());
                log.info("회원 탈퇴 성공: {}", member.getLogin_id());
                return true;
            }
        } catch (Exception e) {
            log.error("회원 탈퇴 실패: {}", memberId, e);
            throw new RuntimeException("회원 탈퇴 처리 중 오류가 발생했습니다.", e);

        }
        return false;
    }

    public void deleteSkinConcern(String login_id) {
        Member member = findMemberByLogin_id(login_id);
        if (member != null) {
            skinConcernRepository.deleteByMember(member);
        }
    }

    @Transactional(readOnly = true)
    public boolean exist(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public MemberSkinDTO getMemberSkinDTO(Long memberId) {
        return memberRepository.getMemberSkinDTO(memberId);
    }

    @Transactional(readOnly = true)
    public Member findMemberByLogin_id(String loginId) {
        return memberRepository.findByLogin_id(loginId);
    }

    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public boolean signup2(List<SkinConcern> skinConcerns) {
        try {
            for (SkinConcern skinConcern : skinConcerns) {
                skinConcernRepository.save(skinConcern);
            }
            return true;
        } catch (Exception e) {
            log.error("스킨 컨선 저장 실패", e);
            return false;
        }
    }

    @Data
    public static class MemberSignupRequest {
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
    }

    @Data
    public static class SignupResult {
        private final boolean success;
    }

    @Data
    public static class UpdatePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }

    @Data
    public static class MemberResponse {
        private final Long id;
        private final String name;
        private final String nickname;
        private final String login_id;
        private final LocalDate birth;
        private final Gender gender;
        private final String email;
        private final Skintype skin_type;
        private final List<String> skin_concern;
        private final String allergy;

        public MemberResponse(Member member) {
            this.id = member.getId();
            this.name = member.getName();
            this.nickname = member.getNickname();
            this.login_id = member.getLogin_id();
            this.birth = member.getBirth();
            this.gender = member.getGender();
            this.email = member.getEmail();
            this.skin_type = member.getSkin_type();
            this.skin_concern = member.getSkin_concern().stream()
                    .map(SkinConcern::getSkin_concern)
                    .collect(Collectors.toList());
            this.allergy = member.getAllergy();
        }
    }
}
