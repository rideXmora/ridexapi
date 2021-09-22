package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.model.dto.EmailResponseDTO;

import java.io.IOException;
import java.util.Map;

public interface EmailSender {
    public EmailResponseDTO sendTextEmail(String from, String to, String templateId, Map<String, String> dynamic_data) throws IOException;

    public EmailResponseDTO sendHTMLEmail(String from, String to, String templateId, Map<String, String> dynamic_data) throws IOException;
}
