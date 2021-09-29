package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface RideRepository extends MongoRepository<Ride, String> {
    @Query("{ '_id': ObjectId(?0), 'rideRequest.driver.phone': ?1 }")
    public Optional<Ride> findByIdAndRideRequestDriverPhone(String id, String phone);
}
