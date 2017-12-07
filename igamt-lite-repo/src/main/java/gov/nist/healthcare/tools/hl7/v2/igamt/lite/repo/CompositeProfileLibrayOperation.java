package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;

public interface CompositeProfileLibrayOperation {
	
	CompositeProfileLibrary	findById(String id);
	List<CompositeProfileLibrary> findAll();

}
