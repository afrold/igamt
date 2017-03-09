package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;

public class SubProfileComponent implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public SubProfileComponent() {

  }

  @Id
  private String id;
  private SubProfileComponentAttributes attributes;
  private String itemId;
  private String path;
  private String pathExp;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getPathExp() {
    return pathExp;
  }

  public void setPathExp(String pathExp) {
    this.pathExp = pathExp;
  }

  private String type;
  private String name;

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

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
