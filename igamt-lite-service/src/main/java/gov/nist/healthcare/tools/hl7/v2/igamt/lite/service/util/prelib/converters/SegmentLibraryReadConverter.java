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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;

/**
 * @author gcr1 12.Feb.16
 */
@ReadingConverter
public class SegmentLibraryReadConverter extends AbstractReadConverter<DBObject, SegmentLibrary> {

  private static final Logger log = LoggerFactory.getLogger(SegmentLibraryReadConverter.class);

  public SegmentLibraryReadConverter() {
    log.info("SegmentLibraryReadConverter Read Converter Created");
  }

  @Override
  public SegmentLibrary convert(DBObject source) {
    SegmentLibrary lib = new SegmentLibrary();
    return lib(source, lib);
  }

  private SegmentLibrary lib(DBObject source, SegmentLibrary lib) {
    lib.setId(readMongoId(source));
    lib.setType(Constant.SEGMENTS);
    lib.setAccountId((Long) source.get(ACCOUNT_ID));
    lib.setMetaData(metaData((DBObject) source.get(METADATA)));
    lib.setScope(SCOPE.valueOf((String) source.get(SCOPE_)));

    lib.setSectionContents((String) source.get(SECTION_COMMENTS));
    lib.setSectionDescription((String) source.get(SECTION_DESCRIPTION));
    lib.setSectionPosition((Integer) source.get(SECTION_POSITION));
    lib.setSectionTitle((String) source.get(SECTION_TITLE));
    BasicDBList libDBObjects = (BasicDBList) source.get(CHILDREN);
    lib.setChildren(new HashSet<SegmentLink>());

    if (libDBObjects != null) {
      for (Object childObj : libDBObjects) {
        DBObject dbObj = (DBObject) childObj;
        String id = readMongoId(dbObj);
        String name = (String) dbObj.get(NAME);
        String ext = (String) dbObj.get(EXT);
        SegmentLink sgl = new SegmentLink(id, name, ext);
        lib.addSegment(sgl);
      }
    }

    return lib;
  }

  SegmentLibraryMetaData metaData(DBObject source) {
    SegmentLibraryMetaData metaData = new SegmentLibraryMetaData();
    metaData.setDate((String) source.get(DATE));
    metaData.setExt((String) source.get(EXTENSION));
    metaData.setHl7Version((String) source.get(HL7_VERSION));
    metaData.setName((String) source.get(NAME));
    metaData.setOrgName((String) source.get(ORG_NAME));
    metaData.setVersion((String) source.get(VERSION));
    return metaData;
  }
}
