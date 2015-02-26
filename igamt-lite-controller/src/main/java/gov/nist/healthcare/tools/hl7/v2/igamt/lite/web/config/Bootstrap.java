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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Bootstrap implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	@Autowired	
	ProfileRepository profileRepository;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		// load VXU profile
		Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(IOUtils.toString(this.getClass().getResourceAsStream("/profiles/vxu/Profile.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("/profiles/vxu/ValueSets.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("/profiles/vxu/PredicateConstraints.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("/profiles/vxu/ConformanceStatementConstraints.xml"), "UTF-8"));
		
	}

}
