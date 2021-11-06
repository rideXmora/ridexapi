package ml.ridex.ridexapi.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class OrgAdminPastRidesDTO {
    @NotBlank
    @Pattern(regexp="(\\+94\\d{9})")
    private String phone;
    @NotNull
    private long startEpoch;
    @NotNull
    private long endEpoch;
}
