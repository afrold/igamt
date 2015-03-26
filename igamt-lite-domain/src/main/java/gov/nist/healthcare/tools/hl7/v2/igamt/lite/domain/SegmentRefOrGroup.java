package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segmentRefOrGroups")
public abstract class SegmentRefOrGroup extends DataModel implements
		java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	protected Long id;

	// @NotNull
	protected Usage usage;

	@NotNull
	@Min(0)
	@Column(nullable = false, name = "MIN")
	protected Integer min;

	@NotNull
	@Column(nullable = false, name = "MAX")
	protected String max;

	@NotNull
	@Column(nullable = false, name = "SEGMENTREFORGROUP_POSITION")
	protected Integer position = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

}
