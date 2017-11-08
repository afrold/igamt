package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p>
 * Created by Maxence Lefort on 12/8/16.
 */
public class SerializableConstraint extends SerializableElement{

    private Constraint constraint;
    private String locationName;

    public SerializableConstraint(Constraint constraint, String locationName) {
        this.constraint = constraint;
        this.locationName = locationName;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    @Override public Element serializeElement() throws ConstraintSerializationException {
        try {
            Element elmConstraint = new Element("Constraint");
            elmConstraint.addAttribute(new Attribute("Id",
                constraint.getConstraintId() == null ? "" : constraint.getConstraintId()));
            if (null != constraint.getConstraintTarget()
                && constraint.getConstraintTarget().length() > "[".length() && constraint
                .getConstraintTarget().contains("[")) {
                elmConstraint.addAttribute(new Attribute("Location", constraint.getConstraintTarget()
                    .substring(0, constraint.getConstraintTarget().indexOf('['))));
            } else {
                elmConstraint.addAttribute(new Attribute("Location", ""));
            }
            elmConstraint.addAttribute(
                new Attribute("LocationName", locationName == null ? "" : locationName));
            elmConstraint.appendChild(
                constraint.getDescription() == null ? "" : constraint.getDescription());
            if (constraint instanceof Predicate) {
                elmConstraint.addAttribute(new Attribute("Type", "pre"));
                elmConstraint.addAttribute(new Attribute("Usage",
                    "C(" + ((Predicate) constraint).getTrueUsage() + "/" + ((Predicate) constraint)
                        .getFalseUsage() + ")"));
            } else if (constraint instanceof ConformanceStatement) {
                elmConstraint.addAttribute(new Attribute("Type", "cs"));
                elmConstraint.addAttribute(new Attribute("Classification",
                    constraint.getConstraintClassification() == null ? "" : constraint.getConstraintClassification()));
            }
            return elmConstraint;
        } catch (Exception e){
            throw new ConstraintSerializationException(e,constraint,locationName);
        }
    }
}
