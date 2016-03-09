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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DTLibService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryNotFoundException;

/**
 * @author gcr1 dtLibRepository
 */
@Service
public class DTLibServiceImpl implements DTLibService {

	Logger log = LoggerFactory.getLogger(DTLibServiceImpl.class);

	@Autowired
	private DatatypeLibraryRepository dtLibRepository;

	@Override
	public List<DatatypeLibrary> findAll() {
		List<DatatypeLibrary> datatypeLibrary = dtLibRepository.findAll();
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public DatatypeLibrary findByScope(DatatypeLibrary.SCOPE scope, DatatypeLibrary dtLibSource)
			throws DatatypeLibraryNotFoundException {
		DatatypeLibrary dtLibTarget = null;
		List<DatatypeLibrary> dtLibList = dtLibRepository.findByScope(scope);
		if (dtLibList.size() > 0) {
			dtLibTarget = dtLibList.get(0);
			if (dtLibSource != null) {
				List<String> dtIds = new ArrayList<String>();
				for (Datatype dt : dtLibSource.getChildren()) {
					dtIds.add(dt.getId());
				}
				Set<Datatype> removals = new HashSet<Datatype>();
				for (Datatype dt : dtLibTarget.getChildren()) {
					if (dtIds.contains(dt.getId())) {
						removals.add(dt);
					}
				}
				dtLibTarget.getChildren().removeAll(removals);
			}
		} else {
			throw new DatatypeLibraryNotFoundException(scope.name());
		}

		log.info("dtLibList=" + dtLibList.size());
		return dtLibTarget;
	}

	@Override
	public DatatypeLibrary findByAccountId(Long accountId) {
		List<DatatypeLibrary> datatypeLibrary = dtLibRepository.findByAccountId(accountId);
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary.get(0);
	}

	@Override
	public DatatypeLibrary apply(DatatypeLibrary library) {
		DatatypeLibrary datatypeLibrary = dtLibRepository.save(library);
		return datatypeLibrary;
	}
}
