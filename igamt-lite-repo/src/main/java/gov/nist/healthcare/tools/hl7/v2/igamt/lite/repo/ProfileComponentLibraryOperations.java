package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;

public interface ProfileComponentLibraryOperations {
	public Set<ProfileComponentLink> findChildrenById(String id);

	ProfileComponentLibrary findById(String id);

}
