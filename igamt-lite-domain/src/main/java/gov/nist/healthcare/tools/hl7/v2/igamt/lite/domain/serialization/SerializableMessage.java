package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.HashMap;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental systemessage. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p>
 * Created by Maxence Lefort on 12/8/16.
 */
public class SerializableMessage extends SerializableSection {

    private Message message;
    private List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups;
    private SerializableConstraints serializableConformanceStatements;
    private SerializableConstraints serializablePredicates;
    private String usageNote;
    private String defPreText;
    private String defPostText;
    private List<Table> tables;
    private boolean showConfLength;
    private HashMap<String,String> locationPathMap;
    
    public SerializableMessage(Message message, String prefix, String headerLevel, List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups,
        SerializableConstraints serializableConformanceStatements, SerializableConstraints serializablePredicates, String usageNote,
        String defPreText, String defPostText, List<Table> tables, HashMap<String,String> locationPathMap, Boolean showConfLength) {
        super(message.getId(),
            prefix + "." + String.valueOf(message.getPosition()),
            String.valueOf(message.getPosition() + 1),
            headerLevel,
            message.getName() != null ?
                message.getName() + " - " + message.getIdentifier() + " - " + message.getDescription()
                : message.getMessageType() + "^" + message.getEvent() + "^" + message.getStructID() + " - " + message.getIdentifier() + " - " + message.getDescription()
            );
        this.message = message;
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

    @Override public Element serializeElement() throws SerializationException {
        Element messageElement = new Element("Message");
        messageElement.addAttribute(new Attribute("ID", this.message.getId() + ""));
        messageElement.addAttribute(new Attribute("Name", this.message.getName() + ""));
        messageElement.addAttribute(new Attribute("Type", this.message.getMessageType()));
        messageElement.addAttribute(new Attribute("Event", this.message.getEvent()));
        messageElement.addAttribute(new Attribute("StructID", this.message.getStructID()));
        messageElement.addAttribute(new Attribute("ShowConfLength",String.valueOf(showConfLength)));
        messageElement.addAttribute(new Attribute("position", this.message.getPosition() + ""));
        if (this.message.getDescription() != null && !this.message.getDescription().equals(""))
            messageElement.addAttribute(new Attribute("Description", this.message.getDescription()));
        if (this.message.getComment() != null && !this.message.getComment().isEmpty()) {
            messageElement.addAttribute(new Attribute("Comment", this.message.getComment()));
        }
        if (this.usageNote != null && !this.usageNote.isEmpty()) {
            messageElement.appendChild(super.createTextElement("UsageNote", this.usageNote));
        }

        if ((this.message != null && !this.defPreText.isEmpty()) || (this.message != null && !this.defPostText.isEmpty())) {
            if (this.defPreText != null && !this.defPreText.isEmpty()) {
                messageElement.appendChild(super.createTextElement("DefPreText",
                    this.defPreText));
            }
            if (this.defPostText != null && !this.defPostText.isEmpty()) {
                messageElement.appendChild(super.createTextElement("DefPostText",
                    this.defPostText));
            }
        }

        for (SerializableSegmentRefOrGroup serializableSegmentRefOrGroup : this.serializableSegmentRefOrGroups) {
            if(serializableSegmentRefOrGroup!=null) {
                if(message.getComments()!=null && !message.getComments().isEmpty()) {
                    this.addComments(serializableSegmentRefOrGroup);
                }
                messageElement.appendChild(serializableSegmentRefOrGroup.serializeElement());
            }
        }
        if(serializableConformanceStatements!=null) {
            messageElement.appendChild(serializableConformanceStatements.serializeElement());
        }
        if(serializablePredicates!=null) {
            messageElement.appendChild(serializablePredicates.serializeElement());
        }
        List<SerializableSection> segmentsSections = super.getSerializableSectionList();
        if(!segmentsSections.isEmpty()){
            for(SerializableSection segmentSection : segmentsSections){
                messageElement.appendChild(segmentSection.serializeElement());
            }
        }
        if(message.getValueSetBindings()!=null && !message.getValueSetBindings().isEmpty()) {
            Element valueSetBindingListElement = super
                .createValueSetBindingListElement(message.getValueSetBindings(), tables,
                    message.getName(),locationPathMap);
            if (valueSetBindingListElement != null) {
                messageElement.appendChild(valueSetBindingListElement);
            }
        }
        if(message.getComments()!=null && !message.getComments().isEmpty()) {
            Element commentListElement = super.createCommentListElement(message.getComments(),message.getName(),locationPathMap);
            if (commentListElement != null) {
                messageElement.appendChild(commentListElement);
            }
        }
        super.sectionElement.appendChild(messageElement);
        return super.sectionElement;
    }
    
    

    private void addComments(SerializableSegmentRefOrGroup serializableSegmentRefOrGroup) {
        String comments = "";
        if(serializableSegmentRefOrGroup.getSegmentRef()!=null) {
            comments = super
                .findComments(serializableSegmentRefOrGroup.getSegmentRef().getPosition(),
                    message.getComments());
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

    public Message getMessage() {
        return message;
    }

    public List<SerializableSegmentRefOrGroup> getSerializableSegmentRefOrGroups() {
        return serializableSegmentRefOrGroups;
    }
}
