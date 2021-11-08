package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.ComplainStatus;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.enums.VehicleType;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.*;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;
import ml.ridex.ridexapi.model.dto.AdminPassengerRideStatsDTO;
import ml.ridex.ridexapi.model.dto.AdminPassengerRidesDTO;
import ml.ridex.ridexapi.model.redis.DriverState;
import ml.ridex.ridexapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ComplainRepository complainRepository;

    @Autowired
    private RedisDriverStateRepository redisDriverStateRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private NotificationService notificationService;

    public Passenger getPassenger(String phone) {
        Optional<Passenger> passengerOptional = passengerRepository.findByPhone(phone);
        if(passengerOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        Passenger passenger = passengerOptional.get();
        return passenger;
    }

    public Passenger savePassenger(Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    public Passenger profileComplete(String phone, String email, String name, String notificationToken) throws EntityNotFoundException, InvalidOperationException {
        Passenger passenger = this.getPassenger(phone);
        passenger.setEmail(email);
        passenger.setName(name);
        passenger.setNotificationToken(notificationToken);
        passenger.setEnabled(true);
        return passengerRepository.save(passenger);
    }

    public Passenger updateProfile(String phone, String name, String email) throws EntityNotFoundException, InvalidOperationException {
        Passenger passenger = this.getPassenger(phone);
        passenger.setName(name);
        passenger.setEmail(email);
        return passengerRepository.save(passenger);
    }

    public RideRequest createRideRequest(String phone, Location startLocation, Location endLocation, Integer distance, VehicleType vehicleType)
            throws EntityNotFoundException, InvalidOperationException {
        Passenger passenger = this.getPassenger(phone);
        Integer totalRides = passenger.getTotalRides();
        if(totalRides == 0)
            totalRides = 1;
        RideRequestPassenger rideRequestPassenger = new RideRequestPassenger(
                passenger.getId(),
                passenger.getPhone(),
                passenger.getName(),
                (double) (passenger.getTotalRating()/totalRides));
        RideRequest rideRequest = new RideRequest(
                rideRequestPassenger,
                startLocation,
                endLocation,
                distance,
                vehicleType,
                RideRequestStatus.PENDING,
                null,
                null,
                Instant.now().getEpochSecond()
        );
        return rideRequestRepository.save(rideRequest);
    }

    public void notifyDrivers(RideRequest rideRequest) {
        double RADIUS = 5000.0;
        List<DriverState> drivers = (List<DriverState>) redisDriverStateRepository.findAll();
        List<String> tokens = new ArrayList<>();
        for(DriverState driverState: drivers) {
            double x1 = rideRequest.getStartLocation().getX();
            double y1 = rideRequest.getStartLocation().getY();
            double x2 = rideRequest.getEndLocation().getX();
            double y2 = rideRequest.getEndLocation().getY();
            if(Math.pow(x1-x2,2) + Math.pow(y1-y2,2) < Math.pow(RADIUS,2)){
                tokens.add(driverState.getNotificationToken());
            }
        }
        notificationService.notifyDrivers(rideRequest, tokens, rideRequest.getVehicleType());
    }

    public RideRequest rideRequestTimeout(String phone, String id) {
        Optional<RideRequest> rideRequestOptional = rideRequestRepository.findByIdAndPassengerPhone(id, phone);
        if(rideRequestOptional.isEmpty())
            throw new EntityNotFoundException("Can't find the the ride request");
        RideRequest rideRequest = rideRequestOptional.get();
        rideRequest.setStatus(RideRequestStatus.TIMEOUT);
        return rideRequestRepository.save(rideRequest);
    }

    public Ride getRide(String phone, String id) {
        Optional<Ride> ride = rideRepository.findByIdAndRideRequestPassengerPhone(id, phone);
        if(ride.isEmpty())
            throw new EntityNotFoundException("Invalid id");
        return ride.get();
    }

    public Ride confirmRide(String phone, String id, String passengerFeedback, Byte driverRating) throws EntityNotFoundException {
        Ride ride = this.getRide(phone, id);
        if(ride.getRideStatus() != RideStatus.FINISHED)
            throw new InvalidOperationException("Wait until the ride complete");
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setPassengerFeedback(passengerFeedback);
        ride.setDriverRating(driverRating);

        Driver driver = driverService.getDriver(ride.getRideRequest().getDriver().getPhone());
        driver.setTotalRating(driver.getTotalRating() + driverRating);
        driver.setTotalRides(driver.getTotalRides() + 1);
        driverService.saveDriver(driver);

        return rideRepository.save(ride);
    }

    public List<Ride> getPastRides(String phone) {
        return rideRepository.findByRideStatusAndRideRequestPassengerPhone(phone, RideStatus.CONFIRMED);
    }

    public Map<Month,AdminPassengerRideStatsDTO> getPastRidesStats(String phone) {
        List<Ride> rides = rideRepository.findByRideStatusAndRideRequestPassengerPhone(phone, RideStatus.CONFIRMED);
        Map<Month, AdminPassengerRideStatsDTO> stats = new HashMap<>();

        for(Ride ride: rides) {
            LocalDate localDate = Instant.ofEpochSecond(ride.getRideRequest().getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
            Month month = localDate.getMonth();
            AdminPassengerRideStatsDTO dto = stats.get(month);
            if(dto == null) {
                stats.put(month, new AdminPassengerRideStatsDTO(1,ride.getPayment()));
            }
            else {
                dto.setCount(dto.getCount()+1);
                dto.setPaymentSum(dto.getPaymentSum()+ ride.getPayment());
            }
        }

        return stats;
    }

    public Map<Month,AdminPassengerRideStatsDTO> getAdminRidesStats() {
        List<Ride> rides = rideRepository.findAll();
        Map<Month, AdminPassengerRideStatsDTO> stats = new HashMap<>();

        for(Ride ride: rides) {
            LocalDate localDate = Instant.ofEpochSecond(ride.getRideRequest().getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
            Month month = localDate.getMonth();
            AdminPassengerRideStatsDTO dto = stats.get(month);
            if(dto == null) {
                stats.put(month, new AdminPassengerRideStatsDTO(1,ride.getPayment()));
            }
            else {
                dto.setCount(dto.getCount()+1);
                dto.setPaymentSum(dto.getPaymentSum()+ ride.getPayment());
            }
        }

        return stats;
    }

    public Complain rideComplain(String phone, String id, String complainString) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if(rideOptional.isEmpty())
            throw new EntityNotFoundException("Invalid ride id");
        Ride ride = rideOptional.get();
        if(!ride.getRideRequest().getPassenger().getPhone().equals(phone))
            throw new InvalidOperationException("Invalid access");
        Complain complain = new Complain(ride, null, complainString, ComplainStatus.RAISED);
        return complainRepository.save(complain);
    }
}
