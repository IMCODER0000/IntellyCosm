package gachonproject.web.api;


import gachonproject.mobile.domain.qna.Answer;
import gachonproject.mobile.domain.qna.Qna;
import gachonproject.mobile.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminQnaApiController {

    @Autowired
    private QnaService qnaService;




    @GetMapping("/api/admin/qna/all")
    public ResponseEntity<List<Qna>> getAllQna() {
        List<Qna> qnas = qnaService.AllQnas();

        return new ResponseEntity<>(qnas, HttpStatus.OK);
    }

    @PutMapping("/api/admin/qna/answer/{qna_id}")
    public ResponseEntity answerQna(@PathVariable Long qna_id,
                                    @RequestBody Qna qna) {

        Qna findQna = qnaService.findqnaById(qna_id);
        if (findQna != null) {
            findQna.setAnswer(qna.getAnswer());
            System.out.println(findQna.getAnswer());
            findQna.setAnswer_status(!findQna.isAnswer_status());
            qnaService.updateQna(findQna);
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);



    }

    @PutMapping("/api/admin/qna/answer2/{qna_id}")
    public ResponseEntity answerQna2(@PathVariable Long qna_id,
                                    @RequestBody Qna qna) {

        Qna findQna = qnaService.findqnaById(qna_id);
        if (findQna != null) {
            findQna.setAnswer(qna.getAnswer());
            System.out.println(findQna.getAnswer());
            findQna.setAnswer_status(findQna.isAnswer_status());
            qnaService.updateQna(findQna);
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);



    }

    @DeleteMapping("/api/admin/qna/delete/{qna_id}")
    public ResponseEntity deleteQna(@PathVariable Long qna_id) {

        String an = "";
        Qna findQna = qnaService.findqnaById(qna_id);
        if (findQna != null) {
            findQna.setAnswer(an);
            System.out.println(findQna.getAnswer());
            findQna.setAnswer_status(!findQna.isAnswer_status());
            qnaService.updateQna(findQna);
        }
        else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(HttpStatus.OK);

    }








}
