package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;

import java.util.List;

@Data
public class DriverDTO {
    private String id;

    private String phone;

    private String email;

    private String name;

    private String city;

    private String drivingLicense;

    private Integer totalIncome;

    private Integer sessionIncome;

    private Integer totalRating;

    private Integer totalRides;

    private Vehicle vehicle;

    private DriverOrganization driverOrganization;

    private String notificationToken;

    private DriverStatus status;

    private Boolean enabled;

    private Boolean suspend;

}
