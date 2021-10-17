package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class RefreshTokenReqDTO {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String token;
}
