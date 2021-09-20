package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;

@Data
@AllArgsConstructor
public class Vehicle {
    private String number;
    private VehicleType vehicleType;
    private String license;
    private String insurance;
}
