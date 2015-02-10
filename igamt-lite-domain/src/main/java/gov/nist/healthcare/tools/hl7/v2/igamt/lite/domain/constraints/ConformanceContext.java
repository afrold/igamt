package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ConformanceContext implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private ConstraintMetaData metaData;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context datatypeContext;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Context segmentContext;

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
	
	@Override
    public ConformanceContext clone() throws CloneNotSupportedException {
		ConformanceContext clonedConformanceContext = (ConformanceContext) super.clone();
		clonedConformanceContext.setDatatypeContext(datatypeContext.clone());
		clonedConformanceContext.setGroupContext(groupContext.clone());
		clonedConformanceContext.setId(null);
		clonedConformanceContext.setMetaData(metaData.clone());
		clonedConformanceContext.setSegmentContext(segmentContext.clone());
        return clonedConformanceContext;
    }
}
