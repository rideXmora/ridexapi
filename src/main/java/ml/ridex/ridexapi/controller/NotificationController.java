package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.model.dto.NotificationRequestDTO;
import ml.ridex.ridexapi.model.dto.SubscriptionRequestDTO;
import ml.ridex.ridexapi.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Deprecated
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private FCMService notificationService;

    @PostMapping("/subscribe")
    public void subscribeToTopic(@Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDto) {
        notificationService.subscribeToTopic(subscriptionRequestDto);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribeFromTopic(@Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDto) {
        notificationService.unsubscribeFromTopic(subscriptionRequestDto);
    }

    @PostMapping("/token")
    public String sendPnsToDevice(@Valid @RequestBody NotificationRequestDTO notificationRequestDto) {
        return notificationService.sendPnsToDevice(notificationRequestDto, null);
    }

    @PostMapping("/topic")
    public String sendPnsToTopic(@Valid @RequestBody NotificationRequestDTO notificationRequestDto) {
        return notificationService.sendPnsToTopic(notificationRequestDto, null);
    }
}
