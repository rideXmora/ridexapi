package ml.ridex.ridexapi.model.daoHelper;

import lombok.Data;

@Data
public class RideRequestPassenger {
    private String id;
    private String phone;
    private String name;
    private Float rating;
}
