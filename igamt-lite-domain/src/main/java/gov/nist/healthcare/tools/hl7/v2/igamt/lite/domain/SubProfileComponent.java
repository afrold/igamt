package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class SubProfileComponent implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public SubProfileComponent() {

  }

  @Id
  private String id;
  private SubProfileComponentAttributes attributes;

  private String path;
  private Integer position;
  private ProfileComponentItemSource source;
  private Integer priority;
  private String type;
  private String name;
  private List<ValueSetOrSingleCodeBinding> oldValueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();
  private List<ValueSetOrSingleCodeBinding> valueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();
  private SingleElementValue oldSingleElementValues = new SingleElementValue();
  private SingleElementValue singleElementValues = new SingleElementValue();
  private List<Comment> oldComments = new ArrayList<Comment>();
  private List<Comment> comments = new ArrayList<Comment>();
  private Predicate oldPredicate = null;
  private String from;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public SubProfileComponentAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(SubProfileComponentAttributes attributes) {
    this.attributes = attributes;
  }


  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }


  public ProfileComponentItemSource getSource() {
    return source;
  }

  public void setSource(ProfileComponentItemSource source) {
    this.source = source;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }



  public List<ValueSetOrSingleCodeBinding> getOldValueSetBindings() {
    return oldValueSetBindings;
  }

  public void setOldValueSetBindings(List<ValueSetOrSingleCodeBinding> oldValueSetBindings) {
    this.oldValueSetBindings = oldValueSetBindings;
  }

  public List<ValueSetOrSingleCodeBinding> getValueSetBindings() {
    return valueSetBindings;
  }

  public void setValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings) {
    this.valueSetBindings = valueSetBindings;
  }



  public SingleElementValue getOldSingleElementValues() {
    return oldSingleElementValues;
  }

  public void setOldSingleElementValues(SingleElementValue oldSingleElementValues) {
    this.oldSingleElementValues = oldSingleElementValues;
  }

  public SingleElementValue getSingleElementValues() {
    return singleElementValues;
  }

  public void setSingleElementValues(SingleElementValue singleElementValues) {
    this.singleElementValues = singleElementValues;
  }

  public List<Comment> getOldComments() {
    return oldComments;
  }

  public void setOldComments(List<Comment> oldComments) {
    this.oldComments = oldComments;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  public Predicate getOldPredicate() {
    return oldPredicate;
  }

  public void setOldPredicate(Predicate oldPredicate) {
    this.oldPredicate = oldPredicate;
  }

}
