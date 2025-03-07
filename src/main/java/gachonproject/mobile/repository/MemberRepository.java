package gachonproject.mobile.repository;


import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.member.SkinConcern;
import gachonproject.mobile.repository.dto.MemberSkinDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public boolean signup1(Member member) {
        try {
            em.persist(member);
            return true;
        } catch (Exception e) {
            throw e;

        }
    }


    public boolean signup2(SkinConcern skinConcern) {
        try {
            em.persist(skinConcern);
            return true;
        } catch (Exception e) {
            throw e;

        }
    }
    public void updateSkinConcern(SkinConcern skinConcern){
        em.merge(skinConcern);
    }

    public Member findByid(Long member_id) {
        try {
            return em.createQuery("SELECT m FROM Member m WHERE m.id = :member_id", Member.class)
                    .setParameter("member_id", member_id)
                    .getSingleResult();
        } catch (NoResultException e) {
            // 일치하는 결과가 없는 경우, 클라이언트에게 아이디가 잘못되었다는 메시지를 반환
            return null;
        }
    }



    public Member findByLogin_id(String login_id) {
        try {
            return em.createQuery("SELECT m FROM Member m WHERE m.login_id = :login_id", Member.class)
                    .setParameter("login_id", login_id)
                    .getSingleResult();
        } catch (NoResultException e) {
            // 일치하는 결과가 없는 경우, 클라이언트에게 아이디가 잘못되었다는 메시지를 반환
            return null;
        }
    }

    public List<SkinConcern> findSkinConcernByLogin_id(String login_id) {
        return em.createQuery("select s from SkinConcern s join s.member m where m.login_id = :login_id", SkinConcern.class)
                .setParameter("login_id", login_id)
                .getResultList();
    }



    public Member findByEmail(String email) {
        return em.createQuery("SELECT m FROM Member m WHERE m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }


    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public void delete(String login_id) {
        final Member member = findByLogin_id(login_id);
        em.remove(member);
    }


    public void update(Member member) {
        em.merge(member);

    }

    public void deleteSkinConcern(SkinConcern skinConcern) {
        em.remove(skinConcern);
    }


    public boolean existsByLoginId(String loginId) {
        try {
            Member findMember = em.createQuery("SELECT m FROM Member m WHERE m.login_id = :loginId", Member.class)
                    .setParameter("loginId", loginId)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }


    public MemberSkinDTO getMemberSkinDTO(Long member_id) {
        List<String> skinConcernNames = em.createQuery(
                        "SELECT sc.skin_concern FROM Member m JOIN m.skin_concern sc WHERE m.id = :member_id", String.class)
                .setParameter("member_id", member_id)
                .getResultList();

        Member member = em.find(Member.class, member_id);

        return new MemberSkinDTO(member.getAllergy(), member.getBirth(), member.getGender(),
                skinConcernNames, member.getSkin_type());
    }








}
