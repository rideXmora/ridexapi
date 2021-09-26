package ml.ridex.ridexapi.model.dto;

import lombok.Data;

@Data
public class OrgAdminEnableDriver {
    private String phone;
    private Boolean enabled;
}
