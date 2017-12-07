package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;

public interface MessageLibraryService {

	List<MessageLibrary> findAll();
	MessageLibrary find(String id);
}
