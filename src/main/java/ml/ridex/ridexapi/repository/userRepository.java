package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface userRepository extends MongoRepository<User, String> {

}
