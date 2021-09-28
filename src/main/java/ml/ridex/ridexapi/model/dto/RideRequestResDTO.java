package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestDriver;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;

@Data
public class RideRequestResDTO {
    private String id;

    private RideRequestPassenger passenger;

    private Location startLocation;

    private Location endLocation;

    private Integer distance;

    private RideRequestStatus status;

    private RideRequestDriver driver;

    private DriverOrganization organization;

    private long timestamp;
}
