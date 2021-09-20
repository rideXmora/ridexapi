package ml.ridex.ridexapi.model.dao;

import lombok.Getter;
import lombok.Setter;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
public class Driver {
    @Id
    private String id;
    @Indexed(unique = true)
    private String phone;

    private String refreshToken;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private List<String> pastRides;

    private Vehicle vehicle;

    private DriverOrganization driverOrganization;

    private Boolean enabled;

    private Boolean suspend;

    public Driver(String phone,
                     String refreshToken,
                     String email,
                     String name,
                     Integer totalRating,
                     Integer totalRides,
                     List<String> pastRides,
                     Vehicle vehicle,
                     DriverOrganization driverOrganization,
                     Boolean suspend,
                     Boolean enabled) {
        this.phone = phone;
        this.refreshToken = refreshToken;
        this.email = email;
        this.name = name;
        this.totalRating = totalRating;
        this.totalRides = totalRides;
        this.pastRides = pastRides;
        this.vehicle = vehicle;
        this.driverOrganization = driverOrganization;
        this.suspend = suspend;
        this.enabled = enabled;
    }
}
