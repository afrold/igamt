package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;

public class Component extends DataElement implements Cloneable {

	private static final long serialVersionUID = 1L;

	private String id;

	public Component() {
		super();
		this.type = Constant.COMPONENT;
		this.id = ObjectId.get().toString();
	}

	@Override
	public String toString() {
		return "Component [id=" + id + ", datatype=" + datatype + ", name="
				+ name + ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(DataElement o) {
		return this.getPosition() - o.getPosition();
	}

	@Override
	public Component clone() throws CloneNotSupportedException {
		Component clonedObj = new Component();
		clonedObj.setId(id);
		clonedObj.setBindingLocation(this.bindingLocation);
		clonedObj.setBindingStrength(this.bindingStrength);
		clonedObj.setComment(comment);
		clonedObj.setConfLength(confLength);
		clonedObj.setDatatype(datatype);
		// if (datatypeRecords.containsKey(datatype.getId())) {
		// clonedObj.setDatatype(datatypeRecords.get(datatype.getId()));
		// } else {
		// Datatype dt = datatype.clone(datatypeRecords, tableRecords);
		// clonedObj.setDatatype(dt);
		// datatypeRecords.put(datatype.getId(), dt);
		// }
		clonedObj.setMaxLength(maxLength);
		clonedObj.setMinLength(minLength);
		clonedObj.setName(name);
		clonedObj.setPosition(position);
		clonedObj.setTable(table);
		// if (table != null) {
		// if (tableRecords.containsKey(table.getId())) {
		// clonedObj.setTable(tableRecords.get(table.getId()));
		// } else {
		// Table dt = table.clone();
		// clonedObj.setTable(dt);
		// tableRecords.put(table.getId(), dt);
		// }
		// } else {
		// clonedObj.setTable(null);
		// }
		clonedObj.setUsage(usage);
		return clonedObj;
	}

}
