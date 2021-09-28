package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;

@Data
@AllArgsConstructor
public class RideRequestVehicle {
    private String number;
    private VehicleType vehicleType;
    private String model;
}
