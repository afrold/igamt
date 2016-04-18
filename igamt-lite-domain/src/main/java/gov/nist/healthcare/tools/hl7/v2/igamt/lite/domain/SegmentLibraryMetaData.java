package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class SegmentLibraryMetaData extends MetaData {

	private static final long serialVersionUID = 1L;
	
	public SegmentLibraryMetaData() {
		super();
	}

	private String segmentLibId = ""; 				//ConformanceProfile/@ID
	
	@Override
	public SegmentLibraryMetaData clone() throws CloneNotSupportedException {
		SegmentLibraryMetaData clonedProfileMetaData = new SegmentLibraryMetaData();

		clonedProfileMetaData.setName(this.getName());
		clonedProfileMetaData.setOrgName(this.getOrgName());
		clonedProfileMetaData.setDate(this.getDate());
		clonedProfileMetaData.setVersion(this.getVersion());
		clonedProfileMetaData.setSegmentLibId(segmentLibId);
		return clonedProfileMetaData;
	}

	public String getSegmentLibId() {
		return segmentLibId;
	}

	public void setSegmentLibId(String segmentLibId) {
		this.segmentLibId = segmentLibId;
	}
}
