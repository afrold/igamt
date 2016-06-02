package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class MessageReadConverter extends AbstractReadConverter<DBObject, Message> {

  private static final Logger log = LoggerFactory.getLogger(MessageReadConverter.class);

  public MessageReadConverter() {
    log.info("MessagesReadConverter Read Converter Created");
  }

  @Override
  public Message convert(DBObject source) {
    Message message = new Message();
    message.setId(readMongoId(source));
    message.setName((String) source.get("name"));
    if (source.get("name") == null) {
      message.setName((String) source.get("messageType") + "_" + (String) source.get("event"));
    }
    message.setMessageType((String) source.get("messageType"));
    message.setComment(readString(source, "comment"));
    message.setDescription((String) source.get("description"));
    message.setEvent((String) source.get("event"));
    message.setIdentifier((String) source.get("identifier"));
    message.setPosition((Integer) source.get("position"));
    message.setStructID((String) source.get("structID"));
    message.setType((String) source.get("type"));
    message.setMessageID((String) source.get("messageID"));



    BasicDBList confStsObjects = (BasicDBList) source.get(CONFORMANCE_STATEMENTS);
    if (confStsObjects != null) {
      List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
      for (Object confStObject : confStsObjects) {
        ConformanceStatement cs = conformanceStatement((DBObject) confStObject);
        confStatements.add(cs);
      }
      message.setConformanceStatements(confStatements);
    }

    BasicDBList predDBObjects = (BasicDBList) source.get(PREDICATES);
    if (predDBObjects != null) {
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (Object predObj : predDBObjects) {
        DBObject predObject = (DBObject) predObj;
        Predicate pred = predicate(predObject);
        predicates.add(pred);
      }
      message.setPredicates(predicates);
    }



    BasicDBList segmentRefOrGroupDBObjects = (BasicDBList) source.get("children");
    for (Object segmentRefOrGroupObject : segmentRefOrGroupDBObjects) {
      DBObject segmentRefOrGroupDBObject = (DBObject) segmentRefOrGroupObject;
      String type = (String) segmentRefOrGroupDBObject.get("type");
      if (Constant.SEGMENTREF.equals(type)) {
        SegmentRef segRef = segmentRef(segmentRefOrGroupDBObject);
        message.addSegmentRefOrGroup(segRef);
      } else {
        Group group = group(segmentRefOrGroupDBObject);
        message.addSegmentRefOrGroup(group);
      }
    }

    return message;
  }

  private SegmentRef segmentRef(DBObject source) {
    SegmentRef segRef = new SegmentRef();
    segRef.setId(readMongoId(source));
    segRef.setType(((String) source.get("type")));
    segRef.setUsage(Usage.valueOf(((String) source.get("usage"))));
    segRef.setComment(readString(source, "comment"));
    segRef.setPosition((Integer) source.get("position"));
    segRef.setMin((Integer) source.get("min"));
    segRef.setMax((String) source.get("max"));
    segRef.setRef(segmentLink((DBObject) source.get("ref")));
    return segRef;
  }

  private SegmentLink segmentLink(DBObject source) {
    SegmentLink sl = new SegmentLink();
    sl.setExt((String) source.get("ext"));
    sl.setId(readMongoId(source));
    sl.setName((String) source.get("name"));
    return sl;
  }

  private Group group(DBObject source) {
    Group group = new Group();
    group.setId(readMongoId(source));
    group.setType(((String) source.get("type")));
    group.setUsage(Usage.valueOf(((String) source.get("usage"))));
    group.setComment(readString(source, "comment"));
    group.setPosition((Integer) source.get("position"));
    group.setMin((Integer) source.get("min"));
    group.setMax((String) source.get("max"));
    group.setName(((String) source.get("name")));
    BasicDBList segmentRefOrGroupDBObjects = (BasicDBList) source.get("children");

    List<SegmentRefOrGroup> segOrGroups = new ArrayList<SegmentRefOrGroup>();

    for (Object segmentRefOrGroupObject : segmentRefOrGroupDBObjects) {
      DBObject segmentRefOrGroupDBObject = (DBObject) segmentRefOrGroupObject;
      String type = (String) segmentRefOrGroupDBObject.get("type");
      if (Constant.SEGMENTREF.equals(type)) {
        SegmentRef segRef = segmentRef(segmentRefOrGroupDBObject);
        segOrGroups.add(segRef);
      } else {
        Group subGroup = group(segmentRefOrGroupDBObject);
        segOrGroups.add(subGroup);
      }
    }
    group.setChildren(segOrGroups);
    return group;
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
    p.setFalseUsage(source.get(FALSE_USAGE) != null ? Usage.valueOf(((String) source
        .get(FALSE_USAGE))) : null);
    p.setTrueUsage(source.get(TRUE_USAGE) != null ? Usage.valueOf(((String) source.get(TRUE_USAGE)))
        : null);
    return p;
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
}
