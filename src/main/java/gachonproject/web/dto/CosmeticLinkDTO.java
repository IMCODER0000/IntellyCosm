package gachonproject.web.dto;


import gachonproject.mobile.domain.cosmetic.Cosmetic;
import lombok.Data;

import java.util.List;

@Data
public class CosmeticLinkDTO {


    private Long p_id;
    private String purchaseSite;
    private String price;
    private String url;


}
