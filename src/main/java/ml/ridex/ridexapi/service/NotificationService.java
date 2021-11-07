package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.enums.VehicleType;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.dto.NotificationRequestDTO;
import ml.ridex.ridexapi.model.dto.SubscriptionRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private static final String TOPIC_CAR = "rides-car";
    private static final String TOPIC_THREE_WHEELER = "rides-three-wheeler";
    private static final String TOPIC_VAN = "rides-van";
    private static final String TOPIC_BIKE = "rides-bike";
    private static final String TITLE = "New ride request";
    private static final String BODY = "Tap to view the request";

    @Autowired
    private FCMService fcmService;

    private String getTopic(VehicleType type) {
        switch (type) {
            case CAR:
                return TOPIC_CAR;
            case VAN:
                return TOPIC_VAN;
            case BIKE:
                return TOPIC_BIKE;
            case THREE_WHEELER:
                return TOPIC_THREE_WHEELER;
            default:
                return null;
        }
    }

    public void subscribeToRideTopic(String token, VehicleType type) throws InvalidOperationException {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO(getTopic(type), Arrays.asList(token));
        fcmService.subscribeToTopic(dto);
    }

    public void unsubscribeFromRideTopic(String token, VehicleType type) throws InvalidOperationException {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO(getTopic(type), Arrays.asList(token));
        fcmService.unsubscribeFromTopic(dto);
    }

    public void notifyDrivers(RideRequest rideRequest, List<String> driverTokens, VehicleType type) throws InvalidOperationException {
        Map<String, String> rideSummary = new HashMap<>();
        rideSummary.put("id", rideRequest.getId());
        rideSummary.put("passengerName", rideRequest.getPassenger().getName());
        rideSummary.put("passengerPhone", rideRequest.getPassenger().getPhone());
        rideSummary.put("passengerRating", rideRequest.getPassenger().getRating().toString());
        rideSummary.put("startLocationX", rideRequest.getStartLocation().getX().toString());
        rideSummary.put("startLocationY", rideRequest.getStartLocation().getY().toString());
        rideSummary.put("endLocationX", rideRequest.getEndLocation().getX().toString());
        rideSummary.put("endLocationY", rideRequest.getEndLocation().getY().toString());

        NotificationRequestDTO dto = new NotificationRequestDTO(getTopic(type), TITLE, BODY);
        fcmService.sendPnsToTopic(dto, rideSummary);
    }

    public void notifyPassenger(String token, String rideId, RideStatus status, String message) throws InvalidOperationException {
        NotificationRequestDTO dto = new NotificationRequestDTO(token, status.toString(), message);

        Map<String, String> rideDetails = new  HashMap<>();
        rideDetails.put("id", rideId);
        fcmService.sendPnsToDevice(dto, rideDetails);
    }
}
