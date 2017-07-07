package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.UUID;


public class DatatypeLibraryMetaData extends MetaData {

  private static final long serialVersionUID = 1L;

  private String datatypeLibId = "";

  public DatatypeLibraryMetaData() {
    super();
  }

  @Override
  public DatatypeLibraryMetaData clone() throws CloneNotSupportedException {
    DatatypeLibraryMetaData clonedProfileMetaData = new DatatypeLibraryMetaData();

    clonedProfileMetaData.setName(this.getName());
    clonedProfileMetaData.setOrgName(this.getOrgName());
    clonedProfileMetaData.setVersion(this.getVersion());
    clonedProfileMetaData.setDatatypeLibId(UUID.randomUUID().toString());
    clonedProfileMetaData.setDescription(this.getDescription());


    return clonedProfileMetaData;
  }

  public String getDatatypeLibId() {
    return datatypeLibId;
  }

  public void setDatatypeLibId(String datatypeLibId) {
    this.datatypeLibId = datatypeLibId;
  }

}
