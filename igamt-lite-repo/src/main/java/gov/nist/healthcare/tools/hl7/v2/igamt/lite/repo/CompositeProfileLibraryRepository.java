package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;

public interface CompositeProfileLibraryRepository extends MongoRepository<CompositeProfileLibrary, String>, CompositeProfileLibrayOperation {

}
