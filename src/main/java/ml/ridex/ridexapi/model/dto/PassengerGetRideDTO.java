package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PassengerGetRideDTO {
    @NotBlank
    private String id;
}
