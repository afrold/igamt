package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;

@ReadingConverter
public class SegmentReadConverter extends AbstractReadConverter<DBObject, Segment> {

  private static final Logger log = LoggerFactory.getLogger(SegmentReadConverter.class);

  public SegmentReadConverter() {
    log.info("SegmentReadConverter Read Converter Created");
  }

  @Override
  public Segment convert(DBObject source) {
    Segment seg = new Segment();
    seg.setId(readMongoId(source));

    // System.out.println(seg.getId());
    seg.setType((String) source.get(TYPE));
    seg.setLabel((String) source.get(LABEL));
    seg.setName(((String) source.get(NAME)));
    seg.setDescription((String) source.get(DESCRIPTION));
    seg.setComment(readString(source, COMMENT));
    seg.setText1(readString(source, TEXT_1));
    seg.setExt((String) source.get(EXT));
    seg.setText2(readString(source, TEXT_2));
    seg.setScope(Constant.SCOPE.valueOf((String) source.get(SCOPE_)));
    seg.setHl7Version((String) source.get(HL7_VERSION));

    BasicDBList libIdsObjects = (BasicDBList) source.get(LIB_IDS);
    if (libIdsObjects != null) {
      for (Object libIdObj : libIdsObjects) {
        seg.getLibIds().add((String) libIdObj);
      }
    }

    BasicDBList fieldObjects = (BasicDBList) source.get(FIELDS);
    if (fieldObjects != null) {
      List<Field> fields = new ArrayList<Field>();
      for (Object fieldObject : fieldObjects) {
        Field f = field((DBObject) fieldObject);
        fields.add(f);
      }
      seg.setFields(fields);
    }

    BasicDBList confStsObjects = (BasicDBList) source.get(CONFORMANCE_STATEMENTS);
    if (confStsObjects != null) {
      List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
      for (Object confStObject : confStsObjects) {
        ConformanceStatement cs = conformanceStatement((DBObject) confStObject);
        confStatements.add(cs);
      }
      seg.setConformanceStatements(confStatements);
    }

    BasicDBList predDBObjects = (BasicDBList) source.get(PREDICATES);
    if (predDBObjects != null) {
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (Object predObj : predDBObjects) {
        DBObject predObject = (DBObject) predObj;
        Predicate pred = predicate(predObject);
        predicates.add(pred);
      }
      seg.setPredicates(predicates);
    }

    DBObject dynamicMappingDBObject = (DBObject) source.get(DYNAMIC_MAPPING);
    if (dynamicMappingDBObject != null) {
      DynamicMapping dyn = dynamicMapping(dynamicMappingDBObject);
      seg.setDynamicMapping(dyn);
    }

    return seg;
  }

  private DynamicMapping dynamicMapping(DBObject source) {
    DynamicMapping p = new DynamicMapping();
    p.setId(readMongoId(source));
    BasicDBList mappingsDBObjects = (BasicDBList) source.get(MAPPINGS);
    if (mappingsDBObjects != null) {
      List<Mapping> mappings = new ArrayList<Mapping>();
      for (Object compObj : mappingsDBObjects) {
        DBObject compObject = (DBObject) compObj;
        Mapping m = mapping(compObject);
        mappings.add(m);
      }
      p.setMappings(mappings);
    }
    return p;
  }

  private Mapping mapping(DBObject source) {
    Mapping p = new Mapping();
    p.setId(readMongoId(source));
    p.setReference(((Integer) source.get(REFERENCE)));
    p.setPosition(((Integer) source.get(POSITION)));
    BasicDBList mappingsDBObjects = (BasicDBList) source.get(CASES);
    if (mappingsDBObjects != null) {
      List<Case> cases = new ArrayList<Case>();
      for (Object compObj : mappingsDBObjects) {
        DBObject compObject = (DBObject) compObj;
        Case m = toCase(compObject);
        cases.add(m);
      }
      p.setCases(cases);
    }

    return p;
  }

