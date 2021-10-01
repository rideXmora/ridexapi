package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.model.dao.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends MongoRepository<Ride, String> {
    @Query("{ '_id': ObjectId(?0), 'rideRequest.driver.phone': ?1 }")
    public Optional<Ride> findByIdAndRideRequestDriverPhone(String id, String phone);

    @Query("{ '_id': ObjectId(?0), 'rideRequest.passenger.phone': ?1 }")
    public Optional<Ride> findByIdAndRideRequestPassengerPhone(String id, String phone);

    @Query("{ 'rideStatus': ?1, 'rideRequest.passenger.phone': ?0 }")
    public List<Ride> findByRideStatusAndRideRequestPassengerPhone(String phone, RideStatus rideStatus);

    @Query("{ 'rideRequest.organization.id': ?0 }")
    public List<Ride> findByRideRequestOrganizationId(String id);

    @Query("{ 'rideStatus': ?1, 'rideRequest.driver.phone': ?0 }")
    public List<Ride> findByRideRequestAndRideRequestDriverPhone(String id, RideStatus rideStatus);
}
