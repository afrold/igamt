package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;

public interface ConstraintsSerialization {
	Constraints deserializeXMLToConformanceStatements(String xmlConstraints);
	Constraints deserializeXMLToPredicates(String xmlConstraints);
	String serializeConstraintsToXML(Constraints conformanceStatements, Constraints predicates);
	nu.xom.Document serializeConstraintsToDoc(Constraints conformanceStatements, Constraints predicates);
	
}
