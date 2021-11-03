package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideStatus;
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

    private static final String TOPIC = "rides";
    private static final String TITLE = "New ride request";
    private static final String BODY = "Tap to view the request";

    @Autowired
    private FCMService fcmService;

    public void subscribeToRideTopic(String token) throws InvalidOperationException {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO(TOPIC, Arrays.asList(token));
        fcmService.subscribeToTopic(dto);
    }

    public void unsubscribeFromRideTopic(String token) throws InvalidOperationException {
        SubscriptionRequestDTO dto = new SubscriptionRequestDTO(TOPIC, Arrays.asList(token));
        fcmService.unsubscribeFromTopic(dto);
    }

    public void notifyDrivers(RideRequest rideRequest, List<String> driverTokens) throws InvalidOperationException {
        Map<String, String> rideSummary = new HashMap<>();
        rideSummary.put("id", rideRequest.getId());
        rideSummary.put("passengerName", rideRequest.getPassenger().getName());
        rideSummary.put("passengerPhone", rideRequest.getPassenger().getPhone());
        rideSummary.put("passengerRating", rideRequest.getPassenger().getRating().toString());
        rideSummary.put("startLocationX", rideRequest.getStartLocation().getX().toString());
        rideSummary.put("startLocationY", rideRequest.getStartLocation().getY().toString());

        NotificationRequestDTO dto = new NotificationRequestDTO(TOPIC, TITLE, BODY);
        fcmService.sendPnsToTopic(dto, rideSummary);
    }

    public void notifyPassenger(String token, RideStatus status, String message) throws InvalidOperationException {
        NotificationRequestDTO dto;
        switch (status) {
            case ACCEPTED:
                dto = new NotificationRequestDTO(token,"Accepted" ,message);
                break;
            case ARRIVED:
                dto = new NotificationRequestDTO(token,"Arrived" ,message);
                break;
            case DROPPED:
                dto = new NotificationRequestDTO(token,"Complete the payment" ,message);
                break;
            default:
                throw new InvalidOperationException("Invalid notification request");
        }
        fcmService.sendPnsToDevice(dto);
    }
}
