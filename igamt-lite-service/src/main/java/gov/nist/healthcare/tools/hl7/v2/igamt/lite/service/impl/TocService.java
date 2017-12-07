package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfiles;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.CompositeProfileSectionDataLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.DatatypeSectionLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.DocumentSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.MessageSectionData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.MessageSectionDataLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.ProfileComponentDataLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.RootSectionData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.SectionDataWithLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.SectionDataWithText;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.SegmentSectionDataLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections.TableSectionDataLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.UnchangedDataRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DataCorrectionSectionPosition;

@Service
public class TocService implements gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TocService {
	@Autowired
	IGDocumentService documentService;
	@Autowired
	UnchangedDataRepository unchangedData;

	@Autowired
	MessageService messageService;

	@Autowired
	SegmentService segmentService;
	@Autowired
	DatatypeService datatypeService;
	@Autowired
	DatatypeLibraryService datatypeLibraryService;
	@Autowired
	TableLibraryService tableLibraryService;

	@Autowired
	SegmentLibraryService segmentLibraryService;

	@Autowired
	TableService tableService;
	@Autowired
	DataCorrectionSectionPosition dataCorrectionSectionPosition;
	@Autowired
	private ProfileComponentLibraryService profileComponentLibraryService;
	@Autowired
	private ProfileComponentService profileComponentService;
	@Autowired

	private CompositeProfileStructureService compositeProfileStructureService;
	@Autowired
	private TableLibraryRepository tableLibraryRepository;

	@Override
	public DocumentSection buildTree(IGDocument ig) {
		DocumentSection toc = new DocumentSection();
		List<Section> sorted = new ArrayList<Section>();

		if (ig.getChildSections() != null && !ig.getChildSections().isEmpty()) {
			sorted.addAll(ig.getChildSections());
			Collections.sort(sorted);
		}
		RootSectionData data = new RootSectionData();
		data.setMetaData(ig.getMetaData());
		toc.setData(data);
		for (Section s : sorted) {
			toc.getChildren().add(createTextSection(s));
		}

		toc.getChildren().add(createProfileSection(ig.getProfile(), toc.getChildren().size() + 1));
		return toc;
	}

	private DocumentSection createTextSection(Section s) {
		DocumentSection ret = new DocumentSection();
		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(s.getSectionPosition());
		dataWithText.setSectionContent(s.getSectionContents());
		dataWithText.setSectionTitle(s.getSectionTitle());
		dataWithText.setReferenceId(s.getId());
		dataWithText.setReferenceType(s.getType());
		ret.setData(dataWithText);
		List<Section> sorted = new ArrayList<Section>();
		if (s.getChildSections() != null && !s.getChildSections().isEmpty())
			sorted.addAll(s.getChildSections());
		Collections.sort(sorted);
		for (Section sub : sorted) {
			ret.getChildren().add(createTextSection(sub));
		}
		return ret;
	}

	private DocumentSection createProfileSection(Profile p, int i) {

		DocumentSection ret = new DocumentSection();
		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(i);
		dataWithText.setSectionContent(p.getSectionContents());
		dataWithText.setSectionTitle(p.getSectionTitle());
		;
		dataWithText.setReferenceId(p.getId());
		dataWithText.setReferenceType(p.getType());
		ret.setData(dataWithText);
		ret.getChildren().add(createProfileComponentLibrarySection(p.getProfileComponentLibrary(), 1));
		ret.getChildren().add(createConformanceProfileSection(p.getMessages(), 2));
		ret.getChildren().add(createCompositeProfile(p.getCompositeProfiles(), 3));
		ret.getChildren().add(createSegmentLibrarySection(p.getSegmentLibrary(), 4));
		ret.getChildren().add(createDatatypeLibrary(p.getDatatypeLibrary(), 5));
		ret.getChildren().add(createTableLibrary(p.getTableLibrary(), 6));
		return ret;

	}

