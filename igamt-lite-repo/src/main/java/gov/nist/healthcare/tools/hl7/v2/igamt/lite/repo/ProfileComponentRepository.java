package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;


public interface ProfileComponentRepository
    extends MongoRepository<ProfileComponent, String>, ProfileComponentOperations {


}
