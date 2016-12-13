package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.List;

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
    private List<SerializableConstraint> serializableConstraints;
    private String usageNote;
    private String defPreText;
    private String defPostText;

    public SerializableMessage(Message message, String prefix, List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups, List<SerializableConstraint> serializableConstraints,String usageNote,String defPreText,String defPostText) {
        super(message.getId(),
            prefix + "." + String.valueOf(message.getPosition()),
            String.valueOf(message.getPosition() + 1),
            message.getName() != null ?
                message.getName() + " - " + message.getIdentifier() + " - " + message.getDescription()
                : message.getMessageType() + "^" + message.getEvent() + "^" + message.getStructID() + " - " + message.getIdentifier() + " - " + message.getDescription()
            );
        this.message = message;
        this.serializableConstraints = serializableConstraints;
        this.serializableSegmentRefOrGroups = serializableSegmentRefOrGroups;
        this.usageNote = usageNote;
        this.defPreText = defPreText;
        this.defPostText = defPostText;
    }

    @Override public Element serializeElement() {
        nu.xom.Element messageElement = new nu.xom.Element("Message");
        messageElement.addAttribute(new Attribute("ID", this.message.getId() + ""));
        messageElement.addAttribute(new Attribute("Name", this.message.getName() + ""));
        messageElement.addAttribute(new Attribute("Type", this.message.getMessageType()));
        messageElement.addAttribute(new Attribute("Event", this.message.getEvent()));
        messageElement.addAttribute(new Attribute("StructID", this.message.getStructID()));
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
            messageElement.appendChild(serializableSegmentRefOrGroup.serializeElement());
        }

        if(this.serializableConstraints != null && !this.serializableConstraints.isEmpty()){
            for(SerializableConstraint serializableConstraint : this.serializableConstraints){
                messageElement.appendChild(serializableConstraint.serializeElement());
            }
        }
        super.sectionElement.appendChild(messageElement);
        return super.sectionElement;
    }
}
