package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Section extends TextbasedSectionModel implements java.io.Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Section() {
		super();
		this.type = Constant.SECTION;
		this.id = ObjectId.get().toString();
	}
	
	public Section(String title) {
		super();
		this.type = Constant.SECTION;
		this.id = ObjectId.get().toString();
		this.sectionTitle = title;
	}
	
	@Id
	private String id;
	
	
	protected Set<Section> childSections = new HashSet<Section>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	@Override
	public Section clone() throws CloneNotSupportedException {
		Section clonedSection = new Section();
		
		clonedSection.setSectionContents(this.sectionContents);
		clonedSection.setSectionDescription(this.sectionDescription);
		clonedSection.setSectionPosition(this.sectionPosition);
		clonedSection.setSectionTitle(this.sectionTitle);
		clonedSection.setChildSections(new HashSet<Section>());
		
		for(Section s:this.childSections){
			clonedSection.addSection(s.clone());
		}
		
		return clonedSection;
	}
	
}
