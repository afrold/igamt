package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CONFORMANCECONTEXT")
public class ConformanceContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private ConstraintMetaData metaData;
	
	private Constraints predicates;
	
	private Constraints conformanceStatements;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConstraintMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(ConstraintMetaData metaData) {
		this.metaData = metaData;
	}

	public Constraints getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(Constraints conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public Constraints getPredicates() {
		return predicates;
	}

	public void setPredicates(Constraints predicates) {
		this.predicates = predicates;
	}
}
