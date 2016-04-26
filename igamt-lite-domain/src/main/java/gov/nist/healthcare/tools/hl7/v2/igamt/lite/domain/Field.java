package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 13, 2015
 */
// @Entity
// @Table(name = "FIELD")
public class Field extends DataElement implements java.io.Serializable,
		Cloneable {

	private static final long serialVersionUID = 1L;

	protected String id;

	public Field() {
		super();
		type = Constant.FIELD;
		this.id = ObjectId.get().toString();
	}

	private String itemNo;

	private Integer min;

	private String max;

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


	@Override
	public String toString() {
		return "Field [id=" + id + ", itemNo=" + itemNo + ", min=" + min
				+ ", max=" + max + ", name=" + name + ", usage=" + usage
				+ ", minLength=" + minLength + ", maxLength=" + maxLength
				+ ", confLength=" + confLength + ", table=" + table
				+ ", bindingStrength=" + bindingStrength + ", bindingLocation="
				+ bindingLocation + ", datatype=" + datatype + ", position="
				+ position + ", comment=" + comment + ", text=" + text
				+ ", type=" + type + "]";
	}

	public int compareTo(Field o) {
		// return this.getPosition() - o.getPosition();
		return Integer.parseInt(this.getItemNo())
				- Integer.parseInt(o.getItemNo());

	}

	public Field clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		Field clonedField = new Field();

		clonedField.setId(id);
		clonedField.setBindingLocation(bindingLocation);
		clonedField.setBindingStrength(bindingStrength);
		clonedField.setComment(comment);
		clonedField.setConfLength(confLength);
		clonedField.setDatatype(datatype);

		// if (dtRecords.containsKey(datatype)) {
		// clonedField.setDatatype(dtRecords.get(datatype.getId()));
		// } else {
		// Datatype dt = datatype.clone(dtRecords, tableRecords);
		// clonedField.setDatatype(dt);
		// dtRecords.put(datatype.getId(), dt);
		// }
		clonedField.setItemNo(itemNo);
		clonedField.setMax(max);
		clonedField.setMaxLength(maxLength);
		clonedField.setMin(min);
		clonedField.setMinLength(minLength);
		clonedField.setName(name);
		clonedField.setPosition(position);
		clonedField.setTable(table);

		// if (table != null) {
		// if (tableRecords.containsKey(table.getId())) {
		// clonedField.setTable(tableRecords.get(table.getId()));
		// } else {
		// Table dt = table.clone();
		// clonedField.setTable(dt);
		// tableRecords.put(table.getId(), dt);
		// }
		// } else {
		// clonedField.setTable(null);
		// }

		clonedField.setText(text);
		clonedField.setUsage(usage);

		return clonedField;
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(id).
            toHashCode();
    }
	
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Field))
            return false;
        if (obj == this)
            return true;

        Field rhs = (Field) obj;
        return new EqualsBuilder().
            append(id, rhs.id).
            isEquals();
    }
}
