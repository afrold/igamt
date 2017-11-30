package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
 
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
//    @JsonSubTypes.Type(value = ProfileComponentSectionData.class, name = "profileComponents"),
//    @JsonSubTypes.Type(value = CompositeProfileSectionData.class, name = "compositeProfiles"),
//    @JsonSubTypes.Type(value = DataTypeLibrarySectionData.class, name = "datatypeLibrary"),
//    
//    @JsonSubTypes.Type(value = SegmentLibrarySectionData.class, name = "segmentLibrary"),
//    @JsonSubTypes.Type(value = TableLibraySectionData.class, name = "tableLibrary"),
//    @JsonSubTypes.Type(value = DataTypeLibrarySectionData.class, name = "narativeSection"),
//    @JsonSubTypes.Type(value = ProfileSectionData.class, name = "profile")
	@JsonSubTypes.Type(value = SectionDataWithText.class, name = "withText"),
	@JsonSubTypes.Type(value = SectionDataWithLink.class, name = "withLink"),
	@JsonSubTypes.Type(value = MessageSectionData.class, name = "message"),
	@JsonSubTypes.Type(value = CompositeProfileSectionData.class, name = "compositeProfile"),
	@JsonSubTypes.Type(value = RootSectionData.class, name = "root")

	
	
})
public abstract class SectionData {
	protected String referenceType;
	protected String referenceId;
	protected  Integer position;

	
	
	public String getReferenceType() {
		return referenceType;
	}
	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer integer) {
		this.position = integer;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
 
}