package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraints;

public class CompositeSegment extends DataModelWithConstraints implements java.io.Serializable, Cloneable,
Comparable<CompositeSegment>{
	
	 private static final long serialVersionUID = 1L;

	  public CompositeSegment() {
	    super();
	    type = Constant.SEGMENT;
	  }

	  @Id
	  private String id;

	  private String label;

	  private String ext;

	  private List<CompositeField> fields = new ArrayList<CompositeField>();

	  private DynamicMapping dynamicMapping = new DynamicMapping();

	  private String name;

	  private String description;

	  protected String comment = "";

	  private String text1 = "";

	  private String text2 = "";
	  
	  private CoConstraints coConstraints = new CoConstraints();
	  

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getExt() {
		return ext;
	}


	public void setExt(String ext) {
		this.ext = ext;
	}


	public List<CompositeField> getFields() {
		return fields;
	}


	public void setFields(List<CompositeField> fields) {
		this.fields = fields;
	}


	public DynamicMapping getDynamicMapping() {
		return dynamicMapping;
	}


	public void setDynamicMapping(DynamicMapping dynamicMapping) {
		this.dynamicMapping = dynamicMapping;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getText1() {
		return text1;
	}


	public void setText1(String text1) {
		this.text1 = text1;
	}


	public String getText2() {
		return text2;
	}


	public void setText2(String text2) {
		this.text2 = text2;
	}


	public CoConstraints getCoConstraints() {
		return coConstraints;
	}


	public void setCoConstraints(CoConstraints coConstraints) {
		this.coConstraints = coConstraints;
	}


	@Override
	public int compareTo(CompositeSegment o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
