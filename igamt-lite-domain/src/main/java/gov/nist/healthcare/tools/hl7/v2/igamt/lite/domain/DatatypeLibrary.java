package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

@Document(collection = "datatype-library")
public class DatatypeLibrary extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Long accountId;
	
	private String date;
	
	private String ext;

	private DatatypeLibraryMetaData metaData;
	
	public DatatypeLibrary() {
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
	private Set<Datatype> children = new HashSet<Datatype>();
	
	@DBRef
	private Set<Table> tables = new HashSet<Table>();

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

	public Set<Datatype> getChildren() {
		return children;
	}

	public void setChildren(Set<Datatype> children) {
		this.children = children;
	}

	public void addDatatype(Datatype d) {
		d.setDtLibExt(ext);
		children.add(d);
	}

	public Datatype save(Datatype d) {
		if (!this.children.contains(d)) {
			children.add(d);
		}
		return d;
	}

	public void delete(String id) {
		Datatype d = findOne(id);
		if (d != null)
			this.children.remove(d);
	}

	public Datatype findOne(String id) {
		if (this.children != null) {
			for (Datatype m : this.children) {
				if (m.getId().equals(id)) {
					return m;
				}
			}
		}

		return null;
	}

	public Datatype findOneByNameAndByLabel(String name, String label) {
		if (this.children != null) {
			for (Datatype dt : this.children) {
				if (dt.getName().equals(name) 
						&& dt.getLabel().equals(label)) {
					return dt;
				}
			}
		}
		return null;
	}
	
	public Component findOneComponent(String id) {
		if (this.children != null)
			for (Datatype m : this.children) {
				Component c = findOneComponent(id, m);
				if (c != null) {
					return c;
				}
			}

		return null;
	}

	public Component findOneComponent(String id, Datatype datatype) {
		if (datatype.getComponents() != null) {
			for (Component c : datatype.getComponents()) {
				if (c.getId().equals(id)) {
					return c;
				} else {
					Component r = findOneComponent(id,
							this.findOne(c.getDatatype()));
					if (r != null) {
						return r;
					}
				}
			}
		}
		return null;
	}

	public Datatype findOneDatatypeByLabel(String label) {
		if (this.children != null)
			for (Datatype d : this.children) {
				if (d.getLabel().equals(label)) {
					return d;
				}
			}
		return null;
	}
	
	public Datatype findOneDatatypeByBase(String baseName){
		if (this.children != null)
			for (Datatype d : this.children){
				if(d.getName().equals(baseName)) {
					return d;
				}
			}
		return null;
	}

	public Predicate findOnePredicate(String predicateId) {
		for (Datatype datatype : this.getChildren()) {
			Predicate predicate = datatype.findOnePredicate(predicateId);
			if (predicate != null) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(
			String conformanceStatementId) {
		for (Datatype datatype : this.getChildren()) {
			ConformanceStatement conf = datatype
					.findOneConformanceStatement(conformanceStatementId);
			if (conf != null) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		for (Datatype datatype : this.getChildren()) {
			if (datatype.deletePredicate(predicateId)) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteConformanceStatement(String confStatementId) {
		for (Datatype datatype : this.getChildren()) {
			if (datatype.deleteConformanceStatement(confStatementId)) {
				return true;
			}
		}
		return false;
	}

	public DatatypeLibrary clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		DatatypeLibrary clonedDatatypes = new DatatypeLibrary();
		clonedDatatypes.setChildren(new HashSet<Datatype>());
		for (Datatype dt : this.children) {
			if (dtRecords.containsKey(dt.getId())) {
				clonedDatatypes.addDatatype(dtRecords.get(dt.getId()));
			} else {
				Datatype clone = dt.clone();
				clone.setId(dt.getId());
				dtRecords.put(dt.getId(), clone);
				clonedDatatypes.addDatatype(clone);
			}
		}

		return clonedDatatypes;
	}
	
	public void merge(DatatypeLibrary dts){
		for (Datatype dt : dts.getChildren()){
			if (this.findOneByNameAndByLabel(dt.getName(), dt.getLabel()) == null){
				this.addDatatype(dt);
			} else {
				dt.setId(this.findOneByNameAndByLabel(dt.getName(), dt.getLabel()).getId()); //FIXME Probably useless...
			}
		}
		
	}
	
	public void setPositionsOrder(){
		List<Datatype> sortedList = new ArrayList<Datatype>(this.getChildren());
		Collections.sort(sortedList);
		for (Datatype elt: sortedList) {
			elt.setSectionPosition(sortedList.indexOf(elt));
		}
	}
	
	@JsonIgnore
	public Constraints getConformanceStatements() {
		//TODO Only byID constraints are considered; might want to consider byName
		Constraints constraints = new Constraints();
		Context dtContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getChildren()) {
			ByID byID = new ByID();
			byID.setByID(d.getLabel());
			if (d.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(d.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setDatatypes(dtContext);
		return constraints;
	}
	
	@JsonIgnore
	public Constraints getPredicates() {
		//TODO Only byID constraints are considered; might want to consider byName
		Constraints constraints = new Constraints();
		Context dtContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getChildren()) {
			ByID byID = new ByID();
			byID.setByID(d.getLabel());
			if (d.getPredicates().size() > 0) {
				byID.setPredicates(d.getPredicates());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setDatatypes(dtContext);
		return constraints;
	}

	public DatatypeLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatatypeLibraryMetaData metaData) {
		this.metaData = metaData;
	}

	public Set<Table> getTables() {
		return tables;
	}

	public void setTables(Set<Table> tables) {
		this.tables = tables;
	}
	
	public Table addTable(Table t){
		if (!this.tables.contains(t)) {
			this.tables.add(t);
		}
		return t;
	}
	
	public Table findTableById(String id) {
		if (this.tables != null) {
			for (Table t : this.tables) {
				if (t.getId().equals(id)) {
					return t;
				}
			}
		}

		return null;
	}
	
	public Table findTableByBindingIdentifier(String bindingIdentifier) {
		if (this.tables != null) {
			for (Table t : this.tables) {
				if (t.getBindingIdentifier().equals(bindingIdentifier)) {
					return t;
				}
			}
		}
		return null;
	}
	

}
