package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Field extends DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = true)
	protected String itemNo;

	@NotNull
	@Column(nullable = false)
	protected BigInteger min;

	@NotNull
	@Column(nullable = false)
	protected String max;

	@OneToOne(optional = false)
	protected Datatype datatype;

	@ManyToOne(fetch = FetchType.LAZY)
	protected Segment segment;

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
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

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

}
