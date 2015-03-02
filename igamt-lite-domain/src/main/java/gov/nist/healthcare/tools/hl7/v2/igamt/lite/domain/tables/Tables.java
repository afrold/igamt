package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

 
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@javax.persistence.Table(name="TABLES")
public class Tables implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy(value="position")
  	@javax.persistence.JoinTable(name = "TABLES_IGTABLE", joinColumns = @JoinColumn(name = "TABLES"), inverseJoinColumns = @JoinColumn(name = "IGTABLE"))
	private Set<Table> tables = new HashSet<Table>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Table> getTables() {
		return tables;
	}
 
	public void addTable(Table t) {
		t.setPosition(tables.size() +1);
		tables.add(t);
 	}

	@Override
	public String toString() {
		return "Tables [id=" + id + ", tables=" + tables + "]";
	}

 
}
