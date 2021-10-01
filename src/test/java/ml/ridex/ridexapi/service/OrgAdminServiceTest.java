package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.daoHelper.Payment;
import ml.ridex.ridexapi.model.dto.OrgAdminPaymentDTO;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgAdminServiceTest {
    OrgAdminService orgAdminService;
    @Mock
    private OrgAdminRepository orgAdminRepository;
    @Mock
    private DriverRepository driverRepository;

    String phone;
    OrgAdmin orgAdmin;
    Driver driver;
    List<Driver> drivers;

    @BeforeEach
    void setup() {
        orgAdminService = new OrgAdminService();
        phone = "+94714461798";
        orgAdmin = new OrgAdmin("ksr", phone,"ksr@gmail.com", "sadafd","Col","Address",null,true);
        driver = new Driver("94714461798", null, null,null ,null,0,0, 0,0, null, null, DriverStatus.OFFLINE,false);
        drivers = Arrays.asList(driver);
        ReflectionTestUtils.setField(orgAdminService, "orgAdminRepository", orgAdminRepository);
        ReflectionTestUtils.setField(orgAdminService, "driverRepository", driverRepository);
    }

    @Test
    void getOrgAdmin() {
        when(orgAdminRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(orgAdmin));
        assertThat(orgAdminService.getOrgAdmin(phone)).isNotNull();
    }

    @Test
    void getRegisteredDrivers() {
        when(driverRepository.findByDriverOrganizationId(anyString(), anyBoolean())).thenReturn(drivers);
        assertThat(orgAdminService.getRegisteredDrivers("OrgID")).asList();
    }

    @Test
    void getUnregisteredDrivers() {
        when(driverRepository.findByDriverOrganizationId(anyString(), anyBoolean())).thenReturn(drivers);
        assertThat(orgAdminService.getUnregisteredDrivers("OrgID")).asList();
    }

    @Test
    void enableDriver() {
        when(driverRepository.findByPhoneAndDriverOrganizationId(anyString(), anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertThat(orgAdminService.enableDriver(phone, "orgId", true)).isNotNull();
    }

    @Test
    @DisplayName("set payment for the first time")
    void setPayment() {
        when(orgAdminRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(orgAdmin));
        when(orgAdminRepository.save(any(OrgAdmin.class))).thenReturn(orgAdmin);
        OrgAdminPaymentDTO payment = new OrgAdminPaymentDTO();
        payment.setRatePerMeter(1000.0);
        payment.setRateWaitingPerMin(10.1);
        payment.setDiscount(12.0);

        Payment res = orgAdminService.setPayment(phone, payment);

        assertThat(res.getRatePerMeter()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("set payment for the first time")
    void setPaymentUpdate() {
        Payment paymentBefore = new Payment();
        paymentBefore.setRatePerMeter(1000.0);
        paymentBefore.setRateWaitingPerMin(10.1);

        OrgAdminPaymentDTO paymentData = new OrgAdminPaymentDTO();
        paymentData.setRatePerMeter(1000.0);
        paymentData.setRateWaitingPerMin(10.1);
        paymentData.setDiscount(12.0);

        Payment paymentAfter = new Payment();
        paymentAfter.setRatePerMeter(1000.0);
        paymentAfter.setRateWaitingPerMin(10.1);
        paymentAfter.setDiscount(12.0);

        orgAdmin.setPayment(paymentBefore);

        when(orgAdminRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(orgAdmin));
        when(orgAdminRepository.save(any(OrgAdmin.class))).thenReturn(orgAdmin);

        Payment res = orgAdminService.setPayment(phone, paymentData);

        assertThat(res.getDiscount()).isEqualTo(12.0);
    }
}