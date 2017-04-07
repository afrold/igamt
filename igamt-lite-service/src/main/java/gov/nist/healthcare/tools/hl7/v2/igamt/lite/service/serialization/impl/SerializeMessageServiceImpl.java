package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    SerializationUtil serializationUtil;

    @Autowired TableService tableService;

    @Override public SerializableMessage serializeMessage(Message message, String prefix, SerializationLayout serializationLayout, String hl7Version, ExportConfig exportConfig) {
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        String type = "ConformanceStatement";
        List<ConformanceStatement> generatedConformanceStatements = message.retrieveAllConformanceStatements();
        ArrayList<ConformanceStatement> conformanceStatementsList = new ArrayList<>();
        for(ConformanceStatement conformanceStatement : generatedConformanceStatements){
        	if(conformanceStatement!= null){
        		conformanceStatementsList.add(conformanceStatement);
        	}
        }
        for(ConformanceStatement conformanceStatement : message.getConformanceStatements()){
        	if(conformanceStatement!= null){
        		conformanceStatementsList.add(conformanceStatement);
        	}
        }
        SerializableConstraints serializableConformanceStatements = serializeConstraints(conformanceStatementsList,message.getName(),message.getPosition(),type);
        type = "ConditionPredicate";
        SerializableConstraints serializablePredicates = serializeConstraints(message.getPredicates(),message.getName(),message.getPosition(),type);
        int segmentSectionPosition = 1;
        String usageNote, defPreText, defPostText;
        usageNote = defPreText = defPostText = "";
        if(message.getUsageNote()!=null&&!message.getUsageNote().isEmpty()){
            usageNote = serializationUtil.cleanRichtext(message.getUsageNote());
            segmentSectionPosition++;
        }
        if(message.getDefPreText()!=null&&!message.getDefPreText().isEmpty()){
            defPreText = serializationUtil.cleanRichtext(message.getDefPreText());
            segmentSectionPosition++;
        }
        if(message.getDefPostText()!=null&&!message.getDefPostText().isEmpty()){
            defPostText = serializationUtil.cleanRichtext(message.getDefPostText());
        }
        Boolean showConfLength = serializationUtil.isShowConfLength(hl7Version);
        List<Table> tables = new ArrayList<>();
        for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : message.getValueSetBindings()){
            if(valueSetOrSingleCodeBinding.getTableId()!=null && !valueSetOrSingleCodeBinding.getTableId().isEmpty()){
                Table table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
                if(table!=null){
                    tables.add(table);
                }
            }
        }
        SerializableMessage serializableMessage = new SerializableMessage(message,prefix,serializableSegmentRefOrGroups,serializableConformanceStatements,serializablePredicates,usageNote,defPreText,defPostText,tables,showConfLength);
        SerializableSection messageSegments = new SerializableSection(message.getId()+"_segments",prefix+"."+String.valueOf(message.getPosition())+"."+segmentSectionPosition,"1","4","Segment definitions");
        this.messageSegmentsNameList = new ArrayList<>();
        this.segmentPosition = 1;
        UsageConfig fieldsUsageConfig = exportConfig.getFieldsExport();
        UsageConfig segmentUsageConfig = exportConfig.getSegmentsExport();
        UsageConfig segmentOrGroupUsageConfig = exportConfig.getSegmentORGroupsMessageExport();
        for(SegmentRefOrGroup segmentRefOrGroup : message.getChildren()){
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup = serializeSegmentRefOrGroup(segmentRefOrGroup,segmentOrGroupUsageConfig,fieldsUsageConfig, null);
            serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
            if(serializationLayout.equals(SerializationLayout.PROFILE)){
                serializeSegment(segmentRefOrGroup,
                    messageSegments.getPrefix() + ".", messageSegments, segmentUsageConfig, fieldsUsageConfig);
            }
        }
        if(!messageSegments.getSerializableSectionList().isEmpty()){
            serializableMessage.addSection(messageSegments);
        }
        return serializableMessage;
    }

}
