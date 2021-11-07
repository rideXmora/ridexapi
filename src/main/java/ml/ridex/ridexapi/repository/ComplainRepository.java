package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.Complain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComplainRepository extends MongoRepository<Complain, String> {

}
