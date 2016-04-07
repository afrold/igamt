package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "valueset-library")
public class TableLibrary extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Long accountId;
	
	private String date;
	
	private String ext;

	private TableLibraryMetaData metaData;
	
	public TableLibrary() {
		super();
	}
	
	private Constant.SCOPE scope;

	public Constant.SCOPE getScope() {
		return scope;
	}

	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
	}

	@DBRef
	private Set<Table> children = new HashSet<Table>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Set<Table> getChildren() {
		return children;
	}

	public void setChildren(Set<Table> children) {
		this.children = children;
	}

	public void addTable(Table d) {
		d.setTabLibExt(ext);
		children.add(d);
	}

	public Table save(Table d) {
		if (!this.children.contains(d)) {
			children.add(d);
		}
		return d;
	}

	public void delete(String id) {
		Table d = findOne(id);
		if (d != null)
			this.children.remove(d);
	}

	public Table findOne(String id) {
		if (this.children != null) {
			for (Table m : this.children) {
				if (m.getId().equals(id)) {
					return m;
				}
			}
		}

		return null;
	}

	public Table findOneByNameAndByBindingIdentifier(String name, String bindngIdentifier) {
		if (this.children != null) {
			for (Table tab : this.children) {
				if (tab.getName().equals(name) 
						&& tab.getBindingIdentifier().equals(bindngIdentifier)) {
					return tab;
				}
			}
		}
		return null;
	}
	
	public Code findOneCode(String id) {
		if (this.children != null)
			for (Table m : this.children) {
				Code c = findOneCode(id, m);
				if (c != null) {
					return c;
				}
			}

		return null;
	}

	public Code findOneCode(String id, Table datatype) {
		if (datatype.getCodes() != null) {
			for (Code c : datatype.getCodes()) {
				if (c.getId().equals(id)) {
					return c;
				}
			}
		}
		return null;
	}

	public TableLibrary clone(HashMap<String, Table> tabRecords)
			throws CloneNotSupportedException {
		TableLibrary clonedDatatypes = new TableLibrary();
		clonedDatatypes.setChildren(new HashSet<Table>());
		for (Table tab : this.children) {
			if (tabRecords.containsKey(tab.getId())) {
				clonedDatatypes.addTable(tabRecords.get(tab.getId()));
			} else {
				Table clone = tab.clone();
				clone.setId(tab.getId());
				tabRecords.put(tab.getId(), clone);
				clonedDatatypes.addTable(clone);
			}
		}

		return clonedDatatypes;
	}
	
	public void merge(TableLibrary tabs){
		for (Table tab : tabs.getChildren()){
			if (this.findOneByNameAndByBindingIdentifier(tab.getName(), tab.getBindingIdentifier()) == null){
				this.addTable(tab);
			}
		}
	}
	
	public void setPositionsOrder(){
		List<Table> sortedList = new ArrayList<Table>(this.getChildren());
		Collections.sort(sortedList);
		for (Table elt: sortedList) {
			elt.setSectionPosition(sortedList.indexOf(elt));
		}
	}
	
//	@JsonIgnore
//	public Code getCode() {
//		//TODO Only byID constraints are considered; might want to consider byName
//		Constraints constraints = new Constraints();
//		Context tabContext = new Context();
//
//		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
//		byNameOrByIDs = new HashSet<ByNameOrByID>();
//		for (Table d : this.getChildren()) {
//			ByID byID = new ByID();
//			byID.setByID(d.getLabel());
//			if (d.getConformanceStatements().size() > 0) {
//				byID.setConformanceStatements(d.getConformanceStatements());
//				byNameOrByIDs.add(byID);
//			}
//		}
//		tabContext.setByNameOrByIDs(byNameOrByIDs);
//
//		constraints.setDatatypes(tabContext);
//		return constraints;
//	}

	public TableLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(TableLibraryMetaData metaData) {
		this.metaData = metaData;
	}

	public Set<Table> getTables() {
		return children;
	}

	public void setTables(Set<Table> children) {
		this.children = children;
	}
	
	public Table findTableById(String id) {
		if (this.children != null) {
			for (Table t : this.children) {
				if (t.getId().equals(id)) {
					return t;
				}
			}
		}

		return null;
	}
	
	public Table findTableByBindingIdentifier(String bindingIdentifier) {
		if (this.children != null) {
			for (Table t : this.children) {
				if (t.getBindingIdentifier().equals(bindingIdentifier)) {
					return t;
				}
			}
		}
		return null;
	}
}
