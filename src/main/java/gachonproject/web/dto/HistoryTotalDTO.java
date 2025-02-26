package gachonproject.web.dto;


import lombok.Data;

import java.util.List;

@Data
public class HistoryTotalDTO {

    private List<TotalModelDTO> rate;
    private List<HistoryDTO> average;

    public HistoryTotalDTO(List<HistoryDTO> average, List<TotalModelDTO> rate) {
        this.average = average;
        this.rate = rate;
    }
}
