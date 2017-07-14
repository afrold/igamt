package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

public class FieldFound {
  private SegmentFound segmentFound;
  private String id;
  private Integer position;
  private String name;
  private Usage usage;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public SegmentFound getSegmentFound() {
    return segmentFound;
  }

  public void setSegmentFound(SegmentFound segmentFound) {
    this.segmentFound = segmentFound;
  }


}
