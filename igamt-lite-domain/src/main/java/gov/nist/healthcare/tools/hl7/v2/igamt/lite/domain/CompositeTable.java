package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

public class CompositeTable extends DataModel implements Serializable, Comparable<CompositeTable>, Cloneable{

	private static final long serialVersionUID = 734059059225906039L;

	  @Id
	  private String id;
	  private String hl7Version;
	  private Set<String> libIds = new HashSet<String>();
	  private String bindingIdentifier;
	  private String name;  
	  private boolean newTable;

	  private String description;
	  private String version;
	  private String oid;
	  private Stability stability;
	  private Extensibility extensibility;

	  private ContentDefinition contentDefinition;
	  private String group;
	  private int order;

	  private List<Code> codes = new ArrayList<Code>();

	  private Constant.SCOPE scope;

	  protected Long accountId;

	  @Deprecated
	  protected String date;

	  protected STATUS status;
	  
	  protected String comment = "";

	  
	  protected String defPreText= "";
	  
	  protected String defPostText = "";

	  
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getHl7Version() {
		return hl7Version;
	}


	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}


	public Set<String> getLibIds() {
		return libIds;
	}


	public void setLibIds(Set<String> libIds) {
		this.libIds = libIds;
	}


	public String getBindingIdentifier() {
		return bindingIdentifier;
	}


	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isNewTable() {
		return newTable;
	}


	public void setNewTable(boolean newTable) {
		this.newTable = newTable;
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


	public List<Code> getCodes() {
		return codes;
	}


	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}


	public Constant.SCOPE getScope() {
		return scope;
	}


	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
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


	public STATUS getStatus() {
		return status;
	}


	public void setStatus(STATUS status) {
		this.status = status;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getDefPreText() {
		return defPreText;
	}


	public void setDefPreText(String defPreText) {
		this.defPreText = defPreText;
	}


	public String getDefPostText() {
		return defPostText;
	}


	public void setDefPostText(String defPostText) {
		this.defPostText = defPostText;
	}


	@Override
	public int compareTo(CompositeTable o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
