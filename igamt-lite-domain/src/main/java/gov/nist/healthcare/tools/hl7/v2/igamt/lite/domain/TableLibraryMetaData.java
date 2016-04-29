package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class TableLibraryMetaData extends MetaData {

	private static final long serialVersionUID = 1L;
	
	public TableLibraryMetaData() {
		super();
	}

	@Override
	public TableLibraryMetaData clone() throws CloneNotSupportedException {
		TableLibraryMetaData clonedProfileMetaData = new TableLibraryMetaData();

		clonedProfileMetaData.setName(this.getName());
		clonedProfileMetaData.setOrgName(this.getOrgName());
		clonedProfileMetaData.setDate(this.getDate());
		clonedProfileMetaData.setVersion(this.getVersion());
		return clonedProfileMetaData;
	}
}
