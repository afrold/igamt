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

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 31, 2015
 */
@ReadingConverter
@SuppressWarnings("deprecation")
public class IGDocumentReadConverter implements Converter<DBObject, IGDocument> {

  private static final Logger log = LoggerFactory.getLogger(IGDocumentReadConverter.class);

  public IGDocumentReadConverter() {
    log.info("IGDocument Read Converter Created");
  }

  @Override
  public IGDocument convert(DBObject source) {
    // System.out.println("convert==>");
    IGDocument igd = new IGDocument();
    igd.setAccountId(readLong(source, "accountId"));
    igd.setChildSections(sections((DBObject) source.get("childSections")));
    igd.setComment(readString(source, "comment"));
    igd.setId(readMongoId(source));
    igd.setMetaData(documentMetaData((DBObject) source.get("metaData")));
    igd.setProfile(profile((DBObject) source.get("profile")));
    igd.setScope(IGDocumentScope.valueOf(((String) source.get("scope"))));
    igd.setType(((String) source.get("type")));
    igd.setUsageNote(readString(source, "usageNote"));
    // System.out.println("<==convert");
    return igd;
  }

  private Profile profile(DBObject source) {
    ProfileReadConverter conv = new ProfileReadConverter();
    return conv.convert(source);
  }

  private DocumentMetaData documentMetaData(DBObject source) {
    DocumentMetaData metaData = new DocumentMetaData();
    metaData.setDate(source.get("date") != null ? ((String) source.get("date")) : null);
    metaData.setExt(source.get("ext") != null ? ((String) source.get("ext")) : null);
    metaData.setIdentifier(source.get("identifier") != null ? ((String) source.get("identifier"))
        : null);
    metaData.setOrgName(source.get("orgName") != null ? ((String) source.get("orgName")) : null);
    metaData.setSpecificationName(source.get("specificationName") != null ? ((String) source
        .get("specificationName")) : null);
    metaData.setStatus(source.get("status") != null ? ((String) source.get("status")) : null);
    metaData.setSubTitle(source.get("subTitle") != null ? ((String) source.get("subTitle")) : null);
    metaData.setTitle(source.get("title") != null ? ((String) source.get("title")) : null);
    metaData.setTopics(source.get("topics") != null ? ((String) source.get("topics")) : null);
    metaData.setVersion(source.get("version") != null ? ((String) source.get("version")) : null);
    metaData.setHl7Version(source.get("hl7Version") != null ? ((String) source.get("hl7Version"))
        : null);
    return metaData;
  }

  private Set<Section> sections(DBObject source) {
    Set<Section> sections = new HashSet<Section>();
    BasicDBList sectionsDBObjects = (BasicDBList) source;
    Iterator<Object> it = sectionsDBObjects.iterator();
    while (it.hasNext()) {
      DBObject childSource = (DBObject) it.next();
      Section targetSection = new Section();
      targetSection.setChildSections(sections((DBObject) childSource.get("childSections")));
      targetSection.setId(readMongoId(childSource));
      targetSection.setSectionContents((String) childSource.get("sectionContents"));
      targetSection.setSectionDescription((String) childSource.get("sectionDescription"));
      targetSection.setSectionPosition((Integer) childSource.get("sectionPosition"));
      targetSection.setSectionTitle((String) childSource.get("sectionTitle"));
      targetSection.setType((String) childSource.get("type"));
      sections.add(targetSection);
    }
    return sections;
  }

  private String readMongoId(DBObject source) {
    if (source.get("_id") != null) {
      if (source.get("_id") instanceof ObjectId) {
        return ((ObjectId) source.get("_id")).toString();
      } else {
        return (String) source.get("_id");
      }
    } else if (source.get("id") != null) {
      if (source.get("id") instanceof ObjectId) {
        return ((ObjectId) source.get("id")).toString();
      } else {
        return (String) source.get("id");
      }
    }
    return null;
  }

  private Long readLong(DBObject source, String tag) {
    if (source.get(tag) != null) {
      if (source.get(tag) instanceof Integer) {
        return Long.valueOf((Integer) source.get(tag));
      } else if (source.get(tag) instanceof String) {
        return Long.valueOf((String) source.get(tag));
      } else if (source.get(tag) instanceof Long) {
        return Long.valueOf((Long) source.get(tag));
      }
    }
    return Long.valueOf(0);
  }

  private String readString(DBObject source, String tag) {
    if (source.get(tag) != null) {
      return String.valueOf((String) source.get(tag));
    }
    return "";
  }

  private Integer getMinLength(DBObject source) {
    return ((Integer) source.get("minLength") == -1 ? 0 : ((Integer) source.get("minLength")));
  }

  private String getConfLength(DBObject source) {
    return "-1".equals((String) source.get("confLength")) ? "" : (String) source.get("confLength");
  }
}
