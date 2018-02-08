package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = Group.class, name = Constant.GROUP),
    @JsonSubTypes.Type(value = SegmentRef.class, name = Constant.SEGMENTREF)})
public abstract class SegmentRefOrGroup extends DataModelWithConstraints implements
    java.io.Serializable, Comparable<SegmentRefOrGroup> {

  private static final long serialVersionUID = 1L;

  protected String id;

  // //@NotNull
  protected Usage usage;

  // @NotNull
  // @Min(0)
  protected Integer min;

  // @NotNull
  protected String max;

  // @NotNull
  // @Column(nullable = false, name = "SEGMENTREFORGROUP_POSITION")
  protected Integer position = 0;

  @Deprecated
  protected String comment = "";
  
  private String added= Constant.NO;

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Deprecated
  public String getComment() {
    return comment;
  }

  @Deprecated
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public int compareTo(SegmentRefOrGroup o) {
    return this.getPosition() - o.getPosition();
  }

public String getAdded() {
	return added;
}

public void setAdded(String added) {
	this.added = added;
}

}
