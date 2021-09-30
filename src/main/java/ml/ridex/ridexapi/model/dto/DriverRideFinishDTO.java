package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class DriverRideFinishDTO {
    @NotBlank
    private String id;

    private String driverFeedback;
    @Min(0)
    @Max(5)
    private Byte passengerRating;
}
