package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class OrgAdminRegDTO {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;
    @NotBlank
    private String businessRegNo;

    private String basedCity;
    @NotBlank
    private String address;
}
