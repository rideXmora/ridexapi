package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<Ride, String> {
}
