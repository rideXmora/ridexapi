package ml.ridex.ridexapi.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PassengerRegistrationResponse {
    private String id;

    private String phone;

    private String token;

    private String email;

    private String name;

    private Integer totalRating;

    private Integer totalRides;

    private List<String> pastRides;

    private Boolean enabled;

    private Boolean suspend;
}
