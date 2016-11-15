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
import java.util.Date;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import nu.xom.Document;

public interface ProfileSerialization {
	Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlConstraints);

	Profile deserializeXMLToProfile(Document docProfile, Document docValueSet, Document docConstraints);

	String serializeProfileToXML(Profile profile, DocumentMetaData metadata, Date dateUpdated);

	Document serializeProfileToDoc(Profile profile, DocumentMetaData metadata, Date dateUpdated);

	InputStream serializeProfileToZip(Profile profile, DocumentMetaData metadata, Date dateUpdated) throws IOException;

	InputStream serializeProfileToZip(Profile profile, String[] ids, DocumentMetaData metadata, Date dateUpdated)
			throws IOException, CloneNotSupportedException;

	InputStream serializeProfileDisplayToZip(Profile profile, String[] ids, DocumentMetaData metadata, Date dateUpdated)
			throws IOException, CloneNotSupportedException;

	InputStream serializeProfileGazelleToZip(Profile profile, String[] ids)
			throws IOException, CloneNotSupportedException;

	InputStream serializeDatatypeToZip(DatatypeLibrary datatypeLibrary) throws IOException;

	String serializeDatatypeLibraryToXML(DatatypeLibrary datatypeLibrary);

	Document serializeDatatypeLibraryToDoc(DatatypeLibrary datatypeLibrary);

}
