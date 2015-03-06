package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ConstraintsSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.TableSerializationImpl;
import nu.xom.Document;

public class ProfileClone {
	public Profile clone(Profile original) {
		ProfileSerializationImpl profileSerializationImpl = new ProfileSerializationImpl();
		TableSerializationImpl tableSerializationImpl = new TableSerializationImpl();
		ConstraintsSerializationImpl constraintsSerializationImpl = new ConstraintsSerializationImpl();
		Document profileDoc = profileSerializationImpl
				.serializeProfileToDoc(original);
		Document constraintsDoc = constraintsSerializationImpl
				.serializeConstraintsToDoc(original.getConformanceStatements(),
						original.getPredicates());
		Document tablesDoc = tableSerializationImpl
				.serializeTableLibraryToDoc(original.getTableLibrary());
		// FIXME need to consider Author and User
		return profileSerializationImpl.deserializeXMLToProfile(profileDoc,
				tablesDoc, constraintsDoc);

	}
}
