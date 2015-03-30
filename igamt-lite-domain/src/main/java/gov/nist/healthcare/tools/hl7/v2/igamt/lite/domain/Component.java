package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class Component extends DataElement {

	private static final long serialVersionUID = 1L;

	public Component() {
		super();
		this.type = Constant.COMPONENT;
	}

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

}
