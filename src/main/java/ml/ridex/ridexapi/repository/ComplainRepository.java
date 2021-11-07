package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.enums.ComplainStatus;
import ml.ridex.ridexapi.model.dao.Complain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComplainRepository extends MongoRepository<Complain, String> {
    public List<Complain> findByRideRideRequestOrganizationId(String id);
    public Optional<Complain> findByIdAndRideRideRequestOrganizationId(String id, String orgId);
}
