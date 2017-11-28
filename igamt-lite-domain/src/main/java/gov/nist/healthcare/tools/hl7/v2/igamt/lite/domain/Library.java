package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class Library extends Section implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  protected LibraryExportConfig exportConfig = new LibraryExportConfig();

  public LibraryExportConfig getExportConfig() {
    return exportConfig;
  }

  public void setExportConfig(LibraryExportConfig exportConfig) {
    this.exportConfig = exportConfig;
  }



}
