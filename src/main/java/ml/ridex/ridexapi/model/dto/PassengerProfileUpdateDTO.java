package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class PassengerProfileUpdateDTO {
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
