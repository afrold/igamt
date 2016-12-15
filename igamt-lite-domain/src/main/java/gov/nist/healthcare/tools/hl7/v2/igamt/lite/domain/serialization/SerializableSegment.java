package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.*;
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
public class SerializableSegment extends SerializableSection {

    private Segment segment;
    private String defPreText,defPostText,name,label,description,comment;
    private List<SerializableConstraint> constraints;
    private Map<Field,Datatype> fieldDatatypeMap;
    private Map<Field,List<Table>> fieldTableMap;
    private Map<CCValue,Table> coConstraintValueTableMap;

    public SerializableSegment(String id, String prefix, String position, String headerLevel, String title,
        Segment segment, String name, String label, String description, String comment, String defPreText, String defPostText, List<SerializableConstraint> constraints, Map<Field,Datatype> fieldDatatypeMap,Map<Field,List<Table>> fieldTableMap, Map<CCValue,Table> coConstraintValueTableMap) {
        super(id, prefix, position, headerLevel, title);
        this.segment = segment;
        this.name = name;
        this.label = label;
        this.description = description;
        this.comment = comment;
        this.defPreText = defPreText;
        this.defPostText = defPostText;
        this.constraints = constraints;
        this.fieldDatatypeMap = fieldDatatypeMap;
        this.fieldTableMap = fieldTableMap;
        this.coConstraintValueTableMap = coConstraintValueTableMap;
    }



