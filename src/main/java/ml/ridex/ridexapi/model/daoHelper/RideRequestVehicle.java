package ml.ridex.ridexapi.model.daoHelper;

import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;

@Data
public class RideRequestVehicle {
    private String number;
    private VehicleType vehicleType;
    private String model;
}
