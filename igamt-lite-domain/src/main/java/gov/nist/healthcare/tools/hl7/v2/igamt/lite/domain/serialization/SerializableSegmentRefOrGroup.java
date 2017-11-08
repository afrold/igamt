package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
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
    private SegmentRef segmentRef;
    private boolean isCompositeProfile;
    private List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups;
    private List<SerializableConstraint> groupConstraintList;
    private String comments;
    private Boolean showInnerLinks;
    private String host;

    //SegmentRef constructor
    public SerializableSegmentRefOrGroup(SegmentRef segmentRef,Segment segment, boolean isCompositeProfile, Boolean showInnerLinks, String host) {
        this(segmentRef,isCompositeProfile);
        this.segmentRef = segmentRef;
        this.segment = segment;
        this.showInnerLinks = showInnerLinks;
        this.host = host;
    }
    //Group constructor
    public SerializableSegmentRefOrGroup(Group group,List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups, List<SerializableConstraint> groupConstraintList, boolean isCompositeProfile) {
        this(group,isCompositeProfile);
        this.serializableSegmentRefOrGroups = serializableSegmentRefOrGroups;
        this.groupConstraintList = groupConstraintList;
    }

    private SerializableSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup, boolean isCompositeProfile) {
        super();
        this.segmentRefOrGroup = segmentRefOrGroup;
        this.isCompositeProfile = isCompositeProfile;
    }

    @Override public Element serializeElement() throws ConstraintSerializationException {
        if (this.segmentRefOrGroup instanceof SegmentRef) {
            return this.serializeSegmentRefDisplay((SegmentRef) this.segmentRefOrGroup, 0);
        } else if (this.segmentRefOrGroup instanceof Group) {
            return this.serializeGroupDisplay((Group) this.segmentRefOrGroup, 0);
        }
        return null;
    }

    private Element serializeGroupDisplay(Group group, int depth)
        throws ConstraintSerializationException {
        Element elementGroup = createGroupElement();
        Element elementGroupBegin = createSegmentElement();
        elementGroupBegin.addAttribute(new Attribute("IdGpe", group.getId()));
        elementGroupBegin.addAttribute(new Attribute("Name", group.getName()));
        elementGroupBegin.addAttribute(new Attribute("Description", "BEGIN " + group.getName() + " GROUP"));
        elementGroupBegin.addAttribute(new Attribute("Usage", String.valueOf(group.getUsage())));
        elementGroupBegin.addAttribute(new Attribute("Min", group.getMin() + ""));
        elementGroupBegin.addAttribute(new Attribute("Max", group.getMax()));
        elementGroupBegin.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "["));
        if(this.getComments()!=null) {
            elementGroupBegin.addAttribute(new Attribute("Comment", this.getComments()));
        }
        elementGroupBegin.addAttribute(new Attribute("Position", group.getPosition().toString()));
        elementGroup.appendChild(elementGroupBegin);

        for (SerializableSegmentRefOrGroup serializableSegmentRefOrGroup : this.serializableSegmentRefOrGroups) {
            elementGroup.appendChild(serializableSegmentRefOrGroup.serializeElement());
        }
        Element elementGroupEnd = createSegmentElement();
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

    private Element createGroupElement() {
      Element elementGroup = this.isCompositeProfile ? new Element("CompositeProfileMessageGroup") : new Element("MessageGroup");
      return elementGroup;
    }
    
    private Element createSegmentElement() {
      Element elementSegment = this.isCompositeProfile ? new Element("CompositeProfileMessageSegment") : new Element("MessageSegment");
      return elementSegment;
    }
    
    private Element serializeSegmentRefDisplay(SegmentRef segmentRef, int depth) {
        Element elementSegment = createSegmentElement();
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
            if(this.showInnerLinks){
              elementSegment.addAttribute(new Attribute("InnerLink", this.generateInnerLink(this.segment, host)));
            }
        }
        elementSegment.addAttribute(new Attribute("Depth", String.valueOf(depth)));
        elementSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().toString()));
        elementSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
        elementSegment.addAttribute(new Attribute("Max", segmentRef.getMax() + ""));
        if (this.getComments() != null)
            elementSegment.addAttribute(new Attribute("Comment", this.getComments()));
        elementSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
        return elementSegment;
    }

    public Segment getSegment() {
        return segment;
    }

    public SegmentRef getSegmentRef() {
        return segmentRef;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public SegmentRefOrGroup getSegmentRefOrGroup() {
        return segmentRefOrGroup;
    }

    public List<SerializableSegmentRefOrGroup> getSerializableSegmentRefOrGroups() {
        return serializableSegmentRefOrGroups;
    }
}
