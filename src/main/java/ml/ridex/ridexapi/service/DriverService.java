package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.daoHelper.*;
import ml.ridex.ridexapi.model.redis.DriverState;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.RedisDriverStateRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
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
    private OrgAdminRepository orgAdminRepository;

    @Autowired
    private RedisDriverStateRepository redisDriverStateRepository;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private NotificationService notificationService;

    public Driver getDriver(String phone) {
        Optional<Driver> driverOptional = driverRepository.findByPhone(phone);
        if(driverOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        return driverOptional.get();
    }

    public OrgAdmin getOrgAdmin(String id) {
        Optional<OrgAdmin> orgAdminOptional = orgAdminRepository.findById(id);
        if(orgAdminOptional.isEmpty())
            throw new EntityNotFoundException("Invalid organizational id");
        return orgAdminOptional.get();
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver profileComplete(
            String phone,
            String name,
            String email,
            String city,
            DriverOrganization driverOrganization,
            String notificationToken
    ) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        driver.setEmail(email);
        driver.setName(name);
        driver.setCity(city);
        driver.setDriverOrganization(driverOrganization);
        driver.setNotificationToken(notificationToken);
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
        // RideRequest
        RideRequest rideRequest = rideRequestOptional.get();
        if(rideRequest.getStatus() == RideRequestStatus.ACCEPTED)
            throw new InvalidOperationException("All ready accepted by someone else");
        if(rideRequest.getStatus() == RideRequestStatus.TIMEOUT)
            throw new InvalidOperationException("Ride request is timeout");
        // Driver
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
                driver.getName(),
                rideRequestVehicle,
                (double) driver.getTotalRating()/totalRides);

        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        rideRequest.setOrganization(driver.getDriverOrganization());
        rideRequest.setDriver(rideRequestDriver);

        OrgAdmin orgAdmin = this.getOrgAdmin(driver.getDriverOrganization().getId());

        if(orgAdmin.getPayments() == null)
            throw new InvalidOperationException("Organize has not complete the profile");

        Payment payment = orgAdmin.getPayments().stream()
                .filter(p -> p.getVehicleType().equals(driver.getVehicle().getVehicleType()))
                .findAny().orElse(null);
        if(payment == null)
            throw new InvalidOperationException("Vehicle type not allowed in this org");
        // Cost calculation
        Double cost = rideRequest.getDistance()
                *payment.getRatePerMeter()
                *(100-payment.getDiscount())/100;

        // update ride rideRequest
        rideRequestRepository.save(rideRequest);

        Ride ride = new Ride(rideRequest, null, null,null, null, cost, RideStatus.ACCEPTED);
        // init ride
        return rideRepository.save(ride);
    }

    public void notifyPassenger(Ride ride) throws EntityNotFoundException {
        // Can remove if rideRequest has the notification token
        Passenger passenger = passengerService.getPassenger(ride.getRideRequest().getPassenger().getPhone());
        String message;
        switch (ride.getRideStatus()) {
            case ACCEPTED:
                message = ride.getRideRequest().getDriver().getName() + "accept your ride";
                break;
            case ARRIVED:
                message = "Driver is arrived. " + ride.getRideRequest().getDriver().getVehicle().getNumber();
                break;
            case PICKED:
                message = "Start the ride";
                break;
            case DROPPED:
                message = "Pay Rs: " + ride.getPayment().toString();
                break;
            default:
                throw new InvalidOperationException("Invalid notification request");
        }
        notificationService.notifyPassenger(passenger.getNotificationToken(), ride.getId(), ride.getRideStatus(), message);
    }

    public Driver toggleStatus(String phone, Location location) throws EntityNotFoundException{
        DriverState state;
        Driver driver = getDriver(phone);
        if(driver.getDriverStatus() == DriverStatus.ONLINE) {
            driver.setDriverStatus(DriverStatus.OFFLINE);
            notificationService.unsubscribeFromRideTopic(driver.getNotificationToken());
            redisDriverStateRepository.deleteById(phone);
        }
        else {
            driver.setDriverStatus(DriverStatus.ONLINE);
            state = new DriverState(phone, location, Instant.now().getEpochSecond(), driver.getNotificationToken());
            notificationService.subscribeToRideTopic(driver.getNotificationToken());
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
            driverState = new DriverState(phone, location, Instant.now().getEpochSecond(), driver.getNotificationToken());
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

    public Ride finishRide(String phone,
                           String id,
                           RideStatus rideStatus,
                           Byte passengerRating,
                           String driverFeedback,
                           Integer waitingTime) throws EntityNotFoundException {
        Optional<Ride> rideOptional = rideRepository.findByIdAndRideRequestDriverPhone(id, phone);
        if(rideOptional.isEmpty())
            throw new EntityNotFoundException("Invalid id");
        Ride ride = rideOptional.get();
        if(ride.getRideStatus() != RideStatus.DROPPED)
            throw new InvalidOperationException("Can't finish the trip without completing");
        // Cost for the waiting time
        if(waitingTime >0) {
            OrgAdmin orgAdmin = this.getOrgAdmin(ride.getRideRequest().getOrganization().getId());
            Payment payment = orgAdmin.getPayments().stream()
                    .filter(p -> p.getVehicleType().equals(ride.getRideRequest().getDriver().getVehicle().getVehicleType()))
                    .findAny().get();
            ride.setPayment(ride.getPayment() + payment.getRateWaitingPerMin()*waitingTime);
        }
        ride.setRideStatus(rideStatus);
        ride.setDriverFeedback(driverFeedback);
        ride.setPassengerRating(passengerRating);

        Passenger passenger = passengerService.getPassenger(ride.getRideRequest().getPassenger().getPhone());
        passenger.setTotalRating(passenger.getTotalRating() + passengerRating);
        passenger.setTotalRides(passenger.getTotalRides() + 1);
        passengerService.savePassenger(passenger);

        return rideRepository.save(ride);
    }

    public List<Ride> getPastRides(String phone) {
        return rideRepository.findByRideRequestAndRideRequestDriverPhone(phone, RideStatus.CONFIRMED);
    }
}
