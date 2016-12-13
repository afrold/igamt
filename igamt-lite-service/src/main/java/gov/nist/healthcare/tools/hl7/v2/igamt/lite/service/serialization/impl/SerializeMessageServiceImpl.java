package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SerializeMessageServiceImpl implements SerializeMessageService{

    @Autowired
    SegmentService segmentService;

    @Override public SerializableMessage serializeMessage(Message message, String prefix) {
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        for(SegmentRefOrGroup segmentRefOrGroup : message.getChildren()){
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup;
            if(segmentRefOrGroup instanceof SegmentRef){
                SegmentLink segmentLink = ((SegmentRef)segmentRefOrGroup).getRef();
                if(segmentLink != null) {
                    Segment segment = segmentService.findById(segmentLink.getId());
                    serializableSegmentRefOrGroup =
                        new SerializableSegmentRefOrGroup((SegmentRef)segmentRefOrGroup, segment);
                    serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
                }
            } else if (segmentRefOrGroup instanceof Group){

            }
        }
        List<SerializableConstraint> serializableConstraints = new ArrayList<>();
        String usageNote, defPreText, defPostText;
        usageNote = defPreText = defPostText = "";

        SerializableMessage serializableMessage = new SerializableMessage(message,prefix,serializableSegmentRefOrGroups,serializableConstraints,usageNote,defPreText,defPostText);

        return serializableMessage;
    }

    private SerializableSegmentRefOrGroup serializeSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup){
        if(segmentRefOrGroup instanceof SegmentRef){
            return serializeSegmentRef((SegmentRef) segmentRefOrGroup);
        } else if (segmentRefOrGroup instanceof Group){
            return serializeGroup((Group) segmentRefOrGroup);
        }
        return null;
    }

    private SerializableSegmentRefOrGroup serializeSegmentRef(SegmentRef segmentRef){
        SerializableSegmentRefOrGroup serializableSegmentRefOrGroup;
        SegmentLink segmentLink = segmentRef.getRef();
        if(segmentLink != null) {
            Segment segment = segmentService.findById(segmentLink.getId());
            serializableSegmentRefOrGroup =
                new SerializableSegmentRefOrGroup(segmentRef, segment);
            return serializableSegmentRefOrGroup;
        }
        return null;
    }

    private SerializableSegmentRefOrGroup serializeGroup(Group group){
        
    }
}
