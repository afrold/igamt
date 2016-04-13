/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;

/**
 * @author gcr1 12.Feb.16
 */
@ReadingConverter
public class TableLibraryReadConverter extends AbstractReadConverter<DBObject, TableLibrary> {

	private static final Logger log = LoggerFactory.getLogger(TableLibraryReadConverter.class);

	public TableLibraryReadConverter() {
		log.info("TableReadConverter Read Converter Created");
	}

	@Override
	public TableLibrary convert(DBObject source) {
		TableLibrary dts = new TableLibrary();
		return tabLib(source, dts);
	} 
	
	private TableLibrary tabLib(DBObject source, TableLibrary tabLib) {
		tabLib.setId(readMongoId(source));
		tabLib.setType(TABLES);
		tabLib.setAccountId((Long) source.get(ACCOUNT_ID));
		
		tabLib.setSectionContents((String) source.get(SECTION_COMMENTS));
		tabLib.setSectionDescription((String) source.get(SECTION_DESCRIPTION));
		tabLib.setSectionPosition((Integer) source.get(SECTION_POSITION));
		tabLib.setSectionTitle((String) source.get(SECTION_TITLE));
		BasicDBList tabLibDBObjects = (BasicDBList) source.get(CHILDREN);
		tabLib.setChildren(new HashSet<Table>());
		
		TableReadConverter tabCnv = new TableReadConverter();
		if (tabLibDBObjects != null) {
			for (Object childObj : tabLibDBObjects) {
				DBObject child = (DBObject) childObj;
				if (tabLib.findTableById(readMongoId(child)) == null) {
					tabLib.addTable(tabCnv.convert(child));
				}
			}
		}

		return tabLib;
	}
}
