package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "SEGMENTREF_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.SegmentRefIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_segmenref"))
	@GeneratedValue(generator = "SEGMENTREF_ID_GENERATOR")
	protected Long id;

	@OneToOne(cascade = CascadeType.ALL)
	protected Segment segment;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected Usage usage;

	@NotNull
	@Column(nullable = false)
	protected BigInteger min;

	@NotNull
	@Column(nullable = false)
	protected String max;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
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

	@Override
	public String toString() {
		return "SegmentRef [id=" + id + ", segment=" + segment + ", usage="
				+ usage + ", min=" + min + ", max=" + max + "]";
	}
	
	

}
