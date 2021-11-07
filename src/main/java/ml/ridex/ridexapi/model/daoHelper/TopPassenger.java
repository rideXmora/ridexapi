package ml.ridex.ridexapi.model.daoHelper;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TopPassenger {
    @Data
    private class Passenger {
        private String phone;
        private String name;
    }
    @Id
    private Passenger id;
    private Double total;
}
