package gachonproject.web.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HistoryContentDTO {


    private LocalDate start_date;
    private LocalDate end_date;

    private List<Float> average;

    private int dat_count;

    public HistoryContentDTO(List<Float> average, int dat_count, LocalDate start_date, LocalDate end_date) {
        this.average = average;
        this.dat_count = dat_count;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
