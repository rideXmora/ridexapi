package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DriverRepository extends MongoRepository<Driver, String> {
    public Optional<Driver> findByPhone(String phone);
    public Optional<Driver> findByPhoneAndSuspend(String phone, boolean suspend);
    public Boolean existsByPhone(String phone);
}
