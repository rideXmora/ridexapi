package ml.ridex.ridexapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@NonNull
public class PassengerRegistrationReqDTO {
    private String phone;

    private String email;

    private String name;

    public PassengerRegistrationReqDTO(String phone, String email, String name) {
        this.phone = phone;
        this.email = email;
        this.name = name;
    }
}
