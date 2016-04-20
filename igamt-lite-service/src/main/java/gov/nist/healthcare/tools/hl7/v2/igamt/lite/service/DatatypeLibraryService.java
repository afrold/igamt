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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;

@Service
public interface DatatypeLibraryService {

	List<DatatypeLibrary> findAll();
	
	DatatypeLibrary findById(String id);

	DatatypeLibrary findByScopeAndVersion(Constant.SCOPE scope, String hl7Version);

	List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version);

	DatatypeLibrary save(DatatypeLibrary library);

	DatatypeLibrary create(DatatypeLibrary library);

	void delete(DatatypeLibrary library);

	List<String> findHl7Versions();
}