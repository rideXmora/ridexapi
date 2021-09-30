package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import ml.ridex.ridexapi.enums.RideStatus;
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

    private Byte driverRating;

    private Byte passengerRating;

    private Double payment;

    private RideStatus rideStatus;

    public Ride(RideRequest rideRequest,
                String driverFeedback,
                String passengerFeedback,
                Byte driverRating,
                Byte passengerRating,
                Double payment,
                RideStatus rideStatus) {
        this.rideRequest = rideRequest;
        this.driverFeedback = driverFeedback;
        this.passengerFeedback = passengerFeedback;
        this.driverRating = driverRating;
        this.passengerRating = passengerRating;
        this.payment = payment;
        this.rideStatus = rideStatus;
    }
}
