package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
 * Created by Maxence Lefort on 12/7/16.
 */
public class SerializableSection extends SerializableElement {

    private Set<SerializableSection> serializableSectionSet;

    protected Element sectionElement;

    protected String id,prefix,position,title;

    public void addSection(SerializableSection serializableSection){
        this.serializableSectionSet.add(serializableSection);
    }

    public SerializableSection(String id,String prefix,String position, String title) {
        this.id = id;
        this.prefix = prefix;
        this.position = position;
        this.title = title;
        this.serializableSectionSet = new TreeSet<>();
        this.sectionElement = new Element("Section");
        this.sectionElement.addAttribute(new Attribute("id", id));
        this.sectionElement.addAttribute(new Attribute("prefix", prefix));
        this.sectionElement.addAttribute(new Attribute("position", position));
        this.sectionElement.addAttribute(new Attribute("h", String.valueOf(3)));
        this.sectionElement.addAttribute(new Attribute("title", title));
    }

    @Override
    public Element serializeElement() {
        for(SerializableSection serializableSection : serializableSectionSet){
            sectionElement.appendChild(serializableSection.serializeElement());
        }
        return sectionElement;
    }

    public Element getSectionElement() {
        return sectionElement;
    }

    protected List<Predicate> findPredicate(Integer target, List<Predicate> predicates) {
        List<Predicate> constraints = new ArrayList<>();
        for (Predicate pre : predicates) {
            if (pre.getConstraintTarget().indexOf('[') != -1) {
                if (target == Integer
                    .parseInt(pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))) {
                    constraints.add(pre);
                }
            }
        }
        return constraints;
    }
}
