package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;

@Data
public class Payment {
    private VehicleType vehicleType;
    private Double ratePerMeter;
    private Double rateWaitingPerMin;
    private Double discount;
}
