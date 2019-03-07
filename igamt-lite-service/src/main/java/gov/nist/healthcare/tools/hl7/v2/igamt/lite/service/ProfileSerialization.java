/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ProfileSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.MessageExportInfo;
import nu.xom.Document;

public interface ProfileSerialization {
	Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlConstraints);

	Profile deserializeXMLToProfile(Document docProfile, Document docValueSet, Document docConstraints);

	InputStream serializeProfileToZip(Profile profile, List<MessageExportInfo> exportInfo, DocumentMetaData metadata) throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException;

	InputStream serializeProfileDisplayToZip(Profile profile, List<MessageExportInfo> exportInfo, DocumentMetaData metadata) throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException;

	InputStream serializeProfileGazelleToZip(Profile profile, List<MessageExportInfo> exportInfo, DocumentMetaData metadata) throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException;
	
	InputStream serializeCompositeProfileToZip(IGDocument doc, String[] exportInfo) throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException;
	
	InputStream serializeCompositeProfileGazelleToZip(IGDocument doc, String[] exportInfo) throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException;
   
	InputStream serializeCompositeProfileDisplayToZip(IGDocument doc, String[] exportInfo) throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException;
	
}
