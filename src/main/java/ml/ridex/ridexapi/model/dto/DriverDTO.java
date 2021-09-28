package ml.ridex.ridexapi.model.dto;

import lombok.Data;
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

    private List<String> pastRides;

    private Vehicle vehicle;

    private DriverOrganization driverOrganization;

    private Boolean enabled;

}
