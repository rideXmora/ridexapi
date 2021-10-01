package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.model.dao.RideRequest;

@Data
public class DriverRideDTO {
    private String id;

    private RideRequest rideRequest;

    private Double payment;

    private RideStatus rideStatus;

}
