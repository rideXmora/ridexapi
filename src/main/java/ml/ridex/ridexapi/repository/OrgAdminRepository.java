package ml.ridex.ridexapi.repository;

import ml.ridex.ridexapi.model.dao.OrgAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrgAdminRepository extends MongoRepository<OrgAdmin, String> {

}
