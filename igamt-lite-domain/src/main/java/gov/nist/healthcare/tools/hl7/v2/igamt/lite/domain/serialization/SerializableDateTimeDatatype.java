package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
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

    private Map<String,String> dateValues;

    public SerializableDateTimeDatatype(String id, String prefix, String position,
        String headerLevel, String title, Datatype datatype, String defPreText, String defPostText,
        String usageNote, List<SerializableConstraint> constraints,
        Map<Component, Datatype> componentDatatypeMap,
        Map<Component, List<ValueSetBinding>> componentValueSetBindingsMap, List<Table> tables, Map<Component, String> componentTextMap, Boolean showConfLength,
        Map<String, String> dateValues) {
        super(id, prefix, position, headerLevel, title, datatype, defPreText, defPostText,
            usageNote, constraints, componentDatatypeMap, componentValueSetBindingsMap, tables, componentTextMap,showConfLength);
        this.dateValues = dateValues;
    }

    @Override
    public Element serializeElement() {
        Element element = super.serializeElement();
        Element dtmElement = new Element("DateTimeDatatype");
        for(String dateElement : dateValues.keySet()){
            Attribute dateValue = new Attribute(dateElement,dateValues.get(dateElement));
            dtmElement.addAttribute(dateValue);
        }
        element.getFirstChildElement("Datatype").appendChild(dtmElement);
        return element;
    }
}
