package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeCompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
 * Created by Maxence Lefort on 3/9/17.
 */
public abstract class SerializeMessageOrCompositeProfile {
    @Autowired SegmentService segmentService;
    @Autowired SerializeConstraintService serializeConstraintService;
    @Autowired SerializationUtil serializationUtil;

    @Autowired SerializeSegmentService serializeSegmentService;

    protected List<String> messageSegmentsNameList;

    protected Map<String,Segment> compositeProfileSegments;

    protected int segmentPosition = 1;

    protected void serializeSegment(SegmentRefOrGroup segmentRefOrGroup, String prefix, SerializableSection segmentsSection, UsageConfig segmentUsageConfig, UsageConfig fieldsUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull) {
        serializeSegment(segmentRefOrGroup, prefix, segmentsSection, segmentUsageConfig, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull, null);
    }

    protected void serializeSegment(SegmentRefOrGroup segmentRefOrGroup, String prefix, SerializableSection segmentsSection, UsageConfig segmentUsageConfig, UsageConfig fieldsUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull, Map<String,Segment> compositeProfileSegments) {
        this.compositeProfileSegments = compositeProfileSegments;
        if(ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(),segmentUsageConfig)) {
            if (segmentRefOrGroup instanceof SegmentRef) {
                SegmentLink segmentLink = ((SegmentRef) segmentRefOrGroup).getRef();
                if (!messageSegmentsNameList.contains(segmentLink.getId())) {
                    segmentsSection.addSection(serializeSegmentService
                        .serializeSegment(segmentLink, prefix + String.valueOf(segmentPosition),
                            segmentPosition, 5, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull));
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
                        segmentUsageConfig, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull);
                }
            }
        }
    }

    protected SerializableConstraints serializeConstraints(List<? extends Constraint> constraints,
        String name, int position, String type){
        List<SerializableConstraint> serializableConstraintList = new ArrayList<>();
        for(Constraint constraint : constraints){
            SerializableConstraint serializableConstraint = new SerializableConstraint(constraint, name);
            serializableConstraintList.add(serializableConstraint);
        }
        String id = UUID.randomUUID().toString();
        SerializableConstraints serializableConstraints = new SerializableConstraints(serializableConstraintList,id,String.valueOf(position),name,type);
        return serializableConstraints;
    }

    protected SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments){
      return this.serializeSegmentRefOrGroup(segmentRefOrGroup, segmentUsageConfig, fieldUsageConfig, compositeProfileSegments,false, null);
    }
      
    protected SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host){
		if (ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), segmentUsageConfig)) {
			if (segmentRefOrGroup instanceof SegmentRef) {
				SegmentRef segmentRef = (SegmentRef) segmentRefOrGroup;
				return serializeSegmentRef(segmentRef, fieldUsageConfig, compositeProfileSegments, showInnerLinks,
						host);
			} else if (segmentRefOrGroup instanceof Group) {
				return serializeGroup((Group) segmentRefOrGroup, segmentUsageConfig, fieldUsageConfig,
						compositeProfileSegments, showInnerLinks, host);
			}
		}
		return null;
    }

    private SerializableSegmentRefOrGroup serializeSegmentRef(SegmentRef segmentRef, UsageConfig usageConfig,Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host){
        SerializableSegmentRefOrGroup serializableSegmentRefOrGroup;
        SegmentLink segmentLink = segmentRef.getRef();
        if(segmentLink != null) {
            Segment segment = null;
            if(compositeProfileSegments!=null){
                segment = findSegmentInCompositeProfileSegments(segmentLink,compositeProfileSegments);
            } else {
                segment = segmentService.findById(segmentLink.getId());
            }
            if(usageConfig != null && segment != null) {
                List<Field> filteredFieldList = new ArrayList<>();
                for (Field field : segment.getFields()) {
                    if (field != null && ExportUtil.diplayUsage(field.getUsage(), usageConfig)) {
                        filteredFieldList.add(field);
                    }
                }
                segment.setFields(filteredFieldList);
            }
            serializableSegmentRefOrGroup =
                new SerializableSegmentRefOrGroup(segmentRef, segment, this instanceof SerializeCompositeProfileService, showInnerLinks, host);
            return serializableSegmentRefOrGroup;
        }
        return null;
    }

    private Segment findSegmentInCompositeProfileSegments(SegmentLink segmentLink,
        Map<String, Segment> compositeProfileSegments) {
        for(String currentId : compositeProfileSegments.keySet()){
            if(currentId.equals(segmentLink.getId())){
                return compositeProfileSegments.get(currentId);
            }
        }
        return null;
    }

    private SerializableSegmentRefOrGroup serializeGroup(Group group, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host){
        SerializableSegmentRefOrGroup serializableGroup;
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup = serializeSegmentRefOrGroup(
                segmentRefOrGroup, segmentUsageConfig, fieldUsageConfig, compositeProfileSegments, showInnerLinks, host);
            if(serializableSegmentRefOrGroup!=null) {
                serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
            }
        }
        List<SerializableConstraint> groupConstraints = serializeConstraintService.serializeConstraints(group,group.getName());
        serializableGroup = new SerializableSegmentRefOrGroup(group,serializableSegmentRefOrGroups,groupConstraints, this instanceof SerializeCompositeProfileService);
        return serializableGroup;
    }
}
