package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	@OneToOne(cascade = CascadeType.ALL)
	private Segment segment;

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	@Override
	public String toString() {
		return "SegmentRef [id=" + id + ", segment=" + segment + ", usage="
				+ usage + ", min=" + min + ", max=" + max + "]";
	}

}
