package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import ml.ridex.ridexapi.enums.ComplainStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Complain {
    @Id
    private String id;

    private Ride ride;

    private String driverComplain;

    private String passengerComplain;

    private ComplainStatus complainStatus;

    public Complain(Ride ride, String driverComplain, String passengerComplain, ComplainStatus complainStatus) {
        this.ride = ride;
        this.driverComplain = driverComplain;
        this.passengerComplain = passengerComplain;
        this.complainStatus = complainStatus;
    }
}
