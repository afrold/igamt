package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public abstract class SegmentRefOrGroup extends DataModel implements
		java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// //@NotNull
	protected Usage usage;

	// @NotNull
	// @Min(0)
	protected Integer min;

	// @NotNull
	protected String max;

	// @NotNull
	// @Column(nullable = false, name = "SEGMENTREFORGROUP_POSITION")
	protected Integer position = 0;

	protected String path;

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
