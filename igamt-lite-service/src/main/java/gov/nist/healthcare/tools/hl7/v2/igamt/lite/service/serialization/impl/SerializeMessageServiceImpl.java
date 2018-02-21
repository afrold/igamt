package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.MessageSerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;

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
public class SerializeMessageServiceImpl extends SerializeMessageOrCompositeProfile implements SerializeMessageService {

	static final Logger logger = LoggerFactory.getLogger(SerializeMessageService.class);
	
    @Autowired
    SerializationUtil serializationUtil;

    @Autowired TableService tableService;

    @Override public SerializableMessage serializeMessage(Message message, String prefix, String headerLevel, SerializationLayout serializationLayout, String hl7Version, ExportConfig exportConfig)  throws
        MessageSerializationException {
      return serializeMessage(message, prefix, headerLevel, serializationLayout, hl7Version, exportConfig, false, null);
    }

    
    private SerializableMessage serializeMessage(Message message, String prefix, String headerLevel, SerializationLayout serializationLayout, String hl7Version, ExportConfig exportConfig, Boolean showInnerLinks, String host)  throws
        MessageSerializationException {
        try {
            List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
            String type = "ConformanceStatement";
            List<ConformanceStatement> generatedConformanceStatements = message.retrieveAllConformanceStatements();
            SerializableConstraints serializableConformanceStatements =
                serializeConstraints(generatedConformanceStatements, message.getName(), message.getPosition(),
                    type);
            type = "ConditionPredicate";
            SerializableConstraints serializablePredicates =
                serializeConstraints(message.getPredicates(), message.getName(), message.getPosition(),
                    type);
            int segmentSectionPosition = 1;
            String usageNote, defPreText, defPostText;
            usageNote = defPreText = defPostText = "";
            if (message.getUsageNote() != null && !message.getUsageNote().isEmpty()) {
                usageNote = serializationUtil.cleanRichtext(message.getUsageNote());
                segmentSectionPosition++;
            }
            if (message.getDefPreText() != null && !message.getDefPreText().isEmpty()) {
                defPreText = serializationUtil.cleanRichtext(message.getDefPreText());
                segmentSectionPosition++;
            }
            if (message.getDefPostText() != null && !message.getDefPostText().isEmpty()) {
                defPostText = serializationUtil.cleanRichtext(message.getDefPostText());
            }
            Boolean showConfLength = serializationUtil.isShowConfLength(hl7Version);
            List<Table> tables = new ArrayList<>();
            for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : message
                .getValueSetBindings()) {
                if (valueSetOrSingleCodeBinding.getTableId() != null && !valueSetOrSingleCodeBinding
                    .getTableId().isEmpty()) {
                    Table table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
                    if (table != null) {
                        tables.add(table);
                    }
                }
            }
            HashMap<String, String> positionNameSegOrGroupMap = super.retrieveComponentsPaths(message);
            SerializableMessage serializableMessage =
                new SerializableMessage(message, prefix, headerLevel, serializableSegmentRefOrGroups,
                    serializableConformanceStatements, serializablePredicates, usageNote, defPreText,
                    defPostText, tables, positionNameSegOrGroupMap, showConfLength);
            SerializableSection messageSegments =
                new SerializableSection(message.getId() + "_segments",
                    prefix + "." + String.valueOf(message.getPosition()) + "."
                        + segmentSectionPosition, "1", "4", "Segment definitions");
            this.messageSegmentsNameList = new ArrayList<>();
            this.segmentPosition = 1;
            UsageConfig fieldsUsageConfig = exportConfig.getFieldsExport();
            UsageConfig segmentUsageConfig = exportConfig.getSegmentsExport();
            UsageConfig segmentOrGroupUsageConfig = exportConfig.getSegmentORGroupsMessageExport();
            for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
                SerializableSegmentRefOrGroup serializableSegmentRefOrGroup =
                    serializeSegmentRefOrGroup(segmentRefOrGroup, segmentOrGroupUsageConfig,
                        fieldsUsageConfig, null, showInnerLinks, host);
                serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
                if (serializationLayout.equals(SerializationLayout.PROFILE)) {
                    serializeSegment(segmentRefOrGroup, messageSegments.getPrefix() + ".",
                        messageSegments, segmentUsageConfig, fieldsUsageConfig, exportConfig.isGreyOutOBX2FlavorColumn(), exportConfig.getCoConstraintExportMode());
                }
            }
            if (!messageSegments.getSerializableSectionList().isEmpty()) {
                serializableMessage.addSection(messageSegments);
            }
            return serializableMessage;
        } catch (Exception e){
            throw new MessageSerializationException(e, message.getName());
        }
    }


	@Override
	public SerializableElement serializeMessage(Message message, String host)  throws
      MessageSerializationException {
		return serializeMessage(message, String.valueOf(0), String.valueOf(1),SerializationLayout.IGDOCUMENT, message.getHl7Version(), ExportConfig.getBasicExportConfig(true), true, host);
	}

}
