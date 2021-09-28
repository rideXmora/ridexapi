package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideRequestDriver {
    private String id;
    private String phone;
    private RideRequestVehicle vehicle;
    private Double rating;
}
