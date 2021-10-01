package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.Payment;
import ml.ridex.ridexapi.model.dto.OrgAdminPaymentDTO;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrgAdminService {
    @Autowired
    private OrgAdminRepository orgAdminRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    public OrgAdmin getOrgAdmin(String phone) {
        Optional<OrgAdmin> orgAdmin = orgAdminRepository.findByPhone(phone);
        if(orgAdmin.isEmpty())
            throw new EntityNotFoundException("User not found");
        return orgAdmin.get();
    }

    public List<Driver> getRegisteredDrivers(String orgId) {
        return driverRepository.findByDriverOrganizationId(orgId, true);
    }

    public List<Driver> getUnregisteredDrivers(String orgId) {
        return driverRepository.findByDriverOrganizationId(orgId, false);
    }

    public Driver enableDriver(String phone, String orgId, boolean enabled) {
        Optional<Driver> driverOptional = driverRepository.findByPhoneAndDriverOrganizationId(phone, orgId);
        if(driverOptional.isEmpty())
            throw new EntityNotFoundException("Driver not found");
        Driver driver = driverOptional.get();
        driver.setEnabled(enabled);
        return driverRepository.save(driver);
    }

    public Payment setPayment(String phone, OrgAdminPaymentDTO data) throws EntityNotFoundException {
        OrgAdmin orgAdmin = getOrgAdmin(phone);
        Payment payment;
        if(orgAdmin.getPayment() == null) {
            payment = new Payment();
            payment.setDiscount(0.0);
        }
        else
            payment = orgAdmin.getPayment();

        if(data.getRatePerMeter() != null)
            payment.setRatePerMeter(data.getRatePerMeter());
        if(data.getRateWaitingPerMin() != null)
            payment.setRateWaitingPerMin(data.getRateWaitingPerMin());
        if(data.getDiscount() != null)
            payment.setDiscount(data.getDiscount());
        orgAdmin.setPayment(payment);
        orgAdminRepository.save(orgAdmin);
        return payment;
    }

    public List<Ride> getPastRides(String id) {
        return rideRepository.findByRideRequestOrganizationId(id);
    }
}