  private Case toCase(DBObject source) {
    Case p = new Case();
    p.setId(readMongoId(source));
    p.setValue(((String) source.get(VALUE)));
    p.setDatatype((String) source.get(Constant.DATATYPE));
    // Datatype d = findDatatypeById(((String)
    // source.get(Constant.DATATYPE)),
    // dtLib);
    // if (d == null) {
    // throw new ProfileConversionException("Datatype "
    // + ((String) source.get(Constant.DATATYPE)) + " not found");
    // }
    // p.setDatatype(d.getId());
    return p;
  }

  // private Datatype findDatatypeById(String id, DatatypeLibrary dtLib) {
  // if (dtLib != null) {
  // Datatype d = dtLib.findOne(id);
  // if (d != null) {
  // return d;
  // }
  // }
  //
  // return null;
  // }

  private Field field(DBObject source) {
    Field f = new Field();
    f.setId(readMongoId(source));
    f.setType(((String) source.get(TYPE)));
    f.setName(((String) source.get(NAME)));
    f.setComment(readString(source, COMMENT));
    f.setMinLength(getMinLength(source));
    f.setMaxLength((String) source.get(MAX_LENGTH));
    f.setConfLength(getConfLength(source));
    f.setPosition((Integer) source.get(POSITION));
//    f.setTables((tableLinks((BasicDBList) source.get(TABLES))));
    f.setUsage(Usage.valueOf((String) source.get(USAGE)));
    f.setItemNo((String) source.get(ITEM_NO));
    f.setMin((Integer) source.get(MIN));
    f.setMax((String) source.get(MAX));
    f.setText(readString(source, TEXT));
    f.setDatatype((datatypeLink((DBObject) source.get(Constant.DATATYPE))));
    return f;
  }

  private Reference reference(DBObject source) {
    if (source != null) {
      Reference reference = new Reference();
      reference.setChapter(((String) source.get(CHAPTER)));
      reference.setSection(((String) source.get(SECTION)));
      reference.setPage((Integer) source.get(PAGE));
      reference.setUrl((String) source.get(URL));
      return reference;
    }
    return null;
  }

  private ConformanceStatement conformanceStatement(DBObject source) {
    ConformanceStatement cs = new ConformanceStatement();
    cs.setId(readMongoId(source));
    cs.setConstraintId(((String) source.get(CONSTRAINT_ID)));
    cs.setConstraintTarget(((String) source.get(CONSTRAINT_TARGET)));
    cs.setDescription((String) source.get(DESCRIPTION));
    cs.setAssertion(((String) source.get(ASSERTION)));
    cs.setReference(reference(((DBObject) source.get(REFERENCE))));
    return cs;
  }

  private Predicate predicate(DBObject source) {
    Predicate p = new Predicate();
    p.setId(readMongoId(source));
    p.setConstraintId(((String) source.get(CONSTRAINT_ID)));
    p.setConstraintTarget(((String) source.get(CONSTRAINT_TARGET)));
    p.setDescription((String) source.get(DESCRIPTION));
    p.setAssertion(((String) source.get(ASSERTION)));
    p.setReference(reference(((DBObject) source.get(REFERENCE))));
    p.setFalseUsage(
        source.get(FALSE_USAGE) != null ? Usage.valueOf(((String) source.get(FALSE_USAGE))) : null);
    p.setTrueUsage(
        source.get(TRUE_USAGE) != null ? Usage.valueOf(((String) source.get(TRUE_USAGE))) : null);
    return p;
  }

  private DatatypeLink datatypeLink(DBObject source) {
    DatatypeLink dl = new DatatypeLink();
    dl.setId(readMongoId(source));
    dl.setName((String) source.get("name"));
    dl.setExt((String) source.get("ext"));

    return dl;
  }

  private TableLink tableLink(DBObject source) {
    if (source == null)
      return null;

    TableLink tl = new TableLink();
    tl.setId(readMongoId(source));
    tl.setBindingIdentifier((String) source.get("bindingIdentifier"));
    tl.setBindingLocation((String) source.get("bindingLocation"));
    tl.setBindingStrength((String) source.get("bindingStrength"));

    return tl;
  }

  private List<TableLink> tableLinks(BasicDBList sourceList) {
    if (sourceList == null)
      return null;
    List<TableLink> links = new ArrayList<TableLink>();
    for (Object libIdObj : sourceList) {
      links.add(tableLink((DBObject) libIdObj));
    }
    return links;
  }
}
