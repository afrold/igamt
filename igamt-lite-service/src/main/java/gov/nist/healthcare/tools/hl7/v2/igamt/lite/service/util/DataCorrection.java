package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.DatatypeLibraryReadConverter;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.DatatypeReadConverter;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.MessageReadConverter;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.SegmentLibraryReadConverter;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.SegmentReadConverter;
//import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.TableLibraryReadConverter;

public class DataCorrection {
//  public void updateMessage() {
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      MessageReadConverter conv = new MessageReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("message");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        Message m = conv.convert(source);
//
//        System.out.println("MessageID: " + m.getMessageID());
//
//        if (m.getMessageID() == null || m.getMessageID().equals("")) {
//          m.setMessageID(UUID.randomUUID().toString());
//          System.out.println("Updated messageID: " + m.getMessageID());
//          mongoOps.save(m);
//        }
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//  }
//
//  public void updateSegmentLibrary() {
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      SegmentLibraryReadConverter conv = new SegmentLibraryReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("segment-library");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        SegmentLibrary segLib = conv.convert(source);
//
//        for (SegmentLink link : segLib.getChildren()) {
//          String id = link.getId();
//          String ext = link.getExt();
//
//          if (ext != null && !ext.equals("")) {
//            Segment s = mongoOps.findOne(Query.query(Criteria.where("_id").is(id)), Segment.class);
//
//            if (s != null) {
//
//              if (s.getExt() == null || !s.getExt().equals(ext)) {
//                s.setExt(ext);
//                mongoOps.save(s);
//                System.out.println("Segment is changed!!! EXT: " + ext + " ID: " + s.getId());
//              }
//            } else {
//              System.out.println("Segment is missing!!!");
//            }
//          }
//        }
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//  }
//
//  public void updateDatatypeLibrary() {
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      DatatypeLibraryReadConverter conv = new DatatypeLibraryReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("datatype-library");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        DatatypeLibrary dtLib = conv.convert(source);
//
//        for (DatatypeLink link : dtLib.getChildren()) {
//          String id = link.getId();
//          String ext = link.getExt();
//
//          if (ext != null && !ext.equals("")) {
//            Datatype dt =
//                mongoOps.findOne(Query.query(Criteria.where("_id").is(id)), Datatype.class);
//
//            if (dt != null) {
//
//              if (dt.getExt() == null || !dt.getExt().equals(ext)) {
//                dt.setExt(ext);
//                mongoOps.save(dt);
//                System.out.println("DT is changed!!! EXT: " + ext + " ID: " + dt.getId());
//              }
//            } else {
//              System.out.println("DT is missing!!!");
//            }
//          }
//        }
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//  }
//
//  public void updateTableLibrary() {
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      TableLibraryReadConverter conv = new TableLibraryReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("table-library");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        TableLibrary tableLib = conv.convert(source);
//
//        for (TableLink link : tableLib.getChildren()) {
//          String id = link.getId();
//          String bindingIdentifier = link.getBindingIdentifier();
//
//          if (bindingIdentifier != null && !bindingIdentifier.equals("")) {
//            Table table = mongoOps.findOne(Query.query(Criteria.where("_id").is(id)), Table.class);
//
//            if (table != null) {
//
//              if (table.getBindingIdentifier() == null
//                  || !table.getBindingIdentifier().equals(bindingIdentifier)) {
//                table.setBindingIdentifier(bindingIdentifier);
//                mongoOps.save(table);
//                System.out.println("Table is changed!!! BindingIdentifier: " + bindingIdentifier
//                    + " ID: " + table.getId());
//              }
//            } else {
//              System.out.println("DT is missing!!!");
//            }
//          }
//        }
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//  }
//
//  public void updateSegment() {
//
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      SegmentReadConverter conv = new SegmentReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("segment");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        Segment seg = conv.convert(source);
//
//        for (Field f : seg.getFields()) {
//          if (f.getConfLength() != null || f.getConfLength().equals("")) {
//            f.setConfLength("1");
//          }
//        }
//
//        mongoOps.save(seg);
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//
//  }
//
//  public void updateDatatype() {
//
//    MongoOperations mongoOps;
//    try {
//      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      DatatypeReadConverter conv = new DatatypeReadConverter();
//
//      DBCollection coll1 = mongoOps.getCollection("datatype");
//      DBCursor cur1 = coll1.find();
//      while (cur1.hasNext()) {
//        DBObject source = cur1.next();
//        Datatype dt = conv.convert(source);
//
//        for (Component c : dt.getComponents()) {
//          if (c.getConfLength() != null || c.getConfLength().equals("")) {
//            c.setConfLength("1");
//          }
//        }
//
//        mongoOps.save(dt);
//      }
//
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      e.printStackTrace();
//    }
//
//  }
//
//  public static void main(String[] args) throws IOException {
//    // new DataCorrection().updateSegment();
//    // new DataCorrection().updateDatatype();
//    // new DataCorrection().updateSegmentLibrary();
//    // new DataCorrection().updateDatatypeLibrary();
//    // new DataCorrection().updateTableLibrary();
//    // new DataCorrection().updateMessage();
//  }
//
//  // public void updateValueSetsForDT() {
//  // MongoOperations mongoOps;
//  // try {
//  // mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//  //
//  // ObjectMapper mapper = new ObjectMapper();
//  // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//  // DatatypeReadConverter conv = new DatatypeReadConverter();
//  //
//  // DBCollection coll1 = mongoOps.getCollection("datatype");
//  // DBCursor cur1 = coll1.find();
//  // while (cur1.hasNext()) {
//  // DBObject source = cur1.next();
//  // Datatype dt = conv.convert(source);
//  //
//  // for (Component c : dt.getComponents()) {
//  // if (c.getTable() != null) {
//  // if (!isAlreadyAdded(c.getTable(), c.getTables())) {
//  // c.getTables().add(c.getTable());
//  // }
//  // }
//  // }
//  //
//  // mongoOps.save(dt);
//  // }
//  //
//  // } catch (Exception e) {
//  // // TODO Auto-generated catch block
//  // e.printStackTrace();
//  // e.printStackTrace();
//  // }
//  // }
//
//  // public void updateValueSetForSegment() {
//  //
//  // MongoOperations mongoOps;
//  // try {
//  // mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//  //
//  // ObjectMapper mapper = new ObjectMapper();
//  // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//  // SegmentReadConverter conv = new SegmentReadConverter();
//  //
//  // DBCollection coll1 = mongoOps.getCollection("segment");
//  // DBCursor cur1 = coll1.find();
//  // while (cur1.hasNext()) {
//  // DBObject source = cur1.next();
//  // Segment seg = conv.convert(source);
//  //
//  // for (Field f : seg.getFields()) {
//  // if (f.getTable() != null) {
//  // if (!isAlreadyAdded(f.getTable(), f.getTables())) {
//  // f.getTables().add(f.getTable());
//  // }
//  // }
//  // }
//  // mongoOps.save(seg);
//  // }
//  //
//  // } catch (Exception e) {
//  // // TODO Auto-generated catch block
//  // e.printStackTrace();
//  // e.printStackTrace();
//  // }
//  //
//  // }
//
//  private boolean isAlreadyAdded(TableLink table, List<TableLink> tables) {
//    for (TableLink tl : tables) {
//      if (tl.getBindingIdentifier().equals(table.getBindingIdentifier()))
//        return true;
//    }
//    return false;
//  }
}
