package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

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

@Entity
@javax.persistence.Table(name = "TABLES")
public class Tables implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@javax.persistence.JoinTable(name = "TABLES_IGTABLE", joinColumns = @JoinColumn(name = "TABLES"), inverseJoinColumns = @JoinColumn(name = "IGTABLE"))
	private final Set<Table> children = new HashSet<Table>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Table> getChildren() {
		return children;
	}

	public void addTable(Table t) {
		children.add(t);
	}

	@Override
	public String toString() {
		return "Tables [id=" + id + ", children=" + children + "]";
	}

}
