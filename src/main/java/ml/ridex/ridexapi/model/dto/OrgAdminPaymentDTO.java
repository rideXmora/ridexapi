package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.VehicleType;

import javax.validation.constraints.NotNull;

@Data
public class OrgAdminPaymentDTO {
    @NotNull
    private VehicleType vehicleType;
    private Double ratePerMeter;
    private Double rateWaitingPerMin;
    private Double discount;
}
