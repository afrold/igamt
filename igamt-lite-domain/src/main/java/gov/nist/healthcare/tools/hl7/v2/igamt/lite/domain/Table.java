package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 26, 2015
 * 
 */
@Document(collection = "table")
public class Table extends DataModel implements Serializable, Comparable<Table>, Cloneable {

  /**
   * 
   */
  private static final long serialVersionUID = 734059059225906039L;

  @Id
  private String id;
  private String hl7Version;
  private Set<String> libIds = new HashSet<String>();
  private String bindingIdentifier;
  private String name;
  private boolean newTable;
  private String managedBy = Constant.Internal;
  private String referenceUrl;
  private String infoForExternal = "";


  @Deprecated
  private String description;
  private String version;
  private String oid = "";
  private Stability stability = Stability.Undefined;
  private Extensibility extensibility = Extensibility.Undefined;

  private ContentDefinition contentDefinition = ContentDefinition.Undefined;
  private String group;
  private int order;

  private List<Code> codes = new ArrayList<Code>();
  private Set<String> codeSystems = new HashSet<String>();


  private Constant.SCOPE scope;
  protected Long accountId;
  private String intensionalComment;

  @Deprecated
  protected String date;

  protected STATUS status;
  @Deprecated
  protected String comment = "";


  protected String defPreText = "";

  protected String defPostText = "";

  protected int numberOfCodes;

  private Set<ShareParticipantPermission> shareParticipantIds =
      new HashSet<ShareParticipantPermission>();

  public Table() {
    super();
    this.type = Constant.TABLE;
    managedBy = Constant.Internal;
    this.status = STATUS.UNPUBLISHED;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }

  public Constant.SCOPE getScope() {
    return scope;
  }

