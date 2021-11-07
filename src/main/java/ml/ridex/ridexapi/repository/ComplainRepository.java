package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.enums.ComplainStatus;
import ml.ridex.ridexapi.model.dao.Complain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ComplainRepository extends MongoRepository<Complain, String> {
    public List<Complain> findByRideRideRequestOrganizationId(String id);
}
