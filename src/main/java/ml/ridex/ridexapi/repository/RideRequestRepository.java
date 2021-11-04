package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.RideRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RideRequestRepository extends MongoRepository<RideRequest, String> {
    public Optional<RideRequest> findByIdAndPhone(String id, String phone);
}
