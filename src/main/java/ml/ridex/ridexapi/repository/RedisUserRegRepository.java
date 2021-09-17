package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.redis.UserReg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRegRepository extends CrudRepository<UserReg, String> {

}
