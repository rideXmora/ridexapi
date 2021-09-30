package ml.ridex.ridexapi.model.daoHelper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideSummary {
    private String id;
    private String passenger;
    private String driver;
    private String organization;
    private Double distance;
    private Double payment;
}
