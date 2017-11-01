package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "igdocument")
public class IGDocument extends DataModel implements java.io.Serializable, Cloneable {

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

  private int position;

  private Set<Section> childSections = new HashSet<Section>();

  private Set<ShareParticipantPermission> shareParticipantIds =
      new HashSet<ShareParticipantPermission>();

  @Transient
  private List<ShareParticipant> realUsers = new ArrayList<ShareParticipant>();

  @Transient
  private ShareParticipant owner;


  /**
   * @return the owner
   */
  public ShareParticipant getOwner() {
    return owner;
  }

  /**
   * @param owner the owner to set
   */
  public void setOwner(ShareParticipant owner) {
    this.owner = owner;
  }

  private void addSection(Section s) {
    s.setSectionPosition(this.childSections.size() + 1);
    this.childSections.add(s);

  }

  public void addProfile(Profile p, Set<Section> sections) {
    if (p.getAccountId() != null) {
      this.setAccountId(p.getAccountId());
    }

    this.setComment(p.getComment());
    this.setScope(p.getScope());
    this.setUsageNote(p.getUsageNote());

    DocumentMetaData documentMetaData = new DocumentMetaData();
    documentMetaData.setExt(p.getMetaData().getExt());
    documentMetaData.setSubTitle(p.getMetaData().getSubTitle());
    documentMetaData.setTitle(p.getMetaData().getName());
    documentMetaData.setVersion(p.getMetaData().getVersion());
    documentMetaData.setOrgName(p.getMetaData().getOrgName());
    documentMetaData.setSpecificationName(p.getMetaData().getSpecificationName());
    documentMetaData.setStatus(p.getMetaData().getStatus());
    documentMetaData.setTopics(p.getMetaData().getTopics());

    this.setMetaData(documentMetaData);


    p.getMetaData().setExt(null);
    p.getMetaData().setSubTitle(null);

    this.setChildSections(sections);


    Section section2 = new Section("Use Cases");
    Section section2_1 = new Section("Actors");
    Section section2_2 = new Section("General Assumptions");
    Section section2_2_1 = new Section("Assumptions");
    Section section2_2_2 = new Section("Pre-conditions");
    Section section2_2_3 = new Section("Post-conditions");
    Section section2_3 = new Section("Use Case");
    Section section2_3_1 = new Section("User Story");
    Section section2_3_2 = new Section("Specific Assumptions");
    Section section2_3_2_1 = new Section("Assumptions");
    Section section2_3_2_2 = new Section("Pre-conditions");
    Section section2_3_2_3 = new Section("Post-conditions");
    Section section2_3_3 = new Section("Scenario");
    Section section2_3_4 = new Section("Context");
    Section section2_3_5 = new Section("Interaction Model");
    Section section2_3_6 = new Section("Functional Requirements");

    section2_2.addSection(section2_2_1);
    section2_2.addSection(section2_2_2);
    section2_2.addSection(section2_2_3);
    section2_3.addSection(section2_3_1);
    section2_3.addSection(section2_3_2);
    section2_3.addSection(section2_3_3);
    section2_3.addSection(section2_3_4);
    section2_3.addSection(section2_3_5);
    section2_3.addSection(section2_3_6);
    section2_3_2.addSection(section2_3_2_1);
    section2_3_2.addSection(section2_3_2_2);
    section2_3_2.addSection(section2_3_2_3);

    section2.addSection(section2_1);
    section2.addSection(section2_2);
    section2.addSection(section2_3);

    this.addSection(section2);

    int positionMessageInfrastructure = this.childSections.size();

    p.setSectionPosition(positionMessageInfrastructure);
    p.setSectionTitle("Message Infrastructure");

    p.getMessages().setSectionPosition(0);
    p.getMessages().setSectionTitle("Conformance Profiles");
    p.getMessages().setType("messages");
    p.getMessages().setSectionContents("");

    int messagePositionNum = 1;
    for (Message m : p.getMessages().getChildren()) {
      m.setPosition(messagePositionNum);
      messagePositionNum = messagePositionNum + 1;
    }

    p.getSegmentLibrary().setSectionPosition(1);
    p.getSegmentLibrary().setSectionTitle("Segments and Field Descriptions");
    p.getSegmentLibrary().setType("segments");
    p.getSegmentLibrary().setSectionContents("");

    p.getDatatypeLibrary().setSectionPosition(2);
    p.getDatatypeLibrary().setSectionTitle("Datatypes");
    p.getDatatypeLibrary().setType("datatypes");
    p.getDatatypeLibrary().setSectionContents("");
    p.getTableLibrary().setSectionPosition(3);
    p.getTableLibrary().setSectionTitle("Value Sets");
    p.getTableLibrary().setType("tables");
    p.getTableLibrary().setSectionContents("xsx");

    this.setProfile(p);
  }

