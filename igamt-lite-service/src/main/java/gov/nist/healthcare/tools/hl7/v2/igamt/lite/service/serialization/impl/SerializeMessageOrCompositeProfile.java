package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SegmentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CoConstraintExportMode;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Comment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeCompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
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
 * Created by Maxence Lefort on 3/9/17.
 */
public abstract class SerializeMessageOrCompositeProfile {
	
	static final Logger logger = LoggerFactory.getLogger(SerializeMessageOrCompositeProfile.class);
	
    @Autowired SegmentService segmentService;
    @Autowired SerializeConstraintService serializeConstraintService;
    @Autowired SerializationUtil serializationUtil;

    @Autowired SerializeSegmentService serializeSegmentService;

    protected List<String> messageSegmentsNameList;

    protected Map<String,Segment> compositeProfileSegments;

    protected int segmentPosition = 1;

    protected void serializeSegment(SegmentRefOrGroup segmentRefOrGroup, String prefix, SerializableSection segmentsSection, UsageConfig segmentRefOrGroupUsageConfig, UsageConfig segmentUsageConfig, UsageConfig fieldsUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull,CoConstraintExportMode coConstraintExportMode)
        throws SegmentSerializationException {
        serializeSegment(segmentRefOrGroup, prefix, segmentsSection, segmentRefOrGroupUsageConfig, segmentUsageConfig, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull, null, coConstraintExportMode);
    }

