package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.ComplainStatus;

@Data
public class RideComplainResDTO {
    private String id;
    private String driverComplain;
    private String passengerComplain;
    private ComplainStatus complainStatus;
}
