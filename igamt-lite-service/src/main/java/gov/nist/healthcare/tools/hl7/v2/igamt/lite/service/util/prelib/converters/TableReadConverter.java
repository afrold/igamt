package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

@ReadingConverter
public class TableReadConverter extends AbstractReadConverter<DBObject, Table> {

  private static final Logger log = LoggerFactory.getLogger(TableReadConverter.class);

  public TableReadConverter() {
    log.info("Table Read Converter Created");
  }

  @Override
  public Table convert(DBObject source) {

    log.info("Table.convert==>");
    Table table = new Table();
    table.setType((String) source.get(TYPE));
    table.setScope(SCOPE.valueOf((String) source.get(SCOPE_)));
    table.setHl7Version((String) source.get(HL7_VERSION));

    table.setCodes(new ArrayList<Code>());
    table.setId(readMongoId(source));
    table.setBindingIdentifier(((String) source.get(BINDING_IDENTIFIER)));
    // Nullity tests added for retro compatibility
    if (source.get(BINDING_IDENTIFIER) == null) {
      table.setBindingIdentifier(((String) source.get(MAPPING_ID)));
    }
    table.setName(((String) source.get(NAME)));
    table.setDescription(((String) source.get(DESCRIPTION)));
    table.setDefPreText(((String) source.get(DESCRIPTION)));
    // Nullity tests added for retro compatibility
    if (source.get(DESCRIPTION) == null) {
      table.setDescription((String) source.get(NAME));
    }
    table.setOrder(source.get(ORDER) != null ? ((Integer) source.get(ORDER)) : 0);
    table.setGroup(((String) source.get(GROUP)));
    table.setVersion(((String) source.get(VERSION)));
    table.setOid(((String) source.get(OID)));
    table.setScope(Constant.SCOPE.valueOf((String) source.get(SCOPE_)));
    // Nullity tests added for retro compatibility
    table.setStability(source.get(STABILITY) == null ? Stability.Dynamic
        : Stability.fromValue((String) source.get(STABILITY)));
    table.setExtensibility(source.get(EXTENSIBILITY) == null ? Extensibility.Open
        : Extensibility.fromValue((String) source.get(EXTENSIBILITY)));
    table
        .setContentDefinition(source.get(CONTENT_DEFINITION) == null ? ContentDefinition.Intensional
            : ContentDefinition.fromValue((String) source.get(CONTENT_DEFINITION)));

    BasicDBList libIds = (BasicDBList) source.get(LIB_IDS);
    if (libIds != null) {
      for (Object libIdObj : libIds) {
        table.getLibIds().add((String) libIdObj);
      }
    }

    BasicDBList codesDBObjects = (BasicDBList) source.get(CODES);
    if (codesDBObjects != null)
      for (Object codeObj : codesDBObjects) {
        DBObject codeObject = (DBObject) codeObj;
        Code code = new Code();
        code.setId(readMongoId(codeObject));
        code.setValue(((String) codeObject.get(VALUE)));
        code.setCodeSystem(((String) codeObject.get(CODE_SYSTEM)));
        code.setCodeSystemVersion((String) codeObject.get(CODE_SYSTEM_VERSION));
        code.setCodeUsage(((String) codeObject.get(CODE_USAGE)));
        code.setType(((String) codeObject.get(TYPE)));
        code.setLabel(((String) codeObject.get(LABEL)));
        code.setComments(readString(codeObject, COMMENTS));
        // Added for retro compatibility
        if (codeObject.get(VALUE) == null) {
          code.setValue(((String) codeObject.get(CODE)));
        }
        if (codeObject.get(CODE_SYSTEM) == null) {
          code.setCodeSystem((String) codeObject.get(CODE_SYSTEM_VERSION));
        }
        table.addCode(code);
      }
    log.info("<==convert");
    return table;
  }
}
