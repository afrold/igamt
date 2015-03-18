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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone.DatatypeClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.DatatypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatatypeServiceImpl implements DatatypeService {

	@Autowired
	private DatatypeRepository datatypeRepository;
	
	private DatatypeClone datatypeClone;

	@Override
	public Iterable<Datatype> findAll() {
		return datatypeRepository.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Datatype save(Datatype p) {
		return datatypeRepository.saveAndFlush(p);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long id) {
		datatypeRepository.delete(id);
	}

	@Override
	public Datatype findOne(Long id) {
		return datatypeRepository.findOne(id);
	}

	@Override
	public Datatype clone(Datatype d) throws CloneNotSupportedException {
		return datatypeClone.clone(d);
	}
}