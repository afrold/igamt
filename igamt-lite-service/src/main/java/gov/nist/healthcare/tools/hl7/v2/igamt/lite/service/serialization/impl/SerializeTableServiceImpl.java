package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AppInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CodeUsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetMetadataConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeTableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
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
    
    @Autowired
    private AppInfo appInfo;

    @Override public SerializableTable serializeTable(TableLink tableLink, String prefix,
        Integer position, CodeUsageConfig valueSetCodesUsageConfig, ValueSetMetadataConfig valueSetMetadataConfig, int maxCodeNumber, HashMap<String, Boolean> codePresence)
			throws TableSerializationException {
        if(tableLink!=null && tableLink.getId()!=null) {
          Table table = tableService.findById(tableLink.getId());
          if(table == null){
          	throw new TableSerializationException(new TableNotFoundException(tableLink.getId()),tableLink.getBindingIdentifier());
					}
          return this.serializeTable(tableLink, table, prefix, position, valueSetCodesUsageConfig, valueSetMetadataConfig,maxCodeNumber,codePresence);
        }
        return null;
    }

    @Override
    public SerializableTable serializeTable(TableLink tableLink, Table table, String prefix,
        Integer position, CodeUsageConfig valueSetCodesUsageConfig,
        ValueSetMetadataConfig valueSetMetadataConfig, int maxCodeNumber, HashMap<String, Boolean> codePresence)
			throws TableSerializationException {
      return serializeTable(table, String.valueOf(3),prefix, position, valueSetCodesUsageConfig, valueSetMetadataConfig,maxCodeNumber,codePresence);
    }

	@Override
	public SerializableTable serializeTable(Table table) throws TableSerializationException {
		ExportConfig defaultConfig = ExportConfig.getBasicExportConfig(true);
		return serializeTable(table,String.valueOf(0),String.valueOf(1),1,defaultConfig.getCodesExport(),defaultConfig.getValueSetsMetadata(),defaultConfig.getMaxCodeNumber(),new HashMap<String, Boolean>());
	}
	
	private SerializableTable serializeTable(Table table, String headerLevel, String prefix,
	        Integer position, CodeUsageConfig valueSetCodesUsageConfig,
	        ValueSetMetadataConfig valueSetMetadataConfig, int maxCodeNumber, HashMap<String, Boolean> codePresence) throws TableSerializationException {
		if (table != null) {
			try {
				String id = table.getId();
				String defPreText, defPostText;
				defPreText = defPostText = "";
				SerializableTable serializedTable = null;
				String title = table.getBindingIdentifier();
				if (table.getName() != null && !table.getName().isEmpty()) {
					title += " - " + table.getName();
				}
				if (table.getDefPreText() != null && !table.getDefPreText().isEmpty()) {
					defPreText = serializationUtil.cleanRichtext(table.getDefPreText());
				}
				if (table.getDefPostText() != null && !table.getDefPostText().isEmpty()) {
					defPostText = serializationUtil.cleanRichtext(table.getDefPostText());
				}
				List<Code> toBeExportedCodes = new ArrayList<>();
				for (Code code : table.getCodes()) {
					if (ExportUtil.diplayCodeUsage(code.getCodeUsage(), valueSetCodesUsageConfig)) {
						toBeExportedCodes.add(code);
					}
				}
				table.setCodes(toBeExportedCodes);
				String referenceUrl = "";
				if (table.getScope().equals(SCOPE.PHINVADS)) {
					String phinvads = appInfo.getProperties().get("PHINVADS");
					if (phinvads != null && table.getOid() != null && !table.getOid().isEmpty()) {
						referenceUrl = phinvads + table.getOid();
					}
				} else {
					if (table.getReferenceUrl() != null) {
						referenceUrl = table.getReferenceUrl();
					}
				}
				boolean exportCodes = true;
				if(codePresence!=null && codePresence.containsKey(table.getId())){
					Boolean tableCodePresence = codePresence.get(table.getId());
					if(tableCodePresence != null && !tableCodePresence.booleanValue()){
						exportCodes = false;
					}
				}
				serializedTable =
					new SerializableTable(id, prefix, String.valueOf(position), headerLevel, title, table,
						table.getBindingIdentifier(), defPreText, defPostText, valueSetMetadataConfig,
						maxCodeNumber, exportCodes, referenceUrl);
				return serializedTable;
			} catch (Exception e){
				throw new TableSerializationException(e,table.getBindingIdentifier());
			}
		}
		return null;
	}
}
