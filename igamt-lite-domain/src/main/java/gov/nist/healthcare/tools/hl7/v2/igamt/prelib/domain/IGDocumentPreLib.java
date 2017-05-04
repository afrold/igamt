package gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;



@Document(collection = "igdocument")
public class IGDocumentPreLib extends DataModel implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public IGDocumentPreLib() {
    super();
    this.type = Constant.Document;
  }

  @Id
  private String id;

  private Long accountId;

  private String comment;

  private String usageNote;

  private DocumentMetaDataPreLib metaData;

  private ProfilePreLib profile;

  private IGDocumentScope scope;

  private Set<Section> childSections = new HashSet<Section>();


  @Override
  public IGDocumentPreLib clone() throws CloneNotSupportedException {
    IGDocumentPreLib clonedDocument = new IGDocumentPreLib();
    clonedDocument.setMetaData(metaData.clone());
    clonedDocument.setProfile(profile.clone());
    clonedDocument.setChildSections(new HashSet<Section>());
    for (Section section : this.childSections) {
      clonedDocument.addSection(section.clone());
    }

    return clonedDocument;
  }

  private void addSection(Section s) {
    s.setSectionPosition(this.childSections.size());
    this.childSections.add(s);

  }

  public void addProfile(ProfilePreLib p) {
    this.setAccountId(p.getAccountId());
    this.setComment(p.getComment());
    this.setScope(p.getScope());
    this.setUsageNote(p.getUsageNote());

    DocumentMetaDataPreLib documentMetaData = new DocumentMetaDataPreLib();
    documentMetaData.setDate(Constant.mdy.format(new Date()));
    documentMetaData.setExt(p.getMetaData().getExt());
    documentMetaData.setSubTitle(p.getMetaData().getSubTitle());
    // documentMetaData.setTitle(p.getMetaData().getName());
    documentMetaData.setType(p.getMetaData().getType());
    // documentMetaData.setVersion(p.getMetaData().getVersion());
    // documentMetaData.setIdentifier(p.getMetaData().getProfileID());
    documentMetaData.setOrgName("NIST");
    documentMetaData.setSpecificationName(p.getMetaData().getSpecificationName());
    documentMetaData.setStatus(p.getMetaData().getStatus());
    documentMetaData.setTopics(p.getMetaData().getTopics());

    this.setMetaData(documentMetaData);


    p.getMetaData().setExt(null);
    p.getMetaData().setSubTitle(null);

    if (this.childSections == null || this.childSections.size() == 0) {
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
    p.getMessages().setType("messages");

    int messagePositionNum = 0;
    for (Message m : p.getMessages().getChildren()) {
      m.setPosition(messagePositionNum);
      messagePositionNum = messagePositionNum + 1;
    }

    p.getSegments().setSectionPosition(1);
    p.getSegments().setSectionTitle("Segments and Field Descriptions");
    p.getSegments().setType("segments");
    for (Segment s : p.getSegments().getChildren()) {
      for (Field f : s.getFields()) {
        if (f.getConfLength().equals("-1")) {
          f.setConfLength("");
        }

        if (f.getMinLength().equals(-1)) {
          f.setMinLength("0");
        }
      }
    }

    p.getDatatypes().setSectionPosition(2);
    p.getDatatypes().setSectionTitle("Datatypes");
    p.getDatatypes().setType("datatypes");
    for (Datatype d : p.getDatatypes().getChildren()) {
      for (Component c : d.getComponents()) {
        if (c.getConfLength().equals("-1")) {
          c.setConfLength("");
        }

        if (c.getMinLength().equals(-1)) {
          c.setMinLength("0");
        }
      }
    }

    p.getTables().setSectionPosition(3);
    p.getTables().setSectionTitle("Value Sets");
    p.getTables().setType("tables");
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

  public DocumentMetaDataPreLib getMetaData() {
    return metaData;
  }


  public void setMetaData(DocumentMetaDataPreLib metaData) {
    this.metaData = metaData;
  }


  public ProfilePreLib getProfile() {
    return profile;
  }


  public void setProfile(ProfilePreLib profile) {
    this.profile = profile;
  }


  public Set<Section> getChildSections() {
    return childSections;
  }


  public void setChildSections(Set<Section> childSections) {
    this.childSections = childSections;
  }

  public void makeDefaultDocument() {
    // TODO
  }


  public IGDocumentScope getScope() {
    return scope;
  }


  public void setScope(IGDocumentScope scope) {
    this.scope = scope;
  }



}
