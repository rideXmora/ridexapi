package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;
import ml.ridex.ridexapi.model.daoHelper.Location;

import javax.validation.constraints.NotNull;

@Data
public class RideRequestDTO {
    @NotNull
    private Location startLocation;
    @NotNull
    private Location endLocation;
    @NotNull
    private Integer distance;
    @NotNull
    private VehicleType vehicleType;
}
