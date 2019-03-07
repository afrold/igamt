package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

public class ProfileComponent
    implements java.io.Serializable, Cloneable, Comparable<ProfileComponent> {

  private static final long serialVersionUID = 1L;

  public ProfileComponent() {
    this.type = Constant.ProfileComponent;
  }

  @Id
  private String id;
  private String name;
  private String description;
  private String Comment;
  private String defPreText = "";
  private String defPostText = "";
  private Date dateUpdated;
  private List<String> compositeProfileStructureList = new ArrayList<String>();
  private List<SubProfileComponent> children = new ArrayList<SubProfileComponent>();
  private String type;
  private SCOPE scope;
  private STATUS status;
  protected String authorNotes = ""; 
  protected Long accountId;


  /**
   * @return the authorNotes
   */
  public String getAuthorNotes() {
    return authorNotes;
  }

  /**
   * @param authorNotes the authorNotes to set
   */
  public void setAuthorNotes(String authorNotes) {
    this.authorNotes = authorNotes;
  }



  public void addCompositeProfileStructure(String id) {
    if (this.compositeProfileStructureList == null) {
      this.compositeProfileStructureList = new ArrayList<>();
    }
    this.compositeProfileStructureList.add(id);
  }

  public void removeCompositeProfileStructure(String id) {
    String toRemove = "";
    for (String s : this.compositeProfileStructureList) {
      if (s.equals(id)) {
        toRemove = s;

      }
    }
    this.compositeProfileStructureList.remove(toRemove);
  }



  public List<String> getCompositeProfileStructureList() {
    return compositeProfileStructureList;
  }


  public void setCompositeProfileStructureList(List<String> compositeProfileStructureList) {
    this.compositeProfileStructureList = compositeProfileStructureList;
  }


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


  public String getDescription() {
    return description;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public String getComment() {
    return Comment;
  }


  public void setComment(String comment) {
    Comment = comment;
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

  public Date getDateUpdated() {
    return dateUpdated;
  }


  public void setDateUpdated(Date date) {
    this.dateUpdated = date;
  }



  public List<SubProfileComponent> getChildren() {
    return children;
  }


  public void setChildren(List<SubProfileComponent> children) {
    this.children = children;
  }

  public void addChildren(Set<SubProfileComponent> children) {
    this.children.addAll(children);
  }



  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int compareTo(ProfileComponent o) {
    // TODO Auto-generated method stub
    return 0;
  }



  public SCOPE getScope() {
    return scope;
  }

  public void setScope(SCOPE scope) {
    this.scope = scope;
  }
  
  
  

  public Long getAccountId() {
	return accountId;
}

public void setAccountId(Long accountId) {
	this.accountId = accountId;
}

@Override
  public ProfileComponent clone() {
    ProfileComponent clonedPc = new ProfileComponent();
    clonedPc.setName(this.name);
    clonedPc.setChildren(this.children);
    clonedPc.setId(this.getId());
    clonedPc.setAccountId(this.getAccountId());
    return clonedPc;
  }

public STATUS getStatus() {
	return status;
}

public void setStatus(STATUS status) {
	this.status = status;
}
}

