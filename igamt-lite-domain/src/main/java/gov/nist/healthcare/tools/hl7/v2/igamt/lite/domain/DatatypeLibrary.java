package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatype-library")
public class DatatypeLibrary extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Long accountId;

	private String date;

	private String ext;

	private DatatypeLibraryMetaData metaData;

	private Constant.SCOPE scope;
	
	public DatatypeLibrary() {
		super();
		this.id = ObjectId.get().toString();
	}

	private Set<String> children = new HashSet<String>();

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

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> children) {
		this.children = children;
	}

	public Constant.SCOPE getScope() {
		return scope;
	}

	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
	}

	public void addDatatype(String d) {
		children.add(d);
	}

	public String save(String d) {
		if (!this.children.contains(d)) {
			children.add(d);
		}
		return d;
	}

	public void delete(String id) {
		String d = findOne(id);
		if (d != null)
			this.children.remove(d);
	}

	public String findOne(String id) {
		if (this.children != null) {
			for (String m : this.children) {
				if (m.equals(id)) {
					return m;
				}
			}
		}

		return null;
	}

	public DatatypeLibrary clone(HashMap<String, Datatype> dtRecords, HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		DatatypeLibrary clonedDatatypes = new DatatypeLibrary();
//		clonedDatatypes.setChildren(new HashSet<Datatype>());
//		for (Datatype dt : this.children) {
//			if (dtRecords.containsKey(dt.getId())) {
//				clonedDatatypes.addDatatype(dtRecords.get(dt.getId()));
//			} else {
//				Datatype clone = dt.clone();
//				clone.setId(dt.getId());
//				dtRecords.put(dt.getId(), clone);
//				clonedDatatypes.addDatatype(clone);
//			}
//		}

		return clonedDatatypes;
	}

	public void merge(DatatypeLibrary dts) {
		this.getChildren().addAll(dts.getChildren());
	}

	public DatatypeLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatatypeLibraryMetaData metaData) {
		this.metaData = metaData;
	}
}
