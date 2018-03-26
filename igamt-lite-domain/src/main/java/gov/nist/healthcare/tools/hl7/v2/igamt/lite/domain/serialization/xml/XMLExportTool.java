package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMComponentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMPredicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentConfiguration;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.DatatypeNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.GroupSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DatatypeComponentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DatatypeSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.FieldSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.MessageSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ProfileSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SegmentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class XMLExportTool {
  public InputStream exportXMLAsValidationFormatForSelectedMessages(Profile profile,
      DocumentMetaData metadata, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap)
      throws CloneNotSupportedException, IOException, ProfileSerializationException,
      TableSerializationException, ConstraintSerializationException {
    this.normalizeProfile(profile, segmentsMap, datatypesMap);

    ByteArrayOutputStream outputStream = null;
    byte[] bytes;
    outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    String profileXMLStr =
        this.serializeProfileToDoc(profile, metadata, segmentsMap, datatypesMap, tablesMap).toXML();
    String valueSetXMLStr = this.serializeTableXML(profile, metadata, tablesMap).toXML();
    String constraintXMLStr = this
        .serializeConstraintsXML(profile, metadata, segmentsMap, datatypesMap, tablesMap).toXML();

    this.generateProfileIS(out, profileXMLStr);
    this.generateValueSetIS(out, valueSetXMLStr);
    this.generateConstraintsIS(out, constraintXMLStr);

    out.close();
    bytes = outputStream.toByteArray();
    return new ByteArrayInputStream(bytes);
  }

  public InputStream exportXMLAsDisplayFormatForSelectedMessages(Profile profile,
      DocumentMetaData metadata, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) throws IOException,
      CloneNotSupportedException, TableSerializationException, ProfileSerializationException {

    this.normalizeProfile(profile, segmentsMap, datatypesMap);

    ByteArrayOutputStream outputStream = null;
    byte[] bytes;
    outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    for (Message m : profile.getMessages().getChildren()) {
      String folderName = m.getIdentifier() + "(" + m.getName() + ")";
      byte[] buf = new byte[1024];
      out.putNextEntry(new ZipEntry(folderName + File.separator + "NIST_DisplayProfile.xml"));
      InputStream inProfile = IOUtils.toInputStream(this.serializeProfileDisplayToXML(profile, m,
          metadata, segmentsMap, datatypesMap, tablesMap));
      int lenTP;
      while ((lenTP = inProfile.read(buf)) > 0) {
        out.write(buf, 0, lenTP);
      }
      out.closeEntry();
      inProfile.close();
    }
    out.close();
    bytes = outputStream.toByteArray();
    return new ByteArrayInputStream(bytes);
  }

  public Element serializeConstraintsXML(Profile profile, DocumentMetaData metadata,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) throws ConstraintSerializationException {

    Constraints predicates = findAllPredicates(profile, segmentsMap, datatypesMap, tablesMap);
    Constraints conformanceStatements =
        findAllConformanceStatement(profile, segmentsMap, datatypesMap, tablesMap);

    Element e = new Element("ConformanceContext");
    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/ConformanceContext.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    e.addAttribute(schemaDecl);

    e.addAttribute(new Attribute("UUID", profile.getId()));


    Element elmMetaData = new Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Constraints for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData.addAttribute(new Attribute("Name", !this.str(metadata.getTitle()).equals("")
          ? this.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(new Attribute("OrgName", !this.str(metadata.getOrgName()).equals("")
          ? this.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(new Attribute("Version", !this.str(metadata.getVersion()).equals("")
          ? this.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date", "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            this.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Status", this.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Topics", this.str(profile.getMetaData().getTopics())));
    }
    e.appendChild(elmMetaData);

    this.serializeMain(e, predicates, conformanceStatements);

    return e;
  }

  public Element serializeTableXML(Profile profile, DocumentMetaData metadata,
      Map<String, Table> tablesMap) throws TableSerializationException {
    Element elmTableLibrary = new Element("ValueSetLibrary");

    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/ValueSets.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    elmTableLibrary.addAttribute(schemaDecl);
    elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier", profile.getId()));

    Element elmMetaData = new Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData.addAttribute(new Attribute("Name", !this.str(metadata.getTitle()).equals("")
          ? this.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(new Attribute("OrgName", !this.str(metadata.getOrgName()).equals("")
          ? this.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(new Attribute("Version", !this.str(metadata.getVersion()).equals("")
          ? this.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date", "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            this.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Status", this.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Topics", this.str(profile.getMetaData().getTopics())));
    }

    Element elmNoValidation = new Element("NoValidation");

    Element elmValueSetDefinitionsHL7Base = new Element("ValueSetDefinitions");
    elmValueSetDefinitionsHL7Base.addAttribute(new Attribute("Group", "HL7_base"));
    elmValueSetDefinitionsHL7Base.addAttribute(new Attribute("Order", "1"));
    Element elmValueSetDefinitionsHL7HL7Profile = new Element("ValueSetDefinitions");
    elmValueSetDefinitionsHL7HL7Profile.addAttribute(new Attribute("Group", "HL7_Profile"));
    elmValueSetDefinitionsHL7HL7Profile.addAttribute(new Attribute("Order", "2"));
    Element elmValueSetDefinitionsHL7External = new Element("ValueSetDefinitions");
    elmValueSetDefinitionsHL7External.addAttribute(new Attribute("Group", "External"));
    elmValueSetDefinitionsHL7External.addAttribute(new Attribute("Order", "3"));
    Element elmValueSetDefinitionsHL7Other = new Element("ValueSetDefinitions");
    elmValueSetDefinitionsHL7Other.addAttribute(new Attribute("Group", "Others"));
    elmValueSetDefinitionsHL7Other.addAttribute(new Attribute("Order", "4"));

    for (String key : tablesMap.keySet()) {
      try {
        HashMap<String, Boolean> codePresenceMap = profile.getTableLibrary().getCodePresence();
        Table t = tablesMap.get(key);

        if (t != null) {
          if (t.getCodes() == null || t.getCodes().size() == 0 || t.getCodes().size() > 500
              || (t.getCodes().size() == 1 && t.getCodes().get(0).getValue().equals("..."))
              || (codePresenceMap.containsKey(t.getId()) && !(codePresenceMap.get(t.getId())))) {
            Element elmBindingIdentifier = new Element("BindingIdentifier");
            if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
              if (t.getBindingIdentifier().startsWith("0396")
                  || t.getBindingIdentifier().startsWith("HL70396")) {
                elmBindingIdentifier.appendChild(this.str(t.getBindingIdentifier()));
              } else {
                elmBindingIdentifier.appendChild(this.str(
                    t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-")));
              }
            } else {
              elmBindingIdentifier.appendChild(this.str(t.getBindingIdentifier()));
            }
            elmNoValidation.appendChild(elmBindingIdentifier);
          }

          Element elmValueSetDefinition = new Element("ValueSetDefinition");
          if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
            if (t.getBindingIdentifier().startsWith("0396")
                || t.getBindingIdentifier().startsWith("HL70396")) {
              elmValueSetDefinition.addAttribute(
                  new Attribute("BindingIdentifier", this.str(t.getBindingIdentifier())));
            } else {
              elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", this
                  .str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-"))));
            }
          } else {
            elmValueSetDefinition.addAttribute(
                new Attribute("BindingIdentifier", this.str(t.getBindingIdentifier())));
          }

          elmValueSetDefinition.addAttribute(new Attribute("Name", this.str(t.getName())));
          if (t.getName() != null && !t.getName().equals(""))
            elmValueSetDefinition.addAttribute(new Attribute("Description", this.str(t.getName())));
          if (t.getVersion() != null && !t.getVersion().equals(""))
            elmValueSetDefinition.addAttribute(new Attribute("Version", this.str(t.getVersion())));
          if (t.getOid() != null && !t.getOid().equals(""))
            elmValueSetDefinition.addAttribute(new Attribute("Oid", this.str(t.getOid())));
          if (t.getStability() != null && !t.getStability().equals("")) {
            if (t.getStability().equals(Stability.Undefined)) {
              elmValueSetDefinition
                  .addAttribute(new Attribute("Stability", this.str(Stability.Static.name())));
            } else {
              elmValueSetDefinition
                  .addAttribute(new Attribute("Stability", this.str(t.getStability().name())));
            }
          }
          if (t.getExtensibility() != null && !t.getExtensibility().equals("")) {
            if (t.getExtensibility().equals(Extensibility.Undefined)) {
              elmValueSetDefinition.addAttribute(
                  new Attribute("Extensibility", this.str(Extensibility.Closed.name())));
            } else {
              elmValueSetDefinition.addAttribute(
                  new Attribute("Extensibility", this.str(t.getExtensibility().name())));
            }
          }
          if (t.getContentDefinition() != null && !t.getContentDefinition().equals("")) {
            if (t.getContentDefinition().equals(ContentDefinition.Undefined)) {
              elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition",
                  this.str(ContentDefinition.Extensional.name())));
            } else {
              elmValueSetDefinition.addAttribute(
                  new Attribute("ContentDefinition", this.str(t.getContentDefinition().name())));
            }
          }
          if (t.getScope().equals(SCOPE.HL7STANDARD)) {
            elmValueSetDefinitionsHL7Base.appendChild(elmValueSetDefinition);
          } else if (t.getScope().equals(SCOPE.USER)) {
            elmValueSetDefinitionsHL7HL7Profile.appendChild(elmValueSetDefinition);
          } else if (t.getScope().equals(SCOPE.PHINVADS)) {
            elmValueSetDefinitionsHL7External.appendChild(elmValueSetDefinition);
          } else {
            elmValueSetDefinitionsHL7Other.appendChild(elmValueSetDefinition);
          }

          if (t.getCodes() != null && t.getCodes().size() <= 500) {
            for (Code c : t.getCodes()) {
              Element elmValueElement = new Element("ValueElement");
              elmValueElement.addAttribute(new Attribute("Value", this.str(c.getValue())));
              elmValueElement
                  .addAttribute(new Attribute("DisplayName", this.str(c.getLabel() + "")));
              if (c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
                elmValueElement
                    .addAttribute(new Attribute("CodeSystem", this.str(c.getCodeSystem())));
              if (c.getCodeSystemVersion() != null && !c.getCodeSystemVersion().equals(""))
                elmValueElement.addAttribute(
                    new Attribute("CodeSystemVersion", this.str(c.getCodeSystemVersion())));
              if (c.getCodeUsage() != null && !c.getCodeUsage().equals(""))
                elmValueElement.addAttribute(new Attribute("Usage", this.str(c.getCodeUsage())));
              if (c.getComments() != null && !c.getComments().equals(""))
                elmValueElement.addAttribute(new Attribute("Comments", this.str(c.getComments())));
              elmValueSetDefinition.appendChild(elmValueElement);
            }
          }
        }
      } catch (Exception e) {
        throw new TableSerializationException(e, key);
      }



    }

    elmTableLibrary.appendChild(elmMetaData);
    elmTableLibrary.appendChild(elmNoValidation);


    if (elmValueSetDefinitionsHL7Base.getChildCount() > 0) {
      elmTableLibrary.appendChild(elmValueSetDefinitionsHL7Base);
    }
    if (elmValueSetDefinitionsHL7HL7Profile.getChildCount() > 0) {
      elmTableLibrary.appendChild(elmValueSetDefinitionsHL7HL7Profile);
    }
    if (elmValueSetDefinitionsHL7External.getChildCount() > 0) {
      elmTableLibrary.appendChild(elmValueSetDefinitionsHL7External);
    }
    if (elmValueSetDefinitionsHL7Other.getChildCount() > 0) {
      elmTableLibrary.appendChild(elmValueSetDefinitionsHL7Other);
    }

    return elmTableLibrary;
  }

  public Document serializeProfileToDoc(Profile profile, DocumentMetaData metadata,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) throws ProfileSerializationException {

    try {
      Element e = new Element("ConformanceProfile");
      this.serializeProfileMetaData(e, profile, metadata, "Validation");

      Element ms = new Element("Messages");
      for (Message m : profile.getMessages().getChildren()) {
        ms.appendChild(this.serializeMessage(m, segmentsMap));
      }
      e.appendChild(ms);

      Element ss = new Element("Segments");
      for (String key : segmentsMap.keySet()) {
        Segment s = segmentsMap.get(key);
        ss.appendChild(this.serializeSegment(s, tablesMap, datatypesMap));
      }
      e.appendChild(ss);

      Element ds = new Element("Datatypes");
      for (String key : datatypesMap.keySet()) {
        Datatype d = datatypesMap.get(key);
        Element dElm = this.serializeDatatypeForValidation(d, tablesMap, datatypesMap);
        if (dElm != null)
          ds.appendChild(dElm);
      }
      e.appendChild(ds);

      Document doc = new Document(e);

      return doc;
    } catch (Exception e) {
      throw new ProfileSerializationException(e, profile != null ? profile.getId() : "");
    }

  }

  private String serializeProfileDisplayToXML(Profile p, Message m, DocumentMetaData metadata,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap)
      throws TableSerializationException, ProfileSerializationException {
    try {
      Element e = new Element("ConformanceProfile");
      this.serializeProfileMetaData(e, p, metadata, "Display");
      e.appendChild(this.serializeDisplayMessage(m, p, segmentsMap, datatypesMap, tablesMap));
      e.appendChild(this.serializeTableXML(p, metadata, tablesMap));
      Document doc = new Document(e);
      return doc.toXML();
    } catch (Exception e) {
      throw new ProfileSerializationException(e, p != null ? p.getId() : "");
    }

  }

  private Element serializeDisplayMessage(Message m, Profile profile,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) throws MessageSerializationException {
    try {
      Element elmMessage = new Element("Message");
      if (m.getName() != null && !m.getName().equals(""))
        elmMessage.addAttribute(new Attribute("Name", this.str(m.getName())));
      elmMessage.addAttribute(new Attribute("Type", this.str(m.getMessageType())));
      elmMessage.addAttribute(new Attribute("Event", this.str(m.getEvent())));
      elmMessage.addAttribute(new Attribute("StructID", this.str(m.getStructID())));
      if (m.getDescription() != null && !m.getDescription().equals(""))
        elmMessage.addAttribute(new Attribute("Description", this.str(m.getDescription())));

      Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
          new HashMap<Integer, SegmentRefOrGroup>();
      for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
        segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
      }

      for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
        String path = i + "[1]";
        SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
        if (segmentRefOrGroup instanceof SegmentRef) {
          elmMessage.appendChild(serializeDisplaySegment((SegmentRef) segmentRefOrGroup, profile, m,
              path, segmentsMap, datatypesMap, tablesMap));
        } else if (segmentRefOrGroup instanceof Group) {
          elmMessage.appendChild(serializeDisplayGroup((Group) segmentRefOrGroup, profile, m, path,
              segmentsMap, datatypesMap, tablesMap));
        }
      }
      return elmMessage;
    } catch (Exception e) {
      throw new MessageSerializationException(e, m != null ? m.getName() : "");
    }

  }

  private Element serializeDisplayGroup(Group group, Profile profile, Message message, String path,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) throws GroupSerializationException {
    try {
      Element elmGroup = new Element("Group");
      elmGroup.addAttribute(new Attribute("ID", this.str(group.getName())));
      elmGroup.addAttribute(new Attribute("Name", this.str(group.getName())));
      elmGroup.addAttribute(new Attribute("Usage", this.str(group.getUsage().value())));
      elmGroup.addAttribute(new Attribute("Min", this.str(group.getMin() + "")));
      elmGroup.addAttribute(new Attribute("Max", this.str(group.getMax())));

      Predicate groupPredicate = this.findPredicate(null, null, message.getPredicates(), path);
      if (groupPredicate != null) {
        try {
          Element elmPredicate = new Element("Predicate");
          elmPredicate.addAttribute(new Attribute("TrueUsage", "" + groupPredicate.getTrueUsage()));
          elmPredicate
              .addAttribute(new Attribute("FalseUsage", "" + groupPredicate.getFalseUsage()));

          Element elmDescription = new Element("Description");
          elmDescription.appendChild(groupPredicate.getDescription());
          elmPredicate.appendChild(elmDescription);

          Node n = this.innerXMLHandler(groupPredicate.getAssertion());
          if (n != null)
            elmPredicate.appendChild(n);

          elmGroup.appendChild(elmPredicate);
        } catch (Exception e) {
          throw new ConstraintSerializationException(e, groupPredicate.getDescription());
        }

      }

      List<ConformanceStatement> groupConformanceStatements =
          this.findConformanceStatements(null, null, message.getConformanceStatements(), path);

      if (groupConformanceStatements.size() > 0) {
        Element elmConformanceStatements = new Element("ConformanceStatements");

        for (ConformanceStatement c : groupConformanceStatements) {
          try {
            Element elmConformanceStatement = new Element("ConformanceStatement");
            elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
            Element elmDescription = new Element("Description");
            elmDescription.appendChild(c.getDescription());
            elmConformanceStatement.appendChild(elmDescription);

            Node n = this.innerXMLHandler(c.getAssertion());
            if (n != null)
              elmConformanceStatement.appendChild(n);

            elmConformanceStatements.appendChild(elmConformanceStatement);
          } catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }
        }

        elmGroup.appendChild(elmConformanceStatements);
      }

      Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
          new HashMap<Integer, SegmentRefOrGroup>();

      for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
        segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
      }

      Element elmStructure = new Element("Structure");

      for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
        String childPath = path + "." + i + "[1]";
        SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
        if (segmentRefOrGroup instanceof SegmentRef) {
          elmStructure.appendChild(serializeDisplaySegment((SegmentRef) segmentRefOrGroup, profile,
              message, childPath, segmentsMap, datatypesMap, tablesMap));
        } else if (segmentRefOrGroup instanceof Group) {
          elmStructure.appendChild(serializeDisplayGroup((Group) segmentRefOrGroup, profile,
              message, childPath, segmentsMap, datatypesMap, tablesMap));
        }
      }

      elmGroup.appendChild(elmStructure);

      return elmGroup;
    } catch (Exception e) {
      if (group != null) {
        throw new GroupSerializationException(e, group.getName());
      }
    }
    return null;

  }

  private Element serializeDisplaySegment(SegmentRef segmentRef, Profile profile, Message message,
      String path, Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) throws SegmentSerializationException {
    Element elmSegment = new Element("Segment");
    Segment segment = segmentsMap.get(segmentRef.getRef().getId());

    try {
      elmSegment.addAttribute(new Attribute("ID", this.str(segment.getLabel())));
      elmSegment.addAttribute(new Attribute("Usage", this.str(segmentRef.getUsage().value())));
      elmSegment.addAttribute(new Attribute("Min", this.str(segmentRef.getMin() + "")));
      elmSegment.addAttribute(new Attribute("Max", this.str(segmentRef.getMax())));
      elmSegment.addAttribute(new Attribute("Name", this.str(segment.getName())));
      elmSegment.addAttribute(new Attribute("Description", this.str(segment.getDescription())));

      Predicate segmentPredicate = this.findPredicate(null, null, message.getPredicates(), path);
      if (segmentPredicate != null) {
        try {
          Element elmPredicate = new Element("Predicate");
          elmPredicate
              .addAttribute(new Attribute("TrueUsage", "" + segmentPredicate.getTrueUsage()));
          elmPredicate
              .addAttribute(new Attribute("FalseUsage", "" + segmentPredicate.getFalseUsage()));

          Element elmDescription = new Element("Description");
          elmDescription.appendChild(segmentPredicate.getDescription());
          elmPredicate.appendChild(elmDescription);

          Node n = this.innerXMLHandler(segmentPredicate.getAssertion());
          if (n != null)
            elmPredicate.appendChild(n);

          elmSegment.appendChild(elmPredicate);
        } catch (Exception e) {
          throw new ConstraintSerializationException(e, segmentPredicate.getDescription());
        }

      }

      List<ConformanceStatement> segmentConformanceStatements =
          this.findConformanceStatements(null, null, message.getConformanceStatements(), path);

      if (segmentConformanceStatements.size() > 0) {
        Element elmConformanceStatements = new Element("ConformanceStatements");

        for (ConformanceStatement c : segmentConformanceStatements) {
          try {
            Element elmConformanceStatement = new Element("ConformanceStatement");
            elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
            Element elmDescription = new Element("Description");
            elmDescription.appendChild(c.getDescription());
            elmConformanceStatement.appendChild(elmDescription);

            Node n = this.innerXMLHandler(c.getAssertion());
            if (n != null)
              elmConformanceStatement.appendChild(n);

            elmConformanceStatements.appendChild(elmConformanceStatement);
          } catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }

        }
        elmSegment.appendChild(elmConformanceStatements);
      }

      Element elmSegmentStructure = new Element("Structure");

      Map<Integer, Field> fields = new HashMap<Integer, Field>();
      for (Field f : segment.getFields()) {
        fields.put(f.getPosition(), f);
      }

      if (fields.size() > 0) {
        elmSegment.appendChild(elmSegmentStructure);
      }

      String targetPosition = null;
      String reference = null;
      String secondReference = null;
      String referenceTableId = null;
      HashMap<String, Datatype> dm = new HashMap<String, Datatype>();
      HashMap<String, Datatype> dm2nd = new HashMap<String, Datatype>();

      if (segment.getName().equals("OBX") || segment.getName().equals("MFA")
          || segment.getName().equals("MFE")) {


        if (segment.getName().equals("OBX")) {
          targetPosition = "5";
          reference = "2";
        }

        if (segment.getName().equals("MFA")) {
          targetPosition = "5";
          reference = "6";
        }

        if (segment.getName().equals("MFE")) {
          targetPosition = "4";
          reference = "5";
        }

        if (segment.getCoConstraintsTable() != null
            && segment.getCoConstraintsTable().getIfColumnDefinition() != null) {
          if (segment.getCoConstraintsTable().getIfColumnDefinition().isPrimitive()) {
            secondReference = segment.getCoConstraintsTable().getIfColumnDefinition().getPath();
          } else {
            secondReference =
                segment.getCoConstraintsTable().getIfColumnDefinition().getPath() + ".1";
          }
        }

        referenceTableId = this.findValueSetID(segment.getValueSetBindings(), reference);

        if (referenceTableId != null) {
          Table table = tablesMap.get(referenceTableId);
          String hl7Version = null;
          hl7Version = table.getHl7Version();
          if (hl7Version == null)
            hl7Version = segment.getHl7Version();

          if (table != null) {
            for (Code c : table.getCodes()) {
              if (c.getValue() != null) {
                Datatype d =
                    this.findHL7DatatypeByNameAndVesion(datatypesMap, c.getValue(), hl7Version);
                if (d != null) {
                  dm.put(c.getValue(), d);
                }
              }
            }
          }
          if (segment.getDynamicMappingDefinition() != null) {
            for (DynamicMappingItem item : segment.getDynamicMappingDefinition()
                .getDynamicMappingItems()) {
              if (item.getFirstReferenceValue() != null && item.getDatatypeId() != null)
                dm.put(item.getFirstReferenceValue(), datatypesMap.get(item.getDatatypeId()));
            }
          }
        }
        if (secondReference != null) {
          for (CoConstraintColumnDefinition definition : segment.getCoConstraintsTable()
              .getThenColumnDefinitionList()) {
            if (definition.isdMReference()) {
              List<CoConstraintTHENColumnData> dataList =
                  segment.getCoConstraintsTable().getThenMapData().get(definition.getId());

              if (dataList != null && segment.getCoConstraintsTable().getIfColumnData() != null) {
                for (int i = 0; i < dataList.size(); i++) {
                  CoConstraintIFColumnData ref =
                      segment.getCoConstraintsTable().getIfColumnData().get(i);
                  CoConstraintTHENColumnData data = dataList.get(i);

                  if (ref != null && data != null && ref.getValueData() != null
                      && ref.getValueData().getValue() != null && data.getDatatypeId() != null
                      && data.getValueData() != null && data.getValueData().getValue() != null) {
                    dm2nd.put(ref.getValueData().getValue(),
                        datatypesMap.get(data.getDatatypeId()));
                  }
                }
              }
            }
          }
        }
      }

      for (int i = 1; i < fields.size() + 1; i++) {
        String fieldPath = path + "." + i + "[1]";
        Field f = fields.get(i);
        if (f != null) {
          if (dm.size() > 0 || dm2nd.size() > 0) {
            if (targetPosition.equals(i + "")) {
              Element elmDynamicField = new Element("DynamicField");
              elmDynamicField.addAttribute(new Attribute("Name", this.str(f.getName())));
              elmDynamicField.addAttribute(new Attribute("Reference", reference));
              if (secondReference != null) {
                elmDynamicField.addAttribute(new Attribute("SecondReference", secondReference));
              }

              for (String key : dm.keySet()) {
                Element elmCase = new Element("Case");
                Datatype d = dm.get(key);
                if (d != null) {
                  elmCase.addAttribute(new Attribute("Value", d.getName()));
                  this.serializeDisplayField(segment, f, datatypesMap.get(f.getDatatype().getId()),
                      elmCase, profile, message, segment, fieldPath, datatypesMap, tablesMap);
                  elmDynamicField.appendChild(elmCase);
                }
              }

              for (String key : dm2nd.keySet()) {
                Element elmCase = new Element("Case");
                Datatype d = dm2nd.get(key);
                if (d != null) {
                  elmCase.addAttribute(new Attribute("Value", d.getName()));
                  elmCase.addAttribute(new Attribute("SecondValue", key));
                  this.serializeDisplayField(segment, f, datatypesMap.get(f.getDatatype().getId()),
                      elmCase, profile, message, segment, fieldPath, datatypesMap, tablesMap);
                  elmDynamicField.appendChild(elmCase);
                }
              }
              elmSegmentStructure.appendChild(elmDynamicField);
            } else {
              this.serializeDisplayField(segment, f, datatypesMap.get(f.getDatatype().getId()),
                  elmSegmentStructure, profile, message, segment, fieldPath, datatypesMap,
                  tablesMap);
            }
          } else {
            this.serializeDisplayField(segment, f, datatypesMap.get(f.getDatatype().getId()),
                elmSegmentStructure, profile, message, segment, fieldPath, datatypesMap, tablesMap);
          }
        }
      }

      return elmSegment;
    } catch (Exception e) {
      throw new SegmentSerializationException(e, segment.getLabel());
    }



  }

  private void serializeDisplayField(Segment s, Field f, Datatype fieldDatatype, Element elmParent,
      Profile profile, Message message, Segment segment, String fieldPath,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap)
      throws FieldSerializationException {
    try {

      if (f.getDatatype() != null && fieldDatatype == null) {
        throw new DatatypeNotFoundException(f.getDatatype().getId());
      }

      Element elmField = new Element("Field");
      elmParent.appendChild(elmField);

      elmField.addAttribute(new Attribute("Name", this.str(f.getName())));
      elmField.addAttribute(new Attribute("Usage", this.str(f.getUsage().toString())));
      elmField.addAttribute(new Attribute("Datatype", this.str(fieldDatatype.getName())));
      elmField.addAttribute(new Attribute("Flavor", this.str(fieldDatatype.getLabel())));
      elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
      if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
        elmField.addAttribute(new Attribute("MaxLength", this.str(f.getMaxLength())));
      if (f.getConfLength() != null && !f.getConfLength().equals(""))
        elmField.addAttribute(new Attribute("ConfLength", this.str(f.getConfLength())));

      List<ValueSetBinding> bindings = findBinding(s.getValueSetBindings(), f.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";
        String bindingStrength = null;
        String bindingLocation = null;

        for (ValueSetBinding binding : bindings) {
          try {
            Table table = tablesMap.get(binding.getTableId());
            bindingStrength = binding.getBindingStrength().toString();
            bindingLocation = binding.getBindingLocation();
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }
          } catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }
        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(fieldDatatype.getName())) {
          if (!bindingString.equals(""))
            elmField.addAttribute(
                new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
          if (bindingStrength != null)
            elmField.addAttribute(new Attribute("BindingStrength", bindingStrength));

          if (fieldDatatype != null && fieldDatatype.getComponents() != null
              && fieldDatatype.getComponents().size() > 0) {
            if (bindingLocation != null && !bindingLocation.equals("")) {
              bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
              elmField.addAttribute(new Attribute("BindingLocation", bindingLocation));
            } else {
              elmField.addAttribute(new Attribute("BindingLocation", "1"));
            }
          }
        }
      }

      elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
      elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
      if (f.getItemNo() != null && !f.getItemNo().equals(""))
        elmField.addAttribute(new Attribute("ItemNo", this.str(f.getItemNo())));

      Predicate fieldPredicate = this.findPredicate(segment.getPredicates(),
          f.getPosition() + "[1]", message.getPredicates(), fieldPath);
      if (fieldPredicate != null) {
        try {
          Element elmPredicate = new Element("Predicate");
          elmPredicate.addAttribute(new Attribute("TrueUsage", "" + fieldPredicate.getTrueUsage()));
          elmPredicate
              .addAttribute(new Attribute("FalseUsage", "" + fieldPredicate.getFalseUsage()));

          Element elmDescription = new Element("Description");
          elmDescription.appendChild(fieldPredicate.getDescription());
          elmPredicate.appendChild(elmDescription);

          Node n = this.innerXMLHandler(fieldPredicate.getAssertion());
          if (n != null)
            elmPredicate.appendChild(n);

          elmField.appendChild(elmPredicate);
        } catch (Exception e) {
          throw new ConstraintSerializationException(e, fieldPredicate.getDescription());
        }

      }

      List<ConformanceStatement> fieldConformanceStatements =
          this.findConformanceStatements(segment.getConformanceStatements(),
              f.getPosition() + "[1]", message.getConformanceStatements(), fieldPath);

      if (fieldConformanceStatements.size() > 0) {
        Element elmConformanceStatements = new Element("ConformanceStatements");

        for (ConformanceStatement c : fieldConformanceStatements) {
          try {
            Element elmConformanceStatement = new Element("ConformanceStatement");
            elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
            Element elmDescription = new Element("Description");
            elmDescription.appendChild(c.getDescription());
            elmConformanceStatement.appendChild(elmDescription);

            Node n = this.innerXMLHandler(c.getAssertion());
            if (n != null)
              elmConformanceStatement.appendChild(n);

            elmConformanceStatements.appendChild(elmConformanceStatement);
          } catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }
        }

        elmField.appendChild(elmConformanceStatements);
      }

      Element elmFieldStructure = new Element("Structure");
      Map<Integer, Component> components = new HashMap<Integer, Component>();

      for (Component c : fieldDatatype.getComponents()) {
        components.put(c.getPosition(), c);
      }

      if (components.size() > 0) {
        elmField.appendChild(elmFieldStructure);
      }

      for (int j = 1; j < components.size() + 1; j++) {
        String componentPath = fieldPath + "." + j + "[1]";
        Component c = components.get(j);
        this.serializeDisplayComponent(c, datatypesMap.get(c.getDatatype().getId()),
            elmFieldStructure, profile, message, fieldDatatype, componentPath, datatypesMap,
            tablesMap);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new FieldSerializationException(e, "Field[" + f.getPosition() + "]");
    }

  }

  private void serializeDisplayComponent(Component c, Datatype componentDatatype, Element elmParent,
      Profile profile, Message message, Datatype fieldDatatype, String componentPath,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap)
      throws DatatypeComponentSerializationException {
    try {

      if (c.getDatatype() != null && componentDatatype == null) {
        throw new DatatypeNotFoundException(c.getDatatype().getId());
      }

      Element elmComponent = new Element("Component");
      elmComponent.addAttribute(new Attribute("Name", this.str(c.getName())));
      elmComponent.addAttribute(new Attribute("Usage", this.str(c.getUsage().toString())));
      elmComponent.addAttribute(new Attribute("Datatype", this.str(componentDatatype.getName())));
      elmComponent.addAttribute(new Attribute("Flavor", this.str(componentDatatype.getLabel())));
      elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
      if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
        elmComponent.addAttribute(new Attribute("MaxLength", this.str(c.getMaxLength())));
      if (c.getConfLength() != null && !c.getConfLength().equals(""))
        elmComponent.addAttribute(new Attribute("ConfLength", this.str(c.getConfLength())));
      List<ValueSetBinding> bindings =
          findBinding(fieldDatatype.getValueSetBindings(), c.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";
        String bindingStrength = null;
        String bindingLocation = null;

        for (ValueSetBinding binding : bindings) {
          try {
            Table table = tablesMap.get(binding.getTableId());
            bindingStrength = binding.getBindingStrength().toString();
            bindingLocation = binding.getBindingLocation();
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }
          } catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }

        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(componentDatatype.getName())) {
          if (!bindingString.equals(""))
            elmComponent.addAttribute(
                new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
          if (bindingStrength != null)
            elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));

          if (fieldDatatype != null && fieldDatatype.getComponents() != null
              && fieldDatatype.getComponents().size() > 0) {
            if (bindingLocation != null && !bindingLocation.equals("")) {
              bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
              elmComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
            } else {
              elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
            }
          }
        }
      }

      Predicate componentPredicate = this.findPredicate(fieldDatatype.getPredicates(),
          c.getPosition() + "[1]", message.getPredicates(), componentPath);
      if (componentPredicate != null) {
        try {
          Element elmPredicate = new Element("Predicate");
          elmPredicate
              .addAttribute(new Attribute("TrueUsage", "" + componentPredicate.getTrueUsage()));
          elmPredicate
              .addAttribute(new Attribute("FalseUsage", "" + componentPredicate.getFalseUsage()));

          Element elmDescription = new Element("Description");
          elmDescription.appendChild(componentPredicate.getDescription());
          elmPredicate.appendChild(elmDescription);

          Node n = this.innerXMLHandler(componentPredicate.getAssertion());
          if (n != null)
            elmPredicate.appendChild(n);

          elmComponent.appendChild(elmPredicate);
        } catch (Exception e) {
          throw new ConstraintSerializationException(e, componentPredicate.getDescription());
        }
      }

      List<ConformanceStatement> componentConformanceStatements =
          this.findConformanceStatements(fieldDatatype.getConformanceStatements(),
              c.getPosition() + "[1]", message.getConformanceStatements(), componentPath);

      if (componentConformanceStatements.size() > 0) {
        Element elmConformanceStatements = new Element("ConformanceStatements");

        for (ConformanceStatement cs : componentConformanceStatements) {
          try {
            Element elmConformanceStatement = new Element("ConformanceStatement");
            elmConformanceStatement.addAttribute(new Attribute("ID", "" + cs.getConstraintId()));
            Element elmDescription = new Element("Description");
            elmDescription.appendChild(cs.getDescription());
            elmConformanceStatement.appendChild(elmDescription);

            Node n = this.innerXMLHandler(cs.getAssertion());
            if (n != null)
              elmConformanceStatement.appendChild(n);

            elmConformanceStatements.appendChild(elmConformanceStatement);
          } catch (Exception e) {
            throw new ConstraintSerializationException(e, cs.getDescription());
          }
        }

        elmComponent.appendChild(elmConformanceStatements);
      }

      Element elmComponentStructure = new Element("Structure");
      Map<Integer, Component> subComponents = new HashMap<Integer, Component>();

      for (Component sc : componentDatatype.getComponents()) {
        subComponents.put(sc.getPosition(), sc);
      }

      if (subComponents.size() > 0) {
        elmComponent.appendChild(elmComponentStructure);
      }

      for (int k = 1; k < subComponents.size() + 1; k++) {
        String subComponentPath = componentPath + "." + k + "[1]";
        Component sc = subComponents.get(k);
        this.serializeDisplaySubComponent(sc, datatypesMap.get(sc.getDatatype().getId()),
            elmComponentStructure, profile, message, componentDatatype, subComponentPath,
            datatypesMap, tablesMap);
      }
      elmParent.appendChild(elmComponent);
    } catch (Exception e) {
      throw new DatatypeComponentSerializationException(e, c.getPosition());
    }
  }

  private void serializeDisplaySubComponent(Component sc, Datatype subComponentDatatype,
      Element elmParent, Profile profile, Message message, Datatype componentDatatype,
      String subComponentPath, Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap)
      throws ConstraintSerializationException, TableSerializationException,
      DatatypeComponentSerializationException {
    try {

      if (sc.getDatatype() != null && subComponentDatatype == null) {
        throw new DatatypeNotFoundException(sc.getDatatype().getId());
      }


      Element elmSubComponent = new Element("SubComponent");
      elmSubComponent.addAttribute(new Attribute("Name", this.str(sc.getName())));
      elmSubComponent.addAttribute(new Attribute("Usage", this.str(sc.getUsage().toString())));
      elmSubComponent
          .addAttribute(new Attribute("Datatype", this.str(subComponentDatatype.getName())));
      elmSubComponent
          .addAttribute(new Attribute("Flavor", this.str(subComponentDatatype.getLabel())));
      elmSubComponent.addAttribute(new Attribute("MinLength", "" + sc.getMinLength()));
      if (sc.getMaxLength() != null && !sc.getMaxLength().equals(""))
        elmSubComponent.addAttribute(new Attribute("MaxLength", this.str(sc.getMaxLength())));
      if (sc.getConfLength() != null && !sc.getConfLength().equals(""))
        elmSubComponent.addAttribute(new Attribute("ConfLength", this.str(sc.getConfLength())));

      List<ValueSetBinding> bindings =
          findBinding(componentDatatype.getValueSetBindings(), sc.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";
        String bindingStrength = null;
        String bindingLocation = null;

        for (ValueSetBinding binding : bindings) {
          try {
            Table table = tablesMap.get(binding.getTableId());
            bindingStrength = binding.getBindingStrength().toString();
            bindingLocation = binding.getBindingLocation();
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }
          } catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }

        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(subComponentDatatype.getName())) {
          if (!bindingString.equals(""))
            elmSubComponent.addAttribute(
                new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
          if (bindingStrength != null)
            elmSubComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));

          if (componentDatatype != null && componentDatatype.getComponents() != null
              && componentDatatype.getComponents().size() > 0) {
            if (bindingLocation != null && !bindingLocation.equals("")) {
              bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
              elmSubComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
            } else {
              elmSubComponent.addAttribute(new Attribute("BindingLocation", "1"));
            }
          }
        }
      }

      Predicate subComponentPredicate = this.findPredicate(componentDatatype.getPredicates(),
          sc.getPosition() + "[1]", message.getPredicates(), subComponentPath);
      if (subComponentPredicate != null) {
        try {
          Element elmPredicate = new Element("Predicate");
          elmPredicate
              .addAttribute(new Attribute("TrueUsage", "" + subComponentPredicate.getTrueUsage()));
          elmPredicate.addAttribute(
              new Attribute("FalseUsage", "" + subComponentPredicate.getFalseUsage()));

          Element elmDescription = new Element("Description");
          elmDescription.appendChild(subComponentPredicate.getDescription());
          elmPredicate.appendChild(elmDescription);

          Node n = this.innerXMLHandler(subComponentPredicate.getAssertion());
          if (n != null)
            elmPredicate.appendChild(n);

          elmSubComponent.appendChild(elmPredicate);
        } catch (Exception e) {
          throw new ConstraintSerializationException(e, subComponentPredicate.getDescription());
        }

      }

      List<ConformanceStatement> subComponentConformanceStatements =
          this.findConformanceStatements(componentDatatype.getConformanceStatements(),
              sc.getPosition() + "[1]", message.getConformanceStatements(), subComponentPath);

      if (subComponentConformanceStatements.size() > 0) {
        Element elmConformanceStatements = new Element("ConformanceStatements");

        for (ConformanceStatement cs : subComponentConformanceStatements) {
          try {
            Element elmConformanceStatement = new Element("ConformanceStatement");
            elmConformanceStatement.addAttribute(new Attribute("ID", "" + cs.getConstraintId()));
            Element elmDescription = new Element("Description");
            elmDescription.appendChild(cs.getDescription());
            elmConformanceStatement.appendChild(elmDescription);

            Node n = this.innerXMLHandler(cs.getAssertion());
            if (n != null)
              elmConformanceStatement.appendChild(n);

            elmConformanceStatements.appendChild(elmConformanceStatement);
          } catch (Exception e) {
            throw new ConstraintSerializationException(e, cs.getDescription());
          }

        }

        elmSubComponent.appendChild(elmConformanceStatements);
      }
      elmParent.appendChild(elmSubComponent);
    } catch (Exception e) {
      throw new DatatypeComponentSerializationException(e, sc.getPosition());
    }

  }

  private Predicate findPredicate(List<Predicate> predicates, String path,
      List<Predicate> messagePredicate, String messagePath) {
    if (predicates != null && path != null) {
      for (Predicate p : predicates) {
        if (p.getConstraintTarget() != null && p.getConstraintTarget().equals(path)) {
          return p;
        }
      }
    }

    if (messagePredicate != null && messagePath != null) {
      for (Predicate p : messagePredicate) {
        if (p.getConstraintTarget() != null && p.getConstraintTarget().equals(messagePath)) {
          return p;
        }
      }
    }
    return null;
  }

  private List<ConformanceStatement> findConformanceStatements(
      List<ConformanceStatement> conformanceStatements, String path,
      List<ConformanceStatement> messageConformanceStatements, String messagePath) {
    List<ConformanceStatement> result = new ArrayList<ConformanceStatement>();

    if (conformanceStatements != null && path != null) {
      for (ConformanceStatement c : conformanceStatements) {
        if (c.getConstraintTarget() != null && c.getConstraintTarget().equals(path)) {
          result.add(c);
        }          
      }
    }
    if (messageConformanceStatements != null && messagePath != null) {
      for (ConformanceStatement c : messageConformanceStatements) {
        if (c.getConstraintTarget() != null && c.getConstraintTarget().equals(messagePath)) {
          result.add(c);
        }
      }
    }
    return result;
  }

  private Constraints findAllConformanceStatement(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    Constraints constraints = new Constraints();
    Context dtContext = new Context();
    Context sContext = new Context();
    Context gContext = new Context();
    Context mContext = new Context();

    Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {
      ByID byID = new ByID();
      byID.setByID(m.getId());
      byID.setConformanceStatements(m.retrieveAllConformanceStatements());
      if (byID.getConformanceStatements().size() > 0)
        byNameOrByIDs.add(byID);
    }
    mContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {

      for (SegmentRefOrGroup sog : m.getChildren()) {
        if (sog instanceof Group) {
          byNameOrByIDs = findAllConformanceStatementsForGroup((Group) sog, byNameOrByIDs);
        }
      }
    }
    gContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      ByID byID = new ByID();
      byID.setByID(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"));
      List<ConformanceStatement> segmentConformanceStatements =
          s.retrieveAllConformanceStatementsForXML(tablesMap);
      if (segmentConformanceStatements.size() > 0) {
        byID.setConformanceStatements(segmentConformanceStatements);
        byNameOrByIDs.add(byID);
      }
    }
    sContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      ByID byID = new ByID();
      byID.setByID(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"));
      byID.setConformanceStatements(d.retrieveAllConformanceStatements());
      if (d.getName().equals("DTM"))
        this.generateConstraintsForDTMFormat(byID, d);
      if (byID.getConformanceStatements().size() > 0)
        byNameOrByIDs.add(byID);
    }
    dtContext.setByNameOrByIDs(byNameOrByIDs);

    constraints.setDatatypes(dtContext);
    constraints.setSegments(sContext);
    constraints.setGroups(gContext);
    constraints.setMessages(mContext);
    return constraints;
  }

  private void generateConstraintsForDTMFormat(ByID byID, Datatype d) {
    DTMConstraints dtmConstraints = d.getDtmConstraints();
    IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
    for (DTMComponentDefinition def : dtmConstraints.getDtmComponentDefinitions()) {
      if (def.getUsage().equals(Usage.R)) {
        ConformanceStatement cs = new ConformanceStatement();
        cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
            + def.getUsage().toString() + ")");
        cs.setConstraintTarget(".");
        cs.setDescription(def.getDescription() + " usage is '" + def.getUsage().toString() + "'.");

        String pattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
        String assertion =
            "<Assertion>" + "<Format Path=\".\" Regex=\"" + pattern + "\"/>" + "</Assertion>";
        cs.setAssertion(assertion);

        byID.getConformanceStatements().add(cs);
      } else if (def.getUsage().equals(Usage.X)) {
        ConformanceStatement cs = new ConformanceStatement();
        cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
            + def.getUsage().toString() + ")");
        cs.setConstraintTarget(".");
        cs.setDescription(def.getDescription() + " usage is '" + def.getUsage().toString() + "'.");

        String pattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
        String assertion =
            "<Assertion>" + "<Format Path=\".\" Regex=\"" + pattern + "\"/>" + "</Assertion>";
        cs.setAssertion(assertion);

        byID.getConformanceStatements().add(cs);
      } else if (def.getUsage().equals(Usage.C)) {
        if (def.getDtmPredicate() != null) {
          DTMPredicate predicate = def.getDtmPredicate();
          if (predicate.getTrueUsage() != null && predicate.getTrueUsage().equals(Usage.R)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if (predicate.getTarget() != null) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                    + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                  + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                  + predicate.getTrueUsage() + "'. Predicate is '"
                  + predicate.getPredicateDescription() + "'.");
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                  + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                  + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                    + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is not literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern1 =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><AND><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2
                    + "\"/>" + "</AND><Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }
          if (predicate.getTrueUsage() != null && predicate.getTrueUsage().equals(Usage.X)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if (predicate.getTarget() != null) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                    + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                  + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                  + predicate.getTrueUsage() + "'. Predicate is '"
                  + predicate.getPredicateDescription() + "'.");
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                  + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                  + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern
                    + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is not literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '"
                    + predicate.getTrueUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern1 =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><AND><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2
                    + "\"/>" + "</AND><Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                    + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }

          if (predicate.getFalseUsage() != null && predicate.getFalseUsage().equals(Usage.R)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if (predicate.getTarget() != null) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern
                    + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                  + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                  + predicate.getFalseUsage() + "'. Predicate is '"
                  + predicate.getPredicateDescription() + "'.");
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                  + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                  + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern
                    + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is not literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern1 =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion =
                    "<Assertion><IMPLY><NOT><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1
                        + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>"
                        + "</AND></NOT><Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                        + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }
          if (predicate.getFalseUsage() != null && predicate.getFalseUsage().equals(Usage.X)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if (predicate.getTarget() != null) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern
                    + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                  + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                  + predicate.getFalseUsage() + "'. Predicate is '"
                  + predicate.getPredicateDescription() + "'.");
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                  + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                  + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\""
                    + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern
                    + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null
                && predicate.getVerb().equals("is not literal value")) {
              if (predicate.getValue() != null && !predicate.getValue().equals("")) {
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE("
                    + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '"
                    + predicate.getFalseUsage() + "'. Predicate is '"
                    + predicate.getPredicateDescription() + "'.");
                String ifPattern1 =
                    config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion =
                    "<Assertion><IMPLY><NOT><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1
                        + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>"
                        + "</AND></NOT><Format Path=\".\" Regex=\"" + thenPattern + "\"/>"
                        + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }

        }
      }
    }


  }

  private Set<ByNameOrByID> findAllConformanceStatementsForGroup(Group g,
      Set<ByNameOrByID> byNameOrByIDs) {
    ByID byID = new ByID();
    byID.setByID(g.getId());
    if (g.getConformanceStatements().size() > 0) {
      byID.setConformanceStatements(g.getConformanceStatements());
      byNameOrByIDs.add(byID);
    }

    for (SegmentRefOrGroup sog : g.getChildren()) {
      if (sog instanceof Group) {
        byNameOrByIDs = findAllConformanceStatementsForGroup((Group) sog, byNameOrByIDs);
      }
    }

    return byNameOrByIDs;

  }

  private Element serializeConstaint(Constraint c, String type)
      throws ConstraintSerializationException {
    try {
      Element elmConstraint = new Element(type);

      if (c.getConstraintId() != null) {
        elmConstraint.addAttribute(new Attribute("ID", c.getConstraintId()));
      }

      if (c.getConstraintTarget() != null && !c.getConstraintTarget().equals(""))
        elmConstraint.addAttribute(new Attribute("Target", c.getConstraintTarget()));

      if (c instanceof Predicate) {
        Predicate pred = (Predicate) c;
        if (pred.getTrueUsage() != null)
          elmConstraint.addAttribute(new Attribute("TrueUsage", pred.getTrueUsage().value()));
        if (pred.getFalseUsage() != null)
          elmConstraint.addAttribute(new Attribute("FalseUsage", pred.getFalseUsage().value()));
      }

      if (c.getReference() != null) {
        Reference referenceObj = c.getReference();
        Element elmReference = new Element("Reference");
        if (referenceObj.getChapter() != null && !referenceObj.getChapter().equals(""))
          elmReference.addAttribute(new Attribute("Chapter", referenceObj.getChapter()));
        if (referenceObj.getSection() != null && !referenceObj.getSection().equals(""))
          elmReference.addAttribute(new Attribute("Section", referenceObj.getSection()));
        if (referenceObj.getPage() == 0)
          elmReference.addAttribute(new Attribute("Page", "" + referenceObj.getPage()));
        if (referenceObj.getUrl() != null && !referenceObj.getUrl().equals(""))
          elmReference.addAttribute(new Attribute("URL", referenceObj.getUrl()));
        elmConstraint.appendChild(elmReference);
      }
      Element elmDescription = new Element("Description");
      elmDescription.appendChild(c.getDescription());
      elmConstraint.appendChild(elmDescription);

      Node n = this.innerXMLHandler(c.getAssertion());
      if (n != null) {
        elmConstraint.appendChild(n);
      } else {
        return null;
      }

      return elmConstraint;
    } catch (Exception e) {
      throw new ConstraintSerializationException(e, c.getDescription());
    }


  }

  private Node innerXMLHandler(String xml) {
    if (xml != null) {
      Builder builder = new Builder(new NodeFactory());
      try {
        Document doc = builder.build(xml, null);
        return doc.getRootElement().copy();
      } catch (ValidityException e) {
        e.printStackTrace();
      } catch (ParsingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private Element serializeByNameOrByID(ByNameOrByID byNameOrByIDObj)
      throws ConstraintSerializationException {
    if (byNameOrByIDObj instanceof ByName) {
      ByName byNameObj = (ByName) byNameOrByIDObj;
      Element elmByName = new Element("ByName");
      elmByName.addAttribute(new Attribute("Name", byNameObj.getByName()));

      for (Constraint c : byNameObj.getPredicates()) {
        Element elmConstaint = this.serializeConstaint(c, "Predicate");
        if (elmConstaint != null)
          elmByName.appendChild(elmConstaint);
      }

      for (Constraint c : byNameObj.getConformanceStatements()) {
        Element elmConstaint = this.serializeConstaint(c, "Constraint");
        if (elmConstaint != null)
          elmByName.appendChild(elmConstaint);
      }

      return elmByName;
    } else if (byNameOrByIDObj instanceof ByID) {
      ByID byIDObj = (ByID) byNameOrByIDObj;
      Element elmByID = new Element("ByID");
      elmByID.addAttribute(new Attribute("ID", byIDObj.getByID()));

      for (Constraint c : byIDObj.getConformanceStatements()) {
        Element elmConstaint = this.serializeConstaint(c, "Constraint");
        if (elmConstaint != null)
          elmByID.appendChild(elmConstaint);
      }

      for (Constraint c : byIDObj.getPredicates()) {
        Element elmConstaint = this.serializeConstaint(c, "Predicate");
        if (elmConstaint != null)
          elmByID.appendChild(elmConstaint);
      }

      return elmByID;
    }

    return null;
  }

  private Element serializeMain(Element e, Constraints predicates,
      Constraints conformanceStatements) throws ConstraintSerializationException {
    Element predicates_Elm = new Element("Predicates");

    Element predicates_dataType_Elm = new Element("Datatype");
    for (ByNameOrByID byNameOrByIDObj : predicates.getDatatypes().getByNameOrByIDs()) {
      Element dataTypeConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (dataTypeConstaint != null)
        predicates_dataType_Elm.appendChild(dataTypeConstaint);
    }
    predicates_Elm.appendChild(predicates_dataType_Elm);

    Element predicates_segment_Elm = new Element("Segment");
    for (ByNameOrByID byNameOrByIDObj : predicates.getSegments().getByNameOrByIDs()) {
      Element segmentConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (segmentConstaint != null)
        predicates_segment_Elm.appendChild(segmentConstaint);
    }
    predicates_Elm.appendChild(predicates_segment_Elm);

    Element predicates_group_Elm = new Element("Group");
    for (ByNameOrByID byNameOrByIDObj : predicates.getGroups().getByNameOrByIDs()) {
      Element groupConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (groupConstaint != null)
        predicates_group_Elm.appendChild(groupConstaint);
    }
    predicates_Elm.appendChild(predicates_group_Elm);

    Element predicates_message_Elm = new Element("Message");
    for (ByNameOrByID byNameOrByIDObj : predicates.getMessages().getByNameOrByIDs()) {
      Element messageConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (messageConstaint != null)
        predicates_message_Elm.appendChild(messageConstaint);
    }
    predicates_Elm.appendChild(predicates_message_Elm);

    e.appendChild(predicates_Elm);

    Element constraints_Elm = new Element("Constraints");

    Element constraints_dataType_Elm = new Element("Datatype");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getDatatypes().getByNameOrByIDs()) {
      Element dataTypeConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (dataTypeConstaint != null)
        constraints_dataType_Elm.appendChild(dataTypeConstaint);
    }
    constraints_Elm.appendChild(constraints_dataType_Elm);

    Element constraints_segment_Elm = new Element("Segment");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getSegments().getByNameOrByIDs()) {
      Element segmentConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (segmentConstaint != null)
        constraints_segment_Elm.appendChild(segmentConstaint);
    }
    constraints_Elm.appendChild(constraints_segment_Elm);

    Element constraints_group_Elm = new Element("Group");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getGroups().getByNameOrByIDs()) {
      Element groupConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (groupConstaint != null)
        constraints_group_Elm.appendChild(groupConstaint);
    }
    constraints_Elm.appendChild(constraints_group_Elm);

    Element constraints_message_Elm = new Element("Message");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getMessages().getByNameOrByIDs()) {
      Element messageConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (messageConstaint != null)
        constraints_message_Elm.appendChild(messageConstaint);
    }
    constraints_Elm.appendChild(constraints_message_Elm);
    e.appendChild(constraints_Elm);

    return e;
  }

  private Set<ByNameOrByID> findAllPredicatesForGroup(Group g, Set<ByNameOrByID> byNameOrByIDs) {
    ByID byID = new ByID();
    byID.setByID(g.getId());
    if (g.getPredicates().size() > 0) {
      byID.setPredicates(g.getPredicates());
      byNameOrByIDs.add(byID);
    }

    for (SegmentRefOrGroup sog : g.getChildren()) {
      if (sog instanceof Group) {
        byNameOrByIDs = findAllPredicatesForGroup((Group) sog, byNameOrByIDs);
      }
    }

    return byNameOrByIDs;

  }

  private Constraints findAllPredicates(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    Constraints constraints = new Constraints();
    Context dtContext = new Context();
    Context sContext = new Context();
    Context gContext = new Context();
    Context mContext = new Context();

    Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {
      ByID byID = new ByID();
      byID.setByID(m.getId());
      if (m.getPredicates().size() > 0) {
        byID.setPredicates(m.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }
    mContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {

      for (SegmentRefOrGroup sog : m.getChildren()) {
        if (sog instanceof Group) {
          byNameOrByIDs = findAllPredicatesForGroup((Group) sog, byNameOrByIDs);
        }
      }
    }
    gContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      ByID byID = new ByID();
      byID.setByID(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"));
      if (s.getPredicates().size() > 0) {
        byID.setPredicates(s.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }

    sContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      ByID byID = new ByID();
      byID.setByID(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"));
      if (d.getPredicates().size() > 0) {
        byID.setPredicates(d.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }
    dtContext.setByNameOrByIDs(byNameOrByIDs);

    constraints.setGroups(gContext);
    constraints.setDatatypes(dtContext);
    constraints.setSegments(sContext);
    constraints.setMessages(mContext);
    return constraints;
  }

  private Element serializeDatatypeForValidation(Datatype d, Map<String, Table> tablesMap,
      Map<String, Datatype> datatypesMap) throws DatatypeSerializationException {

    try {
      Element elmDatatype = new Element("Datatype");
      elmDatatype.addAttribute(new Attribute("ID",
          this.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
      elmDatatype.addAttribute(new Attribute("Name", this.str(d.getName())));
      elmDatatype.addAttribute(new Attribute("Label", this.str(d.getLabel())));
      if (d.getDescription() == null || d.getDescription().equals("")) {
        elmDatatype.addAttribute(new Attribute("Description", "NoDesc"));
      } else {
        elmDatatype.addAttribute(new Attribute("Description", this.str(d.getDescription())));
      }


      if (d.getComponents() != null) {

        Map<Integer, Component> components = new HashMap<Integer, Component>();

        for (Component c : d.getComponents()) {
          components.put(c.getPosition(), c);
        }

        for (int i = 1; i < components.size() + 1; i++) {
          try {
            Component c = components.get(i);
            Datatype componentDatatype = datatypesMap.get(c.getDatatype().getId());
            if (componentDatatype == null)
              throw new DatatypeNotFoundException(c.getDatatype().getId(),
                  c.getDatatype().getLabel());
            Element elmComponent = new Element("Component");
            elmComponent.addAttribute(new Attribute("Name", this.str(c.getName())));
            elmComponent.addAttribute(new Attribute("Usage", this.str(c.getUsage().toString())));
            elmComponent
                .addAttribute(new Attribute("Datatype", this.str(componentDatatype.getLabel() + "_"
                    + componentDatatype.getHl7Version().replaceAll("\\.", "-"))));
            elmComponent.addAttribute(new Attribute("MinLength", this.str(c.getMinLength())));
            elmComponent.addAttribute(new Attribute("MaxLength", this.str(c.getMaxLength())));
            elmComponent.addAttribute(new Attribute("ConfLength", this.str(c.getConfLength())));

            List<ValueSetBinding> bindings = findBinding(d.getValueSetBindings(), c.getPosition());
            if (bindings.size() > 0) {
              String bindingString = "";
              String bindingStrength = null;
              String bindingLocation = null;

              for (ValueSetBinding binding : bindings) {
                Table table = tablesMap.get(binding.getTableId());
                if (table == null)
                  throw new TableNotFoundException(binding.getTableId());
                bindingStrength = binding.getBindingStrength().toString();
                bindingLocation = binding.getBindingLocation();
                if (table != null && table.getBindingIdentifier() != null
                    && !table.getBindingIdentifier().equals("")) {
                  if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                    if (table.getBindingIdentifier().startsWith("0396")
                        || table.getBindingIdentifier().startsWith("HL70396")) {
                      bindingString = bindingString + table.getBindingIdentifier() + ":";
                    } else {
                      bindingString = bindingString + table.getBindingIdentifier() + "_"
                          + table.getHl7Version().replaceAll("\\.", "-") + ":";
                    }
                  } else {
                    bindingString = bindingString + table.getBindingIdentifier() + ":";
                  }
                }
              }

              IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
              DTComponent dtComponent = new DTComponent();
              dtComponent.setDtName(componentDatatype.getName());
              dtComponent.setLocation(c.getPosition());
              if (config.getValueSetAllowedDTs().contains(componentDatatype.getName())
                  || config.getValueSetAllowedComponents().contains(dtComponent)) {
                if (!bindingString.equals(""))
                  elmComponent.addAttribute(new Attribute("Binding",
                      bindingString.substring(0, bindingString.length() - 1)));
                if (bindingStrength != null)
                  elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));

                if (componentDatatype != null && componentDatatype.getComponents() != null
                    && componentDatatype.getComponents().size() > 0) {
                  if (bindingLocation != null && !bindingLocation.equals("")) {
                    bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
                    elmComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
                  } else {
                    elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
                  }
                }
              }
            }

            if (c.isHide())
              elmComponent.addAttribute(new Attribute("Hide", "true"));

            elmDatatype.appendChild(elmComponent);
          } catch (Exception e) {
            throw new DatatypeComponentSerializationException(e, i);
          }

        }
      }
      return elmDatatype;
    } catch (Exception e) {
      throw new DatatypeSerializationException(e, d.getLabel());
    }


  }

  private Element serializeSegment(Segment s, Map<String, Table> tablesMap,
      Map<String, Datatype> datatypesMap) throws SegmentSerializationException {
    try {
      Element elmSegment = new Element("Segment");
      elmSegment.addAttribute(
          new Attribute("ID", s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-")));
      elmSegment.addAttribute(new Attribute("Name", this.str(s.getName())));
      elmSegment.addAttribute(new Attribute("Label", this.str(s.getLabel())));
      if (s.getDescription() == null || s.getDescription().equals("")) {
        elmSegment.addAttribute(new Attribute("Description", "NoDesc"));
      } else {
        elmSegment.addAttribute(new Attribute("Description", this.str(s.getDescription())));
      }

      if (s.getName().equals("OBX") || s.getName().equals("MFA") || s.getName().equals("MFE")) {
        String targetPosition = null;
        String reference = null;
        String secondReference = null;
        String referenceTableId = null;
        HashMap<String, Datatype> dm = new HashMap<String, Datatype>();
        HashMap<String, Datatype> dm2nd = new HashMap<String, Datatype>();

        if (s.getName().equals("OBX")) {
          targetPosition = "5";
          reference = "2";
        }

        if (s.getName().equals("MFA")) {
          targetPosition = "5";
          reference = "6";
        }

        if (s.getName().equals("MFE")) {
          targetPosition = "4";
          reference = "5";
        }

        if (s.getCoConstraintsTable() != null
            && s.getCoConstraintsTable().getIfColumnDefinition() != null) {
          if (s.getCoConstraintsTable().getIfColumnDefinition().isPrimitive()) {
            secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath();
          } else {
            secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath() + ".1";
          }
        }

        referenceTableId = this.findValueSetID(s.getValueSetBindings(), reference);

        if (referenceTableId != null) {
          Table table = tablesMap.get(referenceTableId);
          String hl7Version = null;
          hl7Version = table.getHl7Version();
          if (hl7Version == null)
            hl7Version = s.getHl7Version();

          if (table != null) {
            for (Code c : table.getCodes()) {
              if (c.getValue() != null) {
                Datatype d =
                    this.findHL7DatatypeByNameAndVesion(datatypesMap, c.getValue(), hl7Version);
                if (d != null) {
                  dm.put(c.getValue(), d);
                }
              }
            }
          }
          if (s.getDynamicMappingDefinition() != null) {
            for (DynamicMappingItem item : s.getDynamicMappingDefinition()
                .getDynamicMappingItems()) {
              if (item.getFirstReferenceValue() != null && item.getDatatypeId() != null)
                dm.put(item.getFirstReferenceValue(), datatypesMap.get(item.getDatatypeId()));
            }
          }
        }
        if (secondReference != null) {
          for (CoConstraintColumnDefinition definition : s.getCoConstraintsTable()
              .getThenColumnDefinitionList()) {
            if (definition.isdMReference()) {
              List<CoConstraintTHENColumnData> dataList =
                  s.getCoConstraintsTable().getThenMapData().get(definition.getId());

              if (dataList != null && s.getCoConstraintsTable().getIfColumnData() != null) {
                for (int i = 0; i < dataList.size(); i++) {
                  CoConstraintIFColumnData ref = s.getCoConstraintsTable().getIfColumnData().get(i);
                  CoConstraintTHENColumnData data = dataList.get(i);

                  if (ref != null && data != null && ref.getValueData() != null
                      && ref.getValueData().getValue() != null && data.getDatatypeId() != null
                      && data.getValueData() != null && data.getValueData().getValue() != null) {
                    dm2nd.put(ref.getValueData().getValue(),
                        datatypesMap.get(data.getDatatypeId()));
                  }
                }
              }
            }
          }
        }

        if (dm.size() > 0 || dm2nd.size() > 0) {
          Element elmDynamicMapping = new Element("DynamicMapping");
          Element elmMapping = new Element("Mapping");
          elmMapping.addAttribute(new Attribute("Position", targetPosition));
          elmMapping.addAttribute(new Attribute("Reference", reference));
          if (secondReference != null)
            elmMapping.addAttribute(new Attribute("SecondReference", secondReference));

          for (String key : dm.keySet()) {
            Element elmCase = new Element("Case");
            Datatype d = dm.get(key);
            elmCase.addAttribute(new Attribute("Value", d.getName()));
            elmCase.addAttribute(new Attribute("Datatype",
                d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
            elmMapping.appendChild(elmCase);
          }

          for (String key : dm2nd.keySet()) {
            Element elmCase = new Element("Case");
            Datatype d = dm2nd.get(key);
            if (d != null) {
              elmCase.addAttribute(new Attribute("Value", d.getName()));
              elmCase.addAttribute(new Attribute("SecondValue", key));
              elmCase.addAttribute(new Attribute("Datatype",
                  d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
              elmMapping.appendChild(elmCase);
            }

          }
          elmDynamicMapping.appendChild(elmMapping);
          elmSegment.appendChild(elmDynamicMapping);
        }
      }

      Map<Integer, Field> fields = new HashMap<Integer, Field>();

      for (Field f : s.getFields()) {
        fields.put(f.getPosition(), f);
      }

      for (int i = 1; i < fields.size() + 1; i++) {
        try {
          Field f = fields.get(i);

          if (f != null) {
            if (f.getDatatype() != null && !datatypesMap.containsKey(f.getDatatype().getId())) {
              throw new DatatypeNotFoundException(f.getDatatype().getId());
            }

            Datatype d = datatypesMap.get(f.getDatatype().getId());

            Element elmField = new Element("Field");
            elmField.addAttribute(new Attribute("Name", this.str(f.getName())));
            elmField.addAttribute(new Attribute("Usage", this.str(f.getUsage().toString())));
            elmField.addAttribute(new Attribute("Datatype",
                this.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
            elmField.addAttribute(new Attribute("MinLength", this.str(f.getMinLength())));
            elmField.addAttribute(new Attribute("MaxLength", this.str(f.getMaxLength())));
            elmField.addAttribute(new Attribute("ConfLength", this.str(f.getConfLength())));

            if (f.getConfLength() != null && !f.getConfLength().equals(""))
              elmField.addAttribute(new Attribute("ConfLength", this.str(f.getConfLength())));

            List<ValueSetBinding> bindings = findBinding(s.getValueSetBindings(), f.getPosition());
            if (bindings.size() > 0) {
              String bindingString = "";
              String bindingStrength = null;
              String bindingLocation = null;

              for (ValueSetBinding binding : bindings) {
                try {
                  Table table = tablesMap.get(binding.getTableId());
                  bindingStrength = binding.getBindingStrength().toString();
                  bindingLocation = binding.getBindingLocation();
                  if (table != null && table.getBindingIdentifier() != null
                      && !table.getBindingIdentifier().equals("")) {
                    if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                      if (table.getBindingIdentifier().startsWith("0396")
                          || table.getBindingIdentifier().startsWith("HL70396")) {
                        bindingString = bindingString + table.getBindingIdentifier() + ":";
                      } else {
                        bindingString = bindingString + table.getBindingIdentifier() + "_"
                            + table.getHl7Version().replaceAll("\\.", "-") + ":";
                      }
                    } else {
                      bindingString = bindingString + table.getBindingIdentifier() + ":";
                    }
                  }
                } catch (Exception e) {
                  throw new TableSerializationException(e, binding.getLocation());
                }


              }

              IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
              if (config.getValueSetAllowedDTs().contains(d.getName())) {
                if (!bindingString.equals(""))
                  elmField.addAttribute(new Attribute("Binding",
                      bindingString.substring(0, bindingString.length() - 1)));
                if (bindingStrength != null)
                  elmField.addAttribute(new Attribute("BindingStrength", bindingStrength));

                if (d != null && d.getComponents() != null && d.getComponents().size() > 0) {
                  if (bindingLocation != null && !bindingLocation.equals("")) {
                    bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
                    elmField.addAttribute(new Attribute("BindingLocation", bindingLocation));
                  } else {
                    elmField.addAttribute(new Attribute("BindingLocation", "1"));
                  }
                }
              }
            }

            if (f.isHide())
              elmField.addAttribute(new Attribute("Hide", "true"));
            elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
            elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
            if (f.getItemNo() != null && !f.getItemNo().equals(""))
              elmField.addAttribute(new Attribute("ItemNo", this.str(f.getItemNo())));
            elmSegment.appendChild(elmField);
          }
        } catch (Exception e) {
          throw new FieldSerializationException(e, "Field[" + i + "]");
        }
      }
      return elmSegment;
    } catch (Exception e) {
      throw new SegmentSerializationException(e, s.getLabel());
    }
  }

  private Datatype findHL7DatatypeByNameAndVesion(Map<String, Datatype> datatypesMap, String value,
      String hl7Version) {
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      if (d.getName().equals(value) && d.getHl7Version().equals(hl7Version)
          && d.getScope().toString().equals("HL7STANDARD"))
        return d;
    }
    return null;
  }

  private String findValueSetID(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      String referenceLocation) {
    for (ValueSetOrSingleCodeBinding vsb : valueSetBindings) {
      if (vsb.getLocation().equals(referenceLocation))
        return vsb.getTableId();
    }
    return null;
  }

  private List<ValueSetBinding> findBinding(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      Integer position) {
    List<ValueSetBinding> result = new ArrayList<ValueSetBinding>();
    if (valueSetBindings != null && position != null) {
      for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;

          if (valueSetBinding.getLocation().equals("" + position)) {
            result.add(valueSetBinding);
          }
        }
      }
    }
    return result;
  }

  private Element serializeMessage(Message m, Map<String, Segment> segmentsMap)
      throws MessageSerializationException {
    try {
      Element elmMessage = new Element("Message");
      elmMessage.addAttribute(new Attribute("ID", m.getId()));
      if (m.getIdentifier() != null && !m.getIdentifier().equals(""))
        elmMessage.addAttribute(new Attribute("Identifier", this.str(m.getIdentifier())));
      if (m.getName() != null && !m.getName().equals(""))
        elmMessage.addAttribute(new Attribute("Name", this.str(m.getName())));
      elmMessage.addAttribute(new Attribute("Type", this.str(m.getMessageType())));
      elmMessage.addAttribute(new Attribute("Event", this.str(m.getEvent())));
      elmMessage.addAttribute(new Attribute("StructID", this.str(m.getStructID())));
      if (m.getDescription() != null && !m.getDescription().equals(""))
        elmMessage.addAttribute(new Attribute("Description", this.str(m.getDescription())));

      Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
          new HashMap<Integer, SegmentRefOrGroup>();

      for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
        segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
      }

      for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
        SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
        if (segmentRefOrGroup instanceof SegmentRef) {
          elmMessage.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
        } else if (segmentRefOrGroup instanceof Group) {
          elmMessage.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
        }
      }

      return elmMessage;
    } catch (Exception e) {
      throw new MessageSerializationException(e, m != null ? m.getName() : "");
    }
  }

  private Element serializeGroup(Group group, Map<String, Segment> segmentsMap)
      throws SerializationException {
    try {
      Element elmGroup = new Element("Group");
      elmGroup.addAttribute(new Attribute("ID", this.str(group.getId())));
      elmGroup.addAttribute(new Attribute("Name", this.str(group.getName())));
      elmGroup.addAttribute(new Attribute("Usage", this.str(group.getUsage().value())));
      elmGroup.addAttribute(new Attribute("Min", this.str(group.getMin() + "")));
      elmGroup.addAttribute(new Attribute("Max", this.str(group.getMax())));

      Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
          new HashMap<Integer, SegmentRefOrGroup>();

      for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
        segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
      }

      for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
        SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
        if (segmentRefOrGroup instanceof SegmentRef) {
          elmGroup.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
        } else if (segmentRefOrGroup instanceof Group) {
          elmGroup.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
        }
      }

      return elmGroup;
    } catch (Exception e) {
      if (group != null) {
        throw new GroupSerializationException(e, group.getName());
      }
    }
    return null;
  }

  private Element serializeSegmentRef(SegmentRef segmentRef, Map<String, Segment> segmentsMap)
      throws SerializationException {
    try {
      Segment s = segmentsMap.get(segmentRef.getRef().getId());
      Element elmSegment = new Element("Segment");
      elmSegment.addAttribute(new Attribute("Ref",
          this.str(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"))));
      elmSegment.addAttribute(new Attribute("Usage", this.str(segmentRef.getUsage().value())));
      elmSegment.addAttribute(new Attribute("Min", this.str(segmentRef.getMin() + "")));
      elmSegment.addAttribute(new Attribute("Max", this.str(segmentRef.getMax())));
      return elmSegment;
    } catch (Exception e) {
      if (segmentRef != null) {
        throw new SegmentSerializationException(e, segmentRef.getRef().getLabel());
      }
    }
    return null;

  }

  private String str(String value) {
    return value != null ? value : "";
  }

  private void serializeProfileMetaData(Element e, Profile profile, DocumentMetaData igMetaData,
      String type) {

    if (type.equals("Validation")) {
      Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
          "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/Profile.xsd");
      schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      e.addAttribute(schemaDecl);
    } else if (type.equals("Display")) {
      Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
          "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Display%20Schema/Profile.xsd");
      schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      e.addAttribute(schemaDecl);
    }

    e.addAttribute(new Attribute("ID", profile.getId()));
    ProfileMetaData metaData = profile.getMetaData();
    if (metaData.getType() != null && !metaData.getType().equals(""))
      e.addAttribute(new Attribute("Type", this.str(metaData.getType())));
    if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
      e.addAttribute(new Attribute("HL7Version", this.str(metaData.getHl7Version())));
    if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
      e.addAttribute(new Attribute("SchemaVersion", this.str(metaData.getSchemaVersion())));

    Element elmMetaData = new Element("MetaData");
    elmMetaData.addAttribute(new Attribute("Name", !this.str(igMetaData.getTitle()).equals("")
        ? this.str(igMetaData.getTitle()) : "No Title Info"));
    elmMetaData.addAttribute(new Attribute("OrgName", !this.str(igMetaData.getOrgName()).equals("")
        ? this.str(igMetaData.getOrgName()) : "No Org Info"));
    elmMetaData.addAttribute(new Attribute("Version", !this.str(igMetaData.getVersion()).equals("")
        ? this.str(igMetaData.getVersion()) : "No Version Info"));
    elmMetaData.addAttribute(new Attribute("Date", !this.str(igMetaData.getDate()).equals("")
        ? this.str(igMetaData.getDate()) : "No Date Info"));

    if (metaData.getSpecificationName() != null && !metaData.getSpecificationName().equals(""))
      elmMetaData.addAttribute(
          new Attribute("SpecificationName", this.str(metaData.getSpecificationName())));
    if (metaData.getStatus() != null && !metaData.getStatus().equals(""))
      elmMetaData.addAttribute(new Attribute("Status", this.str(metaData.getStatus())));
    if (metaData.getTopics() != null && !metaData.getTopics().equals(""))
      elmMetaData.addAttribute(new Attribute("Topics", this.str(metaData.getTopics())));

    e.appendChild(elmMetaData);
  }

  private void generateProfileIS(ZipOutputStream out, String profileXML) throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("Profile.xml"));
    InputStream inProfile = IOUtils.toInputStream(profileXML);
    int lenTP;
    while ((lenTP = inProfile.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inProfile.close();
  }

  private void generateValueSetIS(ZipOutputStream out, String valueSetXML) throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("ValueSets.xml"));
    InputStream inValueSet = IOUtils.toInputStream(valueSetXML);
    int lenTP;
    while ((lenTP = inValueSet.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inValueSet.close();
  }

  private void generateConstraintsIS(ZipOutputStream out, String constraintsXML)
      throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("Constraints.xml"));
    InputStream inConstraints = IOUtils.toInputStream(constraintsXML);
    int lenTP;
    while ((lenTP = inConstraints.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inConstraints.close();
  }

  private void normalizeProfile(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap) throws CloneNotSupportedException {
    Map<String, Datatype> toBeAddedDTs = new HashMap<String, Datatype>();
    Map<String, Segment> toBeAddedSegs = new HashMap<String, Segment>();

    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      for (ValueSetOrSingleCodeBinding binding : d.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(d.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));

          if (pathList.size() > 1) {
            Component c = d.findComponentByPosition(Integer.parseInt(pathList.remove(0)));

            Datatype childD = datatypesMap.get(c.getDatatype().getId());
            if (childD == null)
              childD = toBeAddedDTs.get(c.getDatatype().getId());
            Datatype copyD = childD.clone();

            int randumNum = new SecureRandom().nextInt(100000);
            copyD.setId(d.getId() + "_A" + randumNum);
            String ext = d.getExt();
            if (ext == null)
              ext = "";
            copyD.setExt(ext + "_A" + randumNum);
            toBeAddedDTs.put(copyD.getId(), copyD);
            c.getDatatype().setId(copyD.getId());

            visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
          }
        }
      }
    }

    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(s.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));

          if (pathList.size() > 1) {
            Field f = s.findFieldByPosition(Integer.parseInt(pathList.remove(0)));

            Datatype d = datatypesMap.get(f.getDatatype().getId());
            if (d == null)
              d = toBeAddedDTs.get(f.getDatatype().getId());
            Datatype copyD = d.clone();

            int randumNum = new SecureRandom().nextInt(100000);
            copyD.setId(d.getId() + "_A" + randumNum);
            String ext = d.getExt();
            if (ext == null)
              ext = "";
            copyD.setExt(ext + "_A" + randumNum);
            toBeAddedDTs.put(copyD.getId(), copyD);
            f.getDatatype().setId(copyD.getId());

            visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
          }

        }
      }

    }

    for (Message m : profile.getMessages().getChildren()) {
      for (ValueSetOrSingleCodeBinding binding : m.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(m.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));
          SegmentRefOrGroup child = m.findChildByPosition(Integer.parseInt(pathList.remove(0)));
          visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings,
              toBeAddedDTs, toBeAddedSegs);
        }
      }
    }
    for (String key : toBeAddedDTs.keySet()) {
      datatypesMap.put(key, toBeAddedDTs.get(key));
    }
    for (String key : toBeAddedSegs.keySet()) {
      segmentsMap.put(key, toBeAddedSegs.get(key));
    }
  }

  private List<ValueSetBinding> findvalueSetBinding(
      List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {
    List<ValueSetBinding> resutls = new ArrayList<ValueSetBinding>();
    for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
      if (binding instanceof ValueSetBinding) {
        ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
        if (valueSetBinding.getLocation().equals(location))
          resutls.add(valueSetBinding);
      }
    }
    return resutls;
  }

  private void visitDatatype(List<String> pathList, Datatype datatype,
      Map<String, Datatype> datatypesMap, List<ValueSetBinding> valueSetBindings,
      Map<String, Datatype> toBeAddedDTs) throws CloneNotSupportedException {
    if (pathList.size() == 1) {
      List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
      for (ValueSetBinding binding : valueSetBindings) {
        ValueSetBinding newValueSetBinding = binding.clone();
        newValueSetBinding.setLocation(pathList.get(0));
        newValueSetBindings.add(newValueSetBinding);
      }
      List<ValueSetOrSingleCodeBinding> toBeDeleted =
          this.findToBeDeletedValueSetBindinigsByLocation(datatype.getValueSetBindings(),
              pathList.get(0));

      for (ValueSetOrSingleCodeBinding binding : toBeDeleted) {
        datatype.getValueSetBindings().remove(binding);
      }

      datatype.getValueSetBindings().addAll(newValueSetBindings);

    } else if (pathList.size() > 1) {
      Component c = datatype.findComponentByPosition(Integer.parseInt(pathList.remove(0)));

      Datatype d = datatypesMap.get(c.getDatatype().getId());
      if (d == null)
        d = toBeAddedDTs.get(c.getDatatype().getId());
      Datatype copyD = d.clone();

      int randumNum = new SecureRandom().nextInt(100000);
      copyD.setId(d.getId() + "_A" + randumNum);
      String ext = d.getExt();
      if (ext == null)
        ext = "";
      copyD.setExt(ext + "_A" + randumNum);
      toBeAddedDTs.put(copyD.getId(), copyD);
      c.getDatatype().setId(copyD.getId());
      visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
    }

  }

  private List<ValueSetOrSingleCodeBinding> findToBeDeletedValueSetBindinigsByLocation(
      List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {

    List<ValueSetOrSingleCodeBinding> toBeDeleted = new ArrayList<ValueSetOrSingleCodeBinding>();

    for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
      if (binding.getLocation().equals(location)) {
        toBeDeleted.add(binding);
      }
    }

    return toBeDeleted;
  }

  private void visitGroupOrSegmentRef(List<String> pathList, SegmentRefOrGroup srog,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      List<ValueSetBinding> valueSetBindings, Map<String, Datatype> toBeAddedDTs,
      Map<String, Segment> toBeAddedSegs) throws CloneNotSupportedException {
    if (srog instanceof Group) {
      Group g = (Group) srog;
      SegmentRefOrGroup child = g.findChildByPosition(Integer.parseInt(pathList.remove(0)));
      visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings,
          toBeAddedDTs, toBeAddedSegs);
    } else {
      SegmentRef sr = (SegmentRef) srog;
      Segment s = segmentsMap.get(sr.getRef().getId());
      if (s == null)
        s = toBeAddedSegs.get(sr.getRef().getId());
      Segment copyS = s.clone();
      int randumNum = new SecureRandom().nextInt(100000);
      copyS.setId(s.getId() + "_A" + randumNum);
      String ext = s.getExt();
      if (ext == null)
        ext = "";
      copyS.setExt(ext + "_A" + randumNum);

      if (pathList.size() == 1) {
        List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
        for (ValueSetBinding binding : valueSetBindings) {
          ValueSetBinding newValueSetBinding = binding.clone();
          newValueSetBinding.setLocation(pathList.get(0));
          newValueSetBindings.add(newValueSetBinding);
        }
        List<ValueSetOrSingleCodeBinding> toBeDeleted =
            this.findToBeDeletedValueSetBindinigsByLocation(copyS.getValueSetBindings(),
                pathList.get(0));
        for (ValueSetOrSingleCodeBinding binding : toBeDeleted) {
          copyS.getValueSetBindings().remove(binding);
        }
        copyS.getValueSetBindings().addAll(newValueSetBindings);

      } else if (pathList.size() > 1) {
        Field f = copyS.findFieldByPosition(Integer.parseInt(pathList.remove(0)));
        Datatype d = datatypesMap.get(f.getDatatype().getId());
        if (d == null)
          d = toBeAddedDTs.get(f.getDatatype().getId());
        Datatype copyD = d.clone();

        randumNum = new SecureRandom().nextInt(100000);
        copyD.setId(d.getId() + "_A" + randumNum);
        String ext2 = d.getExt();
        if (ext2 == null)
          ext2 = "";
        copyD.setExt(ext2 + "_A" + randumNum);
        toBeAddedDTs.put(copyD.getId(), copyD);
        f.getDatatype().setId(copyD.getId());
        visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
      }
      sr.getRef().setId(copyS.getId());
      toBeAddedSegs.put(copyS.getId(), copyS);
    }
  }

  public InputStream exportXMLAsGazelleFormatForSelectedMessages(Profile profile,
      DocumentMetaData metadata, HashMap<String, Segment> segmentsMap,
      HashMap<String, Datatype> datatypesMap, HashMap<String, Table> tablesMap)
      throws CloneNotSupportedException, IOException, ProfileSerializationException, TableSerializationException {
    this.normalizeProfile(profile, segmentsMap, datatypesMap);

    ByteArrayOutputStream outputStream = null;
    byte[] bytes;
    outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    String profileXMLStr =
        this.serializeProfileGazelleToXML(profile, metadata, segmentsMap, datatypesMap, tablesMap)
            .toXML();
    String valueSetXMLStr = this.serializeTablesMapToGazelleElement(tablesMap).toXML();

    this.generateProfileIS(out, profileXMLStr);
    this.generateValueSetIS(out, valueSetXMLStr);

    out.close();
    bytes = outputStream.toByteArray();
    return new ByteArrayInputStream(bytes);
  }

  private Document serializeProfileGazelleToXML(Profile profile, DocumentMetaData metadata,
      HashMap<String, Segment> segmentsMap, HashMap<String, Datatype> datatypesMap,
      HashMap<String, Table> tablesMap) throws ProfileSerializationException {
    try{
      Element e = new Element("HL7v2xConformanceProfile");
      Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
          "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/Gazelle%20Schema/HL7MessageProfile.xsd");
      schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      e.addAttribute(schemaDecl);
      e.addAttribute(new Attribute("HL7Version",
          this.str(profile.getMetaData().getHl7Version().replaceAll("\\.", "-"))));
      if(profile.getMetaData().getType().equals("HL7") || profile.getMetaData().getType().equals("Implementation") || profile.getMetaData().getType().equals("Constrainable")){
        e.addAttribute(new Attribute("ProfileType", this.str(profile.getMetaData().getType())));  
      }else {
        e.addAttribute(new Attribute("ProfileType", "Implementation"));  
      }
      
      Element metadataElm = new Element("MetaData");
      metadataElm.addAttribute(new Attribute("Name", this.str(metadata.getTitle())));
      metadataElm
          .addAttribute(new Attribute("OrgName", this.str(profile.getMetaData().getOrgName())));
      metadataElm
          .addAttribute(new Attribute("Version", this.str(profile.getMetaData().getVersion())));
      e.appendChild(metadataElm);

      Element impNoteElm = new Element("ImpNote");
      impNoteElm.appendChild(this.str(metadata.getTitle()));
      e.appendChild(impNoteElm);

      Element useCaseElm = new Element("UseCase");
      e.appendChild(useCaseElm);

      Element encodingsElm = new Element("Encodings");
      Element encodingElm = new Element("Encoding");
      encodingElm.appendChild("ER7");
      encodingsElm.appendChild(encodingElm);
      e.appendChild(encodingsElm);

      Element dynamicDefElm = new Element("DynamicDef");
      dynamicDefElm.addAttribute(new Attribute("AccAck", "NE"));
      dynamicDefElm.addAttribute(new Attribute("AppAck", "AL"));
      dynamicDefElm.addAttribute(new Attribute("MsgAckMode", "Deferred"));
      e.appendChild(dynamicDefElm);

      for (Message message : profile.getMessages().getChildren()) {
        try{
          Element hL7v2xStaticDefElm = new Element("HL7v2xStaticDef");
          hL7v2xStaticDefElm.addAttribute(new Attribute("MsgType", this.str(message.getMessageType())));
          hL7v2xStaticDefElm.addAttribute(new Attribute("EventType", this.str(message.getEvent())));
          hL7v2xStaticDefElm
              .addAttribute(new Attribute("MsgStructID", this.str(message.getStructID())));
          hL7v2xStaticDefElm
              .addAttribute(new Attribute("EventDesc", this.str(message.getDescription())));

          Element metadataMessageElm = new Element("MetaData");
          metadataMessageElm.addAttribute(new Attribute("Name", this.str(message.getName())));
          metadataMessageElm
              .addAttribute(new Attribute("OrgName", this.str(profile.getMetaData().getOrgName())));
          hL7v2xStaticDefElm.appendChild(metadataMessageElm);

          Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
              new HashMap<Integer, SegmentRefOrGroup>();
          for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
            segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
          }
          for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
            String path = i + "[1]";
            SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
            if (segmentRefOrGroup instanceof SegmentRef) {
              hL7v2xStaticDefElm.appendChild(serializeGazelleSegment((SegmentRef) segmentRefOrGroup,
                  profile, message, path, segmentsMap, datatypesMap, tablesMap));
            } else if (segmentRefOrGroup instanceof Group) {
              hL7v2xStaticDefElm.appendChild(serializeGazelleGroup((Group) segmentRefOrGroup, profile,
                  message, path, segmentsMap, datatypesMap, tablesMap));
            }
          }
          e.appendChild(hL7v2xStaticDefElm);
        }catch (Exception e1) {
          throw new MessageSerializationException(e1, message != null ? message.getName() : "");
        }
        
      }
      Document doc = new Document(e);
      return doc;
    }catch (Exception e) {
      throw new ProfileSerializationException(e, profile != null ? profile.getId() : "");
    }
  }

  private Element serializeGazelleGroup(Group group, Profile profile, Message message, String path,
      HashMap<String, Segment> segmentsMap, HashMap<String, Datatype> datatypesMap,
      HashMap<String, Table> tablesMap) throws GroupSerializationException {
    try{
      Element elmSegGroup = new Element("SegGroup");
      if (group.getName().contains(".")) {
        elmSegGroup.addAttribute(new Attribute("Name",
            this.str(group.getName().substring(group.getName().lastIndexOf(".") + 1))));
      } else {
        elmSegGroup.addAttribute(new Attribute("Name", this.str(group.getName())));
      }

      elmSegGroup.addAttribute(new Attribute("LongName", this.str(group.getName())));
      if (group.getUsage().value().equals("B")) {
        elmSegGroup.addAttribute(new Attribute("Usage", "X"));
      } else {
        elmSegGroup.addAttribute(new Attribute("Usage", this.str(group.getUsage().value())));
      }
      elmSegGroup.addAttribute(new Attribute("Min", this.str(group.getMin() + "")));
      if (group.getMax().equals("0")) {
        elmSegGroup.addAttribute(new Attribute("Max", "" + 1));
      } else {
        elmSegGroup.addAttribute(new Attribute("Max", this.str(group.getMax())));
      }
      List<ConformanceStatement> groupConformanceStatements =
          this.findConformanceStatements(null, null, message.getConformanceStatements(), path);

      if (groupConformanceStatements.size() > 0) {
        Element elmImpNote = new Element("ImpNote");
        String note = "";
        for (ConformanceStatement c : groupConformanceStatements) {
          try{
            note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription(); 
          }catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }
        }
        elmImpNote.appendChild(note);
        elmSegGroup.appendChild(elmImpNote);
      }

      Predicate groupPredicate = this.findPredicate(null, null, message.getPredicates(), path);
      if (groupPredicate != null) {
        try{
          Element elmPredicate = new Element("Predicate");
          String note = "[C(" + groupPredicate.getTrueUsage() + "/" + groupPredicate.getFalseUsage()
              + ")]" + groupPredicate.getDescription();
          elmPredicate.appendChild(note);
          elmSegGroup.appendChild(elmPredicate); 
        }catch (Exception e) {
          throw new ConstraintSerializationException(e, groupPredicate.getDescription());
        }
      }

      Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

      for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
        segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
      }

      for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
        String childPath = path + "." + i + "[1]";
        SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
        if (segmentRefOrGroup instanceof SegmentRef) {
          elmSegGroup.appendChild(serializeGazelleSegment((SegmentRef) segmentRefOrGroup, profile,
              message, childPath, segmentsMap, datatypesMap, tablesMap));
        } else if (segmentRefOrGroup instanceof Group) {
          elmSegGroup.appendChild(serializeGazelleGroup((Group) segmentRefOrGroup, profile, message,
              childPath, segmentsMap, datatypesMap, tablesMap));
        }
      }

      return elmSegGroup;
    }catch (Exception e) {
      if (group != null) {
        throw new GroupSerializationException(e, group.getName());
      }
    }
    return null;
  }

  private Element serializeGazelleSegment(SegmentRef segmentRef, Profile profile, Message message,
      String path, HashMap<String, Segment> segmentsMap, HashMap<String, Datatype> datatypesMap,
      HashMap<String, Table> tablesMap) throws SegmentSerializationException {
    Element elmSegment = new Element("Segment");

    Segment segment = segmentsMap.get(segmentRef.getRef().getId());
    
    try{
      elmSegment.addAttribute(new Attribute("Name", this.str(segment.getName())));
      elmSegment.addAttribute(new Attribute("LongName", this.str(segment.getDescription())));
      if (segmentRef.getUsage().value().equals("B")) {
        elmSegment.addAttribute(new Attribute("Usage", "X"));
      } else {
        elmSegment.addAttribute(new Attribute("Usage", this.str(segmentRef.getUsage().value())));
      }
      elmSegment.addAttribute(new Attribute("Min", this.str(segmentRef.getMin() + "")));

      if (segmentRef.getMax().equals("0")) {
        elmSegment.addAttribute(new Attribute("Max", "" + 1));
      } else {
        elmSegment.addAttribute(new Attribute("Max", this.str(segmentRef.getMax())));
      }

      List<ConformanceStatement> segmentConformanceStatements =
          this.findConformanceStatements(null, null, message.getConformanceStatements(), path);
      if (segmentConformanceStatements.size() > 0) {
        Element elmImpNote = new Element("ImpNote");
        String note = "";
        for (ConformanceStatement c : segmentConformanceStatements) {
          try{
            note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription(); 
          }catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }
        }
        elmImpNote.appendChild(note);
        elmSegment.appendChild(elmImpNote);
      }

      Predicate segmentPredicate = this.findPredicate(null, null, message.getPredicates(), path);
      if (segmentPredicate != null) {
        try{
          Element elmPredicate = new Element("Predicate");
          String note = "[C(" + segmentPredicate.getTrueUsage() + "/" + segmentPredicate.getFalseUsage()
              + ")]" + segmentPredicate.getDescription();
          elmPredicate.appendChild(note);
          elmSegment.appendChild(elmPredicate);          
        }catch (Exception e) {
          throw new ConstraintSerializationException(e, segmentPredicate.getDescription());
        }
      }

      Map<Integer, Field> fields = new HashMap<Integer, Field>();
      for (Field f : segment.getFields()) {
        fields.put(f.getPosition(), f);
      }

      for (int i = 1; i < fields.size() + 1; i++) {
        String fieldPath = path + "." + i + "[1]";
        Field f = fields.get(i);
        if (f != null) {
          this.serializeGazelleField(f, datatypesMap.get(f.getDatatype().getId()), elmSegment,
              profile, message, segment, fieldPath, datatypesMap, tablesMap);
        }
      }
      return elmSegment;
    }catch (Exception e) {
      throw new SegmentSerializationException(e, segment.getLabel());
    }
  }

  private void serializeGazelleField(Field f, Datatype fieldDatatype, Element elmParent,
      Profile profile, Message message, Segment segment, String fieldPath,
      HashMap<String, Datatype> datatypesMap, HashMap<String, Table> tablesMap) throws FieldSerializationException {
    try{
      if (f.getDatatype() != null && fieldDatatype == null) {
        throw new DatatypeNotFoundException(f.getDatatype().getId());
      }
      
      Element elmField = new Element("Field");
      elmParent.appendChild(elmField);

      elmField.addAttribute(new Attribute("Name", this.str(f.getName())));
      if (f.getUsage().value().equals("B")) {
        elmField.addAttribute(new Attribute("Usage", "X"));
      } else {
        elmField.addAttribute(new Attribute("Usage", this.str(f.getUsage().value())));
      }
      elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
      if (f.getMax().equals("0")) {
        elmField.addAttribute(new Attribute("Max", "" + 1));
      } else {
        elmField.addAttribute(new Attribute("Max", this.str(f.getMax())));
      }

      if (f.getMaxLength() != null && !f.getMaxLength().equals("")) {
        if (f.getMaxLength().equals("*")) {
          elmField.addAttribute(new Attribute("Length", "" + 225));
        } else if (f.getMaxLength().equals("0")) {
          elmField.addAttribute(new Attribute("Length", "" + 1));
        } else {
          elmField.addAttribute(new Attribute("Length", this.str(f.getMaxLength())));
        }
      }
      elmField.addAttribute(new Attribute("Datatype", this.str(fieldDatatype.getName())));


      List<ValueSetBinding> bindings = findBinding(segment.getValueSetBindings(), f.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";

        for (ValueSetBinding binding : bindings) {
          try{
            Table table = tablesMap.get(binding.getTableId());
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }            
          }catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }

        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(fieldDatatype.getName())) {
          if (!bindingString.equals(""))
            elmField.addAttribute(
                new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));
        }
      }

      if (f.getItemNo() != null && !f.getItemNo().equals(""))
        elmField.addAttribute(new Attribute("ItemNo", this.str(f.getItemNo())));

      List<ConformanceStatement> fieldConformanceStatements =
          this.findConformanceStatements(segment.getConformanceStatements(), f.getPosition() + "[1]",
              message.getConformanceStatements(), fieldPath);
      if (fieldConformanceStatements.size() > 0) {
        Element elmImpNote = new Element("ImpNote");
        String note = "";
        for (ConformanceStatement c : fieldConformanceStatements) {
          try{
            note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription();  
          }catch (Exception e) {
            throw new ConstraintSerializationException(e, c.getDescription());
          }
          
        }
        elmImpNote.appendChild(note);
        elmField.appendChild(elmImpNote);
      }

      Predicate fieldPredicate = this.findPredicate(segment.getPredicates(), f.getPosition() + "[1]",
          message.getPredicates(), fieldPath);
      if (fieldPredicate != null) {
        try{
          Element elmPredicate = new Element("Predicate");
          String note = "[C(" + fieldPredicate.getTrueUsage() + "/" + fieldPredicate.getFalseUsage()
              + ")]" + fieldPredicate.getDescription();
          elmPredicate.appendChild(note);
          elmField.appendChild(elmPredicate);          
        }catch (Exception e) {
          throw new ConstraintSerializationException(e, fieldPredicate.getDescription());
        }

      }

      Map<Integer, Component> components = new HashMap<Integer, Component>();

      for (Component c : fieldDatatype.getComponents()) {
        components.put(c.getPosition(), c);
      }

      for (int j = 1; j < components.size() + 1; j++) {
        String componentPath = fieldPath + "." + j + "[1]";
        Component c = components.get(j);
        this.serializeGazelleComponent(c, datatypesMap.get(c.getDatatype().getId()), elmField,
            profile, message, fieldDatatype, componentPath, datatypesMap, tablesMap);
      }
    }catch (Exception e) {
      throw new FieldSerializationException(e, "Field[" + f.getPosition() + "]");
    }
  }

  private void serializeGazelleComponent(Component c, Datatype componentDatatype, Element elmParent,
      Profile profile, Message message, Datatype fieldDatatype, String componentPath,
      HashMap<String, Datatype> datatypesMap, HashMap<String, Table> tablesMap) throws DatatypeComponentSerializationException {
    try{
      if (c.getDatatype() != null && componentDatatype == null) {
        throw new DatatypeNotFoundException(c.getDatatype().getId());
      }
      
      Element elmComponent = new Element("Component");
      elmComponent.addAttribute(new Attribute("Name", this.str(c.getName())));
      if (c.getUsage().value().equals("B")) {
        elmComponent.addAttribute(new Attribute("Usage", "X"));
      } else {
        elmComponent.addAttribute(new Attribute("Usage", this.str(c.getUsage().value())));
      }
      elmComponent.addAttribute(new Attribute("Datatype", this.str(componentDatatype.getName())));
      if (c.getMaxLength() != null && !c.getMaxLength().equals("")) {
        if (c.getMaxLength().equals("*")) {
          elmComponent.addAttribute(new Attribute("Length", "" + 225));
        } else if (c.getMaxLength().equals("0")) {
          elmComponent.addAttribute(new Attribute("Length", "" + 1));
        } else {
          elmComponent.addAttribute(new Attribute("Length", this.str(c.getMaxLength())));
        }
      }

      List<ValueSetBinding> bindings =
          findBinding(fieldDatatype.getValueSetBindings(), c.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";

        for (ValueSetBinding binding : bindings) {
          try{
            Table table = tablesMap.get(binding.getTableId());
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }  
          }catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }
        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(componentDatatype.getName())) {
          if (!bindingString.equals(""))
            elmComponent.addAttribute(
                new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));
        }
      }

      List<ConformanceStatement> componentConformanceStatements =
          this.findConformanceStatements(fieldDatatype.getConformanceStatements(),
              c.getPosition() + "[1]", message.getConformanceStatements(), componentPath);
      if (componentConformanceStatements.size() > 0) {
        Element elmImpNote = new Element("ImpNote");
        String note = "";
        for (ConformanceStatement cs : componentConformanceStatements) {
          try{
            note = note + "\n" + "[" + cs.getConstraintId() + "]" + cs.getDescription();  
          }catch (Exception e) {
            throw new ConstraintSerializationException(e, cs.getDescription());
          }
        }
        elmImpNote.appendChild(note);
        elmComponent.appendChild(elmImpNote);
      }

      Predicate componentPredicate = this.findPredicate(fieldDatatype.getPredicates(),
          c.getPosition() + "[1]", message.getPredicates(), componentPath);
      if (componentPredicate != null) {
        try{
          Element elmPredicate = new Element("Predicate");
          String note = "[C(" + componentPredicate.getTrueUsage() + "/"
              + componentPredicate.getFalseUsage() + ")]" + componentPredicate.getDescription();
          elmPredicate.appendChild(note);
          elmComponent.appendChild(elmPredicate);   
        }catch (Exception e) {
          throw new ConstraintSerializationException(e, componentPredicate.getDescription());
        }
       
      }

      Map<Integer, Component> subComponents = new HashMap<Integer, Component>();

      for (Component sc : componentDatatype.getComponents()) {
        subComponents.put(sc.getPosition(), sc);
      }

      for (int k = 1; k < subComponents.size() + 1; k++) {
        String subComponentPath = componentPath + "." + k + "[1]";
        Component sc = subComponents.get(k);
        this.serializeGazelleSubComponent(sc, datatypesMap.get(sc.getDatatype().getId()),
            elmComponent, profile, message, componentDatatype, subComponentPath, datatypesMap,
            tablesMap);
      }
      elmParent.appendChild(elmComponent);
    }catch (Exception e) {
      throw new DatatypeComponentSerializationException(e, c.getPosition());
    }
    
  }

  private void serializeGazelleSubComponent(Component sc, Datatype subComponentDatatype,
      Element elmParent, Profile profile, Message message, Datatype componentDatatype,
      String subComponentPath, HashMap<String, Datatype> datatypesMap,
      HashMap<String, Table> tablesMap) throws DatatypeComponentSerializationException {
    try{
      if (sc.getDatatype() != null && subComponentDatatype == null) {
        throw new DatatypeNotFoundException(sc.getDatatype().getId());
      }
      Element elmSubComponent = new Element("SubComponent");
      elmSubComponent.addAttribute(new Attribute("Name", this.str(sc.getName())));
      if (sc.getUsage().value().equals("B")) {
        elmSubComponent.addAttribute(new Attribute("Usage", "X"));
      } else {
        elmSubComponent.addAttribute(new Attribute("Usage", this.str(sc.getUsage().value())));
      }
      elmSubComponent
          .addAttribute(new Attribute("Datatype", this.str(subComponentDatatype.getName())));
      if (sc.getMaxLength() != null && !sc.getMaxLength().equals("")) {
        if (sc.getMaxLength().equals("*")) {
          elmSubComponent.addAttribute(new Attribute("Length", "" + 225));
        } else if (sc.getMaxLength().equals("0")) {
          elmSubComponent.addAttribute(new Attribute("Length", "" + 1));
        } else {
          elmSubComponent.addAttribute(new Attribute("Length", this.str(sc.getMaxLength())));
        }
      }

      List<ValueSetBinding> bindings =
          findBinding(componentDatatype.getValueSetBindings(), sc.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";

        for (ValueSetBinding binding : bindings) {
          try{
            Table table = tablesMap.get(binding.getTableId());
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }  
          }catch (Exception e) {
            throw new TableSerializationException(e, binding.getLocation());
          }
        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(subComponentDatatype.getName())) {
          if (!bindingString.equals(""))
            elmSubComponent.addAttribute(
                new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));
        }
      }

      List<ConformanceStatement> subComponentConformanceStatements =
          this.findConformanceStatements(componentDatatype.getConformanceStatements(),
              sc.getPosition() + "[1]", message.getConformanceStatements(), subComponentPath);
      if (subComponentConformanceStatements.size() > 0) {
        Element elmImpNote = new Element("ImpNote");
        String note = "";
        for (ConformanceStatement cs : subComponentConformanceStatements) {
          try{
            note = note + "\n" + "[" + cs.getConstraintId() + "]" + cs.getDescription();  
          }catch (Exception e) {
            throw new ConstraintSerializationException(e, cs.getDescription());
          }
        }
        elmImpNote.appendChild(note);
        elmSubComponent.appendChild(elmImpNote);
      }

      Predicate subComponentPredicate = this.findPredicate(componentDatatype.getPredicates(),
          sc.getPosition() + "[1]", message.getPredicates(), subComponentPath);
      if (subComponentPredicate != null) {
        try{
          Element elmPredicate = new Element("Predicate");
          String note = "[C(" + subComponentPredicate.getTrueUsage() + "/"
              + subComponentPredicate.getFalseUsage() + ")]" + subComponentPredicate.getDescription();
          elmPredicate.appendChild(note);
          elmSubComponent.appendChild(elmPredicate);          
        }catch (Exception e) {
          throw new ConstraintSerializationException(e, subComponentPredicate.getDescription());
        }
      }

      elmParent.appendChild(elmSubComponent);
    }catch (Exception e) {
      throw new DatatypeComponentSerializationException(e, sc.getPosition());
    }
  }

  private Element serializeTablesMapToGazelleElement(Map<String, Table> tablesMap) throws TableSerializationException {
    Element elmSpecification = new Element("Specification");
    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/Gazelle%20Schema/HL7TableSchema.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    elmSpecification.addAttribute(schemaDecl);
    elmSpecification.addAttribute(new Attribute("SpecName", "NOSpecName"));
    elmSpecification.addAttribute(new Attribute("OrgName", "NIST"));
    elmSpecification.addAttribute(new Attribute("HL7Version", "1"));
    elmSpecification.addAttribute(new Attribute("SpecVersion", "1"));
    elmSpecification.addAttribute(new Attribute("Status", "Draft"));
    elmSpecification.addAttribute(new Attribute("ConformanceType", "Tolerant"));
    elmSpecification.addAttribute(new Attribute("Role", "Sender"));
    elmSpecification.addAttribute(new Attribute("HL7OID", ""));
    elmSpecification.addAttribute(new Attribute("ProcRule", "HL7"));

    Element elmConformance = new Element("Conformance");
    elmConformance.addAttribute(new Attribute("AccAck", "NE"));
    elmConformance.addAttribute(new Attribute("AppAck", "AL"));
    elmConformance.addAttribute(new Attribute("StaticID", ""));
    elmConformance.addAttribute(new Attribute("MsgAckMode", "Deferred"));
    elmConformance.addAttribute(new Attribute("QueryStatus", "Event"));
    elmConformance.addAttribute(new Attribute("QueryMode", "Non Query"));
    elmConformance.addAttribute(new Attribute("DynamicID", ""));
    elmSpecification.appendChild(elmConformance);

    Element elmEncodings = new Element("Encodings");
    Element elmEncoding = new Element("Encoding");
    elmEncoding.appendChild("ER7");
    elmEncodings.appendChild(elmEncoding);
    elmSpecification.appendChild(elmEncodings);

    int tableID = 0;
    Element elmHl7tables = new Element("hl7tables");

    for (String key : tablesMap.keySet()) {
      try{
        Table t = tablesMap.get(key);
        tableID = tableID + 1;
        Element elmHl7table = new Element("hl7table");
        elmHl7table.addAttribute(new Attribute("id", tableID + ""));
        if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
          if (t.getBindingIdentifier().startsWith("0396")
              || t.getBindingIdentifier().startsWith("HL70396")) {
            elmHl7table.addAttribute(new Attribute("name", this.str(t.getBindingIdentifier())));
          } else {
            elmHl7table.addAttribute(new Attribute("name",
                this.str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-"))));
          }
        } else {
          elmHl7table.addAttribute(new Attribute("name", this.str(t.getBindingIdentifier())));
        }
        elmHl7table.addAttribute(new Attribute("type", "HL7"));
        int order = 0;
        List<String> codesysList = new ArrayList<String>();

        for (Code c : t.getCodes()) {
          order = order + 1;
          if (c.getCodeSystem() != null && !codesysList.contains(c.getCodeSystem()))
            codesysList.add(c.getCodeSystem());

          Element elmTableElement = new Element("tableElement");
          elmTableElement.addAttribute(new Attribute("order", order + ""));
          elmTableElement.addAttribute(new Attribute("code", this.str(c.getValue())));
          elmTableElement.addAttribute(new Attribute("description", this.str(c.getLabel())));
          elmTableElement.addAttribute(new Attribute("displayName", this.str(c.getLabel())));

          if (c.getCodeSystem() == null || c.getCodeSystem().equals(""))
            elmTableElement.addAttribute(new Attribute("source", "NOSource"));
          else
            elmTableElement.addAttribute(new Attribute("source", this.str(c.getCodeSystem())));
          elmTableElement.addAttribute(new Attribute("usage", "Optional"));
          elmTableElement.addAttribute(new Attribute("creator", ""));
          elmTableElement.addAttribute(new Attribute("date", ""));
          elmTableElement.addAttribute(new Attribute("instruction", ""));
          elmHl7table.appendChild(elmTableElement);
        }

        if (codesysList.size() == 0) {
          elmHl7table.addAttribute(new Attribute("codeSys", ""));
        } else {
          String codeSysSet = "";
          for (String s : codesysList) {
            codeSysSet = codeSysSet + "," + s;
          }
          elmHl7table.addAttribute(new Attribute("codeSys", codeSysSet.substring(1)));
        }

        elmHl7tables.appendChild(elmHl7table); 
      }catch (Exception e) {
        throw new TableSerializationException(e, key);
      }
    }

    elmSpecification.appendChild(elmHl7tables);

    return elmSpecification;
  }
}
