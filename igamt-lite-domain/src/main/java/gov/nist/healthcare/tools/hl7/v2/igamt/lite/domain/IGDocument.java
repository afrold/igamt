package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "igdocument")
public class IGDocument extends DataModel implements java.io.Serializable,
		Cloneable {

	private static final long serialVersionUID = 1L;

	public IGDocument() {
		super();
		this.type = Constant.Document;
	}

	@Id
	private String id;

	private Long accountId;

	private String comment;

	private String usageNote;

	private DocumentMetaData metaData;
	
	private Profile profile;
	
	private IGDocumentScope scope;

	private Set<Section> childSections = new HashSet<Section>();
	
	
	@Override
	public IGDocument clone() throws CloneNotSupportedException {
		IGDocument clonedDocument = new IGDocument();
		clonedDocument.setMetaData(metaData.clone());
		clonedDocument.setProfile(profile.clone());
		clonedDocument.setChildSections(new HashSet<Section>());
		for(Section section:this.childSections){
			clonedDocument.addSection(section.clone());
		}

		return clonedDocument;
	}


	private void addSection(Section s) {
		s.setSectionPosition(this.childSections.size());
		this.childSections.add(s);
		
	}
	
	public void addProfile(Profile p) {
		this.setAccountId(p.getAccountId());
		this.setComment(p.getComment());
		this.setScope(p.getScope());
		this.setUsageNote(p.getUsageNote());
		
		DocumentMetaData documentMetaData = new DocumentMetaData();
		documentMetaData.setDate(p.getMetaData().getDate());
		documentMetaData.setExt(p.getMetaData().getExt());
		documentMetaData.setSubTitle(p.getMetaData().getSubTitle());
		documentMetaData.setTitle(p.getMetaData().getName());
		documentMetaData.setType(p.getMetaData().getType());
		documentMetaData.setVersion(p.getMetaData().getVersion());
		documentMetaData.setIdentifier(p.getMetaData().getIdentifier());
		documentMetaData.setOrgName(p.getMetaData().getOrgName());
		documentMetaData.setSpecificationName(p.getMetaData().getSpecificationName());
		documentMetaData.setStatus(p.getMetaData().getStatus());
		documentMetaData.setTopics(p.getMetaData().getTopics());
		
		this.setMetaData(documentMetaData);
		
		
		p.getMetaData().setExt(null);
		p.getMetaData().setSubTitle(null);
		
		if(this.childSections == null || this.childSections.size() == 0){
			Section section1 = new Section("Introduction");
			Section section1_1 = new Section("Purpose");
			Section section1_2 = new Section("Audience");
			Section section1_3 = new Section("Organization of this guide");
			Section section1_4 = new Section("Referenced profiles - antecedents");
			Section section1_5 = new Section("Scope");
			Section section1_5_1 = new Section("In Scope");
			Section section1_5_2 = new Section("Out of Scope");
			Section section1_6 = new Section("Key technical decisions [conventions]");
			
			Section section2 = new Section("Use Case");
			Section section2_1 = new Section("Actors");
			Section section2_2 = new Section("Use case assumptions");
			Section section2_2_1 = new Section("Pre Conditions");
			Section section2_2_2 = new Section("Post Condition");
			Section section2_2_3 = new Section("Functional Requirements");
			Section section2_3 = new Section("User story");
			Section section2_4 = new Section("Sequence diagram");
			Section section2_4_1 = new Section("Acknowledgement");
			Section section2_4_2 = new Section("Error Handling");
			
			section1_5.addSection(section1_5_1);
			section1_5.addSection(section1_5_2);
			section1.addSection(section1_1);
			section1.addSection(section1_2);
			section1.addSection(section1_3);
			section1.addSection(section1_4);
			section1.addSection(section1_5);
			section1.addSection(section1_6);
			
			section2_2.addSection(section2_2_1);
			section2_2.addSection(section2_2_2);
			section2_2.addSection(section2_2_3);
			section2_4.addSection(section2_4_1);
			section2_4.addSection(section2_4_2);
			section2.addSection(section2_1);
			section2.addSection(section2_2);
			section2.addSection(section2_3);
			section2.addSection(section2_4);
			this.addSection(section1);
			this.addSection(section2);
		}
		
		int positionMessageInfrastructure = this.childSections.size();
		
		p.setSectionPosition(positionMessageInfrastructure);
		p.setSectionTitle("Message Infrastructure");
		
		p.getMessages().setSectionPosition(0);
		p.getMessages().setSectionTitle("Conformance Profiles");
		
		int messagePositionNum = 0;
		for(Message m:p.getMessages().getChildren()){
			m.setSectionPosition(messagePositionNum);
			messagePositionNum = messagePositionNum + 1;
		}
		
		p.getSegments().setSectionPosition(1);
		p.getSegments().setSectionTitle("Segments and Field Descriptions");
		int segmentPositionNum = 0;
		for(Segment s:p.getSegments().getChildren()){
			s.setSectionPosition(segmentPositionNum);
			segmentPositionNum = segmentPositionNum + 1;
		}
		
		p.getDatatypes().setSectionPosition(2);
		p.getDatatypes().setSectionTitle("Datatypes");
		int datatypePositionNum = 0;
		for(Datatype d:p.getDatatypes().getChildren()){
			d.setSectionPosition(datatypePositionNum);
			datatypePositionNum = datatypePositionNum + 1;
		}
		
		p.getTables().setSectionPosition(3);
		p.getTables().setSectionTitle("Value Sets");
		int tablePositionNum = 0;
		for(Table t:p.getTables().getChildren()){
			t.setSectionPosition(tablePositionNum);
			tablePositionNum = tablePositionNum + 1;
		}
		
		this.setProfile(p);
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Long getAccountId() {
		return accountId;
	}


	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getUsageNote() {
		return usageNote;
	}


	public void setUsageNote(String usageNote) {
		this.usageNote = usageNote;
	}

	public DocumentMetaData getMetaData() {
		return metaData;
	}


	public void setMetaData(DocumentMetaData metaData) {
		this.metaData = metaData;
	}


	public Profile getProfile() {
		return profile;
	}


	public void setProfile(Profile profile) {
		this.profile = profile;
	}


	public Set<Section> getChildSections() {
		return childSections;
	}


	public void setChildSections(Set<Section> childSections) {
		this.childSections = childSections;
	}
	
	public void makeDefaultDocument(){
		//TODO
	}


	public IGDocumentScope getScope() {
		return scope;
	}


	public void setScope(IGDocumentScope scope) {
		this.scope = scope;
	}
	
	
	
}
