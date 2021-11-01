package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;
import ml.ridex.ridexapi.model.dto.AdminPassengerRideStatsDTO;
import ml.ridex.ridexapi.model.dto.AdminPassengerRidesDTO;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RideRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
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
    private DriverService driverService;

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

    public Passenger profileComplete(String phone, String email, String name) throws EntityNotFoundException, InvalidOperationException {
        Passenger passenger = this.getPassenger(phone);
        passenger.setEmail(email);
        passenger.setName(name);
        passenger.setEnabled(true);
        return passengerRepository.save(passenger);
    }

    public Passenger updateProfile(String phone, String name, String email) throws EntityNotFoundException, InvalidOperationException {
        Passenger passenger = this.getPassenger(phone);
        passenger.setName(name);
        passenger.setEmail(email);
        return passengerRepository.save(passenger);
    }

    public RideRequest createRideRequest(String phone, Location startLocation, Location endLocation, Integer distance)
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
                RideRequestStatus.PENDING,
                null,
                null,
                Instant.now().getEpochSecond()
        );
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
            LocalDate localDate = LocalDate.ofEpochDay(ride.getRideRequest().getTimestamp());
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
}
