package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Tables implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(mappedBy = "tables", cascade = CascadeType.ALL)
	private Set<Table> tables = new HashSet<Table>();

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private TableLibrary tableLibrary;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Table> getTables() {
		return tables;
	}

	public void setTables(Set<Table> tables) {
		this.tables = tables;
	}

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
	}

	public void addTable(Table t) {
		if (t.getTables() != null) {
			throw new IllegalArgumentException(
					"This table already below to a different tables");
		}
		tables.add(t);
		t.setTables(this);
	}

	@Override
	public String toString() {
		return "Tables [id=" + id + ", tables=" + tables + "]";
	}

}
