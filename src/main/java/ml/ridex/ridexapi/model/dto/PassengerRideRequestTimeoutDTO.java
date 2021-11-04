package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PassengerRideRequestTimeoutDTO {
    @NotBlank
    private String id;
}
