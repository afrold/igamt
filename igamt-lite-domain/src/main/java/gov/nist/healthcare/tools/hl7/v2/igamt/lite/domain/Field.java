package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 13, 2015
 */
// @Entity
// @Table(name = "FIELD")
public class Field extends DataElement implements java.io.Serializable, Cloneable {

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
	private Integer min;

	// @NotNull
	private String max;

	private String text;

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", itemNo=" + itemNo + ", min=" + min
				+ ", max=" + max + ", datatype=" + datatype + name + ", usage="
				+ usage + "minLeLength" + minLength + ", maxLength="
				+ maxLength + ", confLength=" + confLength + ", table=" + table
				+ "]";
	}

	public int compareTo(Field o) {
		//return this.getPosition() - o.getPosition();
		return Integer.parseInt(this.getItemNo()) - Integer.parseInt(o.getItemNo());

	}
	
	@Override
	public Field clone() throws CloneNotSupportedException {
		Field clonedField = new Field();
		
		clonedField.setBindingLocation(bindingLocation);
		clonedField.setBindingStrength(bindingStrength);
		clonedField.setComment(comment);
		clonedField.setConfLength(confLength);
		clonedField.setDatatype(datatype.clone());
		clonedField.setItemNo(itemNo);
		clonedField.setMax(max);
		clonedField.setMaxLength(maxLength);
		clonedField.setMin(min);
		clonedField.setMinLength(minLength);
		clonedField.setName(name);
		clonedField.setPosition(position);
		clonedField.setTable(table.clone());
		clonedField.setText(text);
		clonedField.setUsage(usage);
		
		return clonedField;
	}

}
