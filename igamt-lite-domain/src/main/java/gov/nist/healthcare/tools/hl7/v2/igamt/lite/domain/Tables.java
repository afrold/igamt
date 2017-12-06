package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

@Deprecated
public class Tables extends TextbasedSectionModel implements Serializable, Cloneable {

  /**
   * 
   */
  private static final long serialVersionUID = -2904036105687742572L;

  private String id;

  private String valueSetLibraryIdentifier;

  private String status;

  private String valueSetLibraryVersion;

  private String organizationName;

  private String name; // FIXME Not used in new model

  private String description;

  private String dateCreated;

  private String profileName = "";

  private Set<Table> children = new HashSet<Table>();

  public Tables() {
    super();
    this.type = Constant.TABLELIBRARY;
    this.id = ObjectId.get().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValueSetLibraryIdentifier() {
    return valueSetLibraryIdentifier;
  }

  public void setValueSetLibraryIdentifier(String valueSetLibraryIdentifier) {
    this.valueSetLibraryIdentifier = valueSetLibraryIdentifier;
  }

  public String getValueSetLibraryVersion() {
    return valueSetLibraryVersion;
  }

  public void setValueSetLibraryVersion(String valueSetLibraryVersion) {
    this.valueSetLibraryVersion = valueSetLibraryVersion;
  }

  public String getProfileName() {
    return profileName;
  }

  public void setProfileName(String profileName) {
    this.profileName = profileName;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<Table> getChildren() {
    return children;
  }

  public void setChildren(Set<Table> children) {
    this.children = children;
  }

  public void addTable(Table t) {
    children.add(t);
  }

  public Table findOneTableById(String id) {
    if (this.children != null)
      for (Table t : this.children) {
        if (t != null && t.getId() != null) {
          if (t.getId().equals(id)) {
            return t;
          }
        }
      }
    return null;
  }

  public Table findOneTableByNameAndByVersion(String name, String hl7Version) {
    if (this.children != null) {
      for (Table t : this.children) {
        if (t.getName().equals(name) && t.getVersion().equals(hl7Version)) {
          return t;
        }
      }
    }
    return null;
  }

  public Table findOneTableByName(String name) {
    if (this.children != null) {
      for (Table t : this.children) {
        if (t.getName().equals(name)) {
          return t;
        }
      }
    }
    return null;
  }

  public Code findOneCodeById(String id) {
    if (this.children != null) {
      for (Table m : this.children) {
        Code c = m.findOneCodeById(id);
        if (c != null) {
          return c;
        }
      }
    }
    return null;
  }

  public Code findOneCodeByValue(String value) {
    if (this.children != null) {
      for (Table t : this.children) {
        Code c = t.findOneCodeByValue(value);
        if (c != null) {
          return c;
        }
      }
    }
    return null;
  }

  public void delete(String id) {
    Table t = findOneTableById(id);
    if (t != null)
      this.children.remove(t);
  }

  public boolean deleteCode(String id) {
    if (this.children != null) {
      for (Table m : this.children) {
        Code c = m.findOneCodeById(id);
        if (c != null) {
          return m.deleteCode(c);
        }
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Tables [id=" + id + ", valueSetLibraryIdentifier=" + valueSetLibraryIdentifier
        + ", status=" + status + ", valueSetLibraryVersion=" + valueSetLibraryVersion
        + ", organizationName=" + organizationName + ", name=" + name + ", description="
        + description + ", dateCreated=" + dateCreated + ", children=" + children + "]";
  }

  @Override
  public Tables clone() throws CloneNotSupportedException {
    Tables clonedTables = new Tables();
    clonedTables.setChildren(new HashSet<Table>());
    for (Table t : this.children) {
      Table clone = t.clone();
      clonedTables.addTable(clone);
      // if (tableRecords.containsKey(t.getId())) {
      // clonedTables.addTable(tableRecords.get(t.getId()));
      // } else {
      // Table clone = t.clone();
      // tableRecords.put(t.getId(), clone);
      // clonedTables.addTable(clone);
      // }
    }

    clonedTables.setId(id);
    clonedTables.setDescription(description);
    clonedTables.setName(name);
    clonedTables.setOrganizationName(organizationName);
    clonedTables.setStatus(status);
    clonedTables.setValueSetLibraryIdentifier(valueSetLibraryIdentifier);
    clonedTables.setValueSetLibraryVersion(valueSetLibraryVersion);
    clonedTables.setProfileName(profileName);
    clonedTables.setDateCreated(dateCreated);

    return clonedTables;
  }

  public void merge(Tables tbls) {
    for (Table t : tbls.getChildren()) {
      if (this.findOneTableByNameAndByVersion(t.getName(), t.getVersion()) == null) {
        this.addTable(t);
      } else {
        t.setId(this.findOneTableByNameAndByVersion(t.getName(), t.getVersion()).getId());
      }
    }
  }

  // public void setPositionsOrder(){
  // List<Table> sortedList = new ArrayList<Table>(this.getChildren());
  // Collections.sort(sortedList);
  // for (Table elt: sortedList) {
  // elt.setSectionPosition(sortedList.indexOf(elt));
  // }
  // }

}
