package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;

import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "DATATYPE")
public class Datatype implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name = "LABEL")
	private String label;

	@JsonProperty("children")
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
	@javax.persistence.JoinTable(name = "DATATYPE_COMPONENT", joinColumns = @JoinColumn(name = "DATATYPE"), inverseJoinColumns = @JoinColumn(name = "COMPONENT"))
	@OrderBy(value = "position")
	private Set<Component> components = new HashSet<Component>();

	@NotNull
	@Column(nullable = false, name = "DATATYPE_NAME")
	private String name;

	@Column(nullable = true, name = "DATATYPE_DESC")
	private String description;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OrderBy(value = "position")
	@javax.persistence.JoinTable(name = "DATATYPE_PREDICATE", joinColumns = @JoinColumn(name = "DATATYPE"), inverseJoinColumns = @JoinColumn(name = "PREDICATE"))
	protected Set<Constraint> predicates = new HashSet<Constraint>();

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OrderBy(value = "position")
	@javax.persistence.JoinTable(name = "DATATYPE_CONFSTATEMENT", joinColumns = @JoinColumn(name = "DATATYPE"), inverseJoinColumns = @JoinColumn(name = "CONFSTATEMENT"))
	protected Set<Constraint> conformanceStatements = new HashSet<Constraint>();

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DATATYPES_ID")
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
 		if (components != null) {
			this.components.clear();
			Iterator<Component> it = components.iterator();
			while (it.hasNext()) {
				addComponent(it.next());
			}
		}else{
			this.components = null;
		}
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
		if (predicates != null) {
			this.predicates.clear();
			Iterator<Constraint> it = predicates.iterator();
			while (it.hasNext()) {
				addPredicate(it.next());
			}
		}
	} 
	
	
	public void setConformanceStatements(Set<Constraint> conformanceStatements) {
		if (conformanceStatements != null) {
			this.conformanceStatements.clear();
			Iterator<Constraint> it = conformanceStatements.iterator();
			while (it.hasNext()) {
				addConformanceStatement(it.next());
			}
		}else{
			this.conformanceStatements = null;
		}
	}

	public Set<Constraint> getConformanceStatements() {
		return conformanceStatements;
	}
 

	public void addPredicate(Constraint p) {
 		predicates.add(p);
	}

	public void addConformanceStatement(Constraint cs) {
 		conformanceStatements.add(cs);
	}

	public void addComponent(Component c) {
		c.setPosition(components.size() + 1);
		components.add(c);
	}
	


	@Override
	public String toString() {
		return "Datatype [id=" + id + ", label=" + label + ", components="
				+ components + ", name=" + name + ", description="
				+ description + "]";
	}

}
