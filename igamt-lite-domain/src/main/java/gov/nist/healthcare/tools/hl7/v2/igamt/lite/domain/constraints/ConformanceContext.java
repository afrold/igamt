package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

public class ConformanceContext implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;

	@Id
	@GenericGenerator(name = "CONFORMANCECONTEXT_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.ConformanceContextIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_conformance_context"))
	@GeneratedValue(generator = "CONFORMANCECONTEXT_ID_GENERATOR")
	protected String id;

	@NotNull
	@Column(nullable = false)
	protected String uuid;
	
	@NotNull
	@Column(nullable = false)
	protected MetaData metaData;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context datatypeContext;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context segmentContext;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context groupContext;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public Context getDatatypeContext() {
		return datatypeContext;
	}

	public void setDatatypeContext(Context datatypeContext) {
		this.datatypeContext = datatypeContext;
	}

	public Context getSegmentContext() {
		return segmentContext;
	}

	public void setSegmentContext(Context segmentContext) {
		this.segmentContext = segmentContext;
	}

	public Context getGroupContext() {
		return groupContext;
	}

	public void setGroupContext(Context groupContext) {
		this.groupContext = groupContext;
	}
	
	
}
