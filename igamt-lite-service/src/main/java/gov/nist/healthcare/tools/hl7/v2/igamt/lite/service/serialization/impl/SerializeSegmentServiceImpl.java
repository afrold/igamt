package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CCValue;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeDatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeTableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service public class SerializeSegmentServiceImpl implements SerializeSegmentService {

    @Autowired SegmentService segmentService;

    @Autowired SerializationUtil serializationUtil;

    @Autowired DatatypeService datatypeService;

    @Autowired TableService tableService;

    @Autowired SerializeConstraintService serializeConstraintService;

    @Autowired SerializeDatatypeService serializeDatatypeService;

    @Autowired SerializeTableService serializeTableService;

    @Override
    public SerializableSection serializeSegment(SegmentLink segmentLink, TableLibrary tableLibrary,
        DatatypeLibrary datatypeLibrary, String prefix, Integer position) {
        Segment segment = segmentService.findById(segmentLink.getId());
        if (segment != null) {
            //Create section node
            String id = segment.getId();
            String segmentPosition = String.valueOf(position);
            String headerLevel = String.valueOf(3);
            String title = segmentLink.getLabel() + " - " + segment.getDescription();
            SerializableSection serializableSegmentSection = new SerializableSection(id,prefix,segmentPosition,headerLevel,title);
            //create segment node
            id = segment.getId();
            String name = segmentLink.getName();
            String label = segmentLink.getExt() == null || segmentLink.getExt().isEmpty() ?
                segmentLink.getName() :
                segmentLink.getLabel();
            segmentPosition = "";
            headerLevel = String.valueOf(4);
            title = segment.getName();
            String description = segment.getDescription();
            String comment = "";
            if (segment.getComment() != null && !segment.getComment().isEmpty()) {
                 comment = segment.getComment();
            }
            String defPreText, defPostText;
            defPostText = defPreText = "";

            if ((segment.getText1() != null && !segment.getText1().isEmpty()) || (
                segment.getText2() != null && !segment.getText2().isEmpty())) {
                if (segment.getText1() != null && !segment.getText1().isEmpty()) {
                    defPreText = serializationUtil.cleanRichtext(segment.getText1());
                }
                if (segment.getText2() != null && !segment.getText2().isEmpty()) {
                    defPostText = serializationUtil.cleanRichtext(segment.getText2());
                }
            }

            List<SerializableConstraint> constraints =
                serializeConstraintService.serializeConstraints(segment, segment.getName() + "-");
            Map<Field, Datatype> fieldDatatypeMap = new HashMap<>();
            Map<Field, List<Table>> fieldTableMap = new HashMap<>();
            Map<CCValue, Table> coConstraintValueTableMap = new HashMap<>();
            for (Field field : segment.getFields()) {
                if (field.getDatatype() != null) {
                    Datatype datatype = datatypeService.findById(field.getDatatype().getId());
                    fieldDatatypeMap.put(field, datatype);
                }
                if (field.getTables() != null && !field.getTables().isEmpty()) {
                    List<Table> tables = new ArrayList<>();
                    for (TableLink tableLink : field.getTables()) {
                        Table table = tableService.findById(tableLink.getId());
                        tables.add(table);
                    }
                    fieldTableMap.put(field, tables);
                }
            }

            if (segment.getCoConstraints() != null) {
                CoConstraints coConstraints = segment.getCoConstraints();
                if (coConstraints.getConstraints() != null && !coConstraints.getConstraints()
                    .isEmpty()) {
                    for (CoConstraint coConstraint : coConstraints.getConstraints()) {
                        if (coConstraint.getValues() != null && !coConstraint.getValues()
                            .isEmpty()) {
                            for (CCValue ccValue : coConstraint.getValues()) {
                                Table table = tableService.findById(ccValue.getValue());
                                if (table != null) {
                                    coConstraintValueTableMap.put(ccValue, table);
                                }
                            }
                        }
                    }
                }
            }
            SerializableSegment serializableSegment = new SerializableSegment(id, prefix, segmentPosition, headerLevel, title, segment, name, label, description, comment, defPreText, defPostText, constraints, fieldDatatypeMap, fieldTableMap, coConstraintValueTableMap);
            serializableSegmentSection.addSection(serializableSegment);
            return serializableSegmentSection;
        }
        return null;
    }

}
