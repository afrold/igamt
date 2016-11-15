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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;

@Service
public interface DatatypeLibraryService {

	List<DatatypeLibrary> findAll();

	List<DatatypeLibrary> findByScope(SCOPE scope, Long accountId);

	DatatypeLibrary findById(String id);

	List<DatatypeLibrary> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

	List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version);

	DatatypeLibrary create(String name, String ext, SCOPE scope, String hl7Version, Long accountId);

	List<String> findHl7Versions();

	List<DatatypeLink> bindDatatypes(Set<String> datatypeIds, String datatyeLibraryId, String datatypeLibraryExt,
			Long accountId);

	void delete(String id);

	List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version, String name, Long accountId);

	List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version, String name, Long accountId);

	DatatypeLibrary save(DatatypeLibrary library);

	List<Datatype> getChildren(String id);

	DatatypeLibrary saveMetaData(String libId, DatatypeLibraryMetaData datatypeLibraryMetaData);

	void delete(DatatypeLibrary library);

	List<Datatype> findDatatypesById(String libId);

	DatatypeLibrary save(DatatypeLibrary library, Date dateUpdated);

}
