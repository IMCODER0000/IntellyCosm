package gachonproject.mobile.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDTO {
    private Long memberId;
    private String imageUrl;
    private String requestType;
    private Long analysisId;
}
