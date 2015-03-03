package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

 




import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov)
 * Feb 13, 2015
 */
@Entity
@Table(name="FIELD")
public class Field extends DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public Field() {
		super();
		type = Constant.FIELD;
	}
	
 	@Column(nullable = true,name="ITEMNO")
	private String itemNo;

 	@NotNull
	@Column(nullable = false,name="MIN")
	private BigInteger min;

 	@NotNull
	@Column(nullable = false,name="MAX")
	private String max;

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
