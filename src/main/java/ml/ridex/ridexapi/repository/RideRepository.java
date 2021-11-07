package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.TopDriver;
import org.springframework.data.mongodb.repository.Aggregation;
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

    @Query("{ 'rideRequest.organization.id': ?0, 'rideRequest.driver.phone': ?1, 'rideStatus': ?2, 'rideRequest.timestamp' : {'$gt' : ?3, '$lt' : ?4} }")
    public List<Ride> findByRideRequestBetweenTimeInterval(String id,
                                                           String phone,
                                                           RideStatus rideStatus,
                                                           long startEpoch,
                                                           long endEpoch);

    @Aggregation(pipeline = {"{  $match: {'rideRequest.organization._id': ?0 } }"
            , "{ $group: { _id: { driver: '$rideRequest.driver._id', phone:'$rideRequest.driver.phone', name:'$rideRequest.driver.name' }, total: {$sum:'$payment'} } }"
    , "{ '$sort': { total: -1 } }"})
    public  List<TopDriver> groupByTopDriver(String id);
}
