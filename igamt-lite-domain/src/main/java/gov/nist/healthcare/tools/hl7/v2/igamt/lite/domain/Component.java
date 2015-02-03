package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Component extends DataElement {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "COMPONENT_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.ComponentIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_component"))
	@GeneratedValue(generator = "COMPONENT_ID_GENERATOR")
	protected String id;

	public Component() {
		super();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	protected Datatype belongTo;

	public Datatype getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(Datatype belongTo) {
		this.belongTo = belongTo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Component [id=" + id + ", datatype=" + datatype + ", name="
				+ name + ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + ", uuid=" + uuid + "]";
	}

}
