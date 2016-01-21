package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class DocumentMetaData extends DataModel implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String subTitle;
	private String version;
	private String date;
	private String ext = "";
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	
	@Override
	public DocumentMetaData clone() throws CloneNotSupportedException {
		DocumentMetaData clonedDocumentMetaData = new DocumentMetaData();

		clonedDocumentMetaData.setName(name);
		clonedDocumentMetaData.setSubTitle(subTitle);
		clonedDocumentMetaData.setVersion(version);
		clonedDocumentMetaData.setDate(date);
		clonedDocumentMetaData.setExt(ext);

		return clonedDocumentMetaData;
	}
}
