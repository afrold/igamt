package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="CONFORMANCECONTEXT")
public class ConformanceContext implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private ConstraintMetaData metaData;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="DATATYPES_ID")
 	private Context datatypes;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="SEGMENTS_ID")
	private Context segments;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
 	@JoinColumn(name="GROUPS_ID")
	private Context groups;

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

	 

	 
	public Context getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Context datatypes) {
		this.datatypes = datatypes;
	}

	public Context getSegments() {
		return segments;
	}

	public void setSegments(Context segments) {
		this.segments = segments;
	}

	public Context getGroups() {
		return groups;
	}

	public void setGroups(Context groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "ConformanceContext [id=" + id + ", metaData=" + metaData
				+ ", datatypeContext=" + datatypes + ", segmentContext="
				+ segments + ", groupContext=" + groups + "]";
	}
	
	@Override
    public ConformanceContext clone() throws CloneNotSupportedException {
		ConformanceContext clonedConformanceContext = (ConformanceContext) super.clone();
		clonedConformanceContext.setDatatypes(datatypes.clone());
		clonedConformanceContext.setGroups(groups.clone());
		clonedConformanceContext.setId(null);
		clonedConformanceContext.setMetaData(metaData.clone());
		clonedConformanceContext.setSegments(segments.clone());
        return clonedConformanceContext;
    }
}
