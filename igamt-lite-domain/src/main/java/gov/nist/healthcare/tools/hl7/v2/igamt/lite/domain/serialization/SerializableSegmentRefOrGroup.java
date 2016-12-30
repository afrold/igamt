package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.lang3.StringUtils;

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
 * Created by Maxence Lefort on 12/8/16.
 */
public class SerializableSegmentRefOrGroup extends SerializableElement{

    private SegmentRefOrGroup segmentRefOrGroup;
    private Segment segment;
    private List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups;
    private List<SerializableConstraint> groupConstraintList;

    //SegmentRef constructor
    public SerializableSegmentRefOrGroup(SegmentRef segmentRef,Segment segment) {
        this(segmentRef);
        this.segment = segment;
    }
    //SegmentRef constructor
    public SerializableSegmentRefOrGroup(Group group,List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups, List<SerializableConstraint> groupConstraintList) {
        this(group);
        this.serializableSegmentRefOrGroups = serializableSegmentRefOrGroups;
        this.groupConstraintList = groupConstraintList;
    }

    private SerializableSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup) {
        super();
        this.segmentRefOrGroup = segmentRefOrGroup;
    }

    @Override public Element serializeElement() {
        if (this.segmentRefOrGroup instanceof SegmentRef) {
            return this.serializeSegmentRefDisplay((SegmentRef) this.segmentRefOrGroup, 0);
        } else if (this.segmentRefOrGroup instanceof Group) {
            return this.serializeGroupDisplay((Group) this.segmentRefOrGroup, 0);
        }
        return null;
    }

    private Element serializeGroupDisplay(Group group, int depth) {
        Element elementGroup = new Element("MessageGroup");
        Element elementGroupBegin = new Element("MessageSegment");
        elementGroupBegin.addAttribute(new Attribute("IdGpe", group.getId()));
        elementGroupBegin.addAttribute(new Attribute("Name", group.getName()));
        elementGroupBegin.addAttribute(new Attribute("Description", "BEGIN " + group.getName() + " GROUP"));
        elementGroupBegin.addAttribute(new Attribute("Usage", String.valueOf(depth)));
        elementGroupBegin.addAttribute(new Attribute("Min", group.getMin() + ""));
        elementGroupBegin.addAttribute(new Attribute("Max", group.getMax()));
        elementGroupBegin.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "["));
        elementGroupBegin.addAttribute(new Attribute("Comment", group.getComment()));
        elementGroupBegin.addAttribute(new Attribute("Position", group.getPosition().toString()));
        elementGroup.appendChild(elementGroupBegin);

        for (SerializableSegmentRefOrGroup serializableSegmentRefOrGroup : this.serializableSegmentRefOrGroups) {
            elementGroup.appendChild(serializableSegmentRefOrGroup.serializeElement());
        }
        Element elementGroupEnd = new Element("MessageSegment");
        elementGroupEnd.addAttribute(new Attribute("IdGpe", group.getId()));
        elementGroupEnd.addAttribute(new Attribute("Name", "END " + group.getName() + " GROUP"));
        elementGroupEnd.addAttribute(new Attribute("Description", "END " + group.getName() + " GROUP"));
        elementGroupEnd.addAttribute(new Attribute("Usage", group.getUsage().value()));
        elementGroupEnd.addAttribute(new Attribute("Min", group.getMin() + ""));
        elementGroupEnd.addAttribute(new Attribute("Max", group.getMax()));
        elementGroupEnd.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "]"));
        elementGroupEnd.addAttribute(new Attribute("Depth", String.valueOf(depth)));
        elementGroupEnd.addAttribute(new Attribute("Position", group.getPosition().toString()));
        elementGroup.appendChild(elementGroupEnd);
        if(groupConstraintList!=null&&!groupConstraintList.isEmpty()){
            for(SerializableConstraint serializableConstraint : groupConstraintList){
                elementGroup.appendChild(serializableConstraint.serializeElement());
            }
        }
        return elementGroup;
    }

    private Element serializeSegmentRefDisplay(SegmentRef segmentRef, int depth) {
        Element elementSegment = new Element("MessageSegment");
        elementSegment.addAttribute(new Attribute("IDRef", segmentRef.getId()));
        elementSegment.addAttribute(new Attribute("IDSeg", segmentRef.getRef().getId()));
        if (this.segment != null && this.segment.getName() != null) {
            elementSegment.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth)
                + this.segment.getName()));
            String label = (segmentRef.getRef().getExt() == null || segmentRef.getRef().getExt().isEmpty())
                ? segmentRef.getRef().getName() : segmentRef.getRef().getLabel();
            elementSegment.addAttribute(new Attribute("Label", label));
            if(this.segment.getDescription()!=null) {
                elementSegment.addAttribute(new Attribute("Description", this.segment.getDescription()));
            }
        }
        elementSegment.addAttribute(new Attribute("Depth", String.valueOf(depth)));
        elementSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().toString()));
        elementSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
        elementSegment.addAttribute(new Attribute("Max", segmentRef.getMax() + ""));
        if (segmentRef.getComment() != null)
            elementSegment.addAttribute(new Attribute("Comment", segmentRef.getComment()));
        elementSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
        return elementSegment;
    }
}
