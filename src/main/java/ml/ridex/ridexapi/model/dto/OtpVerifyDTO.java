package ml.ridex.ridexapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class OtpVerifyDTO {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;

    @NotBlank
    @Pattern(regexp = "(\\d{6})")
    private String otp;
}
