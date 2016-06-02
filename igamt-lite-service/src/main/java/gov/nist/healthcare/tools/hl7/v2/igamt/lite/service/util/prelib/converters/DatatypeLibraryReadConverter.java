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
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibraryMetaData;


@ReadingConverter
public class DatatypeLibraryReadConverter extends AbstractReadConverter<DBObject, DatatypeLibrary> {

  final Logger log = LoggerFactory.getLogger(DatatypeLibraryReadConverter.class);

  public DatatypeLibraryReadConverter() {
    log.info("DatatypeLibraryReadConverter Created...");
  }

  @Override
  public DatatypeLibrary convert(DBObject source) {
    log.info("DatatypeLibrary.convert==>");
    DatatypeLibrary dtLib = new DatatypeLibrary();
    return datatypes(source, dtLib);
  }

  private DatatypeLibrary datatypes(DBObject source, DatatypeLibrary dtLib) {
    dtLib.setId(readMongoId(source));
    dtLib.setType(Constant.DATATYPES);
    dtLib.setAccountId((Long) source.get(ACCOUNT_ID));
    dtLib.setMetaData(metaData((DBObject) source.get(METADATA)));

    dtLib.setSectionContents((String) source.get(SECTION_COMMENTS));
    dtLib.setSectionDescription((String) source.get(SECTION_DESCRIPTION));
    dtLib.setSectionPosition((Integer) source.get(SECTION_POSITION));
    dtLib.setSectionTitle((String) source.get(SECTION_TITLE));
    BasicDBList datatypesDBObjects = (BasicDBList) source.get(CHILDREN);
    dtLib.setChildren(new HashSet<DatatypeLink>());
    dtLib.setScope(Constant.SCOPE.valueOf(((String) source.get(SCOPE_))));

    if (datatypesDBObjects != null) {
      for (Object childObj : datatypesDBObjects) {
        dtLib.addDatatype(link((DBObject) childObj, dtLib));
      }
    }

    return dtLib;
  }

  private DatatypeLink link(DBObject source, DatatypeLibrary dtLib) {
    String id = readMongoId(source);
    String name = (String) source.get(NAME);
    String ext = (String) source.get(EXTENSION);
    return new DatatypeLink(id, name, ext);
  }

  DatatypeLibraryMetaData metaData(DBObject source) {
    DatatypeLibraryMetaData metaData = new DatatypeLibraryMetaData();
    metaData.setDate((String) source.get(DATE));
    metaData.setExt((String) source.get(EXTENSION));
    metaData.setHl7Version((String) source.get(HL7_VERSION));
    metaData.setName((String) source.get(NAME));
    metaData.setOrgName((String) source.get(ORG_NAME));
    metaData.setVersion((String) source.get(VERSION));
    return metaData;
  }
}
