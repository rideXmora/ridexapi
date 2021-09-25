package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.OrgAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrgAdminRepository extends MongoRepository<OrgAdmin, String> {
    public Optional<OrgAdmin> findByPhone(String phone);
}
