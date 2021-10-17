package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.model.dto.WSMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/request")
    @SendTo("/ride/request")
    public WSMessageDTO broadcastRequest(WSMessageDTO message) {
        return message;
    }
}
