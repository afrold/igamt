package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
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
	}

	private Set<DatatypeLink> children = new HashSet<DatatypeLink>();

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

	public Set<DatatypeLink> getChildren() {
		return children;
	}

	public void setChildren(Set<DatatypeLink> children) {
		this.children = children;
	}

	public Constant.SCOPE getScope() {
		return scope;
	}

	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public void addDatatype(DatatypeLink dtl) {
		children.add(dtl);
	}

	public DatatypeLink save(DatatypeLink dtl) {
		children.add(dtl);
		return dtl;
	}

	public void temp(DatatypeLink dtl) {
		this.children.remove(dtl);
	}

	public DatatypeLink findOne(String dtId) {
		if (this.children != null) {
			for (DatatypeLink dtl : this.children) {
				if (dtl.equals(dtId)) {
					return dtl;
				}
			}
		}

		return null;
	}

	public DatatypeLink findOne(DatatypeLink dtl) {
		if (this.children != null) {
			for (DatatypeLink dtl1 : this.children) {
				if (dtl1.equals(dtl)) {
					return dtl1;
				}
			}
		}

		return null;
	}
	
	public DatatypeLink findOneByName(String name) {
		if (this.children != null) {
			for (DatatypeLink dtl1 : this.children) {
				if (dtl1.getName().equals(name)) {
					return dtl1;
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
	
	public DatatypeLibrary clone()
			throws CloneNotSupportedException {
		DatatypeLibrary clone = new DatatypeLibrary();
		clone.setChildren(new HashSet<DatatypeLink>(this.getChildren()));
		clone.setExt(this.getExt() + "-" + genRand());
		clone.setMetaData(this.getMetaData().clone());
		clone.setScope(this.getScope());
		clone.setSectionContents(this.getSectionContents()); 
		clone.setSectionDescription(this.getSectionDescription());
		clone.setSectionPosition(this.getSectionPosition());
		clone.setSectionTitle(this.getSectionTitle());
		clone.setType(this.getType());
		clone.setDate(this.getDate());
		return clone;	
	}
	
	private String genRand() {
		return Integer.toString( new Random().nextInt(100));
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
	
	public boolean addLink(Datatype dt) {
		return getChildren().add(new DatatypeLink(dt.getId(), dt.getName(), dt.getExt()));
	}
 }
