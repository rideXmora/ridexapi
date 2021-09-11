package ml.ridex.ridexapi.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PassengerVerifiedResDTO {
    private String id;

    private String phone;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private List<String> pastRides;

    private String token;

    private UUID refreshToken;

    private Boolean enabled;

    private Boolean suspend;
}
