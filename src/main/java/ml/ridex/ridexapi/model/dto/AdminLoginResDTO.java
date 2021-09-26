package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.Role;

import java.util.List;

@Data
public class AdminLoginResDTO {
    private String id;

    private String phone;

    private List<Role> roles;

    private String token;
}
