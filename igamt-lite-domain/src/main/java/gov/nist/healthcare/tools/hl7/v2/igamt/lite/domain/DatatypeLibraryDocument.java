package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.Random;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatype-library-document")
public class DatatypeLibraryDocument extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Long accountId;

	private String ext;

	private DatatypeLibraryMetaData metaData;

	private Constant.SCOPE scope;

	public DatatypeLibraryDocument() {
		super();
		type = Constant.DATATYPE_LIBRARY_DOCUMENT;
	}
	@DBRef
	private DatatypeLibrary datatypeLibrary = new DatatypeLibrary();
	@DBRef
	private TableLibrary tableLibrary = new TableLibrary();

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

	public DatatypeLibraryDocument clone(HashMap<String, Datatype> dtRecords, HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		DatatypeLibraryDocument clonedDatatypes = new DatatypeLibraryDocument();
		// clonedDatatypes.setChildren(new HashSet<Datatype>());
		// for (Datatype dt : this.children) {
		// if (dtRecords.containsKey(dt.getId())) {
		// clonedDatatypes.addDatatype(dtRecords.get(dt.getId()));
		// } else {
		// Datatype clone = dt.clone();
		// clone.setId(dt.getId());
		// dtRecords.put(dt.getId(), clone);
		// clonedDatatypes.addDatatype(clone);
		// }
		// }

		return clonedDatatypes;
	}

	public DatatypeLibraryDocument clone() throws CloneNotSupportedException {
		DatatypeLibraryDocument clone = new DatatypeLibraryDocument();

		clone.setDatatypeLibrary(datatypeLibrary.clone());
		clone.setTableLibrary(tableLibrary.clone());
		clone.setExt(this.getExt() + "-" + genRand());
		clone.setMetaData(this.getMetaData().clone());
		clone.setScope(this.getScope());
		clone.setSectionContents(this.getSectionContents());
		clone.setSectionDescription(this.getSectionDescription());
		clone.setSectionPosition(this.getSectionPosition());
		clone.setSectionTitle(this.getSectionTitle());
		clone.setType(this.getType());
		return clone;
	}

	private String genRand() {
		return Integer.toString(new Random().nextInt(100));
	}

	public void merge(DatatypeLibraryDocument mdts) {
		this.getDatatypeLibrary().getChildren().addAll(mdts.getDatatypeLibrary().getChildren());
		this.getTableLibrary().getChildren().addAll(mdts.getTableLibrary().getChildren());
	}

	public DatatypeLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatatypeLibraryMetaData metaData) {
		this.metaData = metaData;
	}

	public DatatypeLibrary getDatatypeLibrary() {
		return datatypeLibrary;
	}

	public void setDatatypeLibrary(DatatypeLibrary datatypeLibrary) {
		this.datatypeLibrary = datatypeLibrary;
	}

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
	}

}
