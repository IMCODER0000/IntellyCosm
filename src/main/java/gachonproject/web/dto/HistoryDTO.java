package gachonproject.web.dto;


import lombok.Data;

import java.util.List;

@Data
public class HistoryDTO {

    private String model;
    private HistoryContentDTO contents;

    public HistoryDTO(HistoryContentDTO contents, String model) {
        this.contents = contents;
        this.model = model;
    }
}
