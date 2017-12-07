package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "table-library")
public class TableLibrary extends Library implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  private Long accountId;

  private String ext;

  private String valueSetLibraryIdentifier;

  private String status;

  private String valueSetLibraryVersion;

  private String organizationName;


  private String dateCreated;

  private String profileName = "";

  private TableLibraryMetaData metaData;

  private Constant.SCOPE scope;

  private HashMap<String, Boolean> codePresence = new HashMap<String, Boolean>();

  public TableLibrary() {
    super();
    type = Constant.TABLELIBRARY;
    sectionPosition=6;
    codePresence = new HashMap<String, Boolean>();
  }

  private Set<TableLink> children = new HashSet<TableLink>();

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


  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getValueSetLibraryIdentifier() {
    return valueSetLibraryIdentifier;
  }

  public void setValueSetLibraryIdentifier(String valueSetLibraryIdentifier) {
    this.valueSetLibraryIdentifier = valueSetLibraryIdentifier;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getValueSetLibraryVersion() {
    return valueSetLibraryVersion;
  }

  public void setValueSetLibraryVersion(String valueSetLibraryVersion) {
    this.valueSetLibraryVersion = valueSetLibraryVersion;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getProfileName() {
    return profileName;
  }

  public void setProfileName(String profileName) {
    this.profileName = profileName;
  }

  public Set<TableLink> getChildren() {
    return children;
  }

  public void setChildren(Set<TableLink> children) {
    this.children = children;
  }

  public Constant.SCOPE getScope() {
    return scope;
  }

  public void setScope(Constant.SCOPE scope) {
    this.scope = scope;
  }

  public void addTable(TableLink tl) {
    if (tl != null)
      children.add(tl);
  }

  public void addTable(Table tab) {
    if (tab != null)
      children.add(new TableLink(tab.getId(), tab.getBindingIdentifier()));
  }

  public TableLink save(TableLink tl) {
    if (tl != null)
      children.add(tl);
    return tl;
  }

  public void delete(TableLink tl) {
    if (tl != null)
      this.children.remove(tl);
  }

  public TableLink findOneTableById(String id) {
    if (this.children != null) {
      for (TableLink tl : this.children) {
        if (id.equals(tl.getId())) {
          return tl;
        }
      }
    }

    return null;
  }

  public TableLibrary clone(HashMap<String, Table> tabRecords) throws CloneNotSupportedException {
    TableLibrary clonedTables = new TableLibrary();
    // clonedTables.setChildren(new HashSet<Table>());
    // for (Table tab : this.children) {
    // if (tabRecords.containsKey(tab.getId())) {
    // clonedTables.addTable(tabRecords.get(tab.getId()));
    // } else {
    // Table clone = tab.clone();
    // clone.setId(tab.getId());
    // tabRecords.put(tab.getId(), clone);
    // clonedTables.addTable(clone);
    // }
    // }

    return clonedTables;
  }

  public void merge(TableLibrary tabs) {
    this.getChildren().addAll(tabs.getChildren());
  }

  // @JsonIgnore
  // public Code getCode() {
  // //TODO Only byID constraints are considered; might want to consider
  // byName
  // Constraints constraints = new Constraints();
  // Context tabContext = new Context();
  //
  // Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
  // byNameOrByIDs = new HashSet<ByNameOrByID>();
  // for (Table d : this.getChildren()) {
  // ByID byID = new ByID();
  // byID.setByID(d.getLabel());
  // if (d.getConformanceStatements().size() > 0) {
  // byID.setConformanceStatements(d.getConformanceStatements());
  // byNameOrByIDs.add(byID);
  // }
  // }
  // tabContext.setByNameOrByIDs(byNameOrByIDs);
  //
  // constraints.setDatatypes(tabContext);
  // return constraints;
  // }

  public TableLibraryMetaData getMetaData() {
    return metaData;
  }

  public void setMetaData(TableLibraryMetaData metaData) {
    this.metaData = metaData;
  }

  public Set<TableLink> getTables() {
    return children;
  }

  public void setTables(Set<TableLink> children) {
    this.children = children;
  }

  @Override
  public TableLibrary clone() throws CloneNotSupportedException {
    TableLibrary clone = new TableLibrary();
    HashSet<TableLink> clonedChildren = new HashSet<TableLink>();
    for (TableLink tl : this.children) {
      clonedChildren.add(tl.clone());
    }
    clone.setChildren(clonedChildren);
    clone.setExt(this.getExt() + "-" + genRand());
    clone.setMetaData(this.getMetaData().clone());
    clone.setScope(this.getScope());
    clone.setSectionContent(this.getSectionContents());
   // clone.setSectionDescription(this.getSectionDescription());
    clone.setSectionPosition(this.getSectionPosition());
    clone.setSectionTitle(this.getSectionTitle());
    clone.setType(this.getType());
    clone.setExportConfig(this.exportConfig);
    clone.setCodePresence(codePresence);
    return clone;
  }

  private String genRand() {
    return Integer.toString(new Random().nextInt(100));
  }

  public void addTables(Set<TableLink> dtls) {
    children.addAll(dtls);
  }

  /**
   * @return the codePresence
   */
  public HashMap<String, Boolean> getCodePresence() {
    return codePresence;
  }

  /**
   * @param codePresence the codePresence to set
   */
  public void setCodePresence(HashMap<String, Boolean> codePresence) {
    this.codePresence = codePresence;
  }

}
