package gachonproject.mobile.api;

import gachonproject.mobile.service.CosmeticService;
import gachonproject.mobile.service.CosmeticService.CosmeticDetailData;
import gachonproject.mobile.service.CosmeticService.CosmeticListData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;




@RestController
@RequiredArgsConstructor
public class CosmeticApiController {

    private final CosmeticService cosmeticService;



    //화장품 리스트 조회
    @GetMapping("/api/user/cosmetic_list")
    public ResponseEntity<List<CosmeticListData>> getCosmeticList() {
        List<CosmeticListData> cosmeticList = cosmeticService.getCosmeticList();
        return new ResponseEntity<>(cosmeticList, HttpStatus.OK);
    }


    // 화장품 구매처 조회
    @GetMapping("/api/user/cosmetic_detail/{cosmeticId}")
    public ResponseEntity<CosmeticDetailData> getCosmeticDetail(@PathVariable Long cosmeticId) {
        CosmeticDetailData cosmeticDetail = cosmeticService.getCosmeticDetail(cosmeticId);
        if (cosmeticDetail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(cosmeticDetail, HttpStatus.OK);
        }
    }
}
