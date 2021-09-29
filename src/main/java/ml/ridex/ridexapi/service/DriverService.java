package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.*;
import ml.ridex.ridexapi.model.redis.DriverState;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.RedisDriverStateRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RedisDriverStateRepository redisDriverStateRepository;

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
        RideRequest rideRequest = rideRequestOptional.get();
        if(rideRequest.getStatus() == RideRequestStatus.ACCEPTED)
            throw new InvalidOperationException("All ready accepted by someone else");
        if(rideRequestOptional.isEmpty())
            throw new EntityNotFoundException("Invalid id");
        Driver driver = getDriver(phone);
        if(!driver.getEnabled())
            throw new InvalidOperationException("Invalid driver");
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
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setOrganization(driver.getDriverOrganization());
        rideRequest.setDriver(rideRequestDriver);
        rideRequestRepository.save(rideRequest);
        // Payment cal method
        Ride ride = new Ride(rideRequest, null, null, 100.00, RideStatus.ACCEPTED);
        return rideRepository.save(ride);
    }

    public Driver toggleStatus(String phone, Location location) throws EntityNotFoundException{
        DriverState state;
        Driver driver = getDriver(phone);
        if(driver.getDriverStatus() == DriverStatus.ONLINE) {
            driver.setDriverStatus(DriverStatus.OFFLINE);
            redisDriverStateRepository.deleteById(phone);
        }
        else {
            driver.setDriverStatus(DriverStatus.ONLINE);
            state = new DriverState(phone, location, Instant.now().getEpochSecond());
            redisDriverStateRepository.save(state);
        }
        return driverRepository.save(driver);
    }

    public DriverState updateLocation(String phone, Location location) throws EntityNotFoundException {
        Optional<DriverState> driverStateOptional = redisDriverStateRepository.findById(phone);
        DriverState driverState;
        if(driverStateOptional.isEmpty()) {
            Driver driver = getDriver(phone);
            if(driver.getDriverStatus() != DriverStatus.ONLINE)
                throw new InvalidOperationException("Driver is not ONLINE");
            driverState = new DriverState(phone, location, Instant.now().getEpochSecond());
        }
        else
            driverState = driverStateOptional.get();
        driverState.setLocation(location);
        return redisDriverStateRepository.save(driverState);
    }

    public Ride changeRideStatus(String phone, String id, RideStatus rideStatus) {
        Optional<Ride> rideOptional = rideRepository.findByIdAndRideRequestDriverPhone(id, phone);
        if(rideOptional.isEmpty())
            throw new EntityNotFoundException("Invalid id");
        Ride ride = rideOptional.get();
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }
}
