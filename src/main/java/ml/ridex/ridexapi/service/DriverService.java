package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.RideRequestDriver;
import ml.ridex.ridexapi.model.daoHelper.RideRequestVehicle;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RideRepository rideRepository;

    public Driver getDriver(String phone) {
        Optional<Driver> driverOptional = driverRepository.findByPhone(phone);
        if(driverOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        return driverOptional.get();
    }

    public Driver profileComplete(
            String phone,
            String name,
            String email,
            String city,
            DriverOrganization driverOrganization) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        driver.setEmail(email);
        driver.setName(name);
        driver.setCity(city);
        driver.setDriverOrganization(driverOrganization);
        return driverRepository.save(driver);
    }

    public Driver addVehicle(String phone, Vehicle vehicle) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        driver.setVehicle(vehicle);
        return driverRepository.save(driver);
    }

    public Driver profileUpdate(String phone, String city) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        driver.setCity(city);
        return driverRepository.save(driver);
    }

    public Ride acceptRideRequest(String phone, String id) throws EntityNotFoundException {
        Optional<RideRequest> rideRequestOptional = rideRequestRepository.findById(id);
        if(rideRequestOptional.isEmpty())
            throw new EntityNotFoundException("Invalid id");
        Driver driver = getDriver(phone);
        Vehicle vehicle = driver.getVehicle();
        int totalRides = driver.getTotalRides();
        if(totalRides == 0)
            totalRides = 1;
        RideRequestVehicle rideRequestVehicle = new RideRequestVehicle(
                vehicle.getNumber(),
                vehicle.getVehicleType(),
                vehicle.getModel());
        RideRequestDriver rideRequestDriver = new RideRequestDriver(
                driver.getId(),
                driver.getPhone(),
                rideRequestVehicle,
                (double) driver.getTotalRating()/totalRides);
        RideRequest rideRequest = rideRequestOptional.get();
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setOrganization(driver.getDriverOrganization());
        rideRequest.setDriver(rideRequestDriver);
        rideRequestRepository.save(rideRequest);
        // Payment cal method
        Ride ride = new Ride(rideRequest, null, null, 100.00);
        return rideRepository.save(ride);
    }
}
