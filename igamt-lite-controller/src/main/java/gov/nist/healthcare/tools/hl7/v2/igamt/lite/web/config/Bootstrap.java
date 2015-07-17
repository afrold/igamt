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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public void afterPropertiesSet() throws Exception {
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/VXU_V04/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/VXU_V04/ValueSets_all.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/VXU_V04/Constraints.xml"));
		Profile profile = new ProfileSerializationImpl()
		.deserializeXMLToProfile(p, v, c);
		profile.getMetaData().setName("VXU V04 Implementation Guide");
		profile.getMetaData().setIdentifier("CDC IG_VXU_V04 Release 1.5");
		profile.getMetaData().setOrgName("NIST");
		profile.getMetaData().setSubTitle("NIST");
		profile.getMetaData().setVersion("1.0");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		profile.getMetaData().setDate(
				dateFormat.format(Calendar.getInstance().getTime()));
		profile.setScope(ProfileScope.PRELOADED);
		profile.getMetaData().setHl7Version("2.5.1");
		profileService.save(profile);

		p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/OML_O21/Profile.xml"));
		v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/OML_O21/ValueSets_HL7.xml"));
		c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/igs/OML_O21/Constraints.xml"));
		profile = new ProfileSerializationImpl().deserializeXMLToProfile(p,
				v,
				c);
		profile.getMetaData().setName("OML_O21 Implementation Guide");
		profile.getMetaData().setIdentifier("OML_O21");
		profile.getMetaData().setOrgName("NIST");
		profile.getMetaData().setSubTitle("NIST");
		profile.getMetaData().setVersion("1.0");
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		profile.getMetaData().setDate(
				dateFormat.format(Calendar.getInstance().getTime()));
		profile.setScope(ProfileScope.PRELOADED);
		profile.getMetaData().setHl7Version("2.5.1");
		profileService.save(profile);

		for (String i : new String[]{"1", "3", "4", "8"}){

			p = IOUtils.toString(this.getClass().getResourceAsStream(
					"/igs/ADT_0" + i + "/Profile.xml"));
			v = IOUtils.toString(this.getClass().getResourceAsStream(
					"/igs/ADT_0" + i + "/ValueSets_HL7.xml"));
			c = IOUtils.toString(this.getClass().getResourceAsStream(
					"/igs/ADT_0" + i + "/Constraints.xml"));
			profile = new ProfileSerializationImpl().deserializeXMLToProfile(p,
					v,
					c);
			profile.getMetaData().setName("ADT_0"+i+" Implementation Guide");
			profile.getMetaData().setIdentifier("ADT_0"+i);
			profile.getMetaData().setOrgName("NIST");
			profile.getMetaData().setSubTitle("NIST");
			profile.getMetaData().setVersion("1.0");
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			profile.getMetaData().setDate(
					dateFormat.format(Calendar.getInstance().getTime()));
			profile.setScope(ProfileScope.PRELOADED);
			profile.getMetaData().setHl7Version("2.5.1");
			profileService.save(profile);
		}
	}

}