  public void setScope(Constant.SCOPE scope) {
    this.scope = scope;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getLibIds() {
    return libIds;
  }

  public void setLibIds(Set<String> libIds) {
    this.libIds = libIds;
  }

  public String getBindingIdentifier() {
    return bindingIdentifier;
  }

  public void setBindingIdentifier(String bindingIdentifier) {
    this.bindingIdentifier = bindingIdentifier;
  }

  @Deprecated
  public String getDescription() {
    return description;
  }

  @Deprecated
  public void setDescription(String description) {
    this.description = description;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public List<Code> getCodes() {
    return codes;
  }

  public void setCodes(List<Code> codes) {
    this.codes = codes;
  }

  public void addCode(Code c) {
    codes.add(c);
  }

  public boolean deleteCode(Code c) {
    return codes.remove(c);
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public Stability getStability() {
    return stability;
  }

  public void setStability(Stability stability) {
    this.stability = stability;
  }

  public Extensibility getExtensibility() {
    return extensibility;
  }

  public void setExtensibility(Extensibility extensibility) {
    this.extensibility = extensibility;
  }

  public ContentDefinition getContentDefinition() {
    return contentDefinition;
  }

  public void setContentDefinition(ContentDefinition contentDefinition) {
    this.contentDefinition = contentDefinition;
  }

  public Code findOneCodeById(String id) {
    if (this.codes != null)
      for (Code m : this.codes) {
        if (id.equals(m.getId())) {
          return m;
        }
      }
    return null;
  }

  public Code findOneCodeByValue(String value) {
    if (this.codes != null)
      for (Code c : this.codes) {
        if (value.equals(c.getValue())) {
          return c;
        }
      }
    return null;
  }



  public String getDefPreText() {
    return defPreText;
  }

  public void setDefPreText(String defPreText) {
    this.defPreText = defPreText;
  }

  public String getDefPostText() {
    return defPostText;
  }

  public void setDefPostText(String defPostText) {
    this.defPostText = defPostText;
  }

  @Override
  public String toString() {
    return "Table [id=" + id + ", bindingIdentifier=" + bindingIdentifier + ", name=" + name
        + ", description=" + description + ", version=" + version + ", oid=" + oid + ", stability="
        + stability + ", extensibility=" + extensibility + ", contentDefinition="
        + contentDefinition + ", group=" + group + ", order=" + order + "]";
  }

  @Override
  public int compareTo(Table o) {
    int x = String.CASE_INSENSITIVE_ORDER.compare(
        this.bindingIdentifier != null ? this.bindingIdentifier : "",
        o.bindingIdentifier != null ? o.bindingIdentifier : "");
    if (x == 0) {
      x = (this.bindingIdentifier != null ? this.bindingIdentifier : "")
          .compareTo(o.bindingIdentifier != null ? o.bindingIdentifier : "");
    }
    return x;
  }

  @Override
  public Table clone() throws CloneNotSupportedException {
    Table clonedTable = new Table();
    for (Code c : this.codes) {
      clonedTable.addCode(c.clone());
    }

    clonedTable.setId(ObjectId.get().toString());
    clonedTable.setExtensibility(extensibility);
    clonedTable.setBindingIdentifier(bindingIdentifier);
    clonedTable.setDescription(description);
    clonedTable.setContentDefinition(contentDefinition);
    clonedTable.setName(name);
    clonedTable.setOid(oid);
    clonedTable.setStability(stability);
    clonedTable.setVersion(version);
    clonedTable.setType(type);
    clonedTable.setGroup(group);
    clonedTable.setOrder(order);
    clonedTable.setDefPreText(defPreText);
    clonedTable.setDefPostText(defPostText);
    clonedTable.setAuthorNotes(super.getAuthorNotes());
    clonedTable.setCodeSystems(codeSystems);
    clonedTable.setSourceType(sourceType);
    clonedTable.setNumberOfCodes(numberOfCodes);
    clonedTable.setReferenceUrl(referenceUrl);
    clonedTable.setAuthorNotes("");

    return clonedTable;
  }


  /**
   * @return
   */


  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(id).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Table))
      return false;
    if (obj == this)
      return true;

    Table rhs = (Table) obj;
    return new EqualsBuilder().append(id, rhs.id).isEquals();
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  @Deprecated
  public String getDate() {
    return date;
  }

  @Deprecated
  public void setDate(String date) {
    this.date = date;
  }

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isNewTable() {
    return newTable;
  }

  public void setNewTable(boolean newTable) {
    this.newTable = newTable;
  }



  public Set<ShareParticipantPermission> getShareParticipantIds() {
    return shareParticipantIds;
  }

  public void setShareParticipantIds(Set<ShareParticipantPermission> shareParticipantIds) {
    this.shareParticipantIds = shareParticipantIds;
  }

  /**
   * @return the codeSystems
   */
  public Set<String> getCodeSystems() {
    return codeSystems;
  }

  /**
   * @param codeSystems the codeSystems to set
   */
  public void setCodeSystems(Set<String> codeSystems) {
    this.codeSystems = codeSystems;
  }

  /**
   * @return the managedBy
   */
  public String getManagedBy() {
    return managedBy;
  }

  /**
   * @param managedBy the managedBy to set
   */
  public void setManagedBy(String managedBy) {
    this.managedBy = managedBy;
  }

  /**
   * @return the externalUrl
   */
  public String getReferenceUrl() {
    return referenceUrl;
  }

  /**
   * @param externalUrl the externalUrl to set
   */
  public void setReferenceUrl(String externalUrl) {
    this.referenceUrl = externalUrl;
  }

  /**
   * @return the infoForExternal
   */
  public String getInfoForExternal() {
    return infoForExternal;
  }

  /**
   * @param infoForExternal the infoForExternal to set
   */
  public void setInfoForExternal(String infoForExternal) {
    this.infoForExternal = infoForExternal;
  }

  /**
   * @return the intensionalComment
   */
  public String getIntensionalComment() {
    return intensionalComment;
  }

  /**
   * @param intensionalComment the intensionalComment to set
   */
  public void setIntensionalComment(String intensionalComment) {
    this.intensionalComment = intensionalComment;
  }

  public int getNumberOfCodes() {
    return numberOfCodes;
  }

  public void setNumberOfCodes(int numberOfCodes) {
    this.numberOfCodes = numberOfCodes;
  }



}
