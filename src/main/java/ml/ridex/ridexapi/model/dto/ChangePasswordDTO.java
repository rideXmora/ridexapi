package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordDTO {
    @NotBlank
    @Size(max = 20, min =4)
    private String oldPassword;
    @NotBlank
    @Size(max = 20, min =5)
    private String newPassword;
}
