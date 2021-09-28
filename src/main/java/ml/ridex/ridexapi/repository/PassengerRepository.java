package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    public Optional<Passenger> findByPhone(String phone);
    public Boolean existsByPhone(String phone);
}
