package gachonproject.mobile.api;


import gachonproject.mobile.domain.member.Member;
import gachonproject.mobile.domain.qna.Qna;
import gachonproject.mobile.repository.MemberRepository;
import gachonproject.mobile.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QnaApiController {

    private final QnaService qnaService;
    private final MemberRepository memberRepository;


    // 문의 등록
    @PostMapping("/api/user/qna/writing/{member_login_id}")
    public ResponseEntity registerQna(@PathVariable("member_login_id") String member_login_id, @RequestBody @Valid Qna qna){
        Member findMember = memberRepository.findByLogin_id(member_login_id);
        qna.setMember(findMember);
        try {
            qnaService.registerQna(qna);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }



    // 문의 내역 조회
    @GetMapping("/api/user/qna/list/{member_id}")
    public ResponseEntity<List<Qna>> getQnaList(@PathVariable("member_id") Long member_id){
        List<Qna> qnas = qnaService.getAllQnas(member_id);
        if(qnas.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(qnas, HttpStatus.OK);
        }
    }


    // 문의 상세
    @GetMapping("/api/user/{member_id}/qna/{qna_id}/detail")
    public ResponseEntity<Qna> getQnaDetail(@PathVariable("member_id") Long member_id,
                                            @PathVariable("qna_id") Long qna_id){
        Qna qna = qnaService.getQna(member_id, qna_id);
        if (qna != null){
            return new ResponseEntity<>(qna, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






}
