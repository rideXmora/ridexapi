package ml.ridex.ridexapi.model.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
public class Passenger {
    @Id
    private String id;
    @Indexed(unique = true)
    private String phone;

    private String token;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private List<String> pastRides;

    private Boolean enabled;

    private Boolean suspend;

    private String otp;

    public Passenger(String phone,
                     String token,
                     String email,
                     String name,
                     Integer totalRating,
                     Integer totalRides,
                     List<String> pastRides,
                     Boolean suspend,
                     Boolean enabled,
                     String otp) {
        this.phone = phone;
        this.token = token;
        this.email = email;
        this.name = name;
        this.totalRating = totalRating;
        this.totalRides = totalRides;
        this.pastRides = pastRides;
        this.suspend = suspend;
        this.enabled = enabled;
        this.otp = otp;
    }
}
