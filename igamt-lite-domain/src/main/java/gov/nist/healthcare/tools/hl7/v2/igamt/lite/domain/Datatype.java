package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Datatype implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "DATATYPE_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.DatatypeIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_field"))
	@GeneratedValue(generator = "DATATYPE_ID_GENERATOR")
	protected String id;

	@NotNull
	@Column(nullable = false)
	protected String displayName;

	@OneToMany(mappedBy = "datatype", cascade = CascadeType.ALL)
	@OrderColumn(name = "position", nullable = false)
	protected List<Component> components = new ArrayList<Component>();

	@NotNull
	@Column(nullable = false)
	protected String name;

	@Column(nullable = true)
	protected String description;

	@ManyToOne(fetch = FetchType.LAZY)
	protected Datatypes datatypes;

	// TODO. Only for backward compatibility. Remove later
	protected String uuid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Datatypes getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Datatypes datatypes) {
		this.datatypes = datatypes;
	}

	public void addComponent(Component c) {
		if (c.getDatatype() != null)
			throw new IllegalArgumentException(
					"This component already belong to a datatype");
		components.add(c);
		c.setDatatype(this);
	}

}
