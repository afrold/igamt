package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Component extends DataElement {

	private static final long serialVersionUID = 1L;

	public Component() {
		super();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	protected Datatype datatype;

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

}
