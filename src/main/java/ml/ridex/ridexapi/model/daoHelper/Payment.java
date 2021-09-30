package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Payment {
    private Double ratePerMeter;
    private Double rateWaitingPerMin;
    private Double discount;
}
