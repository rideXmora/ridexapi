package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.RideRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRequestRepository extends MongoRepository<RideRequest, String> {
}
