package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class PassengerProfileComplete {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;
}
