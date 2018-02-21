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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ProfileSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.xml.XMLExportTool;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ConstraintsSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;

@Service
public class ProfileSerializationImpl implements ProfileSerialization {
  private Logger log = LoggerFactory.getLogger(ProfileSerializationImpl.class);

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  @Autowired
  private TableSerialization tableSerializationService;

  @Autowired
  private CompositeProfileService compositeProfileService;

  @Autowired
  private ConstraintsSerialization constraintsSerializationService;

  private HashMap<String, Datatype> datatypesMap;
  private HashMap<String, Segment> segmentsMap;
  private Constraints conformanceStatement;
  private Constraints predicates;

  @Override
  public Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet,
      String xmlConstraints) {
    Document profileDoc = this.stringToDom(xmlContentsProfile);
    Element elmConformanceProfile =
        (Element) profileDoc.getElementsByTagName("ConformanceProfile").item(0);

    // Read Profile Meta
    Profile profile = new Profile();
    profile.setMetaData(new ProfileMetaData());
    this.deserializeMetaData(profile, elmConformanceProfile);
    this.deserializeEncodings(profile, elmConformanceProfile);

    // Read Profile Libs
    profile.setSegmentLibrary(new SegmentLibrary());
    profile.setDatatypeLibrary(new DatatypeLibrary());

    profile.setTableLibrary(tableSerializationService.deserializeXMLToTableLibrary(xmlValueSet));

    this.conformanceStatement =
        constraintsSerializationService.deserializeXMLToConformanceStatements(xmlConstraints);
    this.predicates = constraintsSerializationService.deserializeXMLToPredicates(xmlConstraints);
    profile.setConstraintId(this.releaseConstraintId(xmlConstraints));

    this.constructDatatypesMap(
        (Element) elmConformanceProfile.getElementsByTagName("Datatypes").item(0), profile);

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypeService.save(datatypesMap.get(key));
      DatatypeLink link = new DatatypeLink();
      link.setExt(key.replace(d.getName(), ""));
      link.setId(d.getId());
      link.setName(d.getName());

      datatypes.addDatatype(link);
    }
    profile.setDatatypeLibrary(datatypes);

    this.segmentsMap = this.constructSegmentsMap(
        (Element) elmConformanceProfile.getElementsByTagName("Segments").item(0), profile);

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentService.save(segmentsMap.get(key));
      SegmentLink link = new SegmentLink();
      link.setId(s.getId());
      link.setExt(key.replace(s.getName(), ""));
      link.setName(s.getName());
      segments.addSegment(link);
    }
    profile.setSegmentLibrary(segments);

    // Read Profile Messages
    this.deserializeMessages(profile, elmConformanceProfile);

    return profile;
  }

  @Override
  public Profile deserializeXMLToProfile(nu.xom.Document docProfile, nu.xom.Document docValueSet,
      nu.xom.Document docConstraints) {
    return this.deserializeXMLToProfile(docProfile.toXML(), docValueSet.toXML(),
        docConstraints.toXML());
  }

  private void constructDatatypesMap(Element elmDatatypes, Profile profile) {
    this.datatypesMap = new HashMap<String, Datatype>();
    NodeList datatypeNodeList = elmDatatypes.getElementsByTagName("Datatype");

    for (int i = 0; i < datatypeNodeList.getLength(); i++) {
      Element elmDatatype = (Element) datatypeNodeList.item(i);
      if (!datatypesMap.keySet().contains(elmDatatype.getAttribute("ID"))) {
        datatypesMap.put(elmDatatype.getAttribute("ID"),
            this.deserializeDatatype(elmDatatype, profile, elmDatatypes));
      }
    }
  }

  // private Element getDatatypeElement(Element elmDatatypes, String id) {
  // NodeList datatypeNodeList = elmDatatypes
  // .getElementsByTagName("Datatype");
  // for (int i = 0; i < datatypeNodeList.getLength(); i++) {
  // Element elmDatatype = (Element) datatypeNodeList.item(i);
  // if (id.equals(elmDatatype.getAttribute("ID"))) {
  // return elmDatatype;
  // }
  // }
  // return null;
  // }

  private Datatype deserializeDatatype(Element elmDatatype, Profile profile, Element elmDatatypes) {
    String ID = elmDatatype.getAttribute("ID");
    if (!datatypesMap.keySet().contains(ID)) {
      Datatype datatypeObj = new Datatype();
      datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
      if (elmDatatype.getAttribute("Label") != null
          && !elmDatatype.getAttribute("Label").equals("")) {
        datatypeObj.setLabel(elmDatatype.getAttribute("Label"));
      } else {
        datatypeObj.setLabel(elmDatatype.getAttribute("Name"));
      }
      datatypeObj.setName(elmDatatype.getAttribute("Name"));
      datatypeObj.setPredicates(this.findPredicates(this.predicates.getDatatypes(), ID,
          elmDatatype.getAttribute("Name")));
      datatypeObj.setConformanceStatements(this.findConformanceStatement(
          this.conformanceStatement.getDatatypes(), ID, elmDatatype.getAttribute("Name")));

      NodeList nodes = elmDatatype.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++) {
        if (nodes.item(i).getNodeName().equals("Component")) {
          Element elmComponent = (Element) nodes.item(i);
          Component componentObj = new Component();
          componentObj.setName(elmComponent.getAttribute("Name"));
          componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
          componentObj.setPosition(new Integer(elmComponent.getAttribute("Position")));
          // Element elmDt = getDatatypeElement(elmDatatypes,
          // elmComponent.getAttribute("Datatype"));
          // Datatype datatype = this.deserializeDatatype(elmDt,
          // profile, elmDatatypes);
          // TODO
          // componentObj.setDatatype(datatype.getId());
          componentObj.setMinLength(elmComponent.getAttribute("MinLength"));
          if (elmComponent.getAttribute("MaxLength") != null) {
            componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
          }
          if (elmComponent.getAttribute("ConfLength") != null) {
            componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
          }
          // TODO
          if (elmComponent.getAttribute("Binding") != null) {
            // componentObj.setTable(findTableIdByMappingId(elmComponent.getAttribute("Binding"),
            // profile.getTableLibrary()));
          }

          if (elmComponent.getAttribute("Hide") != null
              && elmComponent.getAttribute("Hide").equals("true")) {
            componentObj.setHide(true);
          } else {
            componentObj.setHide(false);
          }

          datatypeObj.addComponent(componentObj);
        }
      }

      // datatypeObj = this.deserializeDatatype(elmDatatype, profile,
      // elmDatatypes);
      datatypesMap.put(ID, datatypeObj);

      return datatypeObj;

    } else {
      return datatypesMap.get(ID);
    }
  }

  private List<ConformanceStatement> findConformanceStatement(Context context, String id,
      String name) {
    Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
    List<ConformanceStatement> result = new ArrayList<ConformanceStatement>();
    for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
      if (byNameOrByID instanceof ByID) {
        ByID byID = (ByID) byNameOrByID;
        if (byID.getByID().equals(id)) {
          for (ConformanceStatement c : byID.getConformanceStatements()) {
            result.add(c);
          }
        } else if (byNameOrByID instanceof ByName) {
          ByName byName = (ByName) byNameOrByID;
          if (byName.getByName().equals(name)) {
            for (ConformanceStatement c : byName.getConformanceStatements()) {
              result.add(c);
            }
          }
        }
      }
    }
    return result;
  }

  private List<Predicate> findPredicates(Context context, String id, String name) {
    Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
    List<Predicate> result = new ArrayList<Predicate>();
    for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
      if (byNameOrByID instanceof ByID) {
        ByID byID = (ByID) byNameOrByID;
        if (byID.getByID().equals(id)) {
          for (Predicate p : byID.getPredicates()) {
            result.add(p);
          }
        }
      } else if (byNameOrByID instanceof ByName) {
        ByName byName = (ByName) byNameOrByID;
        if (byName.getByName().equals(name)) {
          for (Predicate p : byName.getPredicates()) {
            result.add(p);
          }
        }
      }
    }
    return result;
  }

  private Datatype findDatatype(String key, Profile profile) {
    if (datatypesMap.get(key) != null)
      return datatypesMap.get(key);
    throw new IllegalArgumentException("Datatype " + key + " not found");
  }

  private HashMap<String, Segment> constructSegmentsMap(Element elmSegments, Profile profile) {
    HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
    NodeList segmentNodeList = elmSegments.getElementsByTagName("Segment");

    for (int i = 0; i < segmentNodeList.getLength(); i++) {
      Element elmSegment = (Element) segmentNodeList.item(i);
      segmentsMap.put(elmSegment.getAttribute("ID"), this.deserializeSegment(elmSegment, profile));
    }

    return segmentsMap;
  }


  private String findValueSetID(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      String referenceLocation) {
    for (ValueSetOrSingleCodeBinding vsb : valueSetBindings) {
      if (vsb.getLocation().equals(referenceLocation))
        return vsb.getTableId();
    }
    return null;
  }
  
  private void deserializeMetaData(Profile profile, Element elmConformanceProfile) {
    profile.getMetaData().setProfileID(elmConformanceProfile.getAttribute("ID"));
    profile.getMetaData().setType(elmConformanceProfile.getAttribute("Type"));
    profile.getMetaData().setHl7Version(elmConformanceProfile.getAttribute("HL7Version"));
    profile.getMetaData().setSchemaVersion(elmConformanceProfile.getAttribute("SchemaVersion"));

    NodeList nodes = elmConformanceProfile.getElementsByTagName("MetaData");

    Element elmMetaData = (Element) nodes.item(0);
    profile.getMetaData().setName(elmMetaData.getAttribute("Name"));
    profile.getMetaData().setOrgName(elmMetaData.getAttribute("OrgName"));
    profile.getMetaData().setVersion(elmMetaData.getAttribute("Version"));
    profile.getMetaData().setDate(elmMetaData.getAttribute("Date"));
    profile.getMetaData().setSpecificationName(elmMetaData.getAttribute("SpecificationName"));
    profile.getMetaData().setStatus(elmMetaData.getAttribute("Status"));
    profile.getMetaData().setTopics(elmMetaData.getAttribute("Topics"));
  }

  private void deserializeEncodings(Profile profile, Element elmConformanceProfile) {
    NodeList nodes = elmConformanceProfile.getElementsByTagName("Encoding");
    if (nodes != null && nodes.getLength() != 0) {
      Set<String> encodingSet = new HashSet<String>();
      for (int i = 0; i < nodes.getLength(); i++) {
        encodingSet.add(nodes.item(i).getTextContent());
      }
      profile.getMetaData().setEncodings(encodingSet);
    }
  }

  private void deserializeMessages(Profile profile, Element elmConformanceProfile) {
    NodeList nodes = elmConformanceProfile.getElementsByTagName("Message");
    if (nodes != null && nodes.getLength() != 0) {
      Messages messagesObj = new Messages();
      for (int i = 0; i < nodes.getLength(); i++) {
        Message messageObj = new Message();
        Element elmMessage = (Element) nodes.item(i);
        messageObj.setMessageID(elmMessage.getAttribute("ID"));
        messageObj.setIdentifier(elmMessage.getAttribute("Identifier"));
        messageObj.setName(elmMessage.getAttribute("Name"));
        messageObj.setMessageType(elmMessage.getAttribute("Type"));
        messageObj.setEvent(elmMessage.getAttribute("Event"));
        messageObj.setStructID(elmMessage.getAttribute("StructID"));
        messageObj.setDescription(elmMessage.getAttribute("Description"));

        messageObj.setPredicates(this.findPredicates(this.predicates.getMessages(),
            elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));
        messageObj.setConformanceStatements(
            this.findConformanceStatement(this.conformanceStatement.getMessages(),
                elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));

        this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj, elmMessage,
            profile.getSegmentLibrary(), profile.getDatatypeLibrary());
        messagesObj.addMessage(messageObj);
      }
      profile.setMessages(messagesObj);
    }
  }

  private void deserializeSegmentRefOrGroups(Element elmConformanceProfile, Message messageObj,
      Element elmMessage, SegmentLibrary segments, DatatypeLibrary datatypes) {
    List<SegmentRefOrGroup> segmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
    NodeList nodes = elmMessage.getChildNodes();

    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeName().equals("Segment")) {
        this.deserializeSegmentRef(elmConformanceProfile, segmentRefOrGroups,
            (Element) nodes.item(i), segments, datatypes);
      } else if (nodes.item(i).getNodeName().equals("Group")) {
        this.deserializeGroup(elmConformanceProfile, segmentRefOrGroups, (Element) nodes.item(i),
            segments, datatypes);
      }
    }

    messageObj.setChildren(segmentRefOrGroups);

  }

  private void deserializeSegmentRef(Element elmConformanceProfile,
      List<SegmentRefOrGroup> segmentRefOrGroups, Element segmentElm, SegmentLibrary segments,
      DatatypeLibrary datatypes) {
    SegmentRef segmentRefObj = new SegmentRef();
    segmentRefObj.setMax(segmentElm.getAttribute("Max"));
    segmentRefObj.setMin(new Integer(segmentElm.getAttribute("Min")));
    segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
    // segmentRefObj.setRef(this.segmentsMap.get(segmentElm.getAttribute("Ref")).getId());
    segmentRefOrGroups.add(segmentRefObj);
  }

  private Segment deserializeSegment(Element segmentElm, Profile profile) {
    Segment segmentObj = new Segment();
    segmentObj.setDescription(segmentElm.getAttribute("Description"));
    if (segmentElm.getAttribute("Label") != null && !segmentElm.getAttribute("Label").equals("")) {
      segmentObj.setLabel(segmentElm.getAttribute("Label"));
    } else {
      segmentObj.setLabel(segmentElm.getAttribute("Name"));
    }
    segmentObj.setName(segmentElm.getAttribute("Name"));
    segmentObj.setPredicates(this.findPredicates(this.predicates.getSegments(),
        segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));
    segmentObj.setConformanceStatements(
        this.findConformanceStatement(this.conformanceStatement.getSegments(),
            segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));

    NodeList dynamicMapping = segmentElm.getElementsByTagName("Mapping");
    DynamicMapping dynamicMappingObj = null;
    if (dynamicMapping.getLength() > 0) {
      dynamicMappingObj = new DynamicMapping();
    }

    for (int i = 0; i < dynamicMapping.getLength(); i++) {
      Element mappingElm = (Element) dynamicMapping.item(i);
      Mapping mappingObj = new Mapping();
      mappingObj.setPosition(Integer.parseInt(mappingElm.getAttribute("Position")));
      mappingObj.setReference(Integer.parseInt(mappingElm.getAttribute("Reference")));
      NodeList cases = mappingElm.getElementsByTagName("Case");

      for (int j = 0; j < cases.getLength(); j++) {
        Element caseElm = (Element) cases.item(j);
        Case caseObj = new Case();
        caseObj.setValue(caseElm.getAttribute("Value"));
        caseObj.setDatatype(this.findDatatype(caseElm.getAttribute("Datatype"), profile).getId());

        mappingObj.addCase(caseObj);

      }

      dynamicMappingObj.addMapping(mappingObj);

    }

    if (dynamicMappingObj != null)
      segmentObj.setDynamicMapping(dynamicMappingObj);

    NodeList fields = segmentElm.getElementsByTagName("Field");
    for (int i = 0; i < fields.getLength(); i++) {
      Element fieldElm = (Element) fields.item(i);
      segmentObj.addField(
          this.deserializeField(fieldElm, segmentObj, profile, segmentElm.getAttribute("ID"), i));
    }
    return segmentObj;
  }

  private Field deserializeField(Element fieldElm, Segment segment, Profile profile,
      String segmentId, int position) {
    Field fieldObj = new Field();

    fieldObj.setName(fieldElm.getAttribute("Name"));
    fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
    // fieldObj.setDatatype(this.findDatatype(fieldElm.getAttribute("Datatype"),
    // profile).getId());
    fieldObj.setMinLength(fieldElm.getAttribute("MinLength"));
    fieldObj.setPosition(new Integer(fieldElm.getAttribute("Position")));
    if (fieldElm.getAttribute("MaxLength") != null) {
      fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
    }
    if (fieldElm.getAttribute("ConfLength") != null) {
      fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
    }
    // if (fieldElm.getAttribute("Binding") != null) {
    // fieldObj.setTable(findTableIdByMappingId(fieldElm.getAttribute("Binding"),
    // profile.getTableLibrary()));
    // }
    // if (fieldElm.getAttribute("BindingStrength") != null) {
    // fieldObj.setBindingStrength(fieldElm.getAttribute("BindingStrength"));
    // }
    //
    // if (fieldElm.getAttribute("BindingLocation") != null) {
    // fieldObj.setBindingLocation(fieldElm.getAttribute("BindingLocation"));
    // }
    if (fieldElm.getAttribute("Hide") != null && fieldElm.getAttribute("Hide").equals("true")) {
      fieldObj.setHide(true);
    } else {
      fieldObj.setHide(false);
    }
    fieldObj.setMin(new Integer(fieldElm.getAttribute("Min")));
    fieldObj.setMax(fieldElm.getAttribute("Max"));
    if (fieldElm.getAttribute("ItemNo") != null) {
      fieldObj.setItemNo(fieldElm.getAttribute("ItemNo"));
    }
    return fieldObj;
  }

  private void deserializeGroup(Element elmConformanceProfile,
      List<SegmentRefOrGroup> segmentRefOrGroups, Element groupElm, SegmentLibrary segments,
      DatatypeLibrary datatypes) {
    Group groupObj = new Group();
    groupObj.setMax(groupElm.getAttribute("Max"));
    groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
    groupObj.setName(groupElm.getAttribute("Name"));
    groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));

    List<SegmentRefOrGroup> childSegmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();

    NodeList nodes = groupElm.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeName().equals("Segment")) {
        this.deserializeSegmentRef(elmConformanceProfile, childSegmentRefOrGroups,
            (Element) nodes.item(i), segments, datatypes);
      } else if (nodes.item(i).getNodeName().equals("Group")) {
        this.deserializeGroup(elmConformanceProfile, childSegmentRefOrGroups,
            (Element) nodes.item(i), segments, datatypes);
      }
    }

    groupObj.setChildren(childSegmentRefOrGroups);

    segmentRefOrGroups.add(groupObj);
  }

  private Document stringToDom(String xmlSource) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setIgnoringComments(false);
    factory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
      return builder.parse(new InputSource(new StringReader(xmlSource)));
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public HashMap<String, Datatype> getDatatypesMap() {
    return datatypesMap;
  }

  public void setDatatypesMap(HashMap<String, Datatype> datatypesMap) {
    this.datatypesMap = datatypesMap;
  }

  public HashMap<String, Segment> getSegmentsMap() {
    return segmentsMap;
  }

  public void setSegmentsMap(HashMap<String, Segment> segmentsMap) {
    this.segmentsMap = segmentsMap;
  }
  
  @Override
  public InputStream serializeProfileGazelleToZip(Profile original, String[] ids, DocumentMetaData metadata)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException {
    Profile filteredProfile = new Profile();

    HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
    HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    HashMap<String, Table> tablesMap = new HashMap<String, Table>();

    filteredProfile.setBaseId(original.getBaseId());
    filteredProfile.setChanges(original.getChanges());
    filteredProfile.setComment(original.getComment());
    filteredProfile.setConstraintId(original.getConstraintId());
    filteredProfile.setScope(original.getScope());
    filteredProfile.setSectionContents(original.getSectionContents());
    filteredProfile.setSectionDescription(original.getSectionDescription());
    filteredProfile.setSectionPosition(original.getSectionPosition());
    filteredProfile.setSectionTitle(original.getSectionTitle());
    filteredProfile.setSourceId(original.getSourceId());
    filteredProfile.setType(original.getType());
    filteredProfile.setUsageNote(original.getUsageNote());
    filteredProfile.setMetaData(original.getMetaData());

    for (SegmentLink sl : original.getSegmentLibrary().getChildren()) {
      if (sl != null) {
        Segment s = segmentService.findById(sl.getId());
        if (s != null) {
          segmentsMap.put(s.getId(), s);
        }
      }
    }

    for (DatatypeLink dl : original.getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : original.getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {
          tablesMap.put(t.getId(), t);
        }
      }
    }

    Messages messages = new Messages();
    for (Message m : original.getMessages().getChildren()) {
      if (Arrays.asList(ids).contains(m.getId())) {
        if (m.getMessageID() == null)
          m.setMessageID(UUID.randomUUID().toString());
        messages.addMessage(m);
        for (SegmentRefOrGroup seog : m.getChildren()) {
          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
        }
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsGazelleFormatForSelectedMessages(filteredProfile, metadata, segmentsMap, datatypesMap, tablesMap);
  }

  @Override
  public InputStream serializeCompositeProfileDisplayToZip(IGDocument doc, String[] ids)
      throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException {
    Map<String, Segment> segmentsMap = new HashMap<String, Segment>();
    Map<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    Map<String, Table> tablesMap = new HashMap<String, Table>();

    for (SegmentLink sl : doc.getProfile().getSegmentLibrary().getChildren()) {
      if (sl != null) {
        Segment s = segmentService.findById(sl.getId());
        if (s != null) {
          segmentsMap.put(s.getId(), s);
        }
      }
    }

    for (DatatypeLink dl : doc.getProfile().getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : doc.getProfile().getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {
          tablesMap.put(t.getId(), t);
        }
      }
    }



    Profile filteredProfile = new Profile();
    filteredProfile.setBaseId(doc.getProfile().getBaseId());
    filteredProfile.setChanges(doc.getProfile().getChanges());
    filteredProfile.setComment(doc.getProfile().getComment());
    filteredProfile.setConstraintId(doc.getProfile().getConstraintId());
    filteredProfile.setScope(doc.getProfile().getScope());
    filteredProfile.setSectionContents(doc.getProfile().getSectionContents());
    filteredProfile.setSectionDescription(doc.getProfile().getSectionDescription());
    filteredProfile.setSectionPosition(doc.getProfile().getSectionPosition());
    filteredProfile.setSectionTitle(doc.getProfile().getSectionTitle());
    filteredProfile.setSourceId(doc.getProfile().getSourceId());
    filteredProfile.setType(doc.getProfile().getType());
    filteredProfile.setUsageNote(doc.getProfile().getUsageNote());
    filteredProfile.setMetaData(doc.getProfile().getMetaData());

    Messages messages = new Messages();
    for (CompositeProfileStructure cps : doc.getProfile().getCompositeProfiles().getChildren()) {
      if (Arrays.asList(ids).contains(cps.getId())) {
        CompositeProfile cp = compositeProfileService.buildCompositeProfile(cps);
        segmentsMap.putAll(cp.getSegmentsMap());
        datatypesMap.putAll(cp.getDatatypesMap());
        messages.addMessage(cp.convertMessage());
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsDisplayFormatForSelectedMessages(filteredProfile, doc.getMetaData(), segmentsMap, datatypesMap, tablesMap);
  }
  
  @Override
  public InputStream serializeProfileToZip(Profile original, String[] ids,
      DocumentMetaData metadata) throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException {
    Profile filteredProfile = new Profile();

    HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
    HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    HashMap<String, Table> tablesMap = new HashMap<String, Table>();

    filteredProfile.setBaseId(original.getBaseId());
    filteredProfile.setChanges(original.getChanges());
    filteredProfile.setComment(original.getComment());
    filteredProfile.setConstraintId(original.getConstraintId());
    filteredProfile.setScope(original.getScope());
    filteredProfile.setSectionContents(original.getSectionContents());
    filteredProfile.setSectionDescription(original.getSectionDescription());
    filteredProfile.setSectionPosition(original.getSectionPosition());
    filteredProfile.setSectionTitle(original.getSectionTitle());
    filteredProfile.setSourceId(original.getSourceId());
    filteredProfile.setType(original.getType());
    filteredProfile.setUsageNote(original.getUsageNote());
    filteredProfile.setMetaData(original.getMetaData());

//    for (SegmentLink sl : original.getSegmentLibrary().getChildren()) {
//      if (sl != null) {
//        Segment s = segmentService.findById(sl.getId());
//        if (s != null) {
//          segmentsMap.put(s.getId(), s);
//        }
//      }
//    }

    for (DatatypeLink dl : original.getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : original.getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {    
          /*
           * Temporary script Begin
           * This script is to hack Codes for external-user ValueSet. It is just temporary script till implementation for external valueset validation
           */

          if(t != null && t.getSourceType().equals(SourceType.EXTERNAL) && t.getCreatedFrom() != null && !t.getCreatedFrom().isEmpty()){
            Table origin = tableService.findById(t.getCreatedFrom());
            if(origin != null && origin.getCodes() != null && origin.getCodes().size() > 0){
              t.setCodes(origin.getCodes());
            }
          }
          
          /*
           * Temporary script End
           */  
          tablesMap.put(t.getId(), t);
        }
      }
    }

    Messages messages = new Messages();
    for (Message m : original.getMessages().getChildren()) {
      if (Arrays.asList(ids).contains(m.getId())) {
        if (m.getMessageID() == null)
          m.setMessageID(UUID.randomUUID().toString());
        messages.addMessage(m);
        for (SegmentRefOrGroup seog : m.getChildren()) {
          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
        }
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsValidationFormatForSelectedMessages(filteredProfile,
        metadata, segmentsMap, datatypesMap, tablesMap);
  }

  @Override
  public InputStream serializeProfileDisplayToZip(Profile original, String[] ids,
      DocumentMetaData metadata) throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException {

    Profile filteredProfile = new Profile();

    HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
    HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    HashMap<String, Table> tablesMap = new HashMap<String, Table>();

    filteredProfile.setBaseId(original.getBaseId());
    filteredProfile.setChanges(original.getChanges());
    filteredProfile.setComment(original.getComment());
    filteredProfile.setConstraintId(original.getConstraintId());
    filteredProfile.setScope(original.getScope());
    filteredProfile.setSectionContents(original.getSectionContents());
    filteredProfile.setSectionDescription(original.getSectionDescription());
    filteredProfile.setSectionPosition(original.getSectionPosition());
    filteredProfile.setSectionTitle(original.getSectionTitle());
    filteredProfile.setSourceId(original.getSourceId());
    filteredProfile.setType(original.getType());
    filteredProfile.setUsageNote(original.getUsageNote());
    filteredProfile.setMetaData(original.getMetaData());

    for (SegmentLink sl : original.getSegmentLibrary().getChildren()) {
      if (sl != null) {
        Segment s = segmentService.findById(sl.getId());
        if (s != null) {
          segmentsMap.put(s.getId(), s);
        }
      }
    }

    for (DatatypeLink dl : original.getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : original.getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {
          tablesMap.put(t.getId(), t);
        }
      }
    }

    Messages messages = new Messages();
    for (Message m : original.getMessages().getChildren()) {
      if (Arrays.asList(ids).contains(m.getId())) {
        if (m.getMessageID() == null)
          m.setMessageID(UUID.randomUUID().toString());
        messages.addMessage(m);
        for (SegmentRefOrGroup seog : m.getChildren()) {
          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
        }
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsDisplayFormatForSelectedMessages(filteredProfile, metadata, segmentsMap, datatypesMap, tablesMap);
  }
  
  private void visit(SegmentRefOrGroup seog, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    if (seog instanceof SegmentRef) {
      SegmentRef sr = (SegmentRef) seog;
//      Segment s = segmentsMap.get(sr.getRef().getId());
      Segment s = segmentService.findById(sr.getRef().getId());
      segmentsMap.put(s.getId(), s);

      if (s.getName().equals("OBX") || s.getName().equals("MFA") || s.getName().equals("MFE")) {
        String reference = null;
        String referenceTableId = null;

        if (s.getName().equals("OBX")) {
          reference = "2";
        }

        if (s.getName().equals("MFA")) {
          reference = "6";
        }

        if (s.getName().equals("MFE")) {
          reference = "5";
        }

        referenceTableId = this.findValueSetID(s.getValueSetBindings(), reference);

        if (referenceTableId != null) {
          Table table = tablesMap.get(referenceTableId);
          if (table != null) {
            if (table.getHl7Version() == null)
              table.setHl7Version(s.getHl7Version());
            for (Code c : table.getCodes()) {
              System.out.println("Code : " + c);
              if (c.getValue() != null && table.getHl7Version() != null) {
                Datatype d = this.findDatatypeByNameAndVesionAndScope(c.getValue(),
                    table.getHl7Version(), "HL7STANDARD", datatypesMap);
                if (d == null) {
                  d = datatypeService.findByNameAndVesionAndScope(c.getValue(),
                      table.getHl7Version(), "HL7STANDARD");
                  if (d != null) {
                    this.addDatatypeForDM(d, datatypesMap, tablesMap);
                  } else {
                    System.out.println("--------NOT FOUND---------");
                    System.out.println(c.getValue());
                    System.out.println(table.getHl7Version());
                  }
                } else {
                  System.out.println("--------FOUND---------");
                  System.out.println(c.getValue());
                  System.out.println(table.getHl7Version());
                }
              }
            }
          }
        }

        System.out.println("--------- DM END---------");
      }
    } else {
      Group g = (Group) seog;
      for (SegmentRefOrGroup child : g.getChildren()) {
        this.visit(child, segmentsMap, datatypesMap, tablesMap);
      }
    }
  }

  private Datatype findDatatypeByNameAndVesionAndScope(String name, String hl7Version, String scope,
      Map<String, Datatype> datatypesMap) {
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      if (d != null) {
        if (d.getName().equals(name) && d.getHl7Version().equals(hl7Version)
            && d.getScope().toString().equals(scope))
          return d;
      }
    }
    return null;
  }

  private void addDatatypeForDM(Datatype d, Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    if (d != null) {
      int randumNum = new SecureRandom().nextInt(100000);
      d.setExt("ForDM" + randumNum);
      datatypesMap.put(d.getId(), d);
      for (Component c : d.getComponents()) {
        this.addDatatypeForDM(datatypeService.findById(c.getDatatype().getId()), datatypesMap, tablesMap);
      }
      
      for(ValueSetOrSingleCodeBinding binding:d.getValueSetBindings()){
        if(binding instanceof ValueSetBinding){
          Table t = tableService.findById(binding.getTableId());
          if(t != null){
            tablesMap.put(t.getId(), t);
          }
        }
      }
    } else {
      log.error("datatypelink is missing!");
    }
  }

  private String releaseConstraintId(String xmlConstraints) {
    if (xmlConstraints != null) {
      Document conformanceContextDoc = this.stringToDom(xmlConstraints);
      Element elmConformanceContext =
          (Element) conformanceContextDoc.getElementsByTagName("ConformanceContext").item(0);
      return elmConformanceContext.getAttribute("UUID");
    }
    return null;
  }

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }


  @Override
  public InputStream serializeCompositeProfileToZip(IGDocument doc, String[] ids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException {
    Map<String, Segment> segmentsMap = new HashMap<String, Segment>();
    Map<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    Map<String, Table> tablesMap = new HashMap<String, Table>();

    for (SegmentLink sl : doc.getProfile().getSegmentLibrary().getChildren()) {
      if (sl != null) {
        Segment s = segmentService.findById(sl.getId());
        if (s != null) {
          segmentsMap.put(s.getId(), s);
        }
      }
    }

    for (DatatypeLink dl : doc.getProfile().getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : doc.getProfile().getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {
          
          /*
           * Temporary script Begin
           * This script is to hack Codes for external-user ValueSet. It is just temporary script till implementation for external valueset validation
           */

          if(t != null && t.getSourceType().equals(SourceType.EXTERNAL) && t.getCreatedFrom() != null && !t.getCreatedFrom().isEmpty()){
            Table origin = tableService.findById(t.getCreatedFrom());
            if(origin != null && origin.getCodes() != null && origin.getCodes().size() > 0){
              t.setCodes(origin.getCodes());
            }
          }
          
          /*
           * Temporary script End
           */  
          
          
          tablesMap.put(t.getId(), t);
        }
      }
    }



    Profile filteredProfile = new Profile();
    filteredProfile.setBaseId(doc.getProfile().getBaseId());
    filteredProfile.setChanges(doc.getProfile().getChanges());
    filteredProfile.setComment(doc.getProfile().getComment());
    filteredProfile.setConstraintId(doc.getProfile().getConstraintId());
    filteredProfile.setScope(doc.getProfile().getScope());
    filteredProfile.setSectionContents(doc.getProfile().getSectionContents());
    filteredProfile.setSectionDescription(doc.getProfile().getSectionDescription());
    filteredProfile.setSectionPosition(doc.getProfile().getSectionPosition());
    filteredProfile.setSectionTitle(doc.getProfile().getSectionTitle());
    filteredProfile.setSourceId(doc.getProfile().getSourceId());
    filteredProfile.setType(doc.getProfile().getType());
    filteredProfile.setUsageNote(doc.getProfile().getUsageNote());
    filteredProfile.setMetaData(doc.getProfile().getMetaData());

    Messages messages = new Messages();
    for (CompositeProfileStructure cps : doc.getProfile().getCompositeProfiles().getChildren()) {
      if (Arrays.asList(ids).contains(cps.getId())) {
        CompositeProfile cp = compositeProfileService.buildCompositeProfile(cps);
        segmentsMap.putAll(cp.getSegmentsMap());
        datatypesMap.putAll(cp.getDatatypesMap());
        messages.addMessage(cp.convertMessage());
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsValidationFormatForSelectedMessages(filteredProfile,
        doc.getMetaData(), segmentsMap, datatypesMap, tablesMap);
  }

  @Override
  public InputStream serializeCompositeProfileGazelleToZip(IGDocument doc, String[] ids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException {
    HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
    HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
    HashMap<String, Table> tablesMap = new HashMap<String, Table>();

    for (SegmentLink sl : doc.getProfile().getSegmentLibrary().getChildren()) {
      if (sl != null) {
        Segment s = segmentService.findById(sl.getId());
        if (s != null) {
          segmentsMap.put(s.getId(), s);
        }
      }
    }

    for (DatatypeLink dl : doc.getProfile().getDatatypeLibrary().getChildren()) {
      if (dl != null) {
        Datatype d = datatypeService.findById(dl.getId());
        if (d != null) {
          datatypesMap.put(d.getId(), d);
        }
      }
    }

    for (TableLink tl : doc.getProfile().getTableLibrary().getChildren()) {
      if (tl != null) {
        Table t = tableService.findById(tl.getId());
        if (t != null) {
          tablesMap.put(t.getId(), t);
        }
      }
    }



    Profile filteredProfile = new Profile();
    filteredProfile.setBaseId(doc.getProfile().getBaseId());
    filteredProfile.setChanges(doc.getProfile().getChanges());
    filteredProfile.setComment(doc.getProfile().getComment());
    filteredProfile.setConstraintId(doc.getProfile().getConstraintId());
    filteredProfile.setScope(doc.getProfile().getScope());
    filteredProfile.setSectionContents(doc.getProfile().getSectionContents());
    filteredProfile.setSectionDescription(doc.getProfile().getSectionDescription());
    filteredProfile.setSectionPosition(doc.getProfile().getSectionPosition());
    filteredProfile.setSectionTitle(doc.getProfile().getSectionTitle());
    filteredProfile.setSourceId(doc.getProfile().getSourceId());
    filteredProfile.setType(doc.getProfile().getType());
    filteredProfile.setUsageNote(doc.getProfile().getUsageNote());
    filteredProfile.setMetaData(doc.getProfile().getMetaData());

    Messages messages = new Messages();
    for (CompositeProfileStructure cps : doc.getProfile().getCompositeProfiles().getChildren()) {
      if (Arrays.asList(ids).contains(cps.getId())) {
        CompositeProfile cp = compositeProfileService.buildCompositeProfile(cps);
        segmentsMap.putAll(cp.getSegmentsMap());
        datatypesMap.putAll(cp.getDatatypesMap());
        messages.addMessage(cp.convertMessage());
      }
    }

    SegmentLibrary segments = new SegmentLibrary();
    for (String key : segmentsMap.keySet()) {
      segments.addSegment(segmentsMap.get(key));
    }

    DatatypeLibrary datatypes = new DatatypeLibrary();
    for (String key : datatypesMap.keySet()) {
      datatypes.addDatatype(datatypesMap.get(key));
    }

    TableLibrary tables = new TableLibrary();
    for (String key : tablesMap.keySet()) {
      tables.addTable(tablesMap.get(key));
    }

    filteredProfile.setDatatypeLibrary(datatypes);
    filteredProfile.setSegmentLibrary(segments);
    filteredProfile.setMessages(messages);
    filteredProfile.setTableLibrary(tables);

    return new XMLExportTool().exportXMLAsGazelleFormatForSelectedMessages(filteredProfile, doc.getMetaData(), segmentsMap, datatypesMap, tablesMap);
  }

}
