package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Section extends TextbasedSectionModel
    implements java.io.Serializable, Cloneable, Comparable<Section> {

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

  protected String id;
  @JsonProperty("children")
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

  public void addSection(Section section) {
    section.setSectionPosition(this.childSections.size() + 1);
    @SuppressWarnings("unchecked")
    Set<Section> sections = this.childSections;
    sections.add(section);

    this.setChildSections(sections);
  }

  @Override
  public Section clone() throws CloneNotSupportedException {
    Section clonedSection = new Section();

    clonedSection.setSectionContents(this.sectionContents);
    clonedSection.setSectionDescription(this.sectionDescription);
    clonedSection.setSectionPosition(this.sectionPosition);
    clonedSection.setSectionTitle(this.sectionTitle);
    clonedSection.setChildSections(new HashSet<Section>());

    for (Object s : this.childSections) {
      clonedSection.addSection(((Section) s).clone());
    }

    return clonedSection;
  }

  public void merge(Section from) {
    this.setSectionContents(from.sectionContents);
    this.setSectionDescription(from.sectionDescription);
    this.setSectionPosition(from.sectionPosition);
    this.setSectionTitle(from.sectionTitle);
    this.setChildSections(from.getChildSections());
  }


  @Override
  public int compareTo(Section o) {
    return this.getSectionPosition() - o.getSectionPosition();
  }



}
