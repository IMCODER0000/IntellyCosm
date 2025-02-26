package gachonproject.web.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class CosmteticAddFinalDTO {

    private String cosmetic_name;
    private List<String> cosmetic_ingredient;
    private Map<String, List<String>> cosmetic_purchase;
    private MultipartFile file;


}
