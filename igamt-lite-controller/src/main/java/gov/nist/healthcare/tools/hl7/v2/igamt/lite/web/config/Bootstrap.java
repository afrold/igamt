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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.*;
@Service
public class Bootstrap implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ProfileService profileService;
	
	 

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void afterPropertiesSet() throws Exception {
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/profiles/vxu/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/profiles/vxu/ValueSets.xml"));
		String pc = IOUtils.toString(this.getClass().getResourceAsStream(
				"/profiles/vxu/PredicateConstraints.xml"));
		String cs = IOUtils.toString(this.getClass().getResourceAsStream(
				"/profiles/vxu/ConformanceStatementConstraints.xml"));
		// load VXU profile
		Profile profile = new ProfileSerializationImpl()
				.deserializeXMLToProfile(p, v, pc, cs);
		profileService.save(profile);
		
//		profileRepository.saveAndFlush(profile);
		
		System.out.println(profile.getId());

	}

}
