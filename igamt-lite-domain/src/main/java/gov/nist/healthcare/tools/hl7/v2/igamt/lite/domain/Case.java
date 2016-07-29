//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2015.02.06 at 03:07:45 PM EST
//

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class Case implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public Case() {
		super();
		this.id = ObjectId.get().toString();
		this.type = "case";
	}

	private String type;

	private String id;

	protected String value;
	
	protected String secondValue;

	protected String datatype;

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

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
	public String getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(String secondValue) {
		this.secondValue = secondValue;
	}

	@Override
	public Case clone() throws CloneNotSupportedException {
		Case clonedCase = new Case();
		clonedCase.setId(null);
		clonedCase.setValue(value);
		clonedCase.setDatatype(datatype);
		clonedCase.setType(this.type);
		clonedCase.setSecondValue(secondValue);

		return clonedCase;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
