package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Profile extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public Profile() {
    super();
    this.type = Constant.PROFILE;
    scope = IGDocumentScope.PRELOADED;
    this.id = ObjectId.get().toString();
  }

  private IGDocumentScope scope;

  private String id;

  private ProfileMetaData metaData;

  @JsonIgnoreProperties(value = {"accountId", "date"})
  @DBRef
  private SegmentLibrary segmentLibrary = new SegmentLibrary();

  @JsonIgnoreProperties(value = {"accountId", "date"})
  @DBRef
  private DatatypeLibrary datatypeLibrary = new DatatypeLibrary();

  private Messages messages = new Messages();
  private CompositeProfiles compositeProfiles = new CompositeProfiles();



  @DBRef
  private TableLibrary tableLibrary = new TableLibrary();

  @DBRef
  private ProfileComponentLibrary profileComponentLibrary = new ProfileComponentLibrary();

  private Long accountId;

  protected String comment = "";

  protected String usageNote = "";

  private String changes = "";

  private String baseId = null; // baseId is the original version of the
  // profile that was cloned

  private String constraintId;

  private String sourceId;

  public String getBaseId() {
    return baseId;
  }

  public void setBaseId(String baseId) {
    this.baseId = baseId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProfileMetaData getMetaData() {
    return metaData;
  }

  public void setMetaData(ProfileMetaData metaData) {
    this.metaData = metaData;
  }

  public SegmentLibrary getSegmentLibrary() {
    return segmentLibrary;
  }

  public void setSegmentLibrary(SegmentLibrary segmentLibrary) {
    this.segmentLibrary = segmentLibrary;
  }

  public DatatypeLibrary getDatatypeLibrary() {
    return datatypeLibrary;
  }

  public void setDatatypeLibrary(DatatypeLibrary datatypeLibrary) {
    this.datatypeLibrary = datatypeLibrary;
  }

  public TableLibrary getTableLibrary() {
    return tableLibrary;
  }

  public void setTableLibrary(TableLibrary tableLibrary) {
    this.tableLibrary = tableLibrary;
  }

  public Messages getMessages() {
    return messages;
  }

  public void setMessages(Messages messages) {
    this.messages = messages;
  }

  public CompositeProfiles getCompositeProfiles() {
    return compositeProfiles;
  }

  public void setCompositeProfiles(CompositeProfiles compositeProfiles) {
    this.compositeProfiles = compositeProfiles;
  }

  public String getConstraintId() {
    return constraintId;
  }

  public void setConstraintId(String constraintId) {
    this.constraintId = constraintId;
  }

  /**
   * Do not set the version. Hibernate set the version automatically
   * 
   * @param version
   * @return
   */
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

  public IGDocumentScope getScope() {
    return scope;
  }

  public void setScope(IGDocumentScope scope) {
    this.scope = scope;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  @Override
  public String toString() {
    // return "Profile [id=" + id + ", metaData=" + metaData + ", messages="
    // + messages;
    return "Profile [id=" + id + ", metaData=" + metaData;
  }

  @Override
  public Profile clone() throws CloneNotSupportedException {
    Profile clonedProfile = new Profile();
    HashMap<String, Datatype> dtRecords = new HashMap<String, Datatype>();
    HashMap<String, Segment> segRecords = new HashMap<String, Segment>();
    HashMap<String, Table> tabRecords = new HashMap<String, Table>();
    HashMap<String, ProfileComponent> pcRecords = new HashMap<String, ProfileComponent>();


    clonedProfile.setChanges(changes);
    clonedProfile.setComment(comment);
    clonedProfile.setDatatypeLibrary(datatypeLibrary.clone(dtRecords, tabRecords));
    // clonedProfile.setSegmentLibrary(segmentLibrary.clone(segRecords, dtRecords, tabRecords));
    clonedProfile.setTableLibrary(tableLibrary.clone(tabRecords));
    clonedProfile.setMessages(messages.clone(dtRecords, segRecords, tabRecords));
    clonedProfile.setMetaData(metaData.clone());
    clonedProfile.setUsageNote(usageNote);
    clonedProfile.setAccountId(accountId);
    clonedProfile.setScope(scope);
    clonedProfile.setBaseId(baseId != null ? baseId : id);
    clonedProfile.setSourceId(id);
    clonedProfile.setConstraintId(constraintId);

    return clonedProfile;
  }

  public ProfileComponentLibrary getProfileComponentLibrary() {
    return profileComponentLibrary;
  }

  public void setProfileComponentLibrary(ProfileComponentLibrary profileComponentLibrary) {
    this.profileComponentLibrary = profileComponentLibrary;
  }

  public void merge(Profile p) {
    // Note: merge is used for creation of new profiles do we don't consider
    // constraints and annotations
    // in each profile, there is one message library with one message
    this.tableLibrary.merge(p.getTableLibrary());
    this.datatypeLibrary.merge(p.getDatatypeLibrary());
    this.segmentLibrary.merge(p.getSegmentLibrary());
    this.profileComponentLibrary.merge(p.getProfileComponentLibrary());

    for (Message m : p.getMessages().getChildren()) {
      this.messages.addMessage(m);
    }
  }
}
