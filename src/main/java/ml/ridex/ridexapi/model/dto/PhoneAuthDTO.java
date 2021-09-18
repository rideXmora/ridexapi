package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@RequiredArgsConstructor
public class PhoneAuthDTO {
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;
}
