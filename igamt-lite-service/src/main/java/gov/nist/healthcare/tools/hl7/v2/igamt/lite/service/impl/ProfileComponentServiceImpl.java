package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;

@Service
public class ProfileComponentServiceImpl implements ProfileComponentService {
	Logger log = LoggerFactory.getLogger(ProfileComponentServiceImpl.class);
	
	@Autowired
	  private ProfileComponentRepository profileComponentRepository;

	@Override
	public ProfileComponent findById(String id) {
		 log.info("ProfileComponentServiceImpl.findById=" + id);
		    return profileComponentRepository.findOne(id);
	}

}
