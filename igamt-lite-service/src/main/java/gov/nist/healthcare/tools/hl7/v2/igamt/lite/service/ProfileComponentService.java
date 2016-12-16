package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public interface ProfileComponentService {
	
	ProfileComponent findById(String id);
	ProfileComponent create(ProfileComponent pc);
	List<ProfileComponent> findAll();
	ProfileComponent save(ProfileComponent pc);
	void delete(String id);

}
