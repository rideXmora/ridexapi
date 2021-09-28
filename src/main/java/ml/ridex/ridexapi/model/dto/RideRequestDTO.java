package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.model.daoHelper.Location;

@Data
public class RideRequestDTO {
    private Location startLocation;
    private Location endLocation;
    private Integer distance;
}
