package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Views;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.codehaus.jackson.map.annotate.JsonView;

@Entity
public class ConformanceContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;
	
	@JsonView({Views.Profile.class})
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonView({Views.Profile.class})
	private ConstraintMetaData metaData;

	@JsonView({Views.Profile.class})
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context datatypeContext;

	@JsonView({Views.Profile.class})
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context segmentContext;
	
	@JsonView({Views.Profile.class})
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context groupContext;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConstraintMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(ConstraintMetaData metaData) {
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

	@Override
	public String toString() {
		return "ConformanceContext [id=" + id + ", metaData=" + metaData
				+ ", datatypeContext=" + datatypeContext + ", segmentContext="
				+ segmentContext + ", groupContext=" + groupContext + "]";
	}
}
