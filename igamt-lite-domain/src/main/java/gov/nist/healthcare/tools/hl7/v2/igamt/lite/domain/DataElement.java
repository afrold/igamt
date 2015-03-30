package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

////@Entity
//@javax.persistence.Table(name = "DATAELEMENT")
////@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
////@JsonIgnoreProperties({ "datatype" })
public abstract class DataElement extends DataModel implements
		java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	// @Id
	// //@Column(name = "ID")
	// //@GeneratedValue(strategy = GenerationType.TABLE)
	protected String id;

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

	@JsonIgnoreProperties({ "name", "mappingAlternateId", "mappingId",
			"version", "codesys", "oid", "type", "codes" })
	// //@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = {
	// CascadeType.PERSIST, CascadeType.MERGE })
	// //@JoinColumn(name = "TABLE_ID")
	protected Table table;

	// //@Column(nullable = true, name = "BINDING_STRENGTH")
	protected String bindingStrength;

	// //@Column(nullable = true, name = "BINDING_LOCATION")
	protected String bindingLocation;

	// //@Column(nullable = true, name = "DATATYPE_LABEL")
	// protected String datatypeLabel;

	// //@JsonIgnore

	@DBRef
	@JsonIgnoreProperties({ "label", "components", "name", "description",
			"predicates", "conformanceStatements", "datatypes", "comments",
			"usageNote" })
	protected Datatype datatype;

	// //@NotNull
	// //@Column(nullable = false, name = "DATAELEMENT_POSITION")
	protected Integer position = 0;

	// //@Column(name = "COMMENT")
	protected String comment;

	// Caution, not persisted. Use at your own risk
	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
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

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	@Override
	protected DataElement clone() throws CloneNotSupportedException {
		DataElement de = (DataElement) super.clone();
		de.setId(null);
		de.setTable(this.table);
		// de.setDatatype(this.datatype.clone());
		de.setDatatype(this.datatype); // Changed by Harold

		return de;
	}

}