	private DocumentSection createTableLibrary(TableLibrary tableLibrary, int i) {
		// TODO Auto-generated method stub
		DocumentSection ret = new DocumentSection();

		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(i);
		dataWithText.setSectionContent(tableLibrary.getSectionContents());
		dataWithText.setSectionTitle("Value Sets");
		dataWithText.setReferenceId(tableLibrary.getId());
		
		dataWithText.setReferenceType(tableLibrary.getType());
		ret.setData(dataWithText);
		List<Table> children = tableLibraryService.findAllShortTablesByIds(tableLibrary.getId());
		for (Table table : children) {
			ret.getChildren().add(createSectionOfTable(table));
		}
		return ret;
	}

	private DocumentSection createSectionOfTable(Table table) {
		TableSectionDataLink dataLink= new TableSectionDataLink();
		dataLink.setBindingIdentifier(table.getBindingIdentifier());
		dataLink.setDescription(table.getDescription());
		dataLink.setName(table.getName());
		dataLink.setId(table.getId());
		dataLink.setScope(table.getScope());
		dataLink.setNumberOfCodes(table.getNumberOfCodes());
		dataLink.setType(table.getType());
		SectionDataWithLink dataWithLink = new SectionDataWithLink();
		
		dataWithLink.setRef(dataLink);
		DocumentSection  section  = new DocumentSection();
		section.setData(dataWithLink);
		return section;

	}

	private DocumentSection createDatatypeLibrary(DatatypeLibrary datatypeLibrary, int position) {
		DocumentSection ret = new DocumentSection();
		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(position);
		dataWithText.setSectionContent(datatypeLibrary.getSectionContents());
		dataWithText.setSectionTitle("Data Types");
		dataWithText.setReferenceId(datatypeLibrary.getId());
		dataWithText.setReferenceType(datatypeLibrary.getType());
		ret.setData(dataWithText);
		List<Datatype> datatypes = datatypeLibraryService.findDatatypesById(datatypeLibrary.getId());
		for (Datatype dt : datatypes) {
			ret.getChildren().add(createSectionOfDatatype(dt));
		}

		return ret;
	}

	private DocumentSection createSectionOfDatatype(Datatype dt) {
		// TODO Auto-generated method stub
		
		DocumentSection section = new DocumentSection();
		SectionDataWithLink dataLink = new SectionDataWithLink();
		DatatypeSectionLink ref= new DatatypeSectionLink();
		ref.setDescription(dt.getDescription());
		ref.setExt(dt.getExt());
		ref.setId(dt.getId());
		ref.setType(dt.getType());
		ref.setName(dt.getName());
		ref.setHl7Versions(dt.getHl7versions());
		ref.setHl7Version(dt.getHl7Version());
		ref.setScope(dt.getScope());
		ref.setStatus(dt.getStatus());
		dataLink.setRef(ref);
		section.setData(dataLink);
		return section;
	}

	private DocumentSection createSegmentLibrarySection(SegmentLibrary segmentLibrary, int position) {
		DocumentSection ret = new DocumentSection();
		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(position);
		dataWithText.setSectionContent(segmentLibrary.getSectionContents());
		dataWithText.setSectionTitle("Segments and Fields Description");
		dataWithText.setReferenceId(segmentLibrary.getId());
		dataWithText.setReferenceType(segmentLibrary.getType());
		ret.setData(dataWithText);
		
		List<Segment> children= segmentLibraryService.findSegmentsById(segmentLibrary.getId());
		for (Segment segment : children) {
			ret.getChildren().add(createSectionOfSegment(segment));
		}

		return ret;
	}

	private DocumentSection createSectionOfSegment(Segment seg) {
		
		DocumentSection section = new DocumentSection();
		SectionDataWithLink dataLink = new SectionDataWithLink();
		SegmentSectionDataLink ref= new SegmentSectionDataLink();
		ref.setDescription(seg.getDescription());
		ref.setExt(seg.getExt());
		ref.setId(seg.getId());
		ref.setName(seg.getName());
		ref.setType(seg.getType());
		ref.setHl7Version(seg.getHl7Version());
		ref.setScope(seg.getScope());
		dataLink.setRef(ref);
		section.setData(dataLink);
		return section;
	}

