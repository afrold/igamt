/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

/**
 * 
 * @author OMR
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageEventTree;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.Event;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageEventFactory;

@Service
public class IGDocumentCreationImpl implements IGDocumentCreationService {


	private Logger log = LoggerFactory.getLogger(IGDocumentCreationImpl.class);

	@Autowired
	private IGDocumentRepository igdocumentRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SegmentLibraryRepository segmentLibraryRepository;

	@Autowired
	private SegmentRepository segmentRepository;

	@Autowired
	private DatatypeLibraryRepository datatypeLibraryRepository;

	@Autowired
	private DatatypeRepository datatypeRepository;
	
	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private TableLibraryRepository tableLibraryRepository;

	@Autowired
	private TableRepository tableRepository;
  @Autowired
  private ProfileComponentLibraryRepository profileComponentLibraryRepository;

	@Autowired
	private MessageEventFactory messageEventFactory;

	@Override
	public List<String> findHl7Versions() {
		// fetching messages of version hl7Version
		return igdocumentRepository.findHl7Versions();
	}

	@Override
	public List<MessageEventTree> findMessageEvents(String hl7Version) {
		List<IGDocument> igds = igdocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version);
		List<MessageEventTree> messageEvents = new ArrayList<MessageEventTree>();
		if (!igds.isEmpty()) {
			IGDocument igd = igds.get(0);
			Messages msgs = igd.getProfile().getMessages();
			messageEvents = messageEventFactory.createMessageEvents(msgs, hl7Version);
		} else {
			log.debug("IGDocument Not found for hl7Version=" + hl7Version);
		}
		return messageEvents;
	}

	@Override
	public IGDocument createIntegratedIGDocument(List<MessageEvents> msgEvts, DocumentMetaData metadata,
			String hl7Version, Long accountId) throws IGDocumentException {
		// Creation of profile
		IGDocument dSource = igdocumentRepository.findStandardByVersion(hl7Version).get(0);
		IGDocument dTarget = new IGDocument();
		dTarget.setAccountId(accountId);
		Profile pTarget = new Profile();
		pTarget.setAccountId(accountId);

		// Setting igDocument metaData
		DocumentMetaData metaData = metadata;
		dTarget.setMetaData(metaData);
		Date date = new Date();
		dTarget.setDateUpdated(new Date());
		pTarget.setDateUpdated(new Date());
		// metaData.setVersion("1.0");
		// metaData.setIdentifier("Default Identifier");
		// metaData.setSubTitle(metadata.getSubTitle());
		// metaData.setTitle(metadata.getTitle());

		// Setting profile metaData
		ProfileMetaData profileMetaData = new ProfileMetaData();
		pTarget.setMetaData(profileMetaData);
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
		msgsTarget.setSectionContent(dSource.getProfile().getMessages().getSectionContents());
		//msgsTarget.setSectionDescription(dSource.getProfile().getMessages().getSectionDescription());
		msgsTarget.setSectionPosition(dSource.getProfile().getMessages().getSectionPosition());
		SegmentLibrary sgtsTarget = new SegmentLibrary();
		sgtsTarget.setMetaData(dSource.getProfile().getSegmentLibrary().getMetaData());
		sgtsTarget.setScope(Constant.SCOPE.USER);
		segmentLibraryRepository.save(sgtsTarget);

		sgtsTarget.setSectionTitle(dSource.getProfile().getSegmentLibrary().getSectionTitle());
		sgtsTarget.setSectionContent(dSource.getProfile().getSegmentLibrary().getSectionContents());
		//sgtsTarget.setSectionDescription(dSource.getProfile().getSegmentLibrary().getSectionDescription());
		sgtsTarget.setSectionPosition(dSource.getProfile().getSegmentLibrary().getSectionPosition());
		DatatypeLibrary dtsTarget = new DatatypeLibrary();
		dtsTarget.setMetaData(dSource.getProfile().getDatatypeLibrary().getMetaData());
		dtsTarget.setScope(Constant.SCOPE.USER);
		datatypeLibraryRepository.save(dtsTarget);
		dtsTarget.setSectionTitle(dSource.getProfile().getDatatypeLibrary().getSectionTitle());
		dtsTarget.setSectionContent(dSource.getProfile().getDatatypeLibrary().getSectionContents());
		//dtsTarget.setSectionDescription(dSource.getProfile().getDatatypeLibrary().getSectionDescription());
		dtsTarget.setSectionPosition(dSource.getProfile().getDatatypeLibrary().getSectionPosition());
		TableLibrary tabTarget = new TableLibrary();
		tabTarget.setMetaData(dSource.getProfile().getTableLibrary().getMetaData());
		tabTarget.setScope(Constant.SCOPE.USER);
		tableLibraryRepository.save(tabTarget);
		tabTarget.setSectionTitle(dSource.getProfile().getTableLibrary().getSectionTitle());
		tabTarget.setSectionContent(dSource.getProfile().getTableLibrary().getSectionContents());
		//tabTarget.setSectionDescription(dSource.getProfile().getTableLibrary().getSectionDescription());
		tabTarget.setSectionPosition(dSource.getProfile().getTableLibrary().getSectionPosition());
        ProfileComponentLibrary profileComponentLibrary=new ProfileComponentLibrary();

		pTarget.setMessages(msgsTarget);
		pTarget.setSegmentLibrary(sgtsTarget);
		pTarget.setDatatypeLibrary(dtsTarget);
		pTarget.setTableLibrary(tabTarget);
        pTarget.setProfileComponentLibrary(profileComponentLibrary);


		addSections(dSource, dTarget);
		addMessages(msgEvts, dSource.getProfile(), pTarget);

		dTarget.setProfile(pTarget);
		segmentLibraryRepository.save(sgtsTarget);
		datatypeLibraryRepository.save(dtsTarget);
		tableLibraryRepository.save(tabTarget);
        profileComponentLibraryRepository.save(profileComponentLibrary);

		igdocumentRepository.save(dTarget);
		return dTarget;
	}

	@Override
	public IGDocument updateIntegratedIGDocument(List<MessageEvents> msgEvts, IGDocument dTarget)
			throws IGDocumentException {
		// Update profile with additional messages.
		String hl7Version = dTarget.getProfile().getMetaData().getHl7Version();
		IGDocument dSource = igdocumentRepository.findStandardByVersion(hl7Version).get(0);
		addMessages(msgEvts, dSource.getProfile(), dTarget.getProfile());
		dTarget.setDateUpdated(new Date());
		igdocumentRepository.save(dTarget);
		return dTarget;
	}

	private void addSections(IGDocument dSource, IGDocument dTarget) {
		dTarget.setChildSections(dSource.getChildSections());
	}

	private void addMessages(List<MessageEvents> msgEvts, Profile pSource, Profile pTarget) throws IGDocumentException {
		Messages messages = pTarget.getMessages();
		messages.setSectionTitle(pSource.getMessages().getSectionTitle());
		messages.setSectionPosition(pSource.getMessages().getSectionPosition());
		messages.setType(pSource.getMessages().getType());
		int maxPos = findMaxPosition(pTarget.getMessages());
		try {
			for (MessageEvents msgEvt : msgEvts) {
				Message m = pSource.getMessages().findOne(msgEvt.getId());
				Message m1 = null;
				m1 = m.clone();
				m1.setId(null);
				m1.setScope(Constant.SCOPE.USER);
				Iterator<Event> itr = msgEvt.getChildren().iterator();
				if (itr.hasNext()) {
					String event = itr.next().getName();
					log.debug("msgEvt=" + msgEvt.getId() + " " + event);
					m1.setEvent(event);
				} else {
					log.error("MessageEvent contains no events id=" + msgEvt.getId() + " name=" + msgEvt.getName());
				}
				String name = m1.getMessageType() + "^" + m1.getEvent() + "^" + m1.getStructID();
				log.debug("Message.name=" + name);
				m1.setName(name);
				m1.setPosition(++maxPos);
				m1.setDateUpdated(new Date());
				
				for(ValueSetOrSingleCodeBinding vsb:m1.getValueSetBindings()){
					Table t = tableRepository.findOne(vsb.getTableId());
					if (t != null) {
						addTable(t, pSource, pTarget);
					}
				}
				
				messageRepository.save(m1);
				log.info("a pos=" + m1.getPosition());
				messages.addMessage(m1);
				log.info("p pos=" + m1.getPosition());
				for (SegmentRefOrGroup sg : m.getChildren()) {
					if (sg instanceof SegmentRef) {
						addSegment((SegmentRef) sg, pSource, pTarget);
					} else if (sg instanceof Group) {
						addGroup((Group) sg, pSource, pTarget);
					}
				}
			}
		} catch (Exception e) {
			log.error("Message error==>", e);
			throw new IGDocumentException(e);
		}
	}

	int findMaxPosition(Messages msgs) {
		int maxPos = 0;
		for (Message msg : msgs.getChildren()) {
			maxPos = Math.max(maxPos, msg.getPosition());
		}
		return maxPos;
	}

	private void addSegment(SegmentRef sref, Profile pSource, Profile pTarget) {
		SegmentLibrary sgtsSource = pSource.getSegmentLibrary();
		SegmentLibrary sgtsTarget = pTarget.getSegmentLibrary();
		sgtsTarget.setType(pSource.getSegmentLibrary().getType());
		SegmentLink sgt = sref.getRef();
		sgtsTarget.addSegment(sgt);
		Segment seg = segmentRepository.findOne(sref.getRef().getId());
		// if (SCOPE.USER == seg.getScope()) {
		// seg.setId(null);
		// seg.getLibIds().remove(sgtsSource.getId());
		// }
		seg.getLibIds().add(sgtsTarget.getId());
		for(ValueSetOrSingleCodeBinding vsb:seg.getValueSetBindings()){
			Table t = tableRepository.findOne(vsb.getTableId());
			if (t != null) {
				addTable(t, pSource, pTarget);
			}
		}
		/*
		if(seg.getDynamicMappingDefinition() != null && seg.getDynamicMappingDefinition().getMappingStructure() != null && seg.getDynamicMappingDefinition().getMappingStructure().getReferenceValueSetId() != null){
			Table t = tableRepository.findOne(seg.getDynamicMappingDefinition().getMappingStructure().getReferenceValueSetId());
			if (t != null) {
				addTable(t, pSource, pTarget);
				
				for(Code c : t.getCodes()){
					String dtName = c.getValue();
					String hl7Version = null;
					hl7Version = t.getHl7Version();
					if(hl7Version == null) hl7Version = seg.getHl7Version();
					if(hl7Version == null) hl7Version = pSource.getMetaData().getHl7Version();
					if(hl7Version == null) hl7Version = "2.8.2";
					
					
					Datatype dt = datatypeService.findByNameAndVesionAndScope(dtName, hl7Version, "HL7STANDARD");
					if(dt != null) addDatatype(dt, pSource, pTarget);
				}
				
			}	
		}
		*/
		if(seg.getDynamicMappingDefinition() != null){
			List<DynamicMappingItem> items = seg.getDynamicMappingDefinition().getDynamicMappingItems();
			for(DynamicMappingItem item : items){
				Datatype dt = datatypeRepository.findOne(item.getDatatypeId());
				if (dt != null) {
					addDatatype(dt, pSource, pTarget);
				}
			}			
		}
		
		segmentRepository.save(seg);
		for (Field f : seg.getFields()) {
			Datatype dt = datatypeRepository.findOne(f.getDatatype().getId());
			if (dt != null) {
				addDatatype(dt, pSource, pTarget);
			}
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
		DatatypeLibrary dtsSource = pSource.getDatatypeLibrary();
		DatatypeLibrary dtsTarget = pTarget.getDatatypeLibrary();
		// if (SCOPE.HL7STANDARD == dt.getScope()) {
		// //dt.setId(null);
		// dt.getLibIds().remove(dtsSource.getId());
		// }

		dt.getLibIds().add(dtsTarget.getId());
		for(ValueSetOrSingleCodeBinding vsb:dt.getValueSetBindings()){
			Table t = tableRepository.findOne(vsb.getTableId());
			if (t != null) {
				addTable(t, pSource, pTarget);
			}
		}
		datatypeRepository.save(dt);
		DatatypeLink link = new DatatypeLink(dt.getId(), dt.getName(), dt.getExt());
		if (!dtsTarget.getChildren().contains(link)) {
			dtsTarget.addDatatype(link);
			for (Component cpt : dt.getComponents()) {
				Datatype dt1 = datatypeRepository.findOne(cpt.getDatatype().getId());
				addDatatype(dt1, pSource, pTarget);
			}
		}
	}

	private void addTable(Table vsd, Profile pSource, Profile pTarget) {
		TableLibrary vsdSource = pTarget.getTableLibrary();
		TableLibrary vsdTarget = pTarget.getTableLibrary();
		//
		// if (SCOPE.USER == vsd.getScope()) {
		// vsd.setId(null);
		// vsd.getLibIds().remove(vsdSource.getId());
		// }
		vsd.getLibIds().add(vsdTarget.getId());
		tableRepository.save(vsd);
		vsdTarget.addTable(vsd);
	}

	@Override
	public List<IGDocument> findIGDocumentsByHl7Versions() {
		// Fetching all HL7Standard profiles
		return igdocumentRepository.findByScope(IGDocumentScope.HL7STANDARD);
	}
}
