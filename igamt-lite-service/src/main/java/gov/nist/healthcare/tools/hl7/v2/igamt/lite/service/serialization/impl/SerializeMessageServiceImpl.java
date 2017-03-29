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
public class SerializeMessageServiceImpl implements SerializeMessageService{

    @Autowired
    SegmentService segmentService;
    @Autowired
    SerializeConstraintService serializeConstraintService;
    @Autowired
    SerializationUtil serializationUtil;

    @Autowired TableService tableService;


    @Autowired
    SerializeSegmentService serializeSegmentService;

    private List<String> messageSegmentsNameList;

    private int segmentPosition = 1;

    @Override public SerializableMessage serializeMessage(Message message, String prefix, SerializationLayout serializationLayout, String hl7Version, ExportConfig exportConfig) {
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        String type = "ConformanceStatement";
        SerializableConstraints serializableConformanceStatements = serializeConstraints(message.getConformanceStatements(),message,type);
        type = "ConditionPredicate";
        SerializableConstraints serializablePredicates = serializeConstraints(message.getPredicates(),message,type);
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
        UsageConfig segmentOrGroupUsageConfig = exportConfig.getSegmentORGroupsExport();
        for(SegmentRefOrGroup segmentRefOrGroup : message.getChildren()){
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup = serializeSegmentRefOrGroup(segmentRefOrGroup,segmentOrGroupUsageConfig,fieldsUsageConfig);
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

    private void serializeSegment(SegmentRefOrGroup segmentRefOrGroup, String prefix, SerializableSection segmentsSection, UsageConfig segmentUsageConfig, UsageConfig fieldsUsageConfig) {
        if(ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), segmentUsageConfig)) {
            if (segmentRefOrGroup instanceof SegmentRef) {
                SegmentLink segmentLink = ((SegmentRef) segmentRefOrGroup).getRef();
                if (!messageSegmentsNameList.contains(segmentLink.getId())) {
                    segmentsSection.addSection(serializeSegmentService
                        .serializeSegment(segmentLink, prefix + String.valueOf(segmentPosition),
                            segmentPosition, 5, fieldsUsageConfig));
                    messageSegmentsNameList.add(segmentLink.getId());
                    segmentPosition++;
                }
            } else if (segmentRefOrGroup instanceof Group) {
            /*String id = UUID.randomUUID().toString();
            String headerLevel = String.valueOf(4);
            String title = ((Group) segmentRefOrGroup).getName();
            SerializableSection serializableSection = new SerializableSection(id,prefix,String.valueOf(position),headerLevel,title);*/
                for (SegmentRefOrGroup groupSegmentRefOrGroup : ((Group) segmentRefOrGroup)
                    .getChildren()) {
                    serializeSegment(groupSegmentRefOrGroup, prefix, segmentsSection,
                        segmentUsageConfig, fieldsUsageConfig);
                }
            }
        }
    }

    private SerializableConstraints serializeConstraints(List<? extends Constraint> constraints,Message message,String type){
        List<SerializableConstraint> serializableConstraintList = new ArrayList<>();
        for(Constraint constraint : constraints){
            SerializableConstraint serializableConstraint = new SerializableConstraint(constraint, message.getName());
            serializableConstraintList.add(serializableConstraint);
        }
        String id = UUID.randomUUID().toString();
        String position = String.valueOf(message.getPosition());
        String title = message.getName();
        SerializableConstraints serializableConstraints = new SerializableConstraints(serializableConstraintList,id,position,title,type);
        return serializableConstraints;
    }

    private SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig){
        if(segmentRefOrGroup instanceof SegmentRef){
            SegmentRef segmentRef = (SegmentRef) segmentRefOrGroup;
            if(ExportUtil.diplayUsage(segmentRef.getUsage(), segmentUsageConfig)) {
                return serializeSegmentRef(segmentRef, fieldUsageConfig);
            }
        } else if (segmentRefOrGroup instanceof Group){
            return serializeGroup((Group) segmentRefOrGroup,segmentUsageConfig,fieldUsageConfig);
        }
        return null;
    }

    private SerializableSegmentRefOrGroup serializeSegmentRef(SegmentRef segmentRef, UsageConfig usageConfig){
        SerializableSegmentRefOrGroup serializableSegmentRefOrGroup;
        SegmentLink segmentLink = segmentRef.getRef();
        if(segmentLink != null) {
            Segment segment = segmentService.findById(segmentLink.getId());
            if(usageConfig != null) {
                List<Field> filteredFieldList = new ArrayList<>();
                for (Field field : segment.getFields()) {
                    if (ExportUtil.diplayUsage(field.getUsage(), usageConfig)) {
                        filteredFieldList.add(field);
                    }
                }
                segment.setFields(filteredFieldList);
            }
            serializableSegmentRefOrGroup =
                new SerializableSegmentRefOrGroup(segmentRef, segment);
            return serializableSegmentRefOrGroup;
        }
        return null;
    }

    private SerializableSegmentRefOrGroup serializeGroup(Group group, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig){
        SerializableSegmentRefOrGroup serializableGroup;
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup = serializeSegmentRefOrGroup(
                segmentRefOrGroup, segmentUsageConfig, fieldUsageConfig);
            if(serializableSegmentRefOrGroup!=null) {
                serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
            }
        }
        List<SerializableConstraint> groupConstraints = serializeConstraintService.serializeConstraints(group,group.getName());
        serializableGroup = new SerializableSegmentRefOrGroup(group,serializableSegmentRefOrGroups,groupConstraints);
        return serializableGroup;
    }
}
