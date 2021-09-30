package ml.ridex.ridexapi.model.dto;

import lombok.Data;

@Data
public class OrgAdminPaymentDTO {
    private Double ratePerMeter;
    private Double rateWaitingPerMin;
    private Double discount;
}
