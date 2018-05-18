package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class SegmentField {

	private String segmentName;
	private int location;

	public SegmentField(String segmentName, int location) {
		super();
		this.setSegmentName(segmentName);
		this.location = location;
	}

	public SegmentField() {
		super();
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

  public String getSegmentName() {
    return segmentName;
  }

  public void setSegmentName(String segmentName) {
    this.segmentName = segmentName;
  }

}
