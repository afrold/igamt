package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeTableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * Created by Maxence Lefort on 12/13/16.
 */
@Service
public class SerializeTableServiceImpl implements SerializeTableService {

    @Autowired
    TableService tableService;

    @Autowired
    SerializationUtil serializationUtil;

    @Override public SerializableTable serializeTable(TableLink tableLink, String prefix, Integer position) {
        if(tableLink!=null && tableLink.getId()!=null) {
            Table table = tableService.findById(tableLink.getId());
            String id = tableLink.getId();
            String title = "ID not found: "+tableLink.getId();
            String headerLevel = String.valueOf(3);
            String defPreText,defPostText;
            defPreText = defPostText = "";
            if(table!=null) {
                id = table.getId();
                title = table.getBindingIdentifier() + " - " + table.getDescription();
                if (table.getDefPreText() != null && !table.getDefPreText().isEmpty()) {
                    defPreText = serializationUtil.cleanRichtext(table.getDefPreText());
                }
                if (table.getDefPostText() != null && !table.getDefPostText().isEmpty()) {
                    defPostText = serializationUtil.cleanRichtext(table.getDefPostText());
                }
            }
            SerializableTable serializedTable = new SerializableTable(id,prefix,String.valueOf(position),headerLevel,title,table,tableLink.getBindingIdentifier(),defPreText,defPostText);
            return serializedTable;
        }
        return null;
    }
}
