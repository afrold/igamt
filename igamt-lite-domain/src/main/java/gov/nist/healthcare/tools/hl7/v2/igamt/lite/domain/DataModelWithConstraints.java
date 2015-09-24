/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public abstract class DataModelWithConstraints extends DataModel{
	protected List<Predicate> predicates = new ArrayList<Predicate>();

	protected List<ConformanceStatement> conformanceStatements = new ArrayList<ConformanceStatement>();

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(List<ConformanceStatement> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}
	
	public void addPredicate(Predicate p) {
		predicates.add(p);
	}

	public void addConformanceStatement(ConformanceStatement cs) {
		conformanceStatements.add(cs);
	}
	
	public Predicate findOnePredicate(String predicateId) {
		for (Predicate predicate : this.getPredicates()) {
			if (predicate.getId().equals(predicateId)) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(String confId) {
		for (ConformanceStatement conf : this.getConformanceStatements()) {
			if (conf.getId().equals(confId)) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		Predicate p = findOnePredicate(predicateId);
		return p != null && this.getPredicates().remove(p);
	}

	public boolean deleteConformanceStatement(String cId) {
		ConformanceStatement c = findOneConformanceStatement(cId);
		return c != null && this.getConformanceStatements().remove(c);
	}
	
	

}
