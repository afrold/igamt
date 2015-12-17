package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Document extends DataModel implements java.io.Serializable,
		Cloneable {

	private static final long serialVersionUID = 1L;

	public Document() {
		super();
		this.type = Constant.Document;
	}

	@Id
	private String id;

	private Long accountId;

	private String comment = "";

	private String usageNote = "";

	private String changes = "";

	private DocumentMetaData documentMetaData;
	
	private Profile profile;

	private Set<Section> childSections = new HashSet<Section>();
	
	
	@Override
	public Document clone() throws CloneNotSupportedException {
		Document clonedDocument = new Document();
		clonedDocument.setDocumentMetaData(documentMetaData.clone());
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


	public String getChanges() {
		return changes;
	}


	public void setChanges(String changes) {
		this.changes = changes;
	}


	public DocumentMetaData getDocumentMetaData() {
		return documentMetaData;
	}


	public void setDocumentMetaData(DocumentMetaData documentMetaData) {
		this.documentMetaData = documentMetaData;
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
	
	
}
