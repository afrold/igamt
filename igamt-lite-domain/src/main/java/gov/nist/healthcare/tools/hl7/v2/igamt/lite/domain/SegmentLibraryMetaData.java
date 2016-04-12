package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class SegmentLibraryMetaData extends MetaData {

	private static final long serialVersionUID = 1L;
	
	public SegmentLibraryMetaData() {
		super();
	}

	private String datatypLibId = ""; 				//ConformanceProfile/@ID
	
	@Override
	public SegmentLibraryMetaData clone() throws CloneNotSupportedException {
		SegmentLibraryMetaData clonedProfileMetaData = new SegmentLibraryMetaData();

		clonedProfileMetaData.setName(this.getName());
		clonedProfileMetaData.setOrgName(this.getOrgName());
		clonedProfileMetaData.setDate(this.getDate());
		clonedProfileMetaData.setVersion(this.getVersion());
		clonedProfileMetaData.setDatatypLibId(datatypLibId);
		return clonedProfileMetaData;
	}

	public String getDatatypLibId() {
		return datatypLibId;
	}

	public void setDatatypLibId(String datatypLibId) {
		this.datatypLibId = datatypLibId;
	}
}