  public void addProfile(Profile p) {
    if (p.getAccountId() != null) {
      this.setAccountId(p.getAccountId());
    }

    this.setComment(p.getComment());
    this.setScope(p.getScope());
    this.setUsageNote(p.getUsageNote());

    DocumentMetaData documentMetaData = new DocumentMetaData();
    documentMetaData.setExt(p.getMetaData().getExt());
    documentMetaData.setSubTitle(p.getMetaData().getSubTitle());
    documentMetaData.setTitle(p.getMetaData().getName());
    documentMetaData.setVersion(p.getMetaData().getVersion());
    documentMetaData.setOrgName(p.getMetaData().getOrgName());
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

      Section section2 = new Section("Use Cases");
      Section section2_1 = new Section("Actors");
      Section section2_2 = new Section("General Assumptions");
      Section section2_2_1 = new Section("Assumptions");
      Section section2_2_2 = new Section("Pre-conditions");
      Section section2_2_3 = new Section("Post-conditions");
      Section section2_3 = new Section("Use Case");
      Section section2_3_1 = new Section("User Story");
      Section section2_3_2 = new Section("Specific Assumptions");
      Section section2_3_2_1 = new Section("Assumptions");
      Section section2_3_2_2 = new Section("Pre-conditions");
      Section section2_3_2_3 = new Section("Post-conditions");
      Section section2_3_3 = new Section("Scenario");
      Section section2_3_4 = new Section("Context");
      Section section2_3_5 = new Section("Interaction Model");
      Section section2_3_6 = new Section("Functional Requirements");

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
      section2_3.addSection(section2_3_1);
      section2_3.addSection(section2_3_2);
      section2_3.addSection(section2_3_3);
      section2_3.addSection(section2_3_4);
      section2_3.addSection(section2_3_5);
      section2_3.addSection(section2_3_6);
      section2_3_2.addSection(section2_3_2_1);
      section2_3_2.addSection(section2_3_2_2);
      section2_3_2.addSection(section2_3_2_3);

      section2.addSection(section2_1);
      section2.addSection(section2_2);
      section2.addSection(section2_3);
      this.addSection(section1);
      this.addSection(section2);
    }

    int positionMessageInfrastructure = this.childSections.size();

    p.setSectionPosition(positionMessageInfrastructure);
    p.setSectionTitle("Message Infrastructure");

    p.getMessages().setSectionPosition(0);
    p.getMessages().setSectionTitle("Conformance Profiles");
    p.getMessages().setType("messages");

    int messagePositionNum = 1;
    for (Message m : p.getMessages().getChildren()) {
      m.setPosition(messagePositionNum);
      messagePositionNum = messagePositionNum + 1;
    }

    p.getSegmentLibrary().setSectionPosition(1);
    p.getSegmentLibrary().setSectionTitle("Segments and Field Descriptions");
    p.getSegmentLibrary().setType("segments");


    p.getDatatypeLibrary().setSectionPosition(2);
    p.getDatatypeLibrary().setSectionTitle("Datatypes");
    p.getDatatypeLibrary().setType("datatypes");

    p.getTableLibrary().setSectionPosition(3);
    p.getTableLibrary().setSectionTitle("Value Sets");
    p.getTableLibrary().setType("tables");
    this.setProfile(p);
  }

  @Override
  public IGDocument clone() throws CloneNotSupportedException {
    IGDocument clonedDocument = new IGDocument();
    clonedDocument.setMetaData(metaData.clone());
    clonedDocument.setProfile(profile.clone());
    clonedDocument.setChildSections(new HashSet<Section>());
    for (Section section : this.childSections) {
      clonedDocument.addSection(section.clone());
    }

    return clonedDocument;
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

  public void makeDefaultDocument() {
    // TODO
  }


  public IGDocumentScope getScope() {
    return scope;
  }


  public void setScope(IGDocumentScope scope) {
    this.scope = scope;
  }

  public Set<ShareParticipantPermission> getShareParticipantIds() {
    if (shareParticipantIds == null)
      shareParticipantIds = new HashSet<ShareParticipantPermission>();
    return shareParticipantIds;
  }

  public void setShareParticipantIds(Set<ShareParticipantPermission> shareParticipantIds) {
    this.shareParticipantIds = shareParticipantIds;
  }

  /**
   * @return the position
   */
  public int getPosition() {
    return position;
  }

  /**
   * @param position the position to set
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * @return the realUsers
   */
  public List<ShareParticipant> getRealUsers() {
    if (realUsers == null) {
      realUsers = new ArrayList<ShareParticipant>();
    }
    return realUsers;
  }

  /**
   * @param realUsers the realUsers to set
   */
  public void setRealUsers(List<ShareParticipant> realUsers) {
    this.realUsers = realUsers;
  }



}
