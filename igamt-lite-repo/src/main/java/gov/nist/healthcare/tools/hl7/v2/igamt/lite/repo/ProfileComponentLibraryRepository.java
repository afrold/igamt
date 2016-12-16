package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;

public interface ProfileComponentLibraryRepository extends MongoRepository<ProfileComponentLibrary, String>, ProfileComponentLibraryOperations {

//	Set<ProfileComponentLink> findProfileComponentsById(String libId);

}
