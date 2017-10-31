package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.List;
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
 * Created by Maxence Lefort on 12/13/16.
 */
public class SerializableConstraints extends SerializableSection {

    private List<SerializableConstraint> constraints;
    private String id,position,title,type;

    public SerializableConstraints(List<SerializableConstraint> constraints, String id,
        String position, String title, String type) {
        this(constraints, id, "", position, String.valueOf(5), title, type);
    }

    public SerializableConstraints(List<SerializableConstraint> constraints, String id,String prefix,
        String position, String headerLevel, String title, String type) {
        super(id,prefix,position,headerLevel,title);
        this.constraints = constraints;
        this.id = id;
        this.position = position;
        this.title = title;
        this.type = type;
    }

    @Override public Element serializeElement() throws ConstraintSerializationException {
        Element constraintsElement = new Element("Constraints");
        constraintsElement.addAttribute(new Attribute("id", this.id  == null ? "" : this.id));
        constraintsElement.addAttribute(new Attribute("position", this.position == null ? "" : this.position));
        constraintsElement.addAttribute(new Attribute("h", String.valueOf(3)));
        constraintsElement.addAttribute(new Attribute("title", this.title == null ? "" : this.title));
        constraintsElement.addAttribute(new Attribute("Type", this.type == null ? "" : this.type));
        for(SerializableConstraint constraint : constraints){
            constraintsElement.appendChild(constraint.serializeElement());
        }
        return constraintsElement;
    }

    public List<SerializableConstraint> getConstraints() {
        return constraints;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
