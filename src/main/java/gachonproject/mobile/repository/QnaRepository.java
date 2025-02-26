package gachonproject.mobile.repository;


import gachonproject.mobile.domain.qna.Answer;
import gachonproject.mobile.domain.qna.Qna;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QnaRepository {

    @PersistenceContext
    private EntityManager em;

    public void registerQna(Qna qna) {
        em.persist(qna);
    }

    public void responseQna(Answer answer) {
        em.persist(answer);
    }

    public void deleteQna(Long qna_id) {
        Qna qna = em.find(Qna.class, qna_id);
        em.remove(qna);
    }


    public List<Qna> All() {
        return em.createQuery("SELECT q FROM Qna q", Qna.class)
                .getResultList();
    }


    public List<Qna> findAll(Long member_id) {
        return em.createQuery("SELECT q FROM Qna q WHERE q.member.id = :member_id", Qna.class)
                .setParameter("member_id", member_id)
                .getResultList();
    }

    public Qna findQna(Long member_id, Long qna_id) {
        try {
            return em.createQuery("SELECT q FROM Qna q WHERE q.id = :qna_id AND q.member.id = :member_id", Qna.class)
                    .setParameter("qna_id", qna_id)
                    .setParameter("member_id", member_id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // 해당하는 Qna가 없을 경우 null
        }
    }

    public Qna findQnaById(Long qna_id) {
        return em.find(Qna.class, qna_id);
    }


    public List<Qna> noAnswerQnaAll() {
        return em.createQuery("SELECT q FROM Qna q WHERE q.answer_status = :isAnswer", Qna.class)
                .setParameter("isAnswer", Boolean.FALSE)
                .getResultList();
    }

    public void updateQna(Qna qna){
        em.merge(qna);
    }





}
