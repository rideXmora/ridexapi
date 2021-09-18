package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    public Passenger findByPhone(String phone);
    public Boolean existsByPhone(String phone);
}
