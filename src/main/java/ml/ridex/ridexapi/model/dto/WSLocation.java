package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.model.daoHelper.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class WSLocation {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String senderPhone;

    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String receiverPhone;

    @NotNull
    private Location location;
}
