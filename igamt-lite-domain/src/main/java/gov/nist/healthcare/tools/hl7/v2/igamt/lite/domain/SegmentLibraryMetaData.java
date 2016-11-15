package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;
import java.util.UUID;


public class SegmentLibraryMetaData extends MetaData {

  private static final long serialVersionUID = 1L;

  private String segmentLibId = "";

  public SegmentLibraryMetaData() {
    super();
  }

  @Override
  public SegmentLibraryMetaData clone() throws CloneNotSupportedException {
    SegmentLibraryMetaData clonedProfileMetaData = new SegmentLibraryMetaData();

    clonedProfileMetaData.setName(this.getName());
    clonedProfileMetaData.setOrgName(this.getOrgName());
    clonedProfileMetaData.setVersion(this.getVersion());
    clonedProfileMetaData.setSegmentLibId(UUID.randomUUID().toString());
    return clonedProfileMetaData;
  }

  public String getSegmentLibId() {
    return segmentLibId;
  }

  public void setSegmentLibId(String segmentLibId) {
    this.segmentLibId = segmentLibId;
  }
}
