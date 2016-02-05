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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

@Service
public class ProfileCreationImpl implements ProfileCreationService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ProfileService profileService;

	@Override
	public List<String> findHl7Versions() {
		// fetching messages of version hl7Version
		return profileRepository.findHl7Versions();
	}

	@Override
	public List<String[]> summary(String hl7Version, List<String> messageIds) {
		// Fetching messages of version hl7Version
		List<String[]> rst = new ArrayList<String[]>();
		List<Profile> pl = profileRepository.findByScopeAndMetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version);
		for (Profile p : pl) {
			for (Message m : p.getMessages().getChildren()) {
				if (!messageIds.contains(m.getId())) {
					String[] msgDesc = new String[] { m.getId(), m.getEvent(), m.getStructID(), m.getDescription() };
					rst.add(msgDesc);
				}
			}
		}
		return rst;
	}

	@Override
	public Profile createIntegratedProfile(List<String> msgIds, String hl7Version, Long accountId) throws ProfileException {
		// Creation of profile
		Profile pSource = profileRepository.findByScopeAndMetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version)
				.get(0);
		Profile pTarget = new Profile();

		// Setting metaData
		ProfileMetaData metaData = new ProfileMetaData();
		pTarget.setAccountId(accountId);
		pTarget.setMetaData(metaData);
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		metaData.setDate(dateFormat.format(date));
		metaData.setVersion("1.0");
		metaData.setName("Default name");
		metaData.setOrgName("Default org name");
		metaData.setSubTitle("Subtitle");

		metaData.setHl7Version(hl7Version);
		metaData.setStatus("Draft");
		// metaData.setSchemaVersion(SchemaVersion.V1_0.value());

		// Setting profile info
		pTarget.setScope(IGDocumentScope.USER);
		pTarget.setComment("Created " + date.toString());
		pTarget.setSourceId(pSource.getId());
		pTarget.setBaseId(pSource.getId());

		// Filling libraries--was
		Messages msgsTarget = new Messages();
		Segments sgtsTarget = new Segments();
		Datatypes dtsTarget = new Datatypes();
		Tables tabTarget = new Tables();
		pTarget.setMessages(msgsTarget);
		pTarget.setSegments(sgtsTarget);
		pTarget.setDatatypes(dtsTarget);
		pTarget.setTables(tabTarget);

		addMessages(msgIds, pSource, pTarget);
		return pTarget;
	}

	@Override
	public Profile updateIntegratedProfile(List<String> msgIds, Profile pTarget) throws ProfileException {
		// Update profile with additional messages.
		String hl7Version = pTarget.getMetaData().getHl7Version();
		Profile pSource = profileRepository.findByScopeAndMetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version)
				.get(0);
		addMessages(msgIds, pSource, pTarget);
		return pTarget;
	}

	private void addMessages(List<String> msgIds, Profile pSource, Profile pTarget) {
		Messages messages = pTarget.getMessages();
		for (String msgId : msgIds) {
			Message m = pSource.getMessages().findOne(msgId);
			messages.addMessage(m);
			for (SegmentRefOrGroup sg : m.getChildren()) {
				if (sg instanceof SegmentRef) {
					addSegment((SegmentRef) sg, pSource, pTarget);
				} else if (sg instanceof Group) {
					addGroup((Group) sg, pSource, pTarget);
				}
			}
		}
	}

	private void addSegment(SegmentRef sref, Profile pSource, Profile pTarget) {
		Segments sgtsTarget = pTarget.getSegments();
		Segment sgt = pSource.getSegments().findOneSegmentById(sref.getRef());
		sgtsTarget.addSegment(sgt);
		for (Field f : sgt.getFields()) {
			Datatype dt = pSource.getDatatypes().findOne(f.getDatatype());
			Table vsd = pSource.getTables().findOneTableById(f.getTable());
			addDatatype(dt, pSource, pTarget);
			addTable(vsd, pSource, pTarget);
		}
	}

	private void addGroup(Group g, Profile pSource, Profile pTarget) {
		for (SegmentRefOrGroup sg : g.getChildren()) {
			if (sg instanceof SegmentRef) {
				addSegment((SegmentRef) sg, pSource, pTarget);
			} else if (sg instanceof Group) {
				addGroup((Group) sg, pSource, pTarget);
			}
		}
	}

	private void addDatatype(Datatype dt, Profile pSource, Profile pTarget) {
		Datatypes dtsSource= pSource.getDatatypes();
		Datatypes dtsTarget = pTarget.getDatatypes();
		Tables vsdTarget = pTarget.getTables();
		if (dt != null && !dtsTarget.getChildren().contains(dt)){
			dtsTarget.addDatatype(dt);
			for (Component cpt: dt.getComponents()){
				addDatatype(dtsSource.findOne(cpt.getDatatype()), pSource, pTarget);
				addTable(vsdTarget.findOneTableById(cpt.getTable()), pSource, pTarget);
			}
		}
	}

	private void addTable(Table vsd, Profile pSource, Profile pTarget) {
		Tables vsdTarget = pTarget.getTables();
		if (vsd != null && !vsdTarget.getChildren().contains(vsd)){
			vsdTarget.addTable(vsd);
		}
	}

	@Override
	public List<Profile> findProfilesByHl7Versions() {
		// Fetching all HL7Standard profiles
		return profileRepository.findByScope(IGDocumentScope.HL7STANDARD);
	}

}