    @Override public Element serializeElement() {
        nu.xom.Element segmentElement = new nu.xom.Element("Segment");
        if (segment != null) {
            segmentElement.addAttribute(new Attribute("id", segment.getId()));
            segmentElement.addAttribute(new Attribute("Name", this.name));
            segmentElement.addAttribute(new Attribute("Label", this.label));
            segmentElement.addAttribute(new Attribute("Position", ""));
            segmentElement.addAttribute(new Attribute("Description", this.description));
            if (this.comment != null && !this.comment.isEmpty()) {
                segmentElement.addAttribute(new Attribute("Comment", this.comment));
            }

            if ((segment.getText1() != null && !segment.getText1().isEmpty()) || (
                segment.getText2() != null && !segment.getText2().isEmpty())) {
                if (this.defPreText != null && !this.defPreText.isEmpty()) {
                    segmentElement
                        .appendChild(this.createTextElement("DefPreText", this.defPreText));
                }
                if (this.defPostText != null && !this.defPostText.isEmpty()) {
                    segmentElement
                        .appendChild(this.createTextElement("DefPostText", this.defPostText));
                }
            }

            for (int i = 0; i < segment.getFields().size(); i++) {
                Field field = segment.getFields().get(i);
                Element fieldElement = new Element("Field");
                fieldElement.addAttribute(new Attribute("Name", field.getName()));
                fieldElement.addAttribute(new Attribute("Usage", getFullUsage(segment, i).toString()));
                Datatype datatype = fieldDatatypeMap.get(field);
                if (field.getDatatype()!=null&&datatype!=null) {
                    fieldElement.addAttribute(new Attribute("Datatype", datatype.getLabel()));
                } else {
                    fieldElement.addAttribute(new Attribute("Datatype", field.getDatatype() != null ?
                        "! DEBUG: COULD NOT FIND datatype " + field.getDatatype().getLabel() :
                        "! DEBUG: COULD NOT FIND datatype with null id"));
                }
                // Following line means that there are no conformance length
                // for a complex datatype
                if (field.getConfLength() != null && !field.getConfLength().equals("")) {
                    if (field.getDatatype() != null) {
                        if (datatype != null) {
                            if (datatype.getComponents().size() > 0) {
                                fieldElement.addAttribute(new Attribute("ConfLength", ""));
                                fieldElement.addAttribute(new Attribute("MinLength", ""));
                                if (field.getMaxLength() != null && !field.getMaxLength().equals(""))
                                    fieldElement.addAttribute(new Attribute("MaxLength", ""));
                            } else {
                                fieldElement
                                    .addAttribute(new Attribute("ConfLength", field.getConfLength()));
                                fieldElement.addAttribute(
                                    new Attribute("MinLength", "" + field.getMinLength()));
                                if (field.getMaxLength() != null && !field.getMaxLength().equals(""))
                                    fieldElement
                                        .addAttribute(new Attribute("MaxLength", field.getMaxLength()));
                            }
                        }
                    }
                }
                fieldElement.addAttribute(new Attribute("Min", "" + field.getMin()));
                fieldElement.addAttribute(new Attribute("Max", "" + field.getMax()));
                if (field.getTables() != null && !field.getTables().isEmpty()) {
                    List<Table> fieldTables = fieldTableMap.get(field);
                    String temp = "";
                    if (fieldTables.size() > 1) {
                        for (Table table : fieldTables) {
                            String bindingIdentifier = table.getBindingIdentifier();
                            temp += (bindingIdentifier != null && !bindingIdentifier.equals("")) ?
                                "," + bindingIdentifier :
                                ", ! DEBUG: COULD NOT FIND binding identifier " + table
                                    .getBindingIdentifier();
                        }
                    } else {
                        Table table = fieldTables.get(0);
                        String bindingIdentifier = table == null ?
                            null :
                            table.getBindingIdentifier();
                        temp = (bindingIdentifier != null && !bindingIdentifier.equals("")) ?
                            bindingIdentifier : "! DEBUG: COULD NOT FIND binding identifier " + table.getBindingIdentifier();
                    }
                    fieldElement.addAttribute(new Attribute("Binding", temp));
                }
                if (field.getItemNo() != null && !field.getItemNo().equals(""))
                    fieldElement.addAttribute(new Attribute("ItemNo", field.getItemNo()));
                if (field.getComment() != null && !field.getComment().isEmpty())
                    fieldElement.addAttribute(new Attribute("Comment", field.getComment()));
                fieldElement.addAttribute(
                    new Attribute("Position", String.valueOf(field.getPosition())));

                if (field.getText() != null && !field.getText().isEmpty()) {
                    fieldElement.appendChild(this.createTextElement("Text", field.getText()));
                }

                if (!constraints.isEmpty()) {
                    for (SerializableConstraint constraint : constraints) {
                        fieldElement.appendChild(constraint.serializeElement());
                    }
                }
                segmentElement.appendChild(fieldElement);
            }
            CoConstraints coconstraints = segment.getCoConstraints();
            if (coconstraints.getConstraints().size() != 0) {
                //TODO refactor in a SerializableCoConstraint object and create the table in the XSLT
                nu.xom.Element coConstraintsElement = new Element("coconstraints");
                nu.xom.Element tableElement = new nu.xom.Element("table");
                tableElement.addAttribute(new Attribute("cellpadding", "1"));
                tableElement.addAttribute(new Attribute("cellspacing", "0"));
                tableElement.addAttribute(new Attribute("border", "1"));
                tableElement.addAttribute(new Attribute("width", "100%"));

                nu.xom.Element thead = new nu.xom.Element("thead");
                thead.addAttribute(
                    new Attribute("style", "background:#F0F0F0; color:#B21A1C; align:center"));
                nu.xom.Element tr = new nu.xom.Element("tr");
                for (CoConstraintsColumn ccc : coconstraints.getColumnList()) {
                    nu.xom.Element th = new nu.xom.Element("th");
                    th.appendChild(segment.getName() + "-" + ccc.getField().getPosition());
                    tr.appendChild(th);
                }

                nu.xom.Element thd = new nu.xom.Element("th");
                thd.appendChild("Description");
                tr.appendChild(thd);

                nu.xom.Element thc = new nu.xom.Element("th");
                thc.appendChild("Comments");
                tr.appendChild(thc);
                thead.appendChild(tr);
                tableElement.appendChild(thead);

                nu.xom.Element tbody = new nu.xom.Element("tbody");
                tbody.addAttribute(
                    new Attribute("style", "background-color:white;text-decoration:normal"));
                for (CoConstraint coConstraint : coconstraints.getConstraints()) {

                    tr = new nu.xom.Element("tr");
                    for (CCValue coConstraintValue : coConstraint.getValues()) {
                        nu.xom.Element td = new nu.xom.Element("td");
                        if (coConstraintValue != null) {
                            if (coconstraints.getColumnList().get(coConstraint.getValues().indexOf(coConstraintValue))
                                .getConstraintType().equals("v")) {
                                td.appendChild(coConstraintValue.getValue());
                            } else {
                                if (coConstraintValue.getValue() != null && coConstraintValue.getValue().equals("")) {
                                    td.appendChild("N/A");
                                } else {
                                    Table table = coConstraintValueTableMap.get(coConstraintValue);
                                    if (table != null) {
                                        td.appendChild(table.getBindingIdentifier());
                                    } else {
                                        td.appendChild("");
                                    }
                                }
                            }
                        } else {
                            td.appendChild("");
                        }
                        tr.appendChild(td);
                    }
                    nu.xom.Element td = new nu.xom.Element("td");
                    td.appendChild(coConstraint.getDescription());
                    tr.appendChild(td);
                    td = new nu.xom.Element("td");
                    td.appendChild(coConstraint.getComments());
                    tr.appendChild(td);
                    tbody.appendChild(tr);
                }
                tableElement.appendChild(tbody);
                coConstraintsElement.appendChild(tableElement);
                segmentElement.appendChild(coConstraintsElement);
            }
        }

        return segmentElement;
    }

    private String getFullUsage(Segment segment, int i) {
        List<Predicate> predicates = super.findPredicate(i + 1, segment.getPredicates());
        if (predicates == null || predicates.isEmpty()) {
            return segment.getFields().get(i).getUsage().toString();
        } else {
            Predicate p = predicates.get(0);
            return segment.getFields().get(i).getUsage().toString() + "(" + p.getTrueUsage() + "/" + p.getFalseUsage() + ")";
        }
    }

    public List<SerializableConstraint> getConstraints() {
        return constraints;
    }
}
