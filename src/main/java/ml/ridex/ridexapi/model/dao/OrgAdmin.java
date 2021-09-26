package ml.ridex.ridexapi.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class OrgAdmin {
    @Id
    private String id;

    private String name;

    private String phone;

    private String email;

    private String businessRegNo;

    private String basedCity;

    private String address;

    private Boolean suspend;

    private Boolean enabled;

    public OrgAdmin(
            String name,
            String phone,
            String email,
            String businessRegNo,
            String basedCity,
            String address,
            Boolean suspend,
            Boolean enabled) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.businessRegNo = businessRegNo;
        this.basedCity = basedCity;
        this.address = address;
        this.suspend = suspend;
        this.enabled = enabled;
    }
}
