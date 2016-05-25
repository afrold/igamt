/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.IGDocumentConverterFromOldToNew;

@Service
public class Bootstrap implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ProfileService profileService;

	@Autowired
	IGDocumentService documentService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
        //Carefully use this. It will delete all of existing IGDocuments and make new ones converted from the "igdocumentPreLibHL7", "igdocumentPreLibPRELOADED" , and ""igdocumentPreLibUSER" 
//		 covertOldToNew();

	}

	private void covertOldToNew() {
		IGDocumentConverterFromOldToNew old2New = new IGDocumentConverterFromOldToNew();
		old2New.convert();
	}

	private void loadPreloadedIGDocuments() throws Exception {
		IGDocument d = new IGDocument();

		String p = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_ValueSetLibrary.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Constraints.xml"));
		Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);

		profile.setScope(IGDocumentScope.PRELOADED);

		d.addProfile(profile);

		boolean existPreloadedDocument = false;

		String documentID = d.getMetaData().getIdentifier();
		String documentVersion = d.getMetaData().getVersion();

		List<IGDocument> igDocuments = documentService.findAll();

		for (IGDocument igd : igDocuments) {
			if (igd.getScope().equals(IGDocumentScope.PRELOADED) && documentID.equals(igd.getMetaData().getIdentifier())
					&& documentVersion.equals(igd.getMetaData().getVersion())) {
				existPreloadedDocument = true;
			}
		}
		if (!existPreloadedDocument)
			documentService.save(d);
	}

	private void checkTableNameForAllIGDocuments() throws IGDocumentSaveException {

		List<IGDocument> igDocuments = documentService.findAll();

		for (IGDocument igd : igDocuments) {
			boolean ischanged = false;
			TableLibrary tables = igd.getProfile().getTableLibrary();

			for (TableLink tl : tables.getChildren()) {
				// if (t.getName() == null || t.getName().equals("")) {
				// if (t.getDescription() != null) {
				// t.setName(t.getDescription());
				// ischanged = true;
				// } else
				// t.setName("NONAME");
				// }
			}

			if (ischanged)
				documentService.apply(igd);
		}
	}

}