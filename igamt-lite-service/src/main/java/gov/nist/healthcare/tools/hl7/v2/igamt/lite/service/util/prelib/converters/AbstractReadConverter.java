package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataElement;

public abstract class AbstractReadConverter<S, T> implements Converter<S, T> {

  public final static String TABLES = "tables";
  public final static String ACCOUNT_ID = "accountId";
  public final static String SECTION_COMMENTS = "sectionContents";
  public final static String SECTION_DESCRIPTION = "sectionDescription";
  public final static String SECTION_POSITION = "sectionPosition";
  public final static String SECTION_TITLE = "sectionTitle";
  public final static String CHILDREN = "children";

  public final static String TYPE = "type";
  public final static String EXT = "ext";
  public final static String LABEL = "label";
  public final static String NAME = "name";
  public final static String DESCRIPTION = "description";
  public final static String COMMENT = "comment";
  public final static String COMMENTS = "comments";
  public final static String TEXT_1 = "text1";
  public final static String TEXT_2 = "text2";
  public final static String LIB_IDS = "libIds";
  public final static String FIELDS = "fields";
  public final static String CONFORMANCE_STATEMENTS = "conformanceStatements";
  public final static String PREDICATES = "predicates";
  public final static String DYNAMIC_MAPPING = "dynamicMapping";
  public final static String MAPPINGS = "mappings";
  public final static String REFERENCE = "reference";
  public final static String POSITION = "position";
  public final static String CASES = "cases";
  public final static String VALUE = "value";
  public final static String MIN_LENGTH = "minLength";
  public final static String MAX_LENGTH = "maxLength";
  public final static String CONF_LENGTH = "confLength";
  public final static String TABLE = "table";
  public final static String USAGE = "usage";
  public final static String BINDING_LOCATION = "bindingLocation";
  public final static String BINDING_STRENGTH = "bindingStrength";
  public final static String ITEM_NO = "itemNo";
  public final static String MIN = "min";
  public final static String MAX = "max";
  public final static String TEXT = "text";
  public final static String CHAPTER = "chapter";
  public final static String SECTION = "section";
  public final static String PAGE = "page";
  public final static String URL = "url";
  public final static String CONSTRAINT_ID = "constraintId";
  public final static String CONSTRAINT_TARGET = "constraintTarget";
  public final static String ASSERTION = "assertion";
  public final static String FALSE_USAGE = "falseUsage";
  public final static String TRUE_USAGE = "trueUsage";
  public final static String USAGE_NOTE = "usageNote";
  public final static String COMPONENTS = "components";
  public final static String ID = "id";
  public final static String BINDING_IDENTIFIER = "bindingIdentifier";
  public final static String MAPPING_ID = "mappingId";
  public final static String ORDER = "order";
  public final static String GROUP = "group";
  public final static String VERSION = "version";
  public final static String OID = "oid";
  public final static String STABILITY = "stability";
  public final static String EXTENSIBILITY = "extensibility";
  public final static String CONTENT_DEFINITION = "contentDefinition";
  public final static String CODES = "codes";
  public final static String CODE = "code";
  public final static String CODE_SYSTEM = "codeSystem";
  public final static String CODE_SYSTEM_VERSION = "codeSystemVersion";
  public final static String CODE_USAGE = "codeUsage";
  public final static String CODE_SYS = "codeSystemVersion";
  public final static String SCOPE_ = "scope";
  public final static String STATUS_ = "status";
  public final static String HL7_VERSION = "hl7Version";
  public final static String METADATA = "metaData";
  public final static String DATE = "date";
  public final static String EXTENSION = "ext";
  public final static String ORG_NAME = "orgName";

  protected String readMongoId(DBObject source) {
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

  protected String readString(DBObject source, String tag) {
    if (source.get(tag) != null) {
      return String.valueOf(source.get(tag));
    }
    return "";
  }

  protected Long readLong(DBObject source, String tag) {
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

  protected String getMinLength(DBObject source) {
    return (((String) source.get("minLength")).equals("-1") ? DataElement.LENGTH_NA
        : ((String) source.get("minLength")));
  }

  protected String getConfLength(DBObject source) {
    return "-1".equals(source.get("confLength")) ? DataElement.LENGTH_NA
        : (String) source.get("confLength");
  }
}
