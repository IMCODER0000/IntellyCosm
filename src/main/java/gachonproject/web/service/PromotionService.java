package gachonproject.web.service;


import gachonproject.web.domain.PromotionList;
import gachonproject.web.domain.PromotionMain;
import gachonproject.web.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;



    public void promotionAdd(PromotionMain promotionMain){


        promotionRepository.promotionAdd(promotionMain);

    }


    public void promotionDel(Long id){


        promotionRepository.promotionDel(id);
        promotionRepository.promotionListDel(id);


    }


    public void promotionListAdd(List<PromotionList> promotionLists){
        for (PromotionList promotionListss : promotionLists) {
            promotionRepository.promotionAdd2(promotionListss);
        }


    }
    public void promotionListDel(){


        promotionRepository.promotionDelAll();


    }

    public List<PromotionMain> getAllPromotionMain(){
        return promotionRepository.getAllPromotionMain();
    }

    public List<PromotionList> getAllPromotionList(){
        return promotionRepository.getAllPromotionList();
    }

    public List<PromotionMain> findAllPromotionMain(List<Long> ids){

        List<PromotionMain> promotionMains = new ArrayList<>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            PromotionMain allPromotionMain = promotionRepository.findAllPromotionMain(id);
            if (allPromotionMain != null) {
                promotionMains.add(allPromotionMain);
            }
        }
        return promotionMains;

    }


}
