package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;

public interface MessageLibraryOperations {
	
	MessageLibrary findById(String id);
	
	List<MessageLibrary> findAll();
}
