package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;

    private Passenger getPassenger(String phone) {
        Optional<Passenger> passengerOptional = passengerRepository.findByPhone(phone);
        if(passengerOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        return passengerOptional.get();
    }

    public Passenger profileComplete(String phone, String email, String name) throws EntityNotFoundException {
        Passenger passenger = this.getPassenger(phone);
        if(passenger.getSuspend())
            throw new InvalidOperationException("User is suspended");
        passenger.setEmail(email);
        passenger.setName(name);
        passenger.setEnabled(true);
        return passengerRepository.save(passenger);
    }

    public Passenger updateProfile(String phone, String name, String email) throws EntityNotFoundException {
        Passenger passenger = this.getPassenger(phone);
        if(passenger.getSuspend())
            throw new InvalidOperationException("User is suspended");
        passenger.setName(name);
        passenger.setEmail(email);
        return passengerRepository.save(passenger);
    }
}
