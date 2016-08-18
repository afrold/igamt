package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DeltaRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DeltaService;
@Service

public class DeltaServiceImpl implements DeltaService {
	 Logger log = LoggerFactory.getLogger(DeltaServiceImpl.class);
	
	
	
	  @Autowired
	  private DeltaRepository deltaRepository;
	  @Override
	public Delta findById(String id) {
		// TODO Auto-generated method stub
		return deltaRepository.findOne(id);
	}

	@Override
	public List<Delta> findAll() {
		// TODO Auto-generated method stub
		return deltaRepository.findAll();
	}

	@Override
	public Delta save(Delta delta) {
		// TODO Auto-generated method stub
		return deltaRepository.save(delta);
	}

	@Override
	public Delta create(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Delta delta) {
		// TODO Auto-generated method stub
		deltaRepository.delete(delta);
	}

}
