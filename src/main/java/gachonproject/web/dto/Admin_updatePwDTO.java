package gachonproject.web.dto;


import lombok.Data;

@Data
public class Admin_updatePwDTO {
    private String email;
    private String password;
    private String code;
    private String new_password;


}
