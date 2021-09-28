package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends MongoRepository<Driver, String> {
    public Optional<Driver> findByPhone(String phone);
    public Boolean existsByPhone(String phone);

    @Query("{ 'driverOrganization._id': ObjectId(?0), 'enabled': ?1 }")
    public List<Driver> findByDriverOrganizationId(String orgId, boolean enabled);

    @Query("{ 'phone': ?0, 'driverOrganization._id': ObjectId(?1) }")
    public Optional<Driver> findByPhoneAndDriverOrganizationId(String phone, String orgId);
}
