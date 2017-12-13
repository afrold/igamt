package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.DatatypeNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;

import javax.xml.crypto.Data;

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

    @Override
    public SerializableSection serializeSegment(SegmentLink segmentLink, String prefix, Integer position, Integer headerLevel, UsageConfig segmentUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull) throws SegmentSerializationException {
        Segment segment = segmentService.findById(segmentLink.getId());
        return this.serializeSegment(segment,prefix,position,headerLevel,segmentUsageConfig,null,null, false, duplicateOBXDataTypeWhenFlavorNull, null);
    }

    @Override public SerializableSection serializeSegment(SegmentLink segmentLink, String prefix,
        Integer position, Integer headerLevel, UsageConfig segmentUsageConfig,
        Map<String, Segment> compositeProfileSegments, Map<String, Datatype> compositeProfileDatatypes, Map<String, Table> compositeProfileTables, Boolean duplicateOBXDataTypeWhenFlavorNull,Boolean includeTemorary) throws SegmentSerializationException {
        Segment segment = compositeProfileSegments.get(segmentLink.getId());
        if(segment.isTemporary() && !includeTemorary){
        	return null;
        }
        return this.serializeSegment(segment,prefix,position,headerLevel,segmentUsageConfig,compositeProfileDatatypes,compositeProfileTables, false, duplicateOBXDataTypeWhenFlavorNull, null);
        }

    private SerializableSection serializeSegment(Segment segment, String prefix, Integer position, Integer headerLevel, UsageConfig fieldUsageConfig, Map<String, Datatype> compositeProfileDatatypes, Map<String, Table> compositeProfileTables, Boolean showInnerLinks, Boolean duplicateOBXDataTypeWhenFlavorNull, String host) throws SegmentSerializationException {
        if (segment != null) {
            try {
                //Create section node
                String id = segment.getId();
                String segmentPosition = String.valueOf(position);
                String sectionHeaderLevel = String.valueOf(headerLevel);
                String title = segment.getLabel() + " - " + segment.getDescription();
                SerializableSection serializableSegmentSection =
                    new SerializableSection(id, prefix, segmentPosition, sectionHeaderLevel, title);
                //create segment node
                id = segment.getId();
                String name = segment.getName();
                String label = segment.getExt() == null || segment.getExt().isEmpty() ?
                    segment.getName() :
                    segment.getLabel();
                segmentPosition = "";
                sectionHeaderLevel = String.valueOf(headerLevel + 1);
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
                List<ConformanceStatement> generatedConformanceStatements = segment.retrieveAllConformanceStatements();
                segment.setConformanceStatements(generatedConformanceStatements);
                List<SerializableConstraint> constraints =
                    serializeConstraintService.serializeConstraints(segment, segment.getName() + "-");
                Map<Field, Datatype> fieldDatatypeMap = new HashMap<>();
                Map<Field, List<ValueSetOrSingleCodeBinding>> fieldValueSetBindingsMap = new HashMap<>();
                Map<String, Table> coConstraintValueTableMap = new HashMap<>();
                Map<String, Datatype> coConstraintDatatypeMap = new HashMap<>();
                List<Table> tables = new ArrayList<>();
                for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : segment
                    .getValueSetBindings()) {
                    if (valueSetOrSingleCodeBinding.getTableId() != null && !valueSetOrSingleCodeBinding.getTableId().isEmpty()) {
                        Table table = null;
                        if (compositeProfileTables != null && !compositeProfileTables.isEmpty()) {
                            table = compositeProfileTables.get(valueSetOrSingleCodeBinding.getTableId());
                        }
                        if (table == null) {
                            table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
                        }
                        if (table != null) {
                            tables.add(table);
                        } else {
                            throw new TableNotFoundException(valueSetOrSingleCodeBinding.getTableId());
                        }
                    }
                }
                List<Field> fieldsToBeRemoved = new ArrayList<>();
                for (Field field : segment.getFields()) {
                    try {
                        if (ExportUtil.diplayUsage(field.getUsage(), fieldUsageConfig)) {
                            if (field.getDatatype() != null) {
                                Datatype datatype = null;
                                if (compositeProfileDatatypes != null && !compositeProfileDatatypes.isEmpty()) {
                                    datatype = findDatatypeInCompositeProfileDatatypes(
                                        field.getDatatype().getId(), compositeProfileDatatypes);
                                }
                                if (datatype == null) {
                                    datatype = datatypeService.findById(field.getDatatype().getId());
                                }
                                if (datatype != null) {
                                    fieldDatatypeMap.put(field, datatype);
                                } else {
                                    throw new DatatypeNotFoundException(field.getDatatype().getId());
                                }
                            }
                            List<ValueSetOrSingleCodeBinding> fieldValueSetBindings = new ArrayList<>();
                            for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : segment.getValueSetBindings()) {
                                if (valueSetOrSingleCodeBinding.getLocation().equals(String.valueOf(field.getPosition()))) {
                                    fieldValueSetBindings.add(valueSetOrSingleCodeBinding);
                                }
                            }
                            fieldValueSetBindingsMap.put(field, fieldValueSetBindings);
                        } else {
                            fieldsToBeRemoved.add(field);
                        }
                        if (field.getText() != null && !"".equals(field.getText())) {
                            field.setText(serializationUtil.cleanRichtext(field.getText()));
                        }
                    } catch (Exception e) {
                        throw new FieldSerializationException(e, field.getName());
                    }
                }
                for (Field field : fieldsToBeRemoved) {
                    segment.getFields().remove(field);
                }
                if (segment.getCoConstraintsTable() != null
                    && segment.getCoConstraintsTable().getRowSize() > 0) {
                    for (int i = 0; i < segment.getCoConstraintsTable().getRowSize(); i++) {
                        for (CoConstraintColumnDefinition coConstraintColumnDefinition : segment.getCoConstraintsTable().getThenColumnDefinitionList()) {
                            CoConstraintTHENColumnData coConstraintTHENColumnData =
                                segment.getCoConstraintsTable().getThenMapData().get(coConstraintColumnDefinition.getId()).get(i);
                            if (!coConstraintTHENColumnData.getValueSets().isEmpty()) {
                                for (ValueSetData valueSetData : coConstraintTHENColumnData
                                    .getValueSets()) {
                                    Table table = null;
                                    if (compositeProfileTables != null && !compositeProfileTables
                                        .isEmpty()) {
                                        table = compositeProfileTables.get(valueSetData.getTableId());
                                    }
                                    if (table == null) {
                                        table = tableService.findById(valueSetData.getTableId());
                                    }
                                    if (table != null) {
                                        coConstraintValueTableMap
                                            .put(valueSetData.getTableId(), table);
                                    } else {
                                        throw new CoConstraintSerializationException(new TableNotFoundException(valueSetData.getTableId()),
                                            "CoConstraints Then data row " + i + 1);
                                    }
                                }
                            }

                            if (coConstraintTHENColumnData.getDatatypeId() != null) {
                                Datatype dt = datatypeService.findById(coConstraintTHENColumnData.getDatatypeId());
                                if (dt != null) {
                                    coConstraintDatatypeMap
                                        .put(coConstraintTHENColumnData.getDatatypeId(), dt);
                                } else {
                                    throw new CoConstraintSerializationException(
                                        new DatatypeNotFoundException(coConstraintTHENColumnData.getDatatypeId()),
                                        "CoConstraints Then data row " + i + 1);
                                }
                            }
                        }
                    }
                }
                Boolean showConfLength = serializationUtil.isShowConfLength(segment.getHl7Version());
                DynamicMappingDefinition dynamicMappingDefinition = segment.getDynamicMappingDefinition();
                Map<String, Datatype> dynamicMappingDatatypeMap = new HashMap<>();
                if (dynamicMappingDefinition != null) {
                    try {
                        for (DynamicMappingItem dynamicMappingItem : dynamicMappingDefinition.getDynamicMappingItems()) {
                            try {
                                if (dynamicMappingItem != null
                                    && dynamicMappingItem.getDatatypeId() != null) {
                                    Datatype datatype = datatypeService.findById(dynamicMappingItem.getDatatypeId());
                                    if (datatype != null) {
                                        dynamicMappingDatatypeMap
                                            .put(dynamicMappingItem.getDatatypeId(), datatype);
                                    } else {
                                        throw new DatatypeNotFoundException(dynamicMappingItem.getDatatypeId());
                                    }
                                }
                            } catch (Exception e) {
                                throw new DynamicMappingItemSerializationException(e,
                                    dynamicMappingDefinition.getDynamicMappingItems().indexOf(dynamicMappingItem)+1);
                            }
                        }
                    } catch (Exception e) {
                        throw new DynamicMappingSerializationException(e, "DynamicMapping");
                    }
                }
                SerializableSegment serializableSegment =
                    new SerializableSegment(id, prefix, segmentPosition, sectionHeaderLevel, title,
                        segment, name, label, description, comment, defPreText, defPostText,
                        constraints, fieldDatatypeMap, fieldValueSetBindingsMap, tables,
                        coConstraintValueTableMap, dynamicMappingDatatypeMap, showConfLength,
                        showInnerLinks, duplicateOBXDataTypeWhenFlavorNull, host,
                        coConstraintDatatypeMap);
                serializableSegmentSection.addSection(serializableSegment);
                return serializableSegmentSection;
            } catch (Exception e){
                throw new SegmentSerializationException(e,segment.getLabel());
            }
        }
        return null;
    }

    private Datatype findDatatypeInCompositeProfileDatatypes(String id,
        Map<String, Datatype> compositeProfileDatatypes){
        if(compositeProfileDatatypes.containsKey(id)) {
            return compositeProfileDatatypes.get(id);
        }
        return null;
    }

	@Override
	public SerializableSection serializeSegment(Segment segment, String host) throws SegmentSerializationException {
		ExportConfig defaultConfig = ExportConfig.getBasicExportConfig(true);
		return serializeSegment(segment, String.valueOf(0), 1, 1, defaultConfig.getFieldsExport(), null, null, true, false, host);
	}

}
