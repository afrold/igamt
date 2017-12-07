package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

public class Library implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;
  protected Integer sectionPosition;
  protected String sectionTitle;
  protected String sectionContent;
  protected String description;
  protected String type;
  protected Date dateUpdated;


  public Date getDateUpdated() {
	return dateUpdated;
}

public void setDateUpdated(Date dateUpdated) {
	this.dateUpdated = dateUpdated;
}

public String getSectionContent() {
	return sectionContent;
}

public Integer getSectionPosition() {
	return sectionPosition;
}

public void setSectionPosition(Integer sectionPosition) {
	this.sectionPosition = sectionPosition;
}

public String getSectionTitle() {
	return sectionTitle;
}

public void setSectionTitle(String sectionTitle) {
	this.sectionTitle = sectionTitle;
}

public String getSectionContents() {
	return sectionContent;
}

public void setSectionContent(String sectionContent) {
	this.sectionContent = sectionContent;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public static long getSerialversionuid() {
	return serialVersionUID;
}

protected LibraryExportConfig exportConfig = new LibraryExportConfig();

  public LibraryExportConfig getExportConfig() {
    return exportConfig;
  }

  public void setExportConfig(LibraryExportConfig exportConfig) {
    this.exportConfig = exportConfig;
  }



}
