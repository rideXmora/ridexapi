package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByPhone(String phone);
}
