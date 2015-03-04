package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "BYNAME_OR_BYID")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ByNameOrByID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212340093784881862L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "BYNAME_OR_BYID_PREDICATE", joinColumns = @JoinColumn(name = "BYNAME_OR_BYID"), inverseJoinColumns = @JoinColumn(name = "PREDICATE"))
	protected Set<Predicate> predicates = new HashSet<Predicate>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "BYNAME_OR_BYID_CONFSTATEMENT", joinColumns = @JoinColumn(name = "BYNAME_OR_BYID"), inverseJoinColumns = @JoinColumn(name = "CONFSTATEMENT"))
	protected Set<ConformanceStatement> conformanceStatements = new HashSet<ConformanceStatement>();

	public Set<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(Set<Predicate> predicates) {
		this.predicates = predicates;
	}

	public Set<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(
			Set<ConformanceStatement> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addPredicate(Predicate e) {
		predicates.add(e);
	}

	public void addConformanceStatement(ConformanceStatement e) {
		conformanceStatements.add(e);
	}

}
