package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
public class PhoneAuthDTO {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;
}
