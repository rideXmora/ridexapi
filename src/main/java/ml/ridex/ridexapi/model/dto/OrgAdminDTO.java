package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.model.daoHelper.Payment;

import java.util.List;

@Data
public class OrgAdminDTO {
    private String id;

    private String name;

    private String phone;

    private String email;

    private String businessRegNo;

    private String basedCity;

    private String address;

    private List<Payment> payments;

    private Double totalIncome;

    private Boolean enabled;

    private Boolean suspend;
}
