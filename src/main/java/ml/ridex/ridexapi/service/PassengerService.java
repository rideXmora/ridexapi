package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    public Passenger getPassenger(String phone) {
        Optional<Passenger> passengerOptional = passengerRepository.findByPhone(phone);
        if(passengerOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        Passenger passenger = passengerOptional.get();
        return passenger;
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
}
