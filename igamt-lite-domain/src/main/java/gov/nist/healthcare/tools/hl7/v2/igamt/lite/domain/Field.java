package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.math.BigInteger;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 13, 2015
 */
// @Entity
// @Table(name = "FIELD")
public class Field extends DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	protected String id;

	public Field() {
		super();
		type = Constant.FIELD;
		this.id = ObjectId.get().toString();
	}

	private String itemNo;

	// @NotNull
	private BigInteger min;

	// @NotNull
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", itemNo=" + itemNo + ", min=" + min
				+ ", max=" + max + ", datatype=" + datatype + name + ", usage="
				+ usage + "minLeLength" + minLength + ", maxLength="
				+ maxLength + ", confLength=" + confLength + ", table=" + table
				+ "]";
	}

}
