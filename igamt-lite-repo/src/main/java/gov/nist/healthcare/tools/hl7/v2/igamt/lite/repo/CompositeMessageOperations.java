package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;

public interface CompositeMessageOperations {
	public List<CompositeMessage> findByIds(Set<String> ids);

}
