package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AdminSuspendDTO {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;

    @NotNull
    private Boolean suspend;
}
