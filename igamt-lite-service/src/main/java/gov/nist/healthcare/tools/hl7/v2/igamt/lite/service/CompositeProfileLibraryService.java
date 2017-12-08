package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;

public interface CompositeProfileLibraryService {

	
	List<CompositeProfileLibrary> findAll();
	CompositeProfileLibrary findById(String id);

}
