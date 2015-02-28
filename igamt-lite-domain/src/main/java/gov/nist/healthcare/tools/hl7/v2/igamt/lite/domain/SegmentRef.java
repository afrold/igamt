package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="SEGMENTREF")
public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public SegmentRef() {
		super();
		type = Constant.SEGMENT;
	}
	
 	@JsonIgnoreProperties({"fields", "label","dynamicMappings", "name","description","predicates","conformanceStatements","segments"})
 	@OneToOne(fetch = FetchType.EAGER)
 	@JoinColumn(name="REF")
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
