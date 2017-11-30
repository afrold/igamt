package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ApplyInfo;
@JsonTypeName("compositeProfile")
public class CompositeProfileSectionData extends SectionData {
	  private String id;
	  private String name;
	  private String ext;
	  private String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


}
