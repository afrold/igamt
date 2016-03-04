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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.Event;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEventFactory;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.EventNotSetException;

@Service
public class IGDocumentCreationImpl implements IGDocumentCreationService {

	private Logger log = LoggerFactory.getLogger(IGDocumentCreationImpl.class);

	private boolean CREATE = true;
	private boolean UPDATE = false;
	
	@Autowired
	private IGDocumentRepository igdocumentRepository;

	@Autowired
	private IGDocumentService igdocumentService;

	@Override
	public List<String> findHl7Versions() {
		// fetching messages of version hl7Version
		return igdocumentRepository.findHl7Versions();
	}

	@Override
	public List<MessageEvents> summary(String hl7Version) {
		List<IGDocument> igds = igdocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version);
		IGDocument igd = igds.get(0);
		MessageEventFactory mef = new MessageEventFactory(igd);
		Messages msgs = igd.getProfile().getMessages();
		List<MessageEvents> rval = mef.createMessageEvents(msgs);
		return rval;
	}

	@Override
	public IGDocument createIntegratedIGDocument(List<MessageEvents> msgEvts, String hl7Version, Long accountId)
			throws IGDocumentException {
		// Creation of profile
		IGDocument dSource = igdocumentRepository.findStandardByVersion(hl7Version).get(0);
		IGDocument dTarget = new IGDocument();
		dTarget.setAccountId(accountId);
		Profile pTarget = new Profile();
		pTarget.setAccountId(accountId);

		// Setting igDocument metaData
		DocumentMetaData metaData = new DocumentMetaData();
		dTarget.setMetaData(metaData);
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		metaData.setDate(dateFormat.format(date));
		metaData.setVersion("1.0");
		metaData.setIdentifier("Default Identifier");
		metaData.setSubTitle("Default Sub Title");
		metaData.setTitle("Default Title");

		// Setting profile metaData
		ProfileMetaData profileMetaData = new ProfileMetaData();
		pTarget.setMetaData(profileMetaData);
		profileMetaData.setDate(dateFormat.format(date));
		profileMetaData.setVersion("1.0");
		profileMetaData.setName("Default name");
		profileMetaData.setOrgName("Default org name");
		profileMetaData.setSubTitle("Subtitle");

		profileMetaData.setHl7Version(hl7Version);
		profileMetaData.setStatus("Draft");

		// Setting profile info
		pTarget.setScope(IGDocumentScope.USER);
		pTarget.setComment("Created " + date.toString());
		pTarget.setSourceId(dSource.getProfile().getId());
		pTarget.setBaseId(dSource.getProfile().getId());
		pTarget.setSectionTitle(dSource.getProfile().getSectionTitle());
		pTarget.setSectionContents(dSource.getProfile().getSectionContents());
		pTarget.setSectionDescription(dSource.getProfile().getSectionDescription());
		pTarget.setSectionPosition(dSource.getProfile().getSectionPosition());
		// Setting IGDocument info
		dTarget.setScope(IGDocumentScope.USER);
		dTarget.setComment("Created " + date.toString());

		// Filling libraries--was
		Messages msgsTarget = new Messages();
		msgsTarget.setSectionTitle(dSource.getProfile().getMessages().getSectionTitle());
		msgsTarget.setSectionContents(dSource.getProfile().getMessages().getSectionContents());
		msgsTarget.setSectionDescription(dSource.getProfile().getMessages().getSectionDescription());
		msgsTarget.setSectionPosition(dSource.getProfile().getMessages().getSectionPosition());
		Segments sgtsTarget = new Segments();
		sgtsTarget.setSectionTitle(dSource.getProfile().getSegments().getSectionTitle());
		sgtsTarget.setSectionContents(dSource.getProfile().getSegments().getSectionContents());
		sgtsTarget.setSectionDescription(dSource.getProfile().getSegments().getSectionDescription());
		sgtsTarget.setSectionPosition(dSource.getProfile().getSegments().getSectionPosition());
		Datatypes dtsTarget = new Datatypes();
		dtsTarget.setSectionTitle(dSource.getProfile().getDatatypes().getSectionTitle());
		dtsTarget.setSectionContents(dSource.getProfile().getDatatypes().getSectionContents());
		dtsTarget.setSectionDescription(dSource.getProfile().getDatatypes().getSectionDescription());
		dtsTarget.setSectionPosition(dSource.getProfile().getDatatypes().getSectionPosition());
		Tables tabTarget = new Tables();
		tabTarget.setSectionTitle(dSource.getProfile().getTables().getSectionTitle());
		tabTarget.setSectionContents(dSource.getProfile().getTables().getSectionContents());
		tabTarget.setSectionDescription(dSource.getProfile().getTables().getSectionDescription());
		tabTarget.setSectionPosition(dSource.getProfile().getTables().getSectionPosition());
		pTarget.setMessages(msgsTarget);
		pTarget.setSegments(sgtsTarget);
		pTarget.setDatatypes(dtsTarget);
		pTarget.setTables(tabTarget);

		addSections(dSource, dTarget);
		addMessages(msgEvts, dSource.getProfile(), pTarget, CREATE);

		dTarget.setProfile(pTarget);

		return dTarget;
	}

	@Override
	public IGDocument updateIntegratedIGDocument(List<MessageEvents> msgEvts, IGDocument dTarget)
			throws IGDocumentException {
		// Update profile with additional messages.
		String hl7Version = dTarget.getProfile().getMetaData().getHl7Version();
		IGDocument dSource = igdocumentRepository.findStandardByVersion(hl7Version).get(0);
		addMessages(msgEvts, dSource.getProfile(), dTarget.getProfile(), UPDATE);
		return dTarget;
	}

	private void addSections(IGDocument dSource, IGDocument dTarget) {
		dTarget.setChildSections(dSource.getChildSections());
	}

	private void addMessages(List<MessageEvents> msgEvts, Profile pSource, Profile pTarget, boolean create) {
		Messages messages = pTarget.getMessages();
		messages.setType(pSource.getMessages().getType());
		for (MessageEvents msgEvt : msgEvts) {
			Message m = pSource.getMessages().findOne(msgEvt.getId());
			Message m1 = null;
			try {
				m1 = m.clone();
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			m1.setId(ObjectId.get().toString());
			Iterator<Event> itr = msgEvt.getChildren().iterator();
			if (itr.hasNext()) {
				String event = itr.next().getName();
				log.debug("msgEvt=" + msgEvt.getId() + " " + event);
				m1.setEvent(event);
			} else {
				try {
					throw new EventNotSetException("MessageEvent id=" + msgEvt.getId() + " name=" + msgEvt.getName());
				} catch (EventNotSetException e) {
					log.error("Event was set to \"event unk\"", e);
				}
				ObjectId.get().toString();
				m1.setEvent("event unk");
			}
			String name = m1.getMessageType() + "^" + m1.getEvent() + "^" + m1.getStructID();
			log.debug("Message.name=" + name);
			m1.setName(name);
			messages.addMessage(m1);
			if(create) {
			for (SegmentRefOrGroup sg : m.getChildren()) {
				if (sg instanceof SegmentRef) {
					addSegment((SegmentRef) sg, pSource, pTarget);
				} else if (sg instanceof Group) {
					addGroup((Group) sg, pSource, pTarget);
				}
			}
			}
		}
	}

	private void addSegment(SegmentRef sref, Profile pSource, Profile pTarget) {
		Segments sgtsTarget = pTarget.getSegments();
		sgtsTarget.setType(pSource.getSegments().getType());
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
		Datatypes dtsSource = pSource.getDatatypes();
		Datatypes dtsTarget = pTarget.getDatatypes();
		dtsTarget.setType(dtsSource.getType());
		Tables vsdTarget = pTarget.getTables();
		if (dt != null && !dtsTarget.getChildren().contains(dt)) {
			dtsTarget.addDatatype(dt);
			for (Component cpt : dt.getComponents()) {
				addDatatype(dtsSource.findOne(cpt.getDatatype()), pSource, pTarget);
				addTable(vsdTarget.findOneTableById(cpt.getTable()), pSource, pTarget);
			}
		}
	}

	private void addTable(Table vsd, Profile pSource, Profile pTarget) {
		Tables vsdTarget = pTarget.getTables();
		vsdTarget.setType(pSource.getTables().getType());
		if (vsd != null && !vsdTarget.getChildren().contains(vsd)) {
			vsdTarget.addTable(vsd);
		}
	}

	@Override
	public List<IGDocument> findIGDocumentsByHl7Versions() {
		// Fetching all HL7Standard profiles
		return igdocumentRepository.findByScope(IGDocumentScope.HL7STANDARD);
	}

}
