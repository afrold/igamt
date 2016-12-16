package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;

public interface CompositeMessageRepository extends MongoRepository<CompositeMessage, String>, CompositeMessageOperations {

}
