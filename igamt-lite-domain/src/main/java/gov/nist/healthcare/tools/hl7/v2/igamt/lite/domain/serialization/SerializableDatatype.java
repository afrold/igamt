package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.List;
import java.util.Map;

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
 * Created by Maxence Lefort on 12/9/16.
 */
public class SerializableDatatype extends SerializableSection {

    private Datatype datatype;
    private List<SerializableConstraint> constraints;
    private Map<Component,Datatype> componentDatatypeMap;
    private Map<Component,List<Table>> componentTableMap;

    public SerializableDatatype(String id, String prefix, String position, String title,
        Datatype datatype, List<SerializableConstraint> constraints,Map<Component,Datatype> componentDatatypeMap,Map<Component,List<Table>> componentTableMap) {
        super(id, prefix, position, title);
        this.datatype = datatype;
        this.constraints = constraints;
        this.componentDatatypeMap = componentDatatypeMap;
        this.componentTableMap = componentTableMap;
    }

    @Override public Element serializeElement() {
        nu.xom.Element datatypeElement = new nu.xom.Element("Datatype");
        if (this.datatype != null) {
            datatypeElement.addAttribute(new Attribute("ID", datatype.getId() + ""));
            datatypeElement.addAttribute(new Attribute("Name", datatype.getName()));
            datatypeElement.addAttribute(new Attribute("Label", datatype.getLabel()));
            datatypeElement.addAttribute(new Attribute("Description", datatype.getDescription()));
            datatypeElement
                .addAttribute(new Attribute("PurposeAndUse", datatype.getPurposeAndUse()));
            datatypeElement.addAttribute(new Attribute("Comment", datatype.getComment()));
            datatypeElement.addAttribute(new Attribute("Hl7Version",
                datatype.getHl7Version() == null ? "" : datatype.getHl7Version()));
            datatypeElement.addAttribute(new Attribute("id", datatype.getId()));
            for(SerializableConstraint constraint : constraints){
                datatypeElement.appendChild(constraint.serializeElement());
            }
            if (datatype.getComponents() != null) {
                for (int i = 0; i < datatype.getComponents().size(); i++) {
                    Component component = datatype.getComponents().get(i);
                    nu.xom.Element componentElement = new nu.xom.Element("Component");
                    componentElement.addAttribute(new Attribute("Name", component.getName()));
                    componentElement.addAttribute(new Attribute("Usage", getFullUsage(datatype, i)));
                    if (component.getDatatype() != null) {
                        Datatype datatype = componentDatatypeMap.get(component);
                        if(datatype!=null) {
                            componentElement.addAttribute(new Attribute("Datatype",
                                datatype.getLabel()));
                        }
                    } else {
                        componentElement.addAttribute(new Attribute("Datatype",
                            component.getDatatype() != null ?
                                "! DEBUG: COULD NOT FIND datatype " + component.getDatatype().getLabel() :
                                "! DEBUG: COULD NOT FIND datatype with null id"));
                    }
                    if (component.getDatatype() != null) {
                        Datatype datatype = componentDatatypeMap.get(component);
                        if(datatype!=null) {
                            if (datatype.getComponents().size() == 0) {
                                componentElement.addAttribute(
                                    new Attribute("MinLength", "" + component.getMinLength()));
                                if (component.getMaxLength() != null && !component.getMaxLength().equals(""))
                                    componentElement
                                        .addAttribute(new Attribute("MaxLength", component.getMaxLength()));
                                if (component.getConfLength() != null && !component.getConfLength().equals(""))
                                    componentElement.addAttribute(
                                        new Attribute("ConfLength", component.getConfLength()));
                            } else {
                                componentElement.addAttribute(new Attribute("MinLength", ""));
                                componentElement.addAttribute(new Attribute("MaxLength", ""));
                                componentElement.addAttribute(new Attribute("ConfLength", ""));
                            }
                        }
                    }
                    if (component.getComment() != null && !component.getComment().equals(""))
                        componentElement.addAttribute(new Attribute("Comment", component.getComment()));
                    componentElement
                        .addAttribute(new Attribute("Position", component.getPosition().toString()));
                    if (component.getText() != null & !component.getText().isEmpty()) {
                        componentElement.appendChild(
                            this.createTextElement("Text", component.getText()));
                    }

                    if (component.getTables() != null && (component.getTables().size() > 0)) {
                        String bindingIdentifiers = "";
                        for(Table table:componentTableMap.get(component)){
                            if(table!=null){
                                String bindingIdentifier = table.getBindingIdentifier();
                                bindingIdentifiers = !bindingIdentifiers.equals("") ? bindingIdentifiers + "," + bindingIdentifier : bindingIdentifier;
                            }
                        }
                        componentElement.addAttribute(new Attribute("Binding", bindingIdentifiers));
                    }
                    datatypeElement.appendChild(componentElement);
                }
                if (datatype.getComponents().size() == 0) {
                    nu.xom.Element componentElement = new nu.xom.Element("Component");
                    componentElement.addAttribute(new Attribute("Name", datatype.getName()));
                    componentElement.addAttribute(new Attribute("Position", "1"));
                    datatypeElement.appendChild(componentElement);
                }

                if ((datatype != null && !datatype.getDefPreText().isEmpty()) || (datatype != null
                    && !datatype.getDefPostText().isEmpty())) {
                    if (datatype.getDefPreText() != null && !datatype.getDefPreText().isEmpty()) {
                        datatypeElement.appendChild(
                            this.createTextElement("DefPreText", datatype.getDefPreText()));
                    }
                    if (datatype.getDefPostText() != null && !datatype.getDefPostText().isEmpty()) {
                        datatypeElement.appendChild(
                            this.createTextElement("DefPostText", datatype.getDefPostText()));
                    }
                }
                if (datatype.getUsageNote() != null && !datatype.getUsageNote().isEmpty()) {
                    datatypeElement
                        .appendChild(this.createTextElement("UsageNote", datatype.getUsageNote()));
                }
            }
        }
        datatypeElement.addAttribute(new Attribute("prefix", prefix));
        datatypeElement.addAttribute(new Attribute("position", ""));
        Element sectionElement = super.getSectionElement();
        sectionElement.appendChild(datatypeElement);
        return sectionElement;
    }

    private String getFullUsage(Datatype datatype, int i) {
        List<Predicate> predicates = super.findPredicate(i + 1, datatype.getPredicates());
        if (predicates == null || predicates.isEmpty()) {
            return datatype.getComponents().get(i).getUsage().toString();
        } else {
            Predicate predicate = predicates.get(0);
            return datatype.getComponents().get(i).getUsage().toString() + "(" + predicate.getTrueUsage() + "/" + predicate.getFalseUsage()
                + ")";
        }
    }
}
