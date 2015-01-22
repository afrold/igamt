package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Field extends DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "FIELD_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.FieldIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_field"))
	@GeneratedValue(generator = "FIELD_ID_GENERATOR")
	protected String id;

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

	@Override
	public Datatype getDatatype() {
		return datatype;
	}

	@Override
	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", itemNo=" + itemNo + ", min=" + min
				+ ", max=" + max + ", datatype=" + datatype + ", name=" + name
				+ ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + ", uuid=" + uuid + "]";
	}


	
	
	

}
