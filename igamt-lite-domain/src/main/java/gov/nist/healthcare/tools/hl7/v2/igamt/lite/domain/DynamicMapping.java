package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="DYNAMIC_MAPPING")
public class DynamicMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Min(1)
	@NotNull
	@Column(nullable = false,name="MIN")
	private BigInteger min;

	@NotNull
	@Column(nullable = false,name="MAX")
	private String max; 
	
	@NotNull
	@Column(nullable = false,name="POSITION")
	private Integer position;


	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SEGMENT_ID")
	private Segment segment;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "DYNAMIC_MAPPING_MAPPING", joinColumns = @JoinColumn(name = "DYNAMIC_MAPPING"), inverseJoinColumns = @JoinColumn(name = "MAPPING"))
	@OrderBy(value="position")
	protected Set<Mapping> mappings = new HashSet<Mapping>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigInteger getMin() {
		return min;
	}

	public void setMin(BigInteger min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}


	public Set<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(Set<Mapping> mappings) {
		this.mappings = mappings;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	} 
	
	public void addMapping(Mapping m) {
		m.setPosition(mappings.size() +1);
		mappings.add(m);
 	}
	
	
	
	

}
