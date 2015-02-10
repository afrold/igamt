package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.map.annotate.JsonView;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Component extends DataElement {

	private static final long serialVersionUID = 1L;

	@JsonView({Views.Component.class,Views.Datatype.class})
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public Component() {
		super();
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Datatype belongTo;

	public Datatype getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(Datatype belongTo) {
		this.belongTo = belongTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Component [id=" + id + ", datatype=" + datatype + ", name="
				+ name + ", usage=" + usage + ", minLength=" + minLength
				+ ", maxLength=" + maxLength + ", confLength=" + confLength
				+ ", table=" + table + "]";
	}

}
