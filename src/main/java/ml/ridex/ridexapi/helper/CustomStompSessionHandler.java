package ml.ridex.ridexapi.helper;

import ml.ridex.ridexapi.model.dto.WSMessageDTO;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

public class CustomStompSessionHandler implements StompSessionHandler {
    @Override
    public void afterConnected( StompSession session, StompHeaders connectedHeaders) {
        WSMessageDTO data = new WSMessageDTO();
        data.setMessage("hola");
        session.subscribe("/ride/request", this);
        session.send("/app/request", data);
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {

    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {

    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        WSMessageDTO msg = (WSMessageDTO) payload;
        //logger.info("Received : " + msg.getText()+ " from : " + msg.getFrom());
    }
}
