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
 * @author OMR
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

@Service
public class ProfileCreationImpl implements ProfileCreationService{

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ProfileService profileService;


	@Override
	public List<String> findHl7Versions() {
		//fetching messages of version hl7Version
		return profileRepository.findHl7Versions();
	}

	@Override
	public List<String[]> summary(String hl7Version) {
		//Fetching messages of version hl7Version
		List<String[]> rst = new ArrayList<String[]>();
		List<Profile> pl = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7Version);
		for (Profile p : pl){
			for (Message m: p.getMessages().getChildren()){
				String[] msgDesc = new String [] {m.getId(), m.getEvent(), m.getStructID(), m.getDescription()};
				rst.add(msgDesc);
			}
		}
		return rst;
	}

	@Override
	public Profile createIntegratedProfile(List<String> msgIds, String hl7Version) throws ProfileException {
		//Creation of profile
		Profile pSource = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7Version).get(0);

		Profile pTarget = new Profile();

		//Setting metaData
		ProfileMetaData metaData = new ProfileMetaData();
		pTarget.setMetaData(metaData);
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		metaData.setDate(dateFormat.format(date));
		metaData.setVersion("1.0");
//		metaData.setName("");
//		metaData.setOrgName("");
//		metaData.setSubTitle("");

		metaData.setHl7Version(hl7Version);
		metaData.setStatus("Draft");
//		metaData.setSchemaVersion(SchemaVersion.V1_0.value());
		
		//Setting profile info
		pTarget.setScope(ProfileScope.USER);
		pTarget.setComment("Created " + date.toString());
		
		//Filling libraries
		Messages msgsTarget = new Messages();
		Segments sgtsTarget = new Segments();
		Datatypes dtsTarget = new Datatypes();
		pTarget.setMessages(msgsTarget);
		pTarget.setSegments(sgtsTarget);
		pTarget.setDatatypes(dtsTarget);
		for (String msgId : msgIds){
			Message m = pSource.getMessages().findOne(msgId);
			msgsTarget.addMessage(m);
			for (SegmentRefOrGroup sg : m.getChildren()){
				if (sg instanceof SegmentRef){
					addSegment((SegmentRef) sg, pSource, pTarget);
				} else if (sg instanceof Group){
					addGroup((Group) sg, pSource, pTarget);
				}
			}
		}
		profileService.save(pTarget);
		return pTarget;
	}
	
	private void addSegment(SegmentRef sref, Profile pSource, Profile pTarget){
		Segments sgtsTarget = pTarget.getSegments();
		Datatypes dtsTarget = pTarget.getDatatypes();
		Segment sgt = pSource.getSegments().findOne(sref.getRef());
		sgtsTarget.addSegment(sgt);
		for (Field f : sgt.getFields()){
			Datatype dt = pSource.getDatatypes().findOne(f.getDatatype());
			dtsTarget.addDatatype(dt);
		}
	}
	
	private void addGroup(Group g, Profile pSource, Profile pTarget){
		for (SegmentRefOrGroup sg : g.getChildren()){
			if (sg instanceof SegmentRef){
				addSegment((SegmentRef) sg, pSource, pTarget);
			} else if (sg instanceof Group){
				addGroup((Group) sg, pSource, pTarget);
			}
		}
	}

	@Override
	public List<Profile> findProfilesByHl7Versions() {
		//Fetching all HL7Standard profiles
		return profileRepository.findByScope(ProfileScope.HL7STANDARD);
	}


}
