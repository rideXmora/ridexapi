package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.redis.DriverState;
import org.springframework.data.repository.CrudRepository;

public interface RedisDriverStateRepository extends CrudRepository<DriverState, String> {
}
