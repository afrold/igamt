package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

public class ProfileComponentDataLink extends SectionDataLink{
	private String description;
	private String ext; 
	private String name;

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
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public ProfileComponentDataLink() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
