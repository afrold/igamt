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
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 31, 2015
 */
@ReadingConverter
public class ProfileReadConverter extends AbstractReadConverter<DBObject, Profile> {

  private static final Logger log = LoggerFactory.getLogger(ProfileReadConverter.class);

  public ProfileReadConverter() {
    log.info("Profile Read Converter Created");
  }

  @Override
  public Profile convert(DBObject source) {
    Profile profile = new Profile();
    profile.setId(readMongoId(source));
    profile.setComment(readString(source, "comment"));
    profile.setType(((String) source.get("type")));
    profile.setUsageNote(readString(source, "usageNote"));
    profile.setScope(IGDocumentScope.valueOf(((String) source.get("scope"))));
    profile.setChanges(((String) source.get("changes")));
    profile.setAccountId(readLong(source, "accountId"));
    profile.setMetaData(metaData((DBObject) source.get("metaData")));
    DBObject objSegments = ((DBRef) source.get("segmentLibrary")).fetch();
    profile.setSegmentLibrary(segments(objSegments));
    DBObject objDatatypes = ((DBRef) source.get("datatypeLibrary")).fetch();
    profile.setDatatypeLibrary(datatypes(objDatatypes));
    DBObject objTables = ((DBRef) source.get("tableLibrary")).fetch();
    profile.setTableLibrary(tables(objTables));
    DBObject objMessages = ((DBObject) source.get("messages"));
    profile.setMessages(messages(objMessages));

    profile.setSectionContents((String) source.get("sectionContents"));
    profile.setSectionDescription((String) source.get("sectionDescription"));
    profile.setSectionPosition((Integer) source.get("sectionPosition"));
    profile.setSectionTitle((String) source.get("sectionTitle"));

    profile.setConstraintId((String) source.get("constraintId"));

    Object baseId = source.get("baseId");
    profile.setBaseId(baseId != null ? (String) baseId : null);

    Object sourceId = source.get("sourceId");
    profile.setSourceId(sourceId != null ? (String) sourceId : null);

    return profile;
  }

  private ProfileMetaData metaData(DBObject source) {
    ProfileMetaData metaData = new ProfileMetaData();
    metaData.setName(((String) source.get("name")));
    metaData.setOrgName(((String) source.get("orgName")));
    metaData.setStatus(((String) source.get("status")));
    metaData.setTopics(((String) source.get("topics")));
    metaData.setType(((String) source.get("type")));
    metaData.setHl7Version(((String) source.get("hl7Version")));
    metaData.setSchemaVersion(((String) source.get("schemaVersion")));
    metaData.setSubTitle(((String) source.get("subTitle")));
    metaData.setVersion(((String) source.get("version")));
    metaData.setDate(((String) source.get("date")));
    metaData.setExt(source.get("ext") != null ? ((String) source.get("ext")) : null);
    Set<String> encodings = new HashSet<String>();
    Object encodingObj = source.get("encodings");
    BasicDBList encodingDBObjects = (BasicDBList) encodingObj;
    Iterator<Object> it = encodingDBObjects.iterator();
    while (it.hasNext()) {
      encodings.add((String) it.next());
    }
    metaData.setEncodings(encodings);
    return metaData;
  }

  private SegmentLibrary segments(DBObject source) {
    SegmentLibraryReadConverter cnvSeg = new SegmentLibraryReadConverter();
    SegmentLibrary segLib = cnvSeg.convert(source);
    return segLib;
  }

  private DatatypeLibrary datatypes(DBObject source) {
    DatatypeLibraryReadConverter cnvDts = new DatatypeLibraryReadConverter();
    DatatypeLibrary dtLib = cnvDts.convert(source);
    return dtLib;
  }

  private TableLibrary tables(DBObject source) {
    TableLibraryReadConverter cnvTab = new TableLibraryReadConverter();
    TableLibrary tabLib = cnvTab.convert(source);
    return tabLib;
  }

  private Messages messages(DBObject source) {
    MessagesReadConverter cnvMsg = new MessagesReadConverter();
    Messages msgs = cnvMsg.convert(source);
    return msgs;
  }
}
