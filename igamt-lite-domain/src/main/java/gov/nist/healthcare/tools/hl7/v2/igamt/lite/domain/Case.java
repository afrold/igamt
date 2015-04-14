//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.06 at 03:07:45 PM EST 
//

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

////@Entity
////@Table(name = "CASE")
public class Case implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	// //@Column(name = "ID")
	// @Id 
	// //@GeneratedValue(strategy = GenerationType.AUTO)
	protected String id;

	// //@NotNull
	// //@Column(nullable = false, name = "VALUE")
	protected String value;

	// //@OneToOne
	// //@JoinColumn(nullable = false)
	protected Datatype datatype;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSetValue() {
		return (this.value != null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}
	
	@Override
	public Case clone() throws CloneNotSupportedException {
		Case clonedCase = new Case();
		clonedCase.setId(null);
		clonedCase.setValue(value);
		clonedCase.setDatatype(datatype.clone());
		return clonedCase;
	}

}
