package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.ComplainStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Complain;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.Payment;
import ml.ridex.ridexapi.model.dto.OrgAdminPaymentDTO;
import ml.ridex.ridexapi.repository.ComplainRepository;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private ComplainRepository complainRepository;

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

    public List<Payment> setPayment(String phone, List<OrgAdminPaymentDTO> data) throws EntityNotFoundException {
        OrgAdmin orgAdmin = getOrgAdmin(phone);
        List<Payment> payments;
        if(orgAdmin.getPayments() == null) {
            payments = new ArrayList<>();
            //payment.setDiscount(0.0);
        }
        else
            payments = orgAdmin.getPayments();

        for(OrgAdminPaymentDTO dto: data){
            Payment payment = payments.stream()
                    .filter(p -> p.getVehicleType().equals(dto.getVehicleType())).findAny().orElse(null);
            if(payment == null) {
                payment = new Payment();
                payment.setVehicleType(dto.getVehicleType());
                payment.setDiscount(0.0);
                payments.add(payment);
            }
            if(dto.getRatePerMeter() != null)
                payment.setRatePerMeter(dto.getRatePerMeter());
            if(dto.getRateWaitingPerMin() != null)
                payment.setRateWaitingPerMin(dto.getRateWaitingPerMin());
            if(dto.getDiscount() != null)
                payment.setDiscount(dto.getDiscount());
        }

        orgAdmin.setPayments(payments);
        orgAdminRepository.save(orgAdmin);
        return payments;
    }

    public List<Ride> getPastRides(String id) {
        return rideRepository.findByRideRequestOrganizationId(id);
    }

    public List<Ride> getPastRidesBetweenTimePeriod(String orgId, String driverPhone, long startEpoch, long endEpoch) {
        return rideRepository.findByRideRequestBetweenTimeInterval(orgId, driverPhone, RideStatus.CONFIRMED, startEpoch, endEpoch);
    }

    public List<Complain> getComplainList(String phone) throws EntityNotFoundException{
        OrgAdmin orgAdmin = this.getOrgAdmin(phone);
        return complainRepository.findByRideRideRequestOrganizationId(orgAdmin.getId());
    }

    public Complain changeComplainState(String id, String phone, ComplainStatus status) throws EntityNotFoundException {
        OrgAdmin orgAdmin = this.getOrgAdmin(phone);
        Optional<Complain> complainOptional = complainRepository.findByIdAndRideRideRequestOrganizationId(id, orgAdmin.getId());
        if(complainOptional.isEmpty())
            throw new EntityNotFoundException("Can't find the record");
        Complain complain = complainOptional.get();
        complain.setComplainStatus(status);
        return complainRepository.save(complain);
    }
}
