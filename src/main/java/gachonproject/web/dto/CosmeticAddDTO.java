package gachonproject.web.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
public class CosmeticAddDTO {


    private String cosmetic_name;
    private List<String> cosmetic_ingredient;
    private Map<String, List<String>> cosmetic_purchase;



}
