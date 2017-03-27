package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class VariesMapItem {
	private String hl7Version; // Fixed
	private String segmentName; // Fixed
	private String targetLocation; // Fixed
	private String referenceLocation; // Fixed
	private String secondRefereceLocation;

	public VariesMapItem() {
		super();
	}

	public VariesMapItem(String hl7Version, String segmentName, String targetLocation, String referenceLocation,
			String defaultValueSetName) {
		super();
		this.hl7Version = hl7Version;
		this.segmentName = segmentName;
		this.targetLocation = targetLocation;
		this.referenceLocation = referenceLocation;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
	}

	public String getReferenceLocation() {
		return referenceLocation;
	}

	public void setReferenceLocation(String referenceLocation) {
		this.referenceLocation = referenceLocation;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	public String getSecondRefereceLocation() {
		return secondRefereceLocation;
	}

	public void setSecondRefereceLocation(String secondRefereceLocation) {
		this.secondRefereceLocation = secondRefereceLocation;
	}

}
