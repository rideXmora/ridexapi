package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DriverProfileComplete {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String city;

    @NotNull
    private DriverOrganization driverOrganization;
}
