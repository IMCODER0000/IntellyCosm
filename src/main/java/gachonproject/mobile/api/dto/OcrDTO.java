package gachonproject.mobile.api.dto;


import lombok.Data;

import java.util.List;

@Data
public class OcrDTO {

    private int code;
    private String cosmeticName;
    private List<String> result;


    public OcrDTO(int code, String cosmeticName, List<String> result) {
        this.code = code;
        this.cosmeticName = cosmeticName;
        this.result = result;
    }
}