    protected void serializeSegment(SegmentRefOrGroup segmentRefOrGroup, String prefix, SerializableSection segmentsSection, UsageConfig segmentRefOrGroupUsageConfig, UsageConfig segmentUsageConfig, UsageConfig fieldsUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull, Map<String,Segment> compositeProfileSegments, CoConstraintExportMode coConstraintExportMode)
        throws SegmentSerializationException {
        this.compositeProfileSegments = compositeProfileSegments;
        if(ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(),segmentRefOrGroupUsageConfig)) {
            if (segmentRefOrGroup instanceof SegmentRef) {
                SegmentLink segmentLink = ((SegmentRef) segmentRefOrGroup).getRef();
                if (!messageSegmentsNameList.contains(segmentLink.getId())) {
                    segmentsSection.addSection(serializeSegmentService
                        .serializeSegment(segmentLink, prefix + String.valueOf(segmentPosition),
                            segmentPosition, 5, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull, coConstraintExportMode));
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
                        segmentRefOrGroupUsageConfig, segmentUsageConfig, fieldsUsageConfig, duplicateOBXDataTypeWhenFlavorNull, coConstraintExportMode);
                }
            }
        }
    }

    protected SerializableConstraints serializeConstraints(List<? extends Constraint> constraints, String name, int position, String type) throws ConstraintSerializationException {
        List<SerializableConstraint> serializableConstraintList = new ArrayList<>();
        for (Constraint constraint : constraints) {
            try {
                SerializableConstraint serializableConstraint = new SerializableConstraint(constraint, name);
                serializableConstraintList.add(serializableConstraint);
            } catch (Exception e){
                throw new ConstraintSerializationException(e,name);
            }
        }
        String id = UUID.randomUUID().toString();
        SerializableConstraints serializableConstraints =
            new SerializableConstraints(serializableConstraintList, id, String.valueOf(position),
                name, type);
        return serializableConstraints;

    }

    protected SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments)
        throws SerializationException {
      return this.serializeSegmentRefOrGroup(segmentRefOrGroup, segmentUsageConfig, fieldUsageConfig, compositeProfileSegments,false, null);
    }
      
    protected SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host)
        throws SerializationException {
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

    private SerializableSegmentRefOrGroup serializeSegmentRef(SegmentRef segmentRef, UsageConfig usageConfig,Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host) throws SegmentSerializationException {
        SerializableSegmentRefOrGroup serializableSegmentRefOrGroup;
        SegmentLink segmentLink = segmentRef.getRef();
        if(segmentLink != null) {
            try {
                Segment segment = null;
                if (compositeProfileSegments != null) {
                    segment = findSegmentInCompositeProfileSegments(segmentLink,
                        compositeProfileSegments);
                } else {
                    segment = segmentService.findById(segmentLink.getId());
                }
                if (usageConfig != null && segment != null) {
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
            } catch (Exception e){
                throw new SegmentSerializationException(e,segmentLink.getLabel());
            }
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

    private SerializableSegmentRefOrGroup serializeGroup(Group group, UsageConfig segmentUsageConfig, UsageConfig fieldUsageConfig, Map<String,Segment> compositeProfileSegments, Boolean showInnerLinks, String host)
        throws SerializationException {
        try {
            SerializableSegmentRefOrGroup serializableGroup;
            List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
            for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
                SerializableSegmentRefOrGroup serializableSegmentRefOrGroup =
                    serializeSegmentRefOrGroup(segmentRefOrGroup, segmentUsageConfig,
                        fieldUsageConfig, compositeProfileSegments, showInnerLinks, host);
                if (serializableSegmentRefOrGroup != null) {
                    serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
                }
            }
            List<SerializableConstraint> groupConstraints =
                serializeConstraintService.serializeConstraints(group, group.getName());
            serializableGroup =
                new SerializableSegmentRefOrGroup(group, serializableSegmentRefOrGroups,
                    groupConstraints, this instanceof SerializeCompositeProfileService);
            return serializableGroup;
        } catch (Exception e){
            throw new GroupSerializationException(e,group.getName());
        }
    }
    
    protected HashMap<String,String> retrieveComponentsPaths(Message message){
    	HashSet<String> locationsToRetrieve = new HashSet<>();
    	for(Comment comment : message.getComments()){
    		if(comment != null && comment.getLocation() != null && !comment.getLocation().isEmpty() && !locationsToRetrieve.contains(comment.getLocation())){
    			locationsToRetrieve.add(comment.getLocation());
    		}
    	}
    	for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : message.getValueSetBindings()){
    		if(valueSetOrSingleCodeBinding != null && valueSetOrSingleCodeBinding.getLocation()!=null && !valueSetOrSingleCodeBinding.getLocation().isEmpty() && !locationsToRetrieve.contains(valueSetOrSingleCodeBinding.getLocation())){
    			locationsToRetrieve.add(valueSetOrSingleCodeBinding.getLocation());
    		}
    	}
    	return retrieveComponentsPaths(locationsToRetrieve,message.getChildren());
    }
    
    protected HashMap<String,String> retrieveComponentsPaths(CompositeProfile compositeProfile){
    	HashSet<String> locationsToRetrieve = new HashSet<>();
    	for(Comment comment : compositeProfile.getComments()){
    		if(comment != null && comment.getLocation() != null && !comment.getLocation().isEmpty() && !locationsToRetrieve.contains(comment.getLocation())){
    			locationsToRetrieve.add(comment.getLocation());
    		}
    	}
    	for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : compositeProfile.getValueSetBindings()){
    		if(valueSetOrSingleCodeBinding != null && valueSetOrSingleCodeBinding.getLocation()!=null && !valueSetOrSingleCodeBinding.getLocation().isEmpty() && !locationsToRetrieve.contains(valueSetOrSingleCodeBinding.getLocation())){
    			locationsToRetrieve.add(valueSetOrSingleCodeBinding.getLocation());
    		}
    	}
    	return retrieveComponentsPaths(locationsToRetrieve,compositeProfile.getChildren(),compositeProfile.getSegmentsMap());
    }
    
    private HashMap<String,String> retrieveComponentsPaths(HashSet<String> locationsToRetrieve, List<SegmentRefOrGroup> segmentRefOrGroups){
    	return retrieveComponentsPaths(locationsToRetrieve,segmentRefOrGroups,null);
    }

    
    private HashMap<String,String> retrieveComponentsPaths(HashSet<String> locationsToRetrieve, List<SegmentRefOrGroup> segmentRefOrGroups, Map<String, Segment> compositeProfileSegmentsMap){
    	HashMap<String,String> componentsLocationPathMap = new HashMap<>();
    	for(String location : locationsToRetrieve){
    		String path = null;
    		StringTokenizer stringTokenizer = new StringTokenizer(location, ".");
    		if(stringTokenizer.hasMoreTokens()){
	    		try{
	    			Integer locationToken = Integer.parseInt(stringTokenizer.nextToken());
		    		for(SegmentRefOrGroup segmentRefOrGroup : segmentRefOrGroups){
		    			if(segmentRefOrGroup.getPosition() == locationToken){
		    				path = this.retrieveSegmentOrGroupName(segmentRefOrGroup,stringTokenizer,compositeProfileSegmentsMap);
		    				break;
		    			}
		    		}
	    		} catch (NumberFormatException nfe){
	    			logger.error("Unable to retreive path: Comment location is malformed ["+location+"]");
	    			path = location;
	    		}
    		}
    		if(null == path){
    			path = location;
    		}
    		while(stringTokenizer.hasMoreTokens()){
    			path += "." + stringTokenizer.nextToken();
    		}
    		componentsLocationPathMap.put(location, path);
		}
    	return componentsLocationPathMap;
    }
    
    private String retrieveSegmentOrGroupName(SegmentRefOrGroup segmentRefOrGroup, StringTokenizer stringTokenizer,Map<String, Segment> compositeProfileSegmentsMap) {
		if(segmentRefOrGroup instanceof SegmentRef){
			Segment segment = null;
			String segmentId = ((SegmentRef) segmentRefOrGroup).getRef().getId();
			if(compositeProfileSegmentsMap != null){
				segment = compositeProfileSegmentsMap.get(segmentId);
    		} else {
    			segment = segmentService.findById(segmentId);
    		}
    		if(segment!=null){
    			return segment.getName();
    		}
    	} else if(segmentRefOrGroup instanceof Group){
    		Group group = (Group) segmentRefOrGroup;
    		if(stringTokenizer.hasMoreTokens()){
    			String token = stringTokenizer.nextToken();
    			try{
	    			Integer location = Integer.parseInt(token);
	    			for(SegmentRefOrGroup groupSegmentRefOrGroup : group.getChildren()){
	    				if(groupSegmentRefOrGroup.getPosition() == location){
	    					return group.getName()+"."+retrieveSegmentOrGroupName(groupSegmentRefOrGroup, stringTokenizer,compositeProfileSegmentsMap);
	    				}
	    			}
    			} catch (NumberFormatException nfe){
	    			logger.error("Unable to retreive path: Comment group's segment location is malformed ["+token+"]");
	    		}
    		}
    		return group.getName();
    	}
		return null;
	}
}
