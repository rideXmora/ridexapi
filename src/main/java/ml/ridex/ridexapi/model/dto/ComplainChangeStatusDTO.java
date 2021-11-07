package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.enums.ComplainStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ComplainChangeStatusDTO {
    @NotBlank
    private String id;
    @NotNull
    private ComplainStatus complainStatus;
}
