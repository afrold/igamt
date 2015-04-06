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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ByNameRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ByNameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ByNameServiceImpl implements ByNameService {
	@Autowired
	private ByNameRepository byNameRepository;

	@Override
	public Iterable<ByName> findAll() {
		return byNameRepository.findAll();
	}

	@Override
	public ByName save(ByName c) {
		return byNameRepository.save(c);
	}

	@Override
	public void delete(String id) {
		byNameRepository.delete(id);
	}

	@Override
	public ByName findOne(String id) {
		return byNameRepository.findOne(id);
	}

}