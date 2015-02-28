package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="DATATYPE")
public class Datatype implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false,name="LABEL")
	private String label;

	@JsonProperty("children")
	@OneToMany(mappedBy = "belongTo",fetch = FetchType.EAGER, cascade={CascadeType.REMOVE})
	@OrderColumn(name = "position", nullable = true)
	private Set<Component> components = new LinkedHashSet<Component>();

	@NotNull
	@Column(nullable = false,name="DATATYPE_NAME")
	private String name;

	@Column(nullable = true,name="DATATYPE_DESC")
	private String description;
	
	@OneToMany(fetch = FetchType.LAZY)
	@javax.persistence.JoinTable(name = "DATATYPE_PREDICATE", joinColumns = @JoinColumn(name = "DATATYPE"), inverseJoinColumns = @JoinColumn(name = "PREDICATE"))
	protected Set<Constraint> predicates = new HashSet<Constraint>();

	@OneToMany(fetch = FetchType.LAZY)
	@javax.persistence.JoinTable(name = "DATATYPE_CONFSTATEMENT", joinColumns = @JoinColumn(name = "DATATYPE"), inverseJoinColumns = @JoinColumn(name = "CONFSTATEMENT"))
	protected Set<Constraint> conformanceStatements = new HashSet<Constraint>();

 	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name="DATATYPES_ID")
	private Datatypes datatypes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<Component> getComponents() {
		return components;
	}
	
	
	
	
	
	

	public void setComponents(Set<Component> components) {
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

	public Set<Constraint> getPredicates() {
		return predicates;
	}

	public void setPredicates(Set<Constraint> predicates) {
		this.predicates = predicates;
	}

	public Set<Constraint> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(Set<Constraint> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public void addComponent(Component c) {
		if (c.getDatatype() != null)
			throw new IllegalArgumentException(
					"This component already belong to another datatype");
		components.add(c);
		c.setDatatype(this);
	}

	@Override
	public String toString() {
		return "Datatype [id=" + id + ", label=" + label + ", components="
				+ components + ", name=" + name + ", description="
				+ description + "]";
	}

}
