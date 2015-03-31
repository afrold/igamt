package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ConstraintsSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.TableSerializationImpl;
import nu.xom.Document;

import org.springframework.stereotype.Service;

@Service
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
				.serializeTableLibraryToDoc(original.getTables());
		// FIXME need to consider Author and User
		return profileSerializationImpl.deserializeXMLToProfile(profileDoc,
				tablesDoc, constraintsDoc);

	}
}
