package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmailResponseDTO {

    private int statusCode;

    private String body;

    private Map<String, String> headers;
}
