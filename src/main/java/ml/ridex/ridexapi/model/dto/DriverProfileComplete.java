package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class DriverProfileComplete {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String city;

    @NotBlank
    private String drivingLicense;
}
