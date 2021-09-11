package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    public Passenger findByPhoneAndOtp(String phone, String otp);
}
