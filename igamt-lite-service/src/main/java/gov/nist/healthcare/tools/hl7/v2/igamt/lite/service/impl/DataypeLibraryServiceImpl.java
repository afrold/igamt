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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;

/**
 * @author gcr1
 *
 */
@Service
public class DataypeLibraryServiceImpl implements DatatypeLibraryService {
	
	Logger log = LoggerFactory.getLogger(DataypeLibraryServiceImpl.class);

	@Autowired
	private DatatypeLibraryRepository datatypeLibraryRepository;

	@Override
	public List<DatatypeLibrary> findAll() {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findAll();
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}
	
	@Override
	public List<DatatypeLibrary> findByScope(DatatypeLibrary.SCOPE scope) {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByScope(scope);
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}
	
	@Override
	public List<DatatypeLibrary> findByAccountId(Long accountId) {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByAccountId(accountId);
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}
	
	@Override
	public DatatypeLibrary apply(DatatypeLibrary library) {
		DatatypeLibrary datatypeLibrary = datatypeLibraryRepository.save(library);
		return datatypeLibrary;
	}
}
