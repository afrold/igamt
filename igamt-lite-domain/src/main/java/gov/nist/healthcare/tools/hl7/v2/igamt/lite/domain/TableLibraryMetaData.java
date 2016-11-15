package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;
import java.util.UUID;


public class TableLibraryMetaData extends MetaData {

  private static final long serialVersionUID = 1L;

  private String tableLibId = "";

  public TableLibraryMetaData() {
    super();
  }

  @Override
  public TableLibraryMetaData clone() throws CloneNotSupportedException {
    TableLibraryMetaData clonedProfileMetaData = new TableLibraryMetaData();

    clonedProfileMetaData.setName(this.getName());
    clonedProfileMetaData.setOrgName(this.getOrgName());
    clonedProfileMetaData.setVersion(this.getVersion());
    clonedProfileMetaData.setTableLibId(UUID.randomUUID().toString());
    return clonedProfileMetaData;
  }

  public String getTableLibId() {
    return tableLibId;
  }

  public void setTableLibId(String tableLibId) {
    this.tableLibId = tableLibId;
  }
}
