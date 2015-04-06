package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Component extends DataElement {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	public Component() {
		super();
		this.type = Constant.COMPONENT;
		this.id = ObjectId.get().toString();
	}

	private boolean sub = false;

	@Override
	public String toString() {
		return "Component [id=" + id + ", datatype=" + datatype + ", name="
				+ name + ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + "]";
	}

	@Override
	public Component clone() throws CloneNotSupportedException {
		return (Component) super.clone();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}

}
