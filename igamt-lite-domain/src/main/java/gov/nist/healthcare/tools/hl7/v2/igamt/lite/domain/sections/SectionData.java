package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
 
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
//@JsonSubTypes({
//	@JsonSubTypes.Type(value = SectionDataWithText.class, name = "withText"),
//	@JsonSubTypes.Type(value = SectionDataWithLink.class, name = "withLink"),
//	@JsonSubTypes.Type(value = MessageSectionData.class, name = "message"),
//	@JsonSubTypes.Type(value = CompositeProfileSectionData.class, name = "compositeProfile"),
//	@JsonSubTypes.Type(value = RootSectionData.class, name = "root")
//
//	
//	
//})
public abstract class SectionData{

	public SectionData() {
		super();
		// TODO Auto-generated constructor stub
	}
  
}