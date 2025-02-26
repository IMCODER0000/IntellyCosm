package gachonproject.mobile.service;


import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.qna.Qna;
import gachonproject.mobile.repository.QnaRepository;
import gachonproject.mobile.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QnaService {

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private MemberRepository memberRepository;

    //문의등록
    public void registerQna(Qna qna) {
        qnaRepository.registerQna(qna);
    }

    public void deleteQna(Long qna_id) {

        qnaRepository.deleteQna(qna_id);

    }


    @Transactional(readOnly = true)
    public List<Qna> AllQnas(){
        return qnaRepository.All();
    }

    //전체 문의 조회
    @Transactional(readOnly = true)
    public List<Qna> getAllQnas(Long member_id){
        return qnaRepository.findAll(member_id);
    }


    //선택 문의 조회
    @Transactional(readOnly = true)
    public Qna getQna(Long member_id, Long qna_id){
        Member findMember = memberRepository.findByid(member_id);
        return qnaRepository.findQna(findMember.getId(),qna_id);
    }

    @Transactional(readOnly = true)
    public List<Qna> noAnswerQnaAll(Long qna_id){
        List<Qna> qnas = qnaRepository.noAnswerQnaAll();
        return qnas;
    }

    @Transactional(readOnly = true)
    public int
    noAnswerQnaCount(){
        int count = 0;
        List<Qna> qnas = qnaRepository.noAnswerQnaAll();
        for (Qna qna : qnas) {
            count++;
        }
        return count;
    }

    public Qna findqnaById(Long qna_id){
        return qnaRepository.findQnaById(qna_id);
    }

    public void updateQna(Qna qna){
        qnaRepository.updateQna(qna);
    }




}
