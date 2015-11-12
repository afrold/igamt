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

	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
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
		return clonedDynamicMapping;
	}

}
