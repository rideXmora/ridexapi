package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionRequestDTO {

    private String topicName;

    private List<String> tokens;
}
