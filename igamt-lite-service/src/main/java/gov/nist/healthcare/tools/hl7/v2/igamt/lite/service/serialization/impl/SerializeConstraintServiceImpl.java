package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModelWithConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
 * Created by Maxence Lefort on 12/13/16.
 */
@Service
public class SerializeConstraintServiceImpl implements SerializeConstraintService {

    @Override public List<SerializableConstraint> serializeConstraints (
        DataModelWithConstraints dataModelWithConstraints, String location) throws ConstraintSerializationException {
        try {
            List<ConformanceStatement> conformanceStatements = dataModelWithConstraints.getConformanceStatements();
            List<Predicate> predicates = dataModelWithConstraints.getPredicates();
            List<SerializableConstraint> serializableConstraints = new ArrayList<>();
            if (conformanceStatements != null && !conformanceStatements.isEmpty()) {
                for (Constraint constraint : conformanceStatements) {
                    try {
                        SerializableConstraint serializableConstraint =
                            new SerializableConstraint(constraint, location);
                        serializableConstraints.add(serializableConstraint);
                    } catch (Exception e) {
                        throw new ConstraintSerializationException(e, constraint.toString());
                    }
                }
            }
            if (predicates != null && !predicates.isEmpty()) {
                for (Constraint constraint : predicates) {
                    try {
                        SerializableConstraint serializableConstraint =
                            new SerializableConstraint(constraint, location);
                        serializableConstraints.add(serializableConstraint);
                    } catch (Exception e) {
                        throw new ConstraintSerializationException(e, constraint.toString());
                    }
                }
            }
            return serializableConstraints;
        } catch (Exception e){
            throw new ConstraintSerializationException(e,location);
        }
    }

}
