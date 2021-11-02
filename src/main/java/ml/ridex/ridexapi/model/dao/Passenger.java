package ml.ridex.ridexapi.model.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Passenger {
    @Id
    private String id;
    @Indexed(unique = true)
    private String phone;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private String notificationToken;

    private Boolean enabled;

    public Passenger(String phone,
                     String email,
                     String name,
                     Integer totalRating,
                     Integer totalRides,
                     String notificationToken,
                     Boolean enabled) {
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.totalRating = totalRating;
        this.totalRides = totalRides;
        this.notificationToken = notificationToken;
        this.enabled = enabled;
    }
}
