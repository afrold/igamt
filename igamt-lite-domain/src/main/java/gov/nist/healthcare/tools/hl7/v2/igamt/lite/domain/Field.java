package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.annotate.JsonView;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Field extends DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({Views.Profile.class})
	@Column(nullable = true)
	private String itemNo;

	@JsonView({Views.Profile.class})
	@NotNull
	@Column(nullable = false)
	private BigInteger min;

	@JsonView({Views.Profile.class})
	@NotNull
	@Column(nullable = false)
	private String max;

	@JsonView({Views.Profile.class})
	@OneToOne(optional = false)
	private Datatype datatype;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Segment segment;

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

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", itemNo=" + itemNo + ", min=" + min
				+ ", max=" + max + ", datatype=" + datatype + ", name=" + name
				+ ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + "]";
	}

}
