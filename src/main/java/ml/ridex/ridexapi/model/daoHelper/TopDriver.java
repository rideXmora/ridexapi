package ml.ridex.ridexapi.model.daoHelper;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TopDriver {
    @Data
    private class Driver {
        private String phone;
        private String name;
    }
    @Id
    private Driver id;
    private Double total;
}
