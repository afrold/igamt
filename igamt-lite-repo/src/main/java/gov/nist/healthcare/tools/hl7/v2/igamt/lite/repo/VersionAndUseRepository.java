package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;

public interface VersionAndUseRepository extends MongoRepository<VersionAndUse, String>, VersionAndUseOperations {

}
