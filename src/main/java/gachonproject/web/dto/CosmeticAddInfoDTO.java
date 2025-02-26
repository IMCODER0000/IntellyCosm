package gachonproject.web.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CosmeticAddInfoDTO {

    private Long id;
    private String cosmetic_name;
    private String image_path;
    private LocalDate date;
    private String login_id;
    private int score;

    public CosmeticAddInfoDTO(String cosmetic_name, LocalDate date, Long id, String image_path, String login_id, int score) {
        this.cosmetic_name = cosmetic_name;
        this.date = date;
        this.id = id;
        this.image_path = image_path;
        this.login_id = login_id;
        this.score = score;
    }
}
