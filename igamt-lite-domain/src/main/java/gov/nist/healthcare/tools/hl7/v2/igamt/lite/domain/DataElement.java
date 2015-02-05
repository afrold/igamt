package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Filters({ @Filter(name = "length", condition = ":maxLength != '*' and :minLength <= :maxLength or :maxLength == '*'") })
public abstract class DataElement implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@NotNull
	@Column(nullable = false)
	protected String name;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected Usage usage;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	protected Datatype datatype;

	@Min(1)
	protected BigInteger minLength;

	@NotNull
	@Column(nullable = false)
	protected String maxLength;

	protected String confLength;

	@Column(nullable = true)
	protected Table table;

	@Column(nullable = true)
	protected Constraint predicate;

	@Column(nullable = true)
	protected Set<Constraint> conformanceStatements;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public BigInteger getMinLength() {
		return minLength;
	}

	public void setMinLength(BigInteger minLength) {
		this.minLength = minLength;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getConfLength() {
		return confLength;
	}

	public void setConfLength(String confLength) {
		this.confLength = confLength;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Constraint getPredicate() {
		return predicate;
	}

	public void setPredicate(Constraint predicate) {
		this.predicate = predicate;
	}

	public Set<Constraint> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(Set<Constraint> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

}
