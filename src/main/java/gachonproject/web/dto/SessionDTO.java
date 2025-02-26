package gachonproject.web.dto;


import lombok.Data;

@Data
public class SessionDTO {

    private String sessionId;
    private String session;

    public SessionDTO(String session, String sessionId) {
        this.session = session;
        this.sessionId = sessionId;
    }
}
