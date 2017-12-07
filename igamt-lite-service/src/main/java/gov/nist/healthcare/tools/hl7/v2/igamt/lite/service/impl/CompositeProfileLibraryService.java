package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.CompositeProfileLibraryRepository;
@Service
public class CompositeProfileLibraryService
		implements gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileLibraryService {
	@Autowired
	CompositeProfileLibraryRepository repo;
	
	@Override
	public List<CompositeProfileLibrary> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public CompositeProfileLibrary findById(String id) {
		// TODO Auto-generated method stub
		return repo.findById(id);
	}

}
