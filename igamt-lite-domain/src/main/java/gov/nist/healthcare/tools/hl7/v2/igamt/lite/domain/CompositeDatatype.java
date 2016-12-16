package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class CompositeDatatype extends DataModelWithConstraints implements java.io.Serializable, Cloneable,
Comparable<CompositeDatatype> {
	
	private static final long serialVersionUID = 1L;

	  public CompositeDatatype() {
	    super();
	    this.type = Constant.DATATYPE;
	  }

	  @Id
	  private String id;

	  private String label;

	  private String ext="";

	  private String purposeAndUse = "";

	  protected List<CompositeComponent> components = new ArrayList<CompositeComponent>();

	  private String name = "";
	  private List<String> hl7versions=new ArrayList<String>();

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

	public String getPurposeAndUse() {
		return purposeAndUse;
	}

	public void setPurposeAndUse(String purposeAndUse) {
		this.purposeAndUse = purposeAndUse;
	}

	public List<CompositeComponent> getComponents() {
		return components;
	}

	public void setComponents(List<CompositeComponent> components) {
		this.components = components;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getHl7versions() {
		return hl7versions;
	}

	public void setHl7versions(List<String> hl7versions) {
		this.hl7versions = hl7versions;
	}

	@Override
	public int compareTo(CompositeDatatype o) {
		// TODO Auto-generated method stub
		return 0;
	} 

}
