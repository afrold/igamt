package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageLibraryRepository;
@Service
public class MessageLibraryService
		implements gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageLibraryService {
	@Autowired
	 MessageLibraryRepository messageRepo;

	@Override
	public List<MessageLibrary> findAll() {
		// TODO Auto-generated method stub
		return messageRepo.findAll();
	}

	@Override
	public MessageLibrary find(String id) {
		// TODO Auto-generated method stub
		return messageRepo.findById(id);
	}

}
