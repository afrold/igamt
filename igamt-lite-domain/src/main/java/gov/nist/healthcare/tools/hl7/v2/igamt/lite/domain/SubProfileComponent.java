package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;

public class SubProfileComponent implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public SubProfileComponent() {

  }

  @Id
  private String id;
  private SubProfileComponentAttributes attributes;

  private String path;
  private Integer position;
  private Integer priority;
  private String type;
  private String name;
  private List<ValueSetBinding> oldValueSetBindings = new ArrayList<ValueSetBinding>();
  private List<ValueSetBinding> valueSetBindings = new ArrayList<ValueSetBinding>();


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

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }



  public List<ValueSetBinding> getOldValueSetBindings() {
    return oldValueSetBindings;
  }

  public void setOldValueSetBindings(List<ValueSetBinding> oldValueSetBindings) {
    this.oldValueSetBindings = oldValueSetBindings;
  }

  public List<ValueSetBinding> getValueSetBindings() {
    return valueSetBindings;
  }

  public void setValueSetBindings(List<ValueSetBinding> valueSetBindings) {
    this.valueSetBindings = valueSetBindings;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
