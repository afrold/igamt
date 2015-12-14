package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;


public abstract class DocumentModel extends DataModel {	
	protected DocumentMetaData documentMetaData;
	
	protected Set<Section> childSections = new HashSet<Section>();

	public DocumentMetaData getDocumentMetaData() {
		return documentMetaData;
	}

	public void setDocumentMetaData(DocumentMetaData documentMetaData) {
		this.documentMetaData = documentMetaData;
	}

	public Set<Section> getChildSections() {
		return childSections;
	}

	public void setChildSections(Set<Section> childSections) {
		this.childSections = childSections;
	}
	
	public void addSection(Section section){
		section.setSectionPosition(this.childSections.size() + 1);
		childSections.add(section);
	}
}
