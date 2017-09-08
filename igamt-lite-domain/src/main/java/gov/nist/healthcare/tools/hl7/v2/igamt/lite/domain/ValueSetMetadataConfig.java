package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ValueSetMetadataConfig {
	private boolean stability,extensibility,contentDefinition,oid,type;

	public ValueSetMetadataConfig(boolean stability, boolean extensibility, boolean contentDefinition, boolean oid,
			boolean type) {
		super();
		this.stability = stability;
		this.extensibility = extensibility;
		this.contentDefinition = contentDefinition;
		this.oid = oid;
		this.type = type;
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

	public boolean isOid() {
		return oid;
	}

	public void setOid(boolean oid) {
		this.oid = oid;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}
	
	
}
