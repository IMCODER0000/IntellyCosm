package gachonproject.web.dto;


import lombok.Data;

@Data
public class CrawlingResultDTO {

    private String cosmetic_image;
    private String purchase_name;
    private String purchase_logo_image;
    private String purchase_price;
    private String purchase_url;

    public CrawlingResultDTO(String purchase_price, String purchase_name, String purchase_logo_image, String cosmetic_image, String purchase_url) {
        this.purchase_price = purchase_price;
        this.purchase_name = purchase_name;
        this.purchase_logo_image = purchase_logo_image;
        this.cosmetic_image = cosmetic_image;
        this.purchase_url = purchase_url;
    }
}
