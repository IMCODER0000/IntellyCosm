package gachonproject.web.dto;


import lombok.Data;

import java.util.List;

@Data
public class EvaluationDTO {


    private List<TotalModelDTO> totalModels;

    private List<AverageDTO> averageDTOS;

    public EvaluationDTO(List<AverageDTO> averageDTOS, List<TotalModelDTO> totalModels) {
        this.averageDTOS = averageDTOS;
        this.totalModels = totalModels;
    }
}
