package ml.ridex.ridexapi.model.dto;

import lombok.Data;

@Data
public class OrgAdminLoginResDTO {
    private String id;

    private String name;

    private String email;

    private String phone;

    private String businessRegNo;

    private String basedCity;

    private String address;

    private String token;
}
