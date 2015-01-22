package gov.nist.healthcare.hl7tools.igmatlite.domain;

import java.io.Serializable;

public class Constraint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723342171557075960L;

	private long id;
	private ConstraintType type;
	
	private String byName;
	private String byId;
	
	private String constraintId;
	private String constraintTag;
	
	private String constraintReferenceChapter;
	private String constraintReferenceSection;
	private int constraintReferencePage;
	private String constraintReferenceURL;
	
	private String description;
	
	private Assertion assertion;
	
	public Constraint() {
		// TODO Auto-generated constructor stub
	}

	
	public Constraint(long id, ConstraintType type, String byName, String byId,
			String constraintId, String constraintTag,
			String constraintReferenceChapter,
			String constraintReferenceSection, int constraintReferencePage,
			String constraintReferenceURL, String description,
			Assertion assertion) {
		super();
		this.id = id;
		this.type = type;
		this.byName = byName;
		this.byId = byId;
		this.constraintId = constraintId;
		this.constraintTag = constraintTag;
		this.constraintReferenceChapter = constraintReferenceChapter;
		this.constraintReferenceSection = constraintReferenceSection;
		this.constraintReferencePage = constraintReferencePage;
		this.constraintReferenceURL = constraintReferenceURL;
		this.description = description;
		this.assertion = assertion;
	}


	@Override
	public String toString() {
		return "Constraint [id=" + id + ", type=" + type + ", byName=" + byName
				+ ", byId=" + byId + ", constraintId=" + constraintId
				+ ", constraintTag=" + constraintTag
				+ ", constraintReferenceChapter=" + constraintReferenceChapter
				+ ", constraintReferenceSection=" + constraintReferenceSection
				+ ", constraintReferencePage=" + constraintReferencePage
				+ ", constraintReferenceURL=" + constraintReferenceURL
				+ ", description=" + description + ", assertion=" + assertion
				+ "]";
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public ConstraintType getType() {
		return type;
	}


	public void setType(ConstraintType type) {
		this.type = type;
	}


	public String getByName() {
		return byName;
	}


	public void setByName(String byName) {
		this.byName = byName;
	}


	public String getById() {
		return byId;
	}


	public void setById(String byId) {
		this.byId = byId;
	}


	public String getConstraintId() {
		return constraintId;
	}


	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}


	public String getConstraintTag() {
		return constraintTag;
	}


	public void setConstraintTag(String constraintTag) {
		this.constraintTag = constraintTag;
	}


	public String getConstraintReferenceChapter() {
		return constraintReferenceChapter;
	}


	public void setConstraintReferenceChapter(String constraintReferenceChapter) {
		this.constraintReferenceChapter = constraintReferenceChapter;
	}


	public String getConstraintReferenceSection() {
		return constraintReferenceSection;
	}


	public void setConstraintReferenceSection(String constraintReferenceSection) {
		this.constraintReferenceSection = constraintReferenceSection;
	}


	public int getConstraintReferencePage() {
		return constraintReferencePage;
	}


	public void setConstraintReferencePage(int constraintReferencePage) {
		this.constraintReferencePage = constraintReferencePage;
	}


	public String getConstraintReferenceURL() {
		return constraintReferenceURL;
	}


	public void setConstraintReferenceURL(String constraintReferenceURL) {
		this.constraintReferenceURL = constraintReferenceURL;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Assertion getAssertion() {
		return assertion;
	}


	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}
	
	
	
}
