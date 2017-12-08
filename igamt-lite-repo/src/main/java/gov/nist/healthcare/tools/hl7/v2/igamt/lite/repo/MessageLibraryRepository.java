package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;

public interface MessageLibraryRepository extends MongoRepository<MessageLibrary, String>, MessageLibraryOperations {

	MessageLibrary findById(String id);
	List<MessageLibrary> findAll();
}
