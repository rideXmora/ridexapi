package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.VehicleType;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestDriver;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class RideRequest {
    @Id
    private String id;

    private RideRequestPassenger passenger;

    private Location startLocation;

    private Location endLocation;

    private Integer distance;

    private VehicleType vehicleType;

    private RideRequestStatus status;

    private RideRequestDriver driver;

    private DriverOrganization organization;

    private long timestamp;

    public RideRequest(
            RideRequestPassenger passenger,
            Location startLocation,
            Location endLocation,
            Integer distance,
            VehicleType vehicleType,
            RideRequestStatus status,
            RideRequestDriver driver,
            DriverOrganization organization,
            long timestamp) {
        this.passenger = passenger;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distance = distance;
        this.vehicleType = vehicleType;
        this.status = status;
        this.driver = driver;
        this.organization = organization;
        this.timestamp = timestamp;
    }
}
