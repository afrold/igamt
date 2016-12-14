package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableDatatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeConstraintService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeDatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Created by Maxence Lefort on 12/12/16.
 */
@Service
public class SerializeDatatypeServiceImpl implements SerializeDatatypeService {

    @Autowired
    DatatypeService datatypeService;

    @Autowired
    TableService tableService;

    @Autowired
    SerializationUtil serializationUtil;

    @Autowired
    SerializeConstraintService serializeConstraintService;

    @Override public SerializableDatatype serializeDatatype(DatatypeLink datatypeLink, String prefix, Integer position) {
        if(datatypeLink!=null && datatypeLink.getId()!=null) {
            Datatype datatype = datatypeService.findById(datatypeLink.getId());
            String id = datatypeLink.getId();
            String title = "ID not found: "+datatypeLink.getId();
            String headerLevel = String.valueOf(3);
            if(datatype!=null) {
                id = datatype.getId();
                title = datatype.getLabel()+" - "+datatype.getDescription();
            }
            List<SerializableConstraint> constraintsList = serializeConstraintService.serializeConstraints(datatype,datatype.getName());
            String defPreText,defPostText,usageNote;
            defPreText = defPostText = usageNote = "";
            if(datatype.getDefPreText()!=null && !datatype.getDefPreText().isEmpty()){
                defPreText = serializationUtil.cleanRichtext(datatype.getDefPreText());
            }
            if(datatype.getDefPostText()!=null && !datatype.getDefPostText().isEmpty()){
                defPostText = serializationUtil.cleanRichtext(datatype.getDefPostText());
            }
            if(datatype.getUsageNote()!=null && !datatype.getUsageNote().isEmpty()){
                usageNote = serializationUtil.cleanRichtext(datatype.getDefPreText());
            }
            Map<Component,Datatype> componentDatatypeMap = new HashMap<>();
            Map<Component,List<Table>> componentTablesMap = new HashMap<>();
            Map<Component,String> componentTextMap = new HashMap<>();
            for(Component component : datatype.getComponents()){
                if(component.getDatatype()!=null && !component.getDatatype().getId().isEmpty()){
                    Datatype componentDatatype = datatypeService.findById(component.getDatatype().getId());
                    componentDatatypeMap.put(component,componentDatatype);
                }
                if(component.getTables().size()>0){
                    List<Table> componentTables = new ArrayList<>();
                    for(TableLink tableLink : component.getTables()){
                        if(tableLink!=null&&!tableLink.getId().isEmpty()){
                            Table componentTable = tableService.findById(tableLink.getId());
                            componentTables.add(componentTable);
                        }
                    }
                    if(componentTables.size()>0){
                        componentTablesMap.put(component,componentTables);
                    }
                }
                if(component.getText()!=null && !component.getText().isEmpty()){
                    String text = serializationUtil.cleanRichtext(component.getText());
                    componentTextMap.put(component,text);
                }
            }
            SerializableDatatype serializedDatatype = new SerializableDatatype(id,prefix,String.valueOf(position),headerLevel,title,datatype,defPreText,defPostText,usageNote,constraintsList,componentDatatypeMap,componentTablesMap,componentTextMap);
            return serializedDatatype;
        }
        return null;
    }
}
