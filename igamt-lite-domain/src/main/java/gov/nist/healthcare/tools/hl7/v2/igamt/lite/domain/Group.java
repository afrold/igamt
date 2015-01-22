package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Group extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "GROUP_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.GroupIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_group"))
	@GeneratedValue(generator = "GROUP_ID_GENERATOR")
	protected String id;

	@OneToMany(cascade = CascadeType.ALL)
	@OrderColumn(name = "position", nullable = false)
	protected List<SegmentRefOrGroup> segmentsOrGroups = new ArrayList<SegmentRefOrGroup>();

	@NotNull
	@Column(nullable = false)
	protected String name;

	@NotNull
	@Column(nullable = false)
	protected Usage usage;
	@NotNull
	@Column(nullable = false)
	protected BigInteger min;
	@NotNull
	@Column(nullable = false)
	protected String max;

	// TODO. Only for backward compatibility. Remove later
	protected String uuid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<SegmentRefOrGroup> getSegmentsOrGroups() {
		return segmentsOrGroups;
	}

	public void setSegmentsOrGroups(List<SegmentRefOrGroup> segmentsOrGroups) {
		this.segmentsOrGroups = segmentsOrGroups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public BigInteger getMin() {
		return min;
	}

	public void setMin(BigInteger min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", segmentsOrGroups=" + segmentsOrGroups
				+ ", name=" + name + ", usage=" + usage + ", min=" + min
				+ ", max=" + max + ", uuid=" + uuid + "]";
	}


	
}
