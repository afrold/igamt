package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class DatatypeLibraryMetaData extends MetaData {

	private static final long serialVersionUID = 1L;
	
	public DatatypeLibraryMetaData() {
		super();
	}

	@Override
	public DatatypeLibraryMetaData clone() throws CloneNotSupportedException {
		DatatypeLibraryMetaData clonedProfileMetaData = new DatatypeLibraryMetaData();

		clonedProfileMetaData.setName(this.getName());
		clonedProfileMetaData.setOrgName(this.getOrgName());
		clonedProfileMetaData.setDate(this.getDate());
		clonedProfileMetaData.setVersion(this.getVersion());
		return clonedProfileMetaData;
	}
}
