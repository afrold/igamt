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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;

/**
 * @author gcr1
 *
 */
@Service
public class DatatypeServiceImpl implements DatatypeService {

	Logger log = LoggerFactory.getLogger(DatatypeServiceImpl.class);

	@Autowired
	private DatatypeRepository datatypeRepository;

	@Override
	public List<Datatype> findAll() {
		List<Datatype> datatypes = datatypeRepository.findAll();
		log.info("DataypeServiceImpl.findAll=" + datatypes.size());
		return datatypes;
	}

	@Override
	public Datatype findById(String id) {
		log.info("DataypeServiceImpl.findById=" + id);
		Datatype datatype;
		datatype = datatypeRepository.findOne(id);
		return datatype;
	}

	@Override
	public List<Datatype> findByIds(List<String> ids) {
		log.info("DataypeServiceImpl.findByIds=" + ids);
		return datatypeRepository.findByIds(ids);
	}

	@Override
	public List<Datatype> findByLibIds(String dtLibId) {
		List<Datatype> datatypes = datatypeRepository.findByLibIds(dtLibId);
		log.info("DataypeServiceImpl.findAll=" + datatypes.size());
		return datatypes;
	}

	@Override
	public List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		List<Datatype> datatypes = datatypeRepository.findByScopesAndVersion(scopes, hl7Version);
		log.info("DataypeServiceImpl.findByScopesAndVersion=" + datatypes.size());
		return datatypes;
	}

		@Override
	public Datatype save(Datatype datatype) {
		log.info("DataypeServiceImpl.save=" + datatype.getLabel());
		return datatypeRepository.save(datatype);
	}
		
	@Override
	public void delete(Datatype dt) {
		datatypeRepository.delete(dt);
	}
	
	@Override
	public void delete(String id) {
		datatypeRepository.delete(id);
	}
}
