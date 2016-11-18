package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedDataType;

public interface UnchangedDatatypeOperation{

	List<UnchangedDataType> findByNameAndVersions(String name, String version);
}
