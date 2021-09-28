package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Ride {
    @Id
    private String id;

    private RideRequest rideRequest;

    private String driverFeedback;

    private String passengerFeedback;

    private Double payment;

    public Ride(RideRequest rideRequest, String driverFeedback, String passengerFeedback, Double payment) {
        this.rideRequest = rideRequest;
        this.driverFeedback = driverFeedback;
        this.passengerFeedback = passengerFeedback;
        this.payment = payment;
    }
}
