package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DateTimeDatatypeSerializationException;
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
 * <p/>
 * Created by Maxence Lefort on 1/12/17.
 */
public class SerializableDateTimeDatatype extends SerializableDatatype {

    private Datatype datatype;

    public SerializableDateTimeDatatype(String id, String prefix, String position,
        String headerLevel, String title, Datatype datatype, String defPreText, String defPostText,
        String usageNote, List<SerializableConstraint> constraints,
        Map<Component, Datatype> componentDatatypeMap,
        Map<Component, List<ValueSetOrSingleCodeBinding>> componentValueSetBindingsMap, List<Table> tables, Map<Component, String> componentTextMap, Boolean showConfLength, Boolean showInnerLink, String host) {
        super(id, prefix, position, headerLevel, title, datatype, defPreText, defPostText,
            usageNote, constraints, componentDatatypeMap, componentValueSetBindingsMap, tables, componentTextMap,showConfLength, showInnerLink, host);
        this.datatype = datatype;
    }

    @Override
    public Element serializeElement() throws DateTimeDatatypeSerializationException {
        try {
            Element element = super.serializeElement();
            Element dtmElement = new Element("DateTimeDatatype");
            DTMConstraints dtmConstraints = datatype.getDtmConstraints();
            if (dtmConstraints != null) {
                for (DTMComponentDefinition dtmComponentDefinition : dtmConstraints
                    .getDtmComponentDefinitions()) {
                    if (dtmComponentDefinition != null) {
                        Element dtmDefinitionElement = new Element("DateTimeDatatypeDefinition");
                        if (dtmComponentDefinition.getName() != null && !dtmComponentDefinition
                            .getName().isEmpty()) {
                            dtmDefinitionElement.addAttribute(
                                new Attribute("Name", dtmComponentDefinition.getName()));
                        }
                        if (dtmComponentDefinition.getPosition() != null) {
                            dtmDefinitionElement.addAttribute(new Attribute("Position", String.valueOf(dtmComponentDefinition.getPosition())));
                        }
                        if (dtmComponentDefinition.getUsage() != null && !dtmComponentDefinition
                            .getUsage().value().isEmpty()) {
                            String usage = dtmComponentDefinition.getUsage().value();
                            if (usage.equals(Usage.C.value())) {
                                if (dtmComponentDefinition.getDtmPredicate() != null) {
                                    String trueUsage, falseUsage;
                                    if (dtmComponentDefinition.getDtmPredicate().getTrueUsage() != null && !dtmComponentDefinition.getDtmPredicate()
                                        .getTrueUsage().value().isEmpty()
                                        && dtmComponentDefinition.getDtmPredicate().getFalseUsage()
                                        != null && !dtmComponentDefinition.getDtmPredicate()
                                        .getFalseUsage().value().isEmpty()) {
                                        trueUsage = dtmComponentDefinition.getDtmPredicate().getTrueUsage()
                                            .value();
                                        falseUsage = dtmComponentDefinition.getDtmPredicate().getFalseUsage()
                                            .value();
                                        usage =
                                            usage.concat("(" + trueUsage + "/" + falseUsage + ")");
                                    }
                                    if (dtmComponentDefinition.getDtmPredicate()
                                        .getPredicateDescription() != null && !dtmComponentDefinition.getDtmPredicate()
                                        .getPredicateDescription().isEmpty()) {
                                        dtmDefinitionElement.addAttribute(new Attribute("Predicate",
                                            dtmComponentDefinition.getDtmPredicate()
                                                .getPredicateDescription()));
                                    }
                                }
                            }
                            dtmDefinitionElement.addAttribute(new Attribute("Usage", usage));
                        }
                        dtmElement.appendChild(dtmDefinitionElement);
                    }
                }
            }
            element.getFirstChildElement("Datatype").appendChild(dtmElement);
            return element;
        } catch (Exception e){
            throw new DateTimeDatatypeSerializationException(e,datatype.getLabel()!=null?datatype.getLabel():"");
        }
    }
}
