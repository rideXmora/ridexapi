package ml.ridex.ridexapi.model.daoHelper;

import lombok.Data;

@Data
public class RideRequestDriver {
    private String id;
    private String phone;
    private RideRequestVehicle vehicle;
}
