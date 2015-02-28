package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

 
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="COMPONENT")
public class Component extends DataElement {

	private static final long serialVersionUID = 1L;
 

	public Component() {
		super();
		this.type = Constant.COMPONENT;
	}

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="DATATYPE_BELONGTO_ID")
	private Datatype belongTo;

	public Datatype getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(Datatype belongTo) {
		this.belongTo = belongTo;
	}


	@Override
	public String toString() {
		return "Component [id=" + id + ", datatype=" + datatype + ", name="
				+ name + ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + "]";
	}

}
