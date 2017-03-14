package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class ProfileComponent
    implements java.io.Serializable, Cloneable, Comparable<ProfileComponent> {

  private static final long serialVersionUID = 1L;

  public ProfileComponent() {

  }

  @Id
  private String id;
  private String name;
  private String description;
  private String Comment;
  private Date dateUpdated;


  private List<CompositeProfileStructure> appliedTo;
  private Set<SubProfileComponent> children = new HashSet<SubProfileComponent>();

  public List<CompositeProfileStructure> getAppliedTo() {
    return appliedTo;
  }


  public void setAppliedTo(List<CompositeProfileStructure> appliedTo) {
    this.appliedTo = appliedTo;
  }

  public void addAppliedTo(CompositeProfileStructure appliedTo) {
    this.appliedTo.add(appliedTo);
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


  public Date getDateUpdated() {
    return dateUpdated;
  }


  public void setDateUpdated(Date date) {
    this.dateUpdated = date;
  }



  public Set<SubProfileComponent> getChildren() {
    return children;
  }


  public void setChildren(Set<SubProfileComponent> children) {
    this.children = children;
  }

  public void addChildren(Set<SubProfileComponent> children) {
    this.children.addAll(children);
  }


  @Override
  public int compareTo(ProfileComponent o) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public ProfileComponent clone() {
    ProfileComponent clonedPc = new ProfileComponent();
    clonedPc.setName(this.name);
    clonedPc.setChildren(this.children);
    clonedPc.setId(this.getId());
    return clonedPc;
  }
}

