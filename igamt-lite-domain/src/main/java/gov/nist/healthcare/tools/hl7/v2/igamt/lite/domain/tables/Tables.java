package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tables")
public class Tables implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

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
