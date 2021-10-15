package ml.ridex.ridexapi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/request")
    @SendTo("/ride/request")
    public String broadcastRequest(String message) {
        return message;
    }
}
