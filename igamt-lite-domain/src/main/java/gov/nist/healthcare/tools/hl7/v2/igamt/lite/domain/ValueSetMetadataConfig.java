package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ValueSetMetadataConfig {
	private boolean stability,extensibility,contentDefinition;

	public ValueSetMetadataConfig(boolean stability, boolean extensibility, boolean contentDefinition) {
		super();
		this.stability = stability;
		this.extensibility = extensibility;
		this.contentDefinition = contentDefinition;
	}

	public ValueSetMetadataConfig() {
		super();
	}

	public boolean isStability() {
		return stability;
	}

	public void setStability(boolean stability) {
		this.stability = stability;
	}

	public boolean isExtensibility() {
		return extensibility;
	}

	public void setExtensibility(boolean extensibility) {
		this.extensibility = extensibility;
	}

	public boolean isContentDefinition() {
		return contentDefinition;
	}

	public void setContentDefinition(boolean contentDefinition) {
		this.contentDefinition = contentDefinition;
	}
	
}
