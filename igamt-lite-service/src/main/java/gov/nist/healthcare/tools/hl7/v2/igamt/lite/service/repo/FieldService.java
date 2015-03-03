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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.FieldRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FieldService {
	@Autowired
	private FieldRepository fieldRepository;

	public Iterable<Field> findAll() {
		return fieldRepository.findAll();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Field save(Field c) {
		return fieldRepository.save(c);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long id) {
		fieldRepository.delete(id);
	}

	public Field findOne(Long id) {
		return fieldRepository.findOne(id);
	}

}