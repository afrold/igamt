package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


////@Entity
//@javax.persistence.Table(name = "DATAELEMENT")
////@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
////@JsonIgnoreProperties({ "datatype" })
public abstract class DataElement extends DataModel implements
		java.io.Serializable, Cloneable, Comparable<DataElement> {

	private static final long serialVersionUID = 1L;

	// @Id
	// //@Column(name = "ID")
	// //@GeneratedValue(strategy = GenerationType.TABLE)
	// protected String id;

	// //@NotNull
	// //@Column(nullable = false, name = "DATAELEMENT_NAME")
	protected String name;

	// //@NotNull
	// //@Column(name = "USAGEE", nullable = false)
	// // usage is a key word in mysql
	// @Enumerated(EnumType.STRING)
	protected Usage usage;
	//
	// @Min(0)
	// //@Column(name = "MIN_LENGTH")
	protected Integer minLength;

	// //@NotNull
	// //@Column(nullable = false, name = "MAX_LENGTH")
	protected String maxLength;

	// //@Column(name = "CONF_LENGTH")
	protected String confLength;

	// @JsonIgnoreProperties({ "mappingAlternateId", "mappingId", "name",
	// "version", "codesys", "oid", "tableType", "stability",
	// "extensibility", "type", "codes" })
	// //@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = {
	// CascadeType.PERSIST, CascadeType.MERGE })
	// //@JoinColumn(name = "TABLE_ID")
	protected String table;

	// protected String tableId;

	// //@Column(nullable = true, name = "BINDING_STRENGTH")
	protected String bindingStrength;

	// //@Column(nullable = true, name = "BINDING_LOCATION")
	protected String bindingLocation;

	// //@Column(nullable = true, name = "DATATYPE_LABEL")
	// protected String datatypeLabel;

	// //@JsonIgnore

	// @JsonIgnoreProperties({ "label", "components", "name", "description",
	// "predicates", "conformanceStatements", "comment", "usageNote",
	// "type" })
	protected String datatype;

	// protected String datatypeId;

	// //@NotNull
	// //@Column(nullable = false, name = "DATAELEMENT_POSITION")
	protected Integer position = 0;

	// //@Column(name = "COMMENT")
	protected String comment;

	// Caution, not persisted. Use at your own risk
	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
		// this.datatypeId = datatype != null ? datatype.getId() : null;
		// this.setDatatypeLabel(datatype != null ? datatype.getLabel() : null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getConfLength() {
		return confLength;
	}

	public void setConfLength(String confLength) {
		this.confLength = confLength;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
		// this.tableId = table != null ? table.getId() : null;
	}

	public String getBindingStrength() {
		return bindingStrength;
	}

	public void setBindingStrength(String bindingStrength) {
		this.bindingStrength = bindingStrength;
	}

	public String getBindingLocation() {
		return bindingLocation;
	}

	public void setBindingLocation(String bindingLocation) {
		this.bindingLocation = bindingLocation;
	}

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	// public String getDatatypeLabel() {
	// return datatypeLabel;
	// }
	//
	// // DO NO SET
	// public void setDatatypeLabel(String datatypeLabel) {
	// this.datatypeLabel = datatypeLabel;
	// }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	// public String getTableId() {
	// return tableId;
	// }
	//
	// public void setTableId(String tableId) {
	// this.tableId = tableId;
	// }

	@Override
	protected DataElement clone() throws CloneNotSupportedException {
		DataElement de = (DataElement) super.clone();
		de.setTable(this.table);
		// de.setDatatype(this.datatype.clone());
		de.setDatatype(this.datatype); // Changed by Harold

		return de;
	}

	@Override
	public int compareTo(DataElement o) {
		return this.getPosition() - o.getPosition();
	}

}
