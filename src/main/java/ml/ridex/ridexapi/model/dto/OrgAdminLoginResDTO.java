package ml.ridex.ridexapi.model.dto;

import lombok.Data;
import ml.ridex.ridexapi.model.daoHelper.Payment;

import java.util.List;

@Data
public class OrgAdminLoginResDTO {
    private String id;

    private String name;

    private String email;

    private String phone;

    private String businessRegNo;

    private String basedCity;

    private String address;

    private String token;

    private List<Payment> payments;
}
