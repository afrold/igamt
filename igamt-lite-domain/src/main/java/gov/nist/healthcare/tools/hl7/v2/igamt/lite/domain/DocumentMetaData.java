package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class DocumentMetaData extends DataModel implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String title;
	private String version;
	private String date;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	@Override
	public DocumentMetaData clone() throws CloneNotSupportedException {
		DocumentMetaData clonedDocumentMetaData = new DocumentMetaData();

		clonedDocumentMetaData.setName(name);
		clonedDocumentMetaData.setTitle(title);
		clonedDocumentMetaData.setVersion(version);
		clonedDocumentMetaData.setDate(date);

		return clonedDocumentMetaData;
	}
	
	
}
