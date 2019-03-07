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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBindingStrength;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VariesMapItem;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.MessageExportInfo;

@Service
public class ProfileSerializationImpl implements ProfileSerialization {
  private Logger log = LoggerFactory.getLogger(ProfileSerializationImpl.class);

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  @Autowired
  private TableLibraryService tableLibraryService;

  @Autowired
  private TableSerialization tableSerializationService;

  @Autowired
  private DatatypeLibraryService datatypeLibraryService;

  @Autowired
  private SegmentLibraryService segmentLibraryService;

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
    TableLibrary tables = tableSerializationService.deserializeXMLToTableLibrary(xmlValueSet,profile.getMetaData().getHl7Version());
    tables.setMetaData(new TableLibraryMetaData());
    profile.setTableLibrary(tables);

    this.conformanceStatement = constraintsSerializationService.deserializeXMLToConformanceStatements(xmlConstraints);
    this.predicates = constraintsSerializationService.deserializeXMLToPredicates(xmlConstraints);
    profile.setConstraintId(this.releaseConstraintId(xmlConstraints));

    this.constructDatatypesMap((Element) elmConformanceProfile.getElementsByTagName("Datatypes").item(0), profile);

    DatatypeLibrary datatypes = new DatatypeLibrary();
    datatypes.setMetaData(new DatatypeLibraryMetaData());
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      DatatypeLink link = new DatatypeLink();
      link.setExt(key.replace(d.getName(), ""));
      link.setId(d.getId());
      link.setName(d.getName());
      datatypes.addDatatype(link);
    }
    profile.setDatatypeLibrary(datatypes);

    this.segmentsMap = this.constructSegmentsMap((Element) elmConformanceProfile.getElementsByTagName("Segments").item(0), profile);

    SegmentLibrary segments = new SegmentLibrary();
    segments.setMetaData(new SegmentLibraryMetaData());
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      SegmentLink link = new SegmentLink();
      link.setId(s.getId());
      link.setExt(key.replace(s.getName(), ""));
      link.setName(s.getName());
      segments.addSegment(link);
    }
    profile.setSegmentLibrary(segments);

    // Read Profile Messages
    this.deserializeMessages(profile, elmConformanceProfile);

    
    profile.getSegmentLibrary().setSectionTitle("SegmentLib");
    profile.getSegmentLibrary().setSectionContents("Contents");
    profile.getSegmentLibrary().setSectionDescription("DESC");
    profile.getSegmentLibrary().setSectionPosition(4);
    profile.getSegmentLibrary().setScope(SCOPE.USER);
    profile.getDatatypeLibrary().setSectionTitle("DatatypeLib");
    profile.getDatatypeLibrary().setSectionContents("Contents");
    profile.getDatatypeLibrary().setSectionDescription("DESC");
    profile.getDatatypeLibrary().setSectionPosition(5);
    profile.getDatatypeLibrary().setScope(SCOPE.USER);
    profile.getTableLibrary().setSectionTitle("TableLib");
    profile.getTableLibrary().setSectionContents("Contents");
    profile.getTableLibrary().setSectionDescription("DESC");
    profile.getTableLibrary().setSectionPosition(6);
    profile.getTableLibrary().setScope(SCOPE.USER);
    
    
    this.tableLibraryService.save(profile.getTableLibrary());
    this.datatypeLibraryService.save(profile.getDatatypeLibrary());
    this.segmentLibraryService.save(profile.getSegmentLibrary());
    
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
        String id = elmDatatype.getAttribute("ID");
        String name = elmDatatype.getAttribute("Name");

        if (!id.equals(name)) {
          if (!datatypesMap.containsKey(id))
            datatypesMap.put(elmDatatype.getAttribute("ID"), this.deserializeDatatype(elmDatatype, profile, elmDatatypes));
        } else {
          Datatype d = this.datatypeService.findByNameAndVesionAndScope(name, profile.getMetaData().getHl7Version(), "HL7STANDARD");
          
          
          for(ValueSetOrSingleCodeBinding vsosc : d.getValueSetBindings()){
            Table t = this.tableService.findById(vsosc.getTableId());
            if(t != null){
              TableLink tl = profile.getTableLibrary().findOneTableByBindingIdentifier(t.getBindingIdentifier());
              
              if(tl == null) {
                TableLink newTableLink = new TableLink(t.getId(), t.getBindingIdentifier());
                profile.getTableLibrary().addTable(newTableLink);              
              }
            }
          }
          
          for(Component c:d.getComponents()){
            Datatype childD = this.datatypeService.findById(c.getDatatype().getId());
            datatypesMap.put(childD.getId(), childD);
          }
          
          datatypesMap.put(elmDatatype.getAttribute("ID"), this.datatypeService.findByNameAndVesionAndScope(name, profile.getMetaData().getHl7Version(), "HL7STANDARD"));
        }

      }
    }
  }

  private Element getDatatypeElement(Element elmDatatypes, String id) {
    NodeList datatypeNodeList = elmDatatypes.getElementsByTagName("Datatype");
    for (int i = 0; i < datatypeNodeList.getLength(); i++) {
      Element elmDatatype = (Element) datatypeNodeList.item(i);
      if (id.equals(elmDatatype.getAttribute("ID"))) {
        return elmDatatype;
      }
    }
    return null;
  }

  private Datatype deserializeDatatype(Element elmDatatype, Profile profile, Element elmDatatypes) {
    String ID = elmDatatype.getAttribute("ID");
    String name = elmDatatype.getAttribute("Name");

    if (datatypesMap.keySet().contains(ID))
      return datatypesMap.get(ID);
    if (ID.equals(name)) {
      Datatype dt = this.datatypeService.findOneByNameAndVersionAndScope(ID,
          profile.getMetaData().getHl7Version(), "HL7STANDARD");
      datatypesMap.put(ID, dt);
      return dt;
    }


    Datatype datatypeObj = new Datatype();
    datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
    if (elmDatatype.getAttribute("Label") != null
        && !elmDatatype.getAttribute("Label").equals("")) {
      datatypeObj.setLabel(elmDatatype.getAttribute("Label"));
      datatypeObj.setExt(
          elmDatatype.getAttribute("Label").replace(elmDatatype.getAttribute("Name") + "_", ""));
    } else {
      datatypeObj.setLabel(elmDatatype.getAttribute("Name"));
    }
    datatypeObj.setName(elmDatatype.getAttribute("Name"));
    datatypeObj.setPredicates(
        this.findPredicates(this.predicates.getDatatypes(), ID, elmDatatype.getAttribute("Name")));
    datatypeObj.setConformanceStatements(this.findConformanceStatement(
        this.conformanceStatement.getDatatypes(), ID, elmDatatype.getAttribute("Name")));
    datatypeObj.setScope(SCOPE.USER);
    datatypeObj.setHl7Version(profile.getMetaData().getHl7Version());

    NodeList nodes = elmDatatype.getElementsByTagName("Component");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elmComponent = (Element) nodes.item(i);
      Component componentObj = new Component();
      componentObj.setName(elmComponent.getAttribute("Name"));
      componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
      componentObj.setPosition(i + 1);

      Element elmDt = this.getDatatypeElement(elmDatatypes, elmComponent.getAttribute("Datatype"));

      Datatype dt = this.deserializeDatatype(elmDt, profile, elmDatatypes);
      componentObj.setDatatype(new DatatypeLink(dt.getId(), dt.getName(), dt.getExt()));
      componentObj.setMinLength(elmComponent.getAttribute("MinLength"));
      if (elmComponent.getAttribute("MaxLength") != null) {
        componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
      }
      if (elmComponent.getAttribute("ConfLength") != null) {
        componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
      }

      ValueSetBinding valueSetBinding = new ValueSetBinding();
      if (elmComponent.getAttribute("BindingStrength") != null) {
        if (elmComponent.getAttribute("BindingStrength").equals("R")) {
          valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
        } else if (elmComponent.getAttribute("BindingStrength").equals("S")) {
          valueSetBinding.setBindingStrength(ValueSetBindingStrength.S);
        } else if (elmComponent.getAttribute("BindingStrength").equals("U")) {
          valueSetBinding.setBindingStrength(ValueSetBindingStrength.U);
        } else {
          valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
        }
      } else {
        valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
      }

      if (elmComponent.getAttribute("BindingLocation") != null) {
        valueSetBinding.setBindingLocation(elmComponent.getAttribute("BindingLocation"));
      }

      if (elmComponent.getAttribute("Binding") != null
          && !elmComponent.getAttribute("Binding").equals("")) {
        String bindingIdentifier = elmComponent.getAttribute("Binding");
        if (bindingIdentifier.startsWith("HL7") && bindingIdentifier.length() == 7) {
          valueSetBinding.setTableId(profile.getTableLibrary()
              .findOneTableByBindingIdentifier(bindingIdentifier.replace("HL7", "")).getId());
          valueSetBinding.setLocation(componentObj.getPosition() + "");
          datatypeObj.addValueSetBinding(valueSetBinding);
        } else {
          valueSetBinding.setTableId(
              profile.getTableLibrary().findOneTableByBindingIdentifier(bindingIdentifier).getId());
          valueSetBinding.setLocation(componentObj.getPosition() + "");
          datatypeObj.addValueSetBinding(valueSetBinding);
        }


      }

      if (elmComponent.getAttribute("Hide") != null
          && elmComponent.getAttribute("Hide").equals("true")) {
        componentObj.setHide(true);
      } else {
        componentObj.setHide(false);
      }

      datatypeObj.addComponent(componentObj);
    }
    datatypeService.save(datatypeObj);
    datatypesMap.put(ID, datatypeObj);

    return datatypeObj;

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
      String id = elmSegment.getAttribute("ID");
      String name = elmSegment.getAttribute("Name");

      Segment s = this.deserializeSegment(elmSegment, profile);
      if (!id.equals(name)) {
        s.setHl7Version(profile.getMetaData().getHl7Version());
        segmentService.save(s);
      }
      segmentsMap.put(elmSegment.getAttribute("ID"), s);
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
        messageObj.setHl7Version(profile.getMetaData().getHl7Version());
        messageObj.setPredicates(this.findPredicates(this.predicates.getMessages(),
            elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));
        messageObj.setConformanceStatements(
            this.findConformanceStatement(this.conformanceStatement.getMessages(),
                elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));

        this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj, elmMessage,
            profile.getSegmentLibrary(), profile.getDatatypeLibrary());

        messageService.save(messageObj);
        messagesObj.addMessage(messageObj);
      }
      messagesObj.setSectionTitle("Messages");
      messagesObj.setSectionContents("Contents");
      messagesObj.setSectionDescription("DESC");
      messagesObj.setSectionPosition(2);
      profile.setMessages(messagesObj);
    }
  }

  private void deserializeSegmentRefOrGroups(Element elmConformanceProfile, Message messageObj,
      Element elmMessage, SegmentLibrary segments, DatatypeLibrary datatypes) {
    List<SegmentRefOrGroup> segmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
    NodeList nodes = elmMessage.getChildNodes();

    int index = 0;
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeName().equals("Segment")) {
        index = index + 1;
        this.deserializeSegmentRef(elmConformanceProfile, segmentRefOrGroups, (Element) nodes.item(i), segments, datatypes, index);
      } else if (nodes.item(i).getNodeName().equals("Group")) {
        index = index + 1;
        this.deserializeGroup(elmConformanceProfile, segmentRefOrGroups, (Element) nodes.item(i), segments, datatypes, index);
      }
    }

    messageObj.setChildren(segmentRefOrGroups);

  }

  private void deserializeSegmentRef(Element elmConformanceProfile,
      List<SegmentRefOrGroup> segmentRefOrGroups, Element segmentElm, SegmentLibrary segments,
      DatatypeLibrary datatypes, int position) {
    SegmentRef segmentRefObj = new SegmentRef();
    segmentRefObj.setMax(segmentElm.getAttribute("Max"));
    segmentRefObj.setMin(new Integer(segmentElm.getAttribute("Min")));
    segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
    Segment seg = this.segmentsMap.get(segmentElm.getAttribute("Ref"));
    segmentRefObj.setRef(new SegmentLink(seg.getId(), seg.getName(), seg.getExt()));
    segmentRefObj.setPosition(position);
    segmentRefOrGroups.add(segmentRefObj);
  }

  private Segment deserializeSegment(Element segmentElm, Profile profile) {
    String id = segmentElm.getAttribute("ID");
    String name = segmentElm.getAttribute("Name");

    if (id.equals(name))
      return this.segmentService.findByNameAndVersionAndScope(name,
          profile.getMetaData().getHl7Version(), "HL7STANDARD");

    Segment segmentObj = new Segment();
    segmentObj.setDescription(segmentElm.getAttribute("Description"));
    if (segmentElm.getAttribute("Label") != null && !segmentElm.getAttribute("Label").equals("")) {
      segmentObj.setLabel(segmentElm.getAttribute("Label"));
      segmentObj.setExt(
          segmentElm.getAttribute("Label").replace(segmentElm.getAttribute("Name") + "_", ""));
    } else {
      segmentObj.setLabel(segmentElm.getAttribute("Name"));
    }
    segmentObj.setName(segmentElm.getAttribute("Name"));
    segmentObj.setPredicates(this.findPredicates(this.predicates.getSegments(),
        segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));
    segmentObj.setConformanceStatements(
        this.findConformanceStatement(this.conformanceStatement.getSegments(),
            segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));
    segmentObj.setScope(SCOPE.USER);
    segmentObj.setHl7Version(profile.getMetaData().getHl7Version());


    NodeList dynamicMapping = segmentElm.getElementsByTagName("Mapping");
    DynamicMappingDefinition dynamicMappingDefinition = null;
    if (dynamicMapping.getLength() > 0) {
      dynamicMappingDefinition = new DynamicMappingDefinition();
      Element mappingElm = (Element) dynamicMapping.item(0);
      VariesMapItem variesMapItem = new VariesMapItem();
      variesMapItem.setHl7Version(profile.getMetaData().getHl7Version());
      variesMapItem.setReferenceLocation(mappingElm.getAttribute("Reference"));
      variesMapItem.setSegmentName(segmentObj.getName());
      variesMapItem.setTargetLocation(mappingElm.getAttribute("Position"));

      dynamicMappingDefinition.setMappingStructure(variesMapItem);

      NodeList cases = mappingElm.getElementsByTagName("Case");
      for (int j = 0; j < cases.getLength(); j++) {
        Element caseElm = (Element) cases.item(j);
        DynamicMappingItem dynamicMappingItem = new DynamicMappingItem();
        dynamicMappingItem
            .setDatatypeId(this.findDatatype(caseElm.getAttribute("Datatype"), profile).getId());
        dynamicMappingItem.setFirstReferenceValue(caseElm.getAttribute("Value"));
        dynamicMappingDefinition.addDynamicMappingItem(dynamicMappingItem);

      }
      segmentObj.setDynamicMappingDefinition(dynamicMappingDefinition);
    }
    NodeList fields = segmentElm.getElementsByTagName("Field");
    for (int i = 0; i < fields.getLength(); i++) {
      Element fieldElm = (Element) fields.item(i);
      segmentObj.addField(this.deserializeField(fieldElm, segmentObj, profile,
          segmentElm.getAttribute("ID"), i + 1));
    }
    return segmentObj;
  }

  private Field deserializeField(Element fieldElm, Segment segment, Profile profile,
      String segmentId, int position) {
    Field fieldObj = new Field();
    fieldObj.setName(fieldElm.getAttribute("Name"));
    fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
    Datatype dt = this.findDatatype(fieldElm.getAttribute("Datatype"), profile);
    fieldObj.setDatatype(new DatatypeLink(dt.getId(), dt.getName(), dt.getExt()));
    fieldObj.setMinLength(fieldElm.getAttribute("MinLength"));
    fieldObj.setPosition(position);
    if (fieldElm.getAttribute("MaxLength") != null) {
      fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
    }
    if (fieldElm.getAttribute("ConfLength") != null) {
      fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
    }

    if (fieldElm.getAttribute("Binding") != null && !fieldElm.getAttribute("Binding").equals("")) {
      String ids = fieldElm.getAttribute("Binding");

      for (String id : ids.split("\\:")) {
        ValueSetBinding valueSetBinding = new ValueSetBinding();
        if (fieldElm.getAttribute("BindingStrength") != null) {
          if (fieldElm.getAttribute("BindingStrength").equals("R")) {
            valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
          } else if (fieldElm.getAttribute("BindingStrength").equals("S")) {
            valueSetBinding.setBindingStrength(ValueSetBindingStrength.S);
          } else if (fieldElm.getAttribute("BindingStrength").equals("U")) {
            valueSetBinding.setBindingStrength(ValueSetBindingStrength.U);
          } else {
            valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
          }

        } else {
          valueSetBinding.setBindingStrength(ValueSetBindingStrength.R);
        }

        if (fieldElm.getAttribute("BindingLocation") != null) {
          valueSetBinding.setBindingLocation(fieldElm.getAttribute("BindingLocation"));
        }

        if (id.startsWith("HL7") && id.length() == 7) {
          valueSetBinding.setTableId(profile.getTableLibrary()
              .findOneTableByBindingIdentifier(id.replace("HL7", "")).getId());
          valueSetBinding.setLocation(position + "");
          segment.addValueSetBinding(valueSetBinding);
        } else {
          TableLink tl = profile.getTableLibrary().findOneTableByBindingIdentifier(id);
          if(tl != null){
            valueSetBinding.setTableId(tl.getId());
            valueSetBinding.setLocation(position + "");
            segment.addValueSetBinding(valueSetBinding);            
          }
        }
      }



    }

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
      DatatypeLibrary datatypes, int position) {
    Group groupObj = new Group();
    String ID = groupElm.getAttribute("ID");
    groupObj.setMax(groupElm.getAttribute("Max"));
    groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
    groupObj.setName(groupElm.getAttribute("Name"));
    groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));
    groupObj.setPosition(position);
    groupObj.setPredicates(this.findPredicates(this.predicates.getGroups(), ID, groupObj.getName()));
    groupObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getGroups(), ID, groupObj.getName()));
    List<SegmentRefOrGroup> childSegmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();

    NodeList nodes = groupElm.getChildNodes();
    int index = 0;
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeName().equals("Segment")) {
        index = index + 1;
        this.deserializeSegmentRef(elmConformanceProfile, childSegmentRefOrGroups,
            (Element) nodes.item(i), segments, datatypes, index);
      } else if (nodes.item(i).getNodeName().equals("Group")) {
        index = index + 1;
        this.deserializeGroup(elmConformanceProfile, childSegmentRefOrGroups,
            (Element) nodes.item(i), segments, datatypes, index);
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
  public InputStream serializeProfileGazelleToZip(Profile original, List<MessageExportInfo> exportInfo,
      DocumentMetaData metadata) throws IOException, CloneNotSupportedException,
      ProfileSerializationException, TableSerializationException {
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
//    for (Message m : original.getMessages().getChildren()) {
//      if (Arrays.asList(ids).contains(m.getId())) {
//        if (m.getMessageID() == null)
//          m.setMessageID(UUID.randomUUID().toString());
//        messages.addMessage(m);
//        for (SegmentRefOrGroup seog : m.getChildren()) {
//          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
//        }
//      }
//    }

    for(MessageExportInfo info : exportInfo){
    	if(!info.isAutoGenerated()){
    		Message m=this.messageService.findById(info.getMsgId());
    		m.setMessageID(UUID.randomUUID().toString());
    	    messages.addMessage(m);
            for (SegmentRefOrGroup seog : m.getChildren()) {
              this.visit(seog, segmentsMap, datatypesMap, tablesMap);
            }
    	}else{
    		Message gen = this.messageService.findById(info.getOriginId());
    		gen.setMessageID(UUID.randomUUID().toString());
    		gen.setName(info.getName());
    		gen.setEvent(info.getIdentifier());
    		gen.setEvent(info.getEvent());
    	    messages.addMessage(gen);
    	       for (SegmentRefOrGroup seog : gen.getChildren()) {
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

    return new XMLExportTool().exportXMLAsGazelleFormatForSelectedMessages(filteredProfile,
        metadata, segmentsMap, datatypesMap, tablesMap);
  }

  @Override
  public InputStream serializeCompositeProfileDisplayToZip(IGDocument doc, String[] ids)
      throws IOException, CloneNotSupportedException, TableSerializationException,
      ProfileSerializationException {
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

    return new XMLExportTool().exportXMLAsDisplayFormatForSelectedMessages(filteredProfile,
        doc.getMetaData(), segmentsMap, datatypesMap, tablesMap);
  }

  @Override
  public InputStream serializeProfileToZip(Profile original, List<MessageExportInfo> exportInfo,
      DocumentMetaData metadata) throws IOException, CloneNotSupportedException,
      ProfileSerializationException, TableSerializationException, ConstraintSerializationException {
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

    // for (SegmentLink sl : original.getSegmentLibrary().getChildren()) {
    // if (sl != null) {
    // Segment s = segmentService.findById(sl.getId());
    // if (s != null) {
    // segmentsMap.put(s.getId(), s);
    // }
    // }
    // }

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
           * Temporary script Begin This script is to hack Codes for external-user ValueSet. It is
           * just temporary script till implementation for external valueset validation
           */

          if (t != null && t.getSourceType().equals(SourceType.EXTERNAL)
              && t.getCreatedFrom() != null && !t.getCreatedFrom().isEmpty()) {
            Table origin = tableService.findById(t.getCreatedFrom());
            if (origin != null && origin.getCodes() != null && origin.getCodes().size() > 0) {
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
    
    
    for(MessageExportInfo info : exportInfo){
    	if(!info.isAutoGenerated()){
    		Message m=this.messageService.findById(info.getMsgId());
    		m.setMessageID(UUID.randomUUID().toString());
    	    messages.addMessage(m);
            for (SegmentRefOrGroup seog : m.getChildren()) {
              this.visit(seog, segmentsMap, datatypesMap, tablesMap);
            }
    	}else{
    		Message gen = this.messageService.findById(info.getOriginId());
    		gen.setMessageID(UUID.randomUUID().toString());
    		gen.setName(info.getName());
    		gen.setEvent(info.getIdentifier());
    		gen.setEvent(info.getEvent());
    	    messages.addMessage(gen);
    	       for (SegmentRefOrGroup seog : gen.getChildren()) {
    	              this.visit(seog, segmentsMap, datatypesMap, tablesMap);
    	      }

    	}
    	
    }
    
    
    
//    for (Message m : original.getMessages().getChildren()) {
//      if (Arrays.asList(ids).contains(m.getId())) {
//        if (m.getMessageID() == null) m.setMessageID(UUID.randomUUID().toString());
//        messages.addMessage(m);
//        for (SegmentRefOrGroup seog : m.getChildren()) {
//          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
//        }
//        
//        if(original.getMessages() != null && original.getMessages().getConfig() != null && original.getMessages().getConfig().getAckBinding() != null){
//          String ackMessageId = original.getMessages().getConfig().getAckBinding().get(m.getId());
//          if(ackMessageId != null) {
//            Message ackM = this.messageService.findById(ackMessageId);
//            ackM.setMessageID("ACK_" + m.getMessageID());
//            messages.addMessage(ackM);
//            for (SegmentRefOrGroup seog : ackM.getChildren()) {
//              this.visit(seog, segmentsMap, datatypesMap, tablesMap);
//            }
//          }
//        }
//      }
//    }

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
  public InputStream serializeProfileDisplayToZip(Profile original, List<MessageExportInfo> exportInfo,
      DocumentMetaData metadata) throws IOException, CloneNotSupportedException,
      TableSerializationException, ProfileSerializationException {

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

//    Messages messages = new Messages();
//    for (Message m : original.getMessages().getChildren()) {
//      if (Arrays.asList(ids).contains(m.getId())) {
//        if (m.getMessageID() == null)
//          m.setMessageID(UUID.randomUUID().toString());
//        messages.addMessage(m);
//        for (SegmentRefOrGroup seog : m.getChildren()) {
//          this.visit(seog, segmentsMap, datatypesMap, tablesMap);
//        }
//      }
//    }
    Messages messages = new Messages();
    
    
    for(MessageExportInfo info : exportInfo){
    	if(!info.isAutoGenerated()){
    		Message m=this.messageService.findById(info.getMsgId());
    		m.setMessageID(UUID.randomUUID().toString());
    	    messages.addMessage(m);
            for (SegmentRefOrGroup seog : m.getChildren()) {
              this.visit(seog, segmentsMap, datatypesMap, tablesMap);
            }
    	}else{
    		Message gen = this.messageService.findById(info.getOriginId());
    		gen.setMessageID(UUID.randomUUID().toString());
    		gen.setName(info.getName());
    		gen.setEvent(info.getIdentifier());
    		gen.setEvent(info.getEvent());
    	    messages.addMessage(gen);
    	       for (SegmentRefOrGroup seog : gen.getChildren()) {
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

    return new XMLExportTool().exportXMLAsDisplayFormatForSelectedMessages(filteredProfile,
        metadata, segmentsMap, datatypesMap, tablesMap);
  }

  private void visit(SegmentRefOrGroup seog, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    if (seog instanceof SegmentRef) {
      SegmentRef sr = (SegmentRef) seog;
      // Segment s = segmentsMap.get(sr.getRef().getId());
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

  private void addDatatypeForDM(Datatype d, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {
    if (d != null && !datatypesMap.containsKey(d.getId())) {
      datatypesMap.put(d.getId(), d);
      for (Component c : d.getComponents()) {
        this.addDatatypeForDM(datatypeService.findById(c.getDatatype().getId()), datatypesMap, tablesMap);
      }

      for (ValueSetOrSingleCodeBinding binding : d.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          Table t = tableService.findById(binding.getTableId());
          if (t != null && !tablesMap.containsKey(t.getId())) {
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
      throws IOException, CloneNotSupportedException, ProfileSerializationException,
      TableSerializationException, ConstraintSerializationException {
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
           * Temporary script Begin This script is to hack Codes for external-user ValueSet. It is
           * just temporary script till implementation for external valueset validation
           */

          if (t != null && t.getSourceType().equals(SourceType.EXTERNAL)
              && t.getCreatedFrom() != null && !t.getCreatedFrom().isEmpty()) {
            Table origin = tableService.findById(t.getCreatedFrom());
            if (origin != null && origin.getCodes() != null && origin.getCodes().size() > 0) {
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
    if (doc.getProfile().getMetaData().getExt() != null
        && !"".equals(doc.getProfile().getMetaData().getExt())) {
      filteredProfile.getMetaData().setName(doc.getProfile().getMetaData().getExt());
    }
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
      throws IOException, CloneNotSupportedException, ProfileSerializationException,
      TableSerializationException {
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

    return new XMLExportTool().exportXMLAsGazelleFormatForSelectedMessages(filteredProfile,
        doc.getMetaData(), segmentsMap, datatypesMap, tablesMap);
  }

}
