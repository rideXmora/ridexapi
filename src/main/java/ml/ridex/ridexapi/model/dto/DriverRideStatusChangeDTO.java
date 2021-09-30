package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.RideStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DriverRideStatusChangeDTO {
    @NotBlank
    private String id;
}
