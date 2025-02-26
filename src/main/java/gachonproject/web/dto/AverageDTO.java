package gachonproject.web.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class AverageDTO {

    private LocalDate date;

    private int evaluation;



}