	private DocumentSection createCompositeProfile(CompositeProfiles compositeProfiles, int position) {
		DocumentSection ret = new DocumentSection();

		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(position);
		dataWithText.setSectionContent(compositeProfiles.getSectionContents());
		dataWithText.setSectionTitle("Composite Profiles");
		dataWithText.setReferenceId(compositeProfiles.getId());
		dataWithText.setReferenceType(compositeProfiles.getType());
		ret.setData(dataWithText);

		for (CompositeProfileStructure link : compositeProfiles.getChildren()) {
			ret.getChildren().add(createSectionOfCompositeProfile(link));
		}

		return ret;
	}

	private DocumentSection createSectionOfCompositeProfile(CompositeProfileStructure cp) {
		DocumentSection ret = new DocumentSection();
		SectionDataWithLink dataLink= new SectionDataWithLink();
		CompositeProfileSectionDataLink ref = new CompositeProfileSectionDataLink();
		ref.setExt(cp.getExt());
		ref.setName(cp.getName());
		ref.setDescription(cp.getDescription());
		dataLink.setRef(ref);
		ret.setData(dataLink);
		return ret;
	}

	private DocumentSection createConformanceProfileSection(Messages messages, int position) {
		DocumentSection ret = new DocumentSection();
		// ret.setChildren(new ArrayList<MessageSectionData>());

		SectionDataWithText dataWithText = new SectionDataWithText();
		dataWithText.setPosition(position);
		dataWithText.setSectionContent(messages.getSectionContents());
		dataWithText.setSectionTitle("Conformance Profiles");
		dataWithText.setReferenceId(messages.getId());
		dataWithText.setReferenceType(messages.getType());
		ret.setData(dataWithText);
		List<Message> sorted = new ArrayList<Message>();
		sorted.addAll(messages.getChildren());
		Collections.sort(sorted);

		for (Message link : sorted) {
			
			MessageSectionDataLink sectionDataLink = new MessageSectionDataLink();
			sectionDataLink.setDescription(link.getDescription());
			sectionDataLink.setIdentifier(link.getIdentifier());
			sectionDataLink.setMessageId(link.getMessageID());
			sectionDataLink.setMessageType(link.getMessageType());
			sectionDataLink.setPosition(link.getPosition());
			sectionDataLink.setType(link.getType());
			sectionDataLink.setId(link.getId());	
			 SectionDataWithLink data = new SectionDataWithLink();
			 data.setRef(sectionDataLink);
			DocumentSection section= new DocumentSection();
			section.setData( data);
			
			
			ret.getChildren().add(section);
		}

		return ret;
	}
	private DocumentSection createProfileComponentLibrarySection(ProfileComponentLibrary profileComponentLibrary,
			int position) {
		// TODO Auto-generated method stub
		DocumentSection ret = new DocumentSection();
		
		SectionDataWithText dataWithText = new SectionDataWithText();
		
		dataWithText.setPosition(position);
		dataWithText.setSectionContent(profileComponentLibrary.getSectionContents());
		dataWithText.setSectionTitle("Profile Components");
		dataWithText.setReferenceId(profileComponentLibrary.getId());
		dataWithText.setReferenceType("profilecomponents");
		ret.setData(dataWithText);
		List<ProfileComponent> pcs = profileComponentLibraryService.findProfileComponentsById(profileComponentLibrary.getId());
		if (!profileComponentLibrary.getChildren().isEmpty()) {
			for (ProfileComponent link : pcs) {
				ret.getChildren().add(createSectionOfProfileComponent(link));
			}
		}

		return ret;
	}

	private DocumentSection createSectionOfProfileComponent(ProfileComponent pc) {
		DocumentSection ret = new DocumentSection();
		SectionDataWithLink dataLink= new SectionDataWithLink();
		ProfileComponentDataLink ref = new ProfileComponentDataLink();
		ref.setId(pc.getId());
		ref.setType(pc.getType());
		ref.setName(pc.getName());
		ref.setDescription(pc.getDescription());
		dataLink.setRef(ref);
		ret.setData(dataLink);
		return ret;
	}

}
