package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverRepository extends MongoRepository<Driver, String> {
    public Boolean existsByPhone(String phone);
}
