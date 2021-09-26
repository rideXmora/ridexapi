package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonService {
    @Autowired
    private OrgAdminRepository orgAdminRepository;

    public List<OrgAdmin> getAllOrg() {
        return orgAdminRepository.findAll();
    }
}
