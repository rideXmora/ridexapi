package ml.ridex.ridexapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PassengerVerifyDTO {
    private String phone;

    private String otp;

}
