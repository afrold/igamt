package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 26, 2015
 * 
 */
@Document(collection = "table")
public class Table extends SectionModel implements Serializable,
		Comparable<Table>, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 734059059225906039L;

	@Id
	private String id;

	// @NotNull
	private String bindingIdentifier;

	// @NotNull
	private String name;

	private String description;
	private String version;
	private String oid;
	private Stability stability;
	private Extensibility extensibility;
	private ContentDefinition contentDefinition;
	private String group;
	private int order;

	private List<Code> codes = new ArrayList<Code>();

	// @DBRef
	// private Tables tables;

	public Table() {
		super();
		this.type = Constant.TABLE;
		this.id = ObjectId.get().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBindingIdentifier() {
		return bindingIdentifier;
	}

	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public void addCode(Code c) {
		codes.add(c);
	}

	public boolean deleteCode(Code c) {
		return codes.remove(c);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public Stability getStability() {
		return stability;
	}

	public void setStability(Stability stability) {
		this.stability = stability;
	}

	public Extensibility getExtensibility() {
		return extensibility;
	}

	public void setExtensibility(Extensibility extensibility) {
		this.extensibility = extensibility;
	}

	public ContentDefinition getContentDefinition() {
		return contentDefinition;
	}

	public void setContentDefinition(ContentDefinition contentDefinition) {
		this.contentDefinition = contentDefinition;
	}

	public Code findOneCodeById(String id) {
		if (this.codes != null)
			for (Code m : this.codes) {
				if (id.equals(m.getId())) {
					return m;
				}
			}
		return null;
	}

	public Code findOneCodeByValue(String value) {
		if (this.codes != null)
			for (Code c : this.codes) {
				if (value.equals(c.getValue())) {
					return c;
				}
			}
		return null;
	}

	@Override
	public String toString() {
		return "Table [id=" + id + ", bindingIdentifier=" + bindingIdentifier + ", name=" + name + ", description="
				+ description + ", version=" + version + ", oid=" + oid + ", stability=" + stability
				+ ", extensibility=" + extensibility + ", contentDefinition=" + contentDefinition + ", group=" + group
				+ ", order=" + order + ", codes=" + codes + "]";
	}

	@Override
	public int compareTo(Table o) {
		int x = String.CASE_INSENSITIVE_ORDER.compare(this.bindingIdentifier != null ? this.bindingIdentifier: "",
				o.bindingIdentifier != null ? o.bindingIdentifier: "");
		if (x == 0) {
			x = (this.bindingIdentifier != null ? this.bindingIdentifier: "").compareTo(o.bindingIdentifier != null ? o.bindingIdentifier: "");
		}
		return x;
	}

	@Override
	public Table clone() throws CloneNotSupportedException {
		Table clonedTable = new Table();
		for (Code c : this.codes) {
			clonedTable.addCode(c.clone());
		}

		clonedTable.setId(id);
		clonedTable.setExtensibility(extensibility);
		clonedTable.setBindingIdentifier(bindingIdentifier);
		clonedTable.setDescription(description);
		clonedTable.setContentDefinition(contentDefinition);
		clonedTable.setName(name);
		clonedTable.setOid(oid);
		clonedTable.setStability(stability);
		clonedTable.setVersion(version);
		clonedTable.setType(type);
		clonedTable.setGroup(group);
		clonedTable.setOrder(order);

		return clonedTable;
	}
}
