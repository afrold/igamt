package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

////@Entity
////@Table(name = "DYNAMIC_MAPPING")
public class DynamicMapping implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	// @Id
	// //@Column(name = "ID")
	// //@GeneratedValue(strategy = GenerationType.AUTO)
	protected String id;

	// @Min(1)
	// //@NotNull
	// //@Column(nullable = false, name = "MIN")
	private Integer min;

	// //@NotNull
	// //@Column(nullable = false, name = "MAX")
	private String max;

	// //@NotNull
	// //@Column(nullable = false, name = "DYNAMIC_MAPPING_POSITION")
	private Integer position = 0;

	// //@OneToMany(cascade = CascadeType.ALL)
	// //@JoinTable(name = "DYNAMIC_MAPPING_MAPPING", joinColumns =
	// //@JoinColumn(name = "DYNAMIC_MAPPING"), inverseJoinColumns =
	// //@JoinColumn(name = "MAPPING"))
	// @OrderBy(value = "position")
	protected List<Mapping> mappings = new ArrayList<Mapping>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void addMapping(Mapping m) {
		m.setPosition(mappings.size() + 1);
		mappings.add(m);
	}

	@Override
	public DynamicMapping clone() throws CloneNotSupportedException {
		DynamicMapping clonedDynamicMapping = new DynamicMapping();
		clonedDynamicMapping.setId(id);
		clonedDynamicMapping.setMappings(mappings);
		for (Mapping m : this.mappings) {
			clonedDynamicMapping.addMapping(m.clone());
		}
		clonedDynamicMapping.setMax(max);
		clonedDynamicMapping.setMin(min);
		clonedDynamicMapping.setPosition(position);
		return clonedDynamicMapping;
	}

}
