package ml.ridex.ridexapi.model.dao;

import lombok.Getter;
import lombok.Setter;
import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Driver {
    @Id
    private String id;
    @Indexed(unique = true)
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

    private DriverStatus driverStatus;

    private Boolean enabled;

    public Driver(
            String phone,
            String email,
            String name,
            String city,
            String drivingLicense,
            Integer totalIncome,
            Integer sessionIncome,
            Integer totalRating,
            Integer totalRides,
            Vehicle vehicle,
            DriverOrganization driverOrganization,
            String notificationToken,
            DriverStatus driverStatus,
            Boolean enabled) {
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.city = city;
        this.drivingLicense = drivingLicense;
        this.totalRating = totalRating;
        this.totalIncome = totalIncome;
        this.sessionIncome = sessionIncome;
        this.totalRides = totalRides;
        this.vehicle = vehicle;
        this.driverOrganization = driverOrganization;
        this.notificationToken = notificationToken;
        this.driverStatus = driverStatus;
        this.enabled = enabled;
    }
}
