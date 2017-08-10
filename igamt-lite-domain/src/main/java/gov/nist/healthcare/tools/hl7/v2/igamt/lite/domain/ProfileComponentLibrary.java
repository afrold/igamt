package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

@Document(collection = "profileComponent-library")
public class ProfileComponentLibrary extends TextbasedSectionModel
    implements java.io.Serializable, Cloneable {
  private static final long serialVersionUID = 1L;

  @Id
  private String id;
  private String name;

  private SCOPE scope;

  private Long accountId;

  public ProfileComponentLibrary() {
    super();
    type = Constant.ProfileComponentLibrary;
  }

  private Set<ProfileComponentLink> children = new HashSet<ProfileComponentLink>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }



  public Set<ProfileComponentLink> getChildren() {
    return children;
  }

  public void setChildren(Set<ProfileComponentLink> children) {
    this.children = children;
  }


  public void addProfileComponent(ProfileComponentLink pcLink) {
    children.add(pcLink);
  }



  public void delete(ProfileComponent pc) {
    this.children.remove(pc);
  }

  public void deleteAll(Set<ProfileComponentLink> pc) {
    this.children.removeAll(pc);
  }

  public ProfileComponentLink findOne(String pcId) {
    if (this.children != null) {
      for (ProfileComponentLink pcLink : this.children) {
        if (pcLink.getId().equals(pcId)) {
          return pcLink;
        }
      }
    }

    return null;
  }

  // public ProfileComponent findOneByPath(String path) {
  // if (this.children != null) {
  // for (ProfileComponent pc : this.children) {
  // if (pc.getPath().equals(path)) {
  // return pc;
  // }
  // }
  // }
  //
  // return null;
  // }
  public void addProfileComponentLinks(Set<ProfileComponentLink> pcLinks) {
    children.addAll(pcLinks);
  }

  @Override
  public ProfileComponentLibrary clone() throws CloneNotSupportedException {
    ProfileComponentLibrary clone = new ProfileComponentLibrary();

    HashSet<ProfileComponentLink> clonedChildren = new HashSet<ProfileComponentLink>();
    for (ProfileComponentLink pcLink : this.children) {
      clonedChildren.add(pcLink.clone());
    }
    clone.setChildren(clonedChildren);
    clone.setName(this.getName());
    clone.setType(this.getType());
    return clone;
  }

  public void merge(ProfileComponentLibrary pcLib) {
    pcLib.getChildren().addAll(pcLib.getChildren());
  }

  public SCOPE getScope() {
    return scope;
  }

  public void setScope(SCOPE scope) {
    this.scope = scope;
  }



}
