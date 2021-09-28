package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PassengerVerifiedResDTO {
    private String id;

    private String phone;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private List<String> pastRides;

    private String token;

    private String refreshToken;

    private Boolean enabled;
}
