package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.dto.WSLocation;
import ml.ridex.ridexapi.model.dto.WSMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
//    @Autowired
//    private ChatMessageService chatMessageService;
//    @Autowired
//    private ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload WSLocation wsLocation) {
        messagingTemplate.convertAndSendToUser(
                wsLocation.getReceiverPhone(),"/queue/messages",
                wsLocation);
    }
}
