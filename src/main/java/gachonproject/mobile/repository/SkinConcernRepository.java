package gachonproject.mobile.repository;

import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkinConcernRepository extends JpaRepository<SkinConcern, Long> {
    List<SkinConcern> findByMemberId(Long memberId);

    @Query("SELECT sc FROM SkinConcern sc WHERE sc.member.login_id = :login_id")
    List<SkinConcern> findByMemberLoginId(@Param("login_id") String login_id);

    @Modifying
    @Query("DELETE FROM SkinConcern sc WHERE sc.member = :member")
    void deleteByMember(@Param("member") Member member);
}
