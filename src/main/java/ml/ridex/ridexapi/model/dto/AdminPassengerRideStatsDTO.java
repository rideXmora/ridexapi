package ml.ridex.ridexapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPassengerRideStatsDTO {
    private Integer count;
    private Double paymentSum;
}
