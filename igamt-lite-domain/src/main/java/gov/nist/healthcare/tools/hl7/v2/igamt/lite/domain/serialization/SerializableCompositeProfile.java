package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.CompositeProfileSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.HashMap;
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
 * Created by Maxence Lefort on 03/29/17.
 */
public class SerializableCompositeProfile extends SerializableSection {

    private CompositeProfile compositeProfile;
    private List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups;
    private SerializableConstraints serializableConformanceStatements;
    private SerializableConstraints serializablePredicates;
    private String usageNote;
    private String defPreText;
    private String defPostText;
    private List<Table> tables;
    private boolean showConfLength;
    private HashMap<String,String> locationPathMap;
    
    public SerializableCompositeProfile(CompositeProfile compositeProfile, String prefix, String title,
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups,
        SerializableConstraints serializableConformanceStatements,
        SerializableConstraints serializablePredicates, String usageNote, String defPreText,
        String defPostText, List<Table> tables, HashMap<String,String> locationPathMap, Boolean showConfLength) {
        super(compositeProfile.getIdentifier(),
            prefix + "." + String.valueOf(compositeProfile.getPosition()),
            String.valueOf(compositeProfile.getPosition() + 1),
            String.valueOf("3"),title);
        this.compositeProfile = compositeProfile;
        this.serializableConformanceStatements = serializableConformanceStatements;
        this.serializablePredicates = serializablePredicates;
        this.serializableSegmentRefOrGroups = serializableSegmentRefOrGroups;
        this.usageNote = usageNote;
        this.defPreText = defPreText;
        this.defPostText = defPostText;
        this.tables = tables;
        this.showConfLength = showConfLength;
        this.locationPathMap = locationPathMap;
    }

    @Override public Element serializeElement() throws CompositeProfileSerializationException {
        try {
            Element compositeProfileElement = new Element("CompositeProfile");
            compositeProfileElement
                .addAttribute(new Attribute("ID", this.compositeProfile.getIdentifier() + ""));
            compositeProfileElement
                .addAttribute(new Attribute("Name", this.compositeProfile.getName() + ""));
            compositeProfileElement.addAttribute(new Attribute("Type", this.compositeProfile.getMessageType()));
            compositeProfileElement.addAttribute(new Attribute("Event", this.compositeProfile.getEvent()));
            compositeProfileElement.addAttribute(new Attribute("StructID", this.compositeProfile.getStructID()));
            compositeProfileElement.addAttribute(new Attribute("ShowConfLength", String.valueOf(showConfLength)));
            compositeProfileElement
                .addAttribute(new Attribute("position", this.compositeProfile.getPosition() + ""));
            if (this.compositeProfile.getDescription() != null && !this.compositeProfile.getDescription().equals(""))
                compositeProfileElement.addAttribute(new Attribute("Description", this.compositeProfile.getDescription()));
            if (this.compositeProfile.getComment() != null && !this.compositeProfile.getComment().isEmpty()) {
                compositeProfileElement.addAttribute(new Attribute("Comment", this.compositeProfile.getComment()));
            }
            if (this.usageNote != null && !this.usageNote.isEmpty()) {
                compositeProfileElement.appendChild(super.createTextElement("UsageNote", this.usageNote));
            }

            if ((this.compositeProfile != null && !this.defPreText.isEmpty()) || (
                this.compositeProfile != null && !this.defPostText.isEmpty())) {
                if (this.defPreText != null && !this.defPreText.isEmpty()) {
                    compositeProfileElement.appendChild(super.createTextElement("DefPreText", this.defPreText));
                }
                if (this.defPostText != null && !this.defPostText.isEmpty()) {
                    compositeProfileElement.appendChild(super.createTextElement("DefPostText", this.defPostText));
                }
            }

            for (SerializableSegmentRefOrGroup serializableSegmentRefOrGroup : this.serializableSegmentRefOrGroups) {
                if (serializableSegmentRefOrGroup != null) {
                    if (compositeProfile.getComments() != null && !compositeProfile.getComments().isEmpty()) {
                        this.addComments(serializableSegmentRefOrGroup);
                    }
                    compositeProfileElement.appendChild(serializableSegmentRefOrGroup.serializeElement());
                }
            }
            if (serializableConformanceStatements != null) {
                compositeProfileElement.appendChild(serializableConformanceStatements.serializeElement());
            }
            if (serializablePredicates != null) {
                compositeProfileElement.appendChild(serializablePredicates.serializeElement());
            }
            List<SerializableSection> segmentsSections = super.getSerializableSectionList();
            if (!segmentsSections.isEmpty()) {
                for (SerializableSection segmentSection : segmentsSections) {
                    compositeProfileElement.appendChild(segmentSection.serializeElement());
                }
            }
            if (compositeProfile.getValueSetBindings() != null && !compositeProfile.getValueSetBindings().isEmpty()) {
                Element valueSetBindingListElement = super
                    .createValueSetBindingListElement(compositeProfile.getValueSetBindings(), tables,
                        compositeProfile.getName(), locationPathMap);
                if (valueSetBindingListElement != null) {
                    compositeProfileElement.appendChild(valueSetBindingListElement);
                }
            }
            if (compositeProfile.getComments() != null && !compositeProfile.getComments().isEmpty()) {
                Element commentListElement = super
                    .createCommentListElement(compositeProfile.getComments(), compositeProfile.getName(),
                        locationPathMap);
                if (commentListElement != null) {
                    compositeProfileElement.appendChild(commentListElement);
                }
            }
            super.sectionElement.appendChild(compositeProfileElement);
            return super.sectionElement;
        } catch (Exception e){
            throw new CompositeProfileSerializationException(e,this.compositeProfile!=null?this.compositeProfile.getName():"");
        }
    }

    private void addComments(SerializableSegmentRefOrGroup serializableSegmentRefOrGroup) {
        String comments = "";
        if(serializableSegmentRefOrGroup.getSegmentRef()!=null) {
            comments = super
                .findComments(serializableSegmentRefOrGroup.getSegmentRef().getPosition(),
                    compositeProfile.getComments());
            serializableSegmentRefOrGroup.setComments(comments);
        }
        if(serializableSegmentRefOrGroup.getSegmentRefOrGroup() instanceof Group){
            for(SerializableSegmentRefOrGroup serializableSegmentRefOrGroupChildren : serializableSegmentRefOrGroup.getSerializableSegmentRefOrGroups()){
                addComments(serializableSegmentRefOrGroupChildren);
            }
        }
    }

    public SerializableConstraints getSerializableConformanceStatements() {
        return serializableConformanceStatements;
    }

    public SerializableConstraints getSerializablePredicates() {
        return serializablePredicates;
    }

    public CompositeProfile getCompositeProfile() {
        return compositeProfile;
    }

    public List<SerializableSegmentRefOrGroup> getSerializableSegmentRefOrGroups() {
        return serializableSegmentRefOrGroups;
    }
}
