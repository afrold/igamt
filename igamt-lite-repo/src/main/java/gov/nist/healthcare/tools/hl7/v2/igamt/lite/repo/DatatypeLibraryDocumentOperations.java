package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

public interface DatatypeLibraryDocumentOperations {

	public DatatypeLibraryDocument findById(String id);

	List<DatatypeLibraryDocument> findByScope(SCOPE scope, Long accountId);
	
	public List<DatatypeLibraryDocument> findByAccountId(Long accountId, String hl7Version);

	public List<DatatypeLibraryDocument> findScopesNVersion(List<SCOPE> scopes, String hl7version);

	public List<String> findHl7Versions();

	List<DatatypeLibraryDocument> findDups(DatatypeLibrary dtl);
 
	List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version, String name,
			Long accountId);
	
	List<DatatypeLibraryDocument> findLibrariesByFlavorName(SCOPE scope,
			String hl7Version, String name, Long accountId);

	List<DatatypeLibraryDocument> findByIds(Set<String> ids);

	List<DatatypeLibraryDocument> findByScope(SCOPE scope);
	
}
