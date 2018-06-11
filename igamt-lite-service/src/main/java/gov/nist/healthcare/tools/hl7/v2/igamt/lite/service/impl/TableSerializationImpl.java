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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Attribute;

@Service
public class TableSerializationImpl implements TableSerialization {

  @Autowired
  private TableService tableService;

  @Autowired
  private SerializationUtil serializationUtil;

  @Override
  public TableLibrary deserializeXMLToTableLibrary(String xmlContents, String hl7Version) {
    Document tableLibraryDoc = this.stringToDom(xmlContents);
    TableLibrary tableLibrary = new TableLibrary();
    Element elmTableLibrary =
        (Element) tableLibraryDoc.getElementsByTagName("ValueSetLibrary").item(0);
    tableLibrary
        .setValueSetLibraryIdentifier(elmTableLibrary.getAttribute("ValueSetLibraryIdentifier"));
    this.deserializeXMLToTable(elmTableLibrary, tableLibrary, hl7Version);

    return tableLibrary;
  }

  @Override
  public TableLibrary deserializeXMLToTableLibrary(nu.xom.Document xmlDoc, String hl7Version) {
    return deserializeXMLToTableLibrary(xmlDoc.toString(), hl7Version);
  }

  @Override
  public String serializeTableLibraryToXML(TableLibrary tableLibrary, DocumentMetaData metadata,
      Date dateUpdated) {
    return this.serializeTableLibraryToDoc(tableLibrary, metadata, dateUpdated).toXML();
  }

  @Override
  public String serializeTableLibraryToXML(DatatypeLibrary datatypeLibrary) {
    TableLibrary tables = new TableLibrary();
    // tables.setChildren(datatypeLibrary.getTables());
    return this.serializeTableLibraryToDoc(tables, new DocumentMetaData(), tables.getDateUpdated())
        .toXML();
  }

  @Override
  public nu.xom.Document serializeTableLibraryToDoc(TableLibrary tableLibrary,
      DocumentMetaData metadata, Date dateUpdated) {
    Profile p = new Profile();
    p.setTableLibrary(tableLibrary);
    p.setMetaData(null);
    return this.serializeTableLibraryToDoc(p, metadata, dateUpdated);
  }

  @Override
  public String serializeTableLibraryToXML(Profile profile, DocumentMetaData metadata,
      Date dateUpdated) {
    return this.serializeTableLibraryToDoc(profile, metadata, dateUpdated).toXML();
  }

  @Override
  public String serializeTableLibraryToGazelleXML(Profile profile) {
    return this.serializeTableLibraryToGazelleDoc(profile).toXML();
  }

  public nu.xom.Element serializeTableLibraryToGazelleElement(Profile profile) {
    TableLibrary tableLibrary = profile.getTableLibrary();

    nu.xom.Element elmSpecification = new nu.xom.Element("Specification");

    // if(profile.getMetaData() == null){
    elmSpecification.addAttribute(new Attribute("SpecName", "NOSpecName"));
    elmSpecification.addAttribute(new Attribute("OrgName", "NIST"));
    elmSpecification.addAttribute(new Attribute("HL7Version", "1"));
    elmSpecification.addAttribute(new Attribute("SpecVersion", "1"));
    elmSpecification.addAttribute(new Attribute("Status", "Draft"));
    elmSpecification.addAttribute(new Attribute("ConformanceType", "Tolerant"));
    elmSpecification.addAttribute(new Attribute("Role", "Sender"));
    elmSpecification.addAttribute(new Attribute("HL7OID", ""));
    elmSpecification.addAttribute(new Attribute("ProcRule", "HL7"));
    // }else {
    // elmSpecification.addAttribute(new Attribute("SpecName",
    // serializationUtil.str(profile.getMetaData().getSpecificationName())));
    // elmSpecification.addAttribute(new Attribute("OrgName",
    // serializationUtil.str(profile.getMetaData().getOrgName())));
    // elmSpecification.addAttribute(new Attribute("HL7Version",
    // serializationUtil.str(profile.getMetaData().getHl7Version())));
    // elmSpecification.addAttribute(new Attribute("SpecVersion",
    // serializationUtil.str(profile.getMetaData().getVersion())));
    // elmSpecification.addAttribute(new Attribute("Status",
    // serializationUtil.str(profile.getMetaData().getStatus())));
    // elmSpecification.addAttribute(new Attribute("ConformanceType",
    // "Tolerant"));
    // elmSpecification.addAttribute(new Attribute("Role", "Sender"));
    // elmSpecification.addAttribute(new Attribute("HL7OID", ""));
    // elmSpecification.addAttribute(new Attribute("ProcRule", "HL7"));
    // }

    nu.xom.Element elmConformance = new nu.xom.Element("Conformance");
    elmConformance.addAttribute(new Attribute("AccAck", "NE"));
    elmConformance.addAttribute(new Attribute("AppAck", "AL"));
    elmConformance.addAttribute(new Attribute("StaticID", ""));
    elmConformance.addAttribute(new Attribute("MsgAckMode", "Deferred"));
    elmConformance.addAttribute(new Attribute("QueryStatus", "Event"));
    elmConformance.addAttribute(new Attribute("QueryMode", "Non Query"));
    elmConformance.addAttribute(new Attribute("DynamicID", ""));
    elmSpecification.appendChild(elmConformance);

    nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
    nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
    elmEncoding.appendChild("ER7");
    elmEncodings.appendChild(elmEncoding);
    elmSpecification.appendChild(elmEncodings);

    int tableID = 0;
    nu.xom.Element elmHl7tables = new nu.xom.Element("hl7tables");

    for (TableLink link : tableLibrary.getChildren()) {
      Table t = tableService.findById(link.getId());
      tableID = tableID + 1;
      nu.xom.Element elmHl7table = new nu.xom.Element("hl7table");
      elmHl7table.addAttribute(new Attribute("id", tableID + ""));
      elmHl7table
          .addAttribute(new Attribute("name", serializationUtil.str(t.getBindingIdentifier())));
      elmHl7table.addAttribute(new Attribute("type", "HL7"));

      int order = 0;
      List<String> codesysList = new ArrayList<String>();

      for (Code c : t.getCodes()) {
        order = order + 1;
        if (c.getCodeSystem() != null && !codesysList.contains(c.getCodeSystem()))
          codesysList.add(c.getCodeSystem());

        nu.xom.Element elmTableElement = new nu.xom.Element("tableElement");
        elmTableElement.addAttribute(new Attribute("order", order + ""));
        elmTableElement.addAttribute(new Attribute("code", serializationUtil.str(c.getValue())));
        elmTableElement
            .addAttribute(new Attribute("description", serializationUtil.str(c.getLabel())));
        elmTableElement
            .addAttribute(new Attribute("displayName", serializationUtil.str(c.getLabel())));

        if (c.getCodeSystem() == null || c.getCodeSystem().equals(""))
          elmTableElement.addAttribute(new Attribute("source", "NOSource"));
        else
          elmTableElement
              .addAttribute(new Attribute("source", serializationUtil.str(c.getCodeSystem())));
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
    }

    elmSpecification.appendChild(elmHl7tables);

    return elmSpecification;
  }

  @Override
  public nu.xom.Element serializeTableLibraryToElement(Profile profile, DocumentMetaData metadata,
      Date dateUpdated) {
    TableLibrary tableLibrary = profile.getTableLibrary();

    nu.xom.Element elmTableLibrary = new nu.xom.Element("ValueSetLibrary");

    if (tableLibrary.getValueSetLibraryIdentifier() == null
        || tableLibrary.getValueSetLibraryIdentifier().equals("")) {
      elmTableLibrary
          .addAttribute(new Attribute("ValueSetLibraryIdentifier", UUID.randomUUID().toString()));
    } else {
      elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",
          serializationUtil.str(tableLibrary.getValueSetLibraryIdentifier())));
    }
    nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData
          .addAttribute(new Attribute("Name", !serializationUtil.str(metadata.getTitle()).equals("")
              ? serializationUtil.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(
          new Attribute("OrgName", !serializationUtil.str(metadata.getOrgName()).equals("")
              ? serializationUtil.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(
          new Attribute("Version", !serializationUtil.str(metadata.getVersion()).equals("")
              ? serializationUtil.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date",
          dateUpdated != null ? DateUtils.format(dateUpdated) : "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            serializationUtil.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData.addAttribute(
            new Attribute("Status", serializationUtil.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData.addAttribute(
            new Attribute("Topics", serializationUtil.str(profile.getMetaData().getTopics())));
    }

    HashMap<String, nu.xom.Element> valueSetDefinitionsMap = new HashMap<String, nu.xom.Element>();

    for (TableLink link : tableLibrary.getChildren()) {
      Table t = tableService.findById(link.getId());

      if (t != null) {
        nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
        if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
          elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", serializationUtil
              .str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-"))));
        } else {
          elmValueSetDefinition.addAttribute(
              new Attribute("BindingIdentifier", serializationUtil.str(t.getBindingIdentifier())));
        }

        elmValueSetDefinition
            .addAttribute(new Attribute("Name", serializationUtil.str(t.getName())));
        if (t.getDescription() != null && !t.getDescription().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Description", serializationUtil.str(t.getDescription())));
        if (t.getVersion() != null && !t.getVersion().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Version", serializationUtil.str(t.getVersion())));
        if (t.getOid() != null && !t.getOid().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Oid", serializationUtil.str(t.getOid())));
        if (t.getStability() != null && !t.getStability().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Stability", serializationUtil.str(t.getStability().name())));
        if (t.getExtensibility() != null && !t.getExtensibility().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Extensibility", serializationUtil.str(t.getExtensibility().name())));
        if (t.getContentDefinition() != null && !t.getContentDefinition().equals(""))
          elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition",
              serializationUtil.str(t.getContentDefinition().name())));

        nu.xom.Element elmValueSetDefinitions = null;
        if (t.getGroup() != null && !t.getGroup().equals("")) {
          elmValueSetDefinitions = valueSetDefinitionsMap.get(t.getGroup());
        } else {
          elmValueSetDefinitions = valueSetDefinitionsMap.get("NOGroup");
        }
        if (elmValueSetDefinitions == null) {
          elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");

          if (t.getGroup() != null && !t.getGroup().equals("")) {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", t.getGroup()));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", t.getOrder() + ""));
            valueSetDefinitionsMap.put(t.getGroup(), elmValueSetDefinitions);
          } else {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", "NOGroup"));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", "0"));
            valueSetDefinitionsMap.put("NOGroup", elmValueSetDefinitions);
          }

        }
        elmValueSetDefinitions.appendChild(elmValueSetDefinition);

        if (t.getCodes() != null) {
          for (Code c : t.getCodes()) {
            nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
            elmValueElement
                .addAttribute(new Attribute("Value", serializationUtil.str(c.getValue())));
            elmValueElement.addAttribute(
                new Attribute("DisplayName", serializationUtil.str(c.getLabel() + "")));
            if (c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
              elmValueElement.addAttribute(
                  new Attribute("CodeSystem", serializationUtil.str(c.getCodeSystem())));
            if (c.getCodeSystemVersion() != null && !c.getCodeSystemVersion().equals(""))
              elmValueElement.addAttribute(new Attribute("CodeSystemVersion",
                  serializationUtil.str(c.getCodeSystemVersion())));
            if (c.getCodeUsage() != null && !c.getCodeUsage().equals(""))
              elmValueElement
                  .addAttribute(new Attribute("Usage", serializationUtil.str(c.getCodeUsage())));
            if (c.getComments() != null && !c.getComments().equals(""))
              elmValueElement
                  .addAttribute(new Attribute("Comments", serializationUtil.str(c.getComments())));
            elmValueSetDefinition.appendChild(elmValueElement);
          }
        }
      }
    }

    elmTableLibrary.appendChild(elmMetaData);

    for (nu.xom.Element elmValueSetDefinitions : valueSetDefinitionsMap.values()) {
      elmTableLibrary.appendChild(elmValueSetDefinitions);
    }

    return elmTableLibrary;
  }

  @Override
  public nu.xom.Document serializeTableLibraryToDoc(Profile profile, DocumentMetaData metadata,
      Date dateUpdated) {
    return new nu.xom.Document(this.serializeTableLibraryToElement(profile, metadata, dateUpdated));
  }

  @Override
  public nu.xom.Document serializeTableLibraryToGazelleDoc(Profile profile) {
    return new nu.xom.Document(this.serializeTableLibraryToGazelleElement(profile));
  }

  private void deserializeXMLToTable(Element elmTableLibrary, TableLibrary tableLibrary, String hl7Version) {
    NodeList valueSetDefinitionsNode = elmTableLibrary.getElementsByTagName("ValueSetDefinitions");
    for (int i = 0; i < valueSetDefinitionsNode.getLength(); i++) {
      Element valueSetDefinitionsElement = (Element) valueSetDefinitionsNode.item(i);
      
      if(!valueSetDefinitionsElement.getAttribute("Group").equals("HL7")){
        NodeList valueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ValueSetDefinition");
        for (int j = 0; j < valueSetDefinitionNodes.getLength(); j++) {
          Element elmTable = (Element) valueSetDefinitionNodes.item(j);
          Table tableObj = new Table();
          tableObj.setScope(SCOPE.USER);
          tableObj.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
          tableObj.setName(elmTable.getAttribute("Name"));
          tableObj.setStatus(STATUS.UNPUBLISHED);
          tableObj.setGroup(valueSetDefinitionsElement.getAttribute("Group"));
          String orderStr = valueSetDefinitionsElement.getAttribute("Order");
          if (orderStr != null && !orderStr.equals("")) {
            tableObj.setOrder(Integer.parseInt(orderStr));
          }

          if (elmTable.getAttribute("Description") != null
              && !elmTable.getAttribute("Description").equals(""))
            tableObj.setDefPreText(elmTable.getAttribute("Description"));
          if (elmTable.getAttribute("Version") != null
              && !elmTable.getAttribute("Version").equals(""))
            tableObj.setVersion(elmTable.getAttribute("Version"));
          if (elmTable.getAttribute("Oid") != null && !elmTable.getAttribute("Oid").equals(""))
            tableObj.setOid(elmTable.getAttribute("Oid"));

          if (elmTable.getAttribute("Extensibility") != null
              && !elmTable.getAttribute("Extensibility").equals("")) {
            tableObj
                .setExtensibility(Extensibility.fromValue(elmTable.getAttribute("Extensibility")));
          } else {
            tableObj.setExtensibility(Extensibility.fromValue("Open"));
          }

          if (elmTable.getAttribute("Stability") != null
              && !elmTable.getAttribute("Stability").equals("")) {
            tableObj.setStability(Stability.fromValue(elmTable.getAttribute("Stability")));
          } else {
            tableObj.setStability(Stability.fromValue("Static"));
          }

          if (elmTable.getAttribute("ContentDefinition") != null
              && !elmTable.getAttribute("ContentDefinition").equals("")) {
            tableObj.setContentDefinition(
                ContentDefinition.fromValue(elmTable.getAttribute("ContentDefinition")));
          } else {
            tableObj.setContentDefinition(ContentDefinition.fromValue("Extensional"));
          }

          Set<String> codeSystems = this.deserializeXMLToCode(elmTable, tableObj);
          tableObj.setCodeSystems(codeSystems);
          tableObj = tableService.save(tableObj);
          TableLink link = new TableLink();
          link.setBindingIdentifier(tableObj.getBindingIdentifier());
          link.setId(tableObj.getId());
          tableLibrary.addTable(link);
        } 
      }else {
        NodeList valueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ValueSetDefinition");
        for (int j = 0; j < valueSetDefinitionNodes.getLength(); j++) {
          Element elmTable = (Element) valueSetDefinitionNodes.item(j);
          String bindingIdentifier = elmTable.getAttribute("BindingIdentifier");
          bindingIdentifier = bindingIdentifier.replace("HL7", "");
          Table t = this.tableService.findByScopeAndVersionAndBindingIdentifier(SCOPE.HL7STANDARD, hl7Version, bindingIdentifier);
          if(t == null) t = this.tableService.findByScopeAndVersionAndBindingIdentifier(SCOPE.HL7STANDARD, "2.8.2", bindingIdentifier);
          TableLink link = new TableLink();
          link.setBindingIdentifier(t.getBindingIdentifier());
          link.setId(t.getId());
          tableLibrary.addTable(link);
        }
      }
    }
  }

  private Set<String> deserializeXMLToCode(Element elmTable, Table tableObj) {
    Set<String> codeSystems = new HashSet<String>();
    NodeList nodes = elmTable.getElementsByTagName("ValueElement");

    for (int i = 0; i < nodes.getLength(); i++) {
      Element elmCode = (Element) nodes.item(i);

      Code codeObj = new Code();

      codeObj.setValue(elmCode.getAttribute("Value"));
      codeObj.setLabel(elmCode.getAttribute("DisplayName"));

      if (elmCode.getAttribute("CodeSystem") != null
          && !elmCode.getAttribute("CodeSystem").equals("")){
        codeSystems.add(elmCode.getAttribute("CodeSystem"));
        codeObj.setCodeSystem(elmCode.getAttribute("CodeSystem"));
      }
      if (elmCode.getAttribute("CodeSystemVersion") != null
          && !elmCode.getAttribute("CodeSystemVersion").equals(""))
        codeObj.setCodeSystemVersion(elmCode.getAttribute("CodeSystemVersion"));
      if (elmCode.getAttribute("Comments") != null && !elmCode.getAttribute("Comments").equals(""))
        codeObj.setComments(elmCode.getAttribute("Comments"));

      if (elmCode.getAttribute("Usage") != null && !elmCode.getAttribute("Usage").equals("")) {
        codeObj.setCodeUsage(elmCode.getAttribute("Usage"));
      } else {
        codeObj.setCodeUsage("R");
      }

      tableObj.addCode(codeObj);
    }
    
    return codeSystems;

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

  @Override
  public String serializeTableLibraryUsingMapToXML(Profile profile, DocumentMetaData metadata,
      Map<String, Table> tablesMap, Date dateUpdated) {
    TableLibrary tableLibrary = profile.getTableLibrary();

    nu.xom.Element elmTableLibrary = new nu.xom.Element("ValueSetLibrary");

    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/ValueSets.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    elmTableLibrary.addAttribute(schemaDecl);

    if (tableLibrary.getValueSetLibraryIdentifier() == null
        || tableLibrary.getValueSetLibraryIdentifier().equals("")) {
      elmTableLibrary
          .addAttribute(new Attribute("ValueSetLibraryIdentifier", UUID.randomUUID().toString()));
    } else {
      elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",
          serializationUtil.str(tableLibrary.getValueSetLibraryIdentifier())));
    }
    nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData
          .addAttribute(new Attribute("Name", !serializationUtil.str(metadata.getTitle()).equals("")
              ? serializationUtil.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(
          new Attribute("OrgName", !serializationUtil.str(metadata.getOrgName()).equals("")
              ? serializationUtil.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(
          new Attribute("Version", !serializationUtil.str(metadata.getVersion()).equals("")
              ? serializationUtil.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date",
          dateUpdated != null ? DateUtils.format(dateUpdated) : "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            serializationUtil.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData.addAttribute(
            new Attribute("Status", serializationUtil.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData.addAttribute(
            new Attribute("Topics", serializationUtil.str(profile.getMetaData().getTopics())));
    }

    HashMap<String, nu.xom.Element> valueSetDefinitionsMap = new HashMap<String, nu.xom.Element>();

    for (TableLink link : tableLibrary.getChildren()) {
      Table t = tablesMap.get(link.getId());

      if (t != null) {
        nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
        if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
          elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", serializationUtil
              .str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-"))));
        } else {
          elmValueSetDefinition.addAttribute(
              new Attribute("BindingIdentifier", serializationUtil.str(t.getBindingIdentifier())));
        }

        elmValueSetDefinition
            .addAttribute(new Attribute("Name", serializationUtil.str(t.getName())));
        if (t.getDescription() != null && !t.getDescription().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Description", serializationUtil.str(t.getDescription())));
        if (t.getVersion() != null && !t.getVersion().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Version", serializationUtil.str(t.getVersion())));
        if (t.getOid() != null && !t.getOid().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Oid", serializationUtil.str(t.getOid())));
        if (t.getStability() != null && !t.getStability().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Stability", serializationUtil.str(t.getStability().name())));
        if (t.getExtensibility() != null && !t.getExtensibility().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("Extensibility", serializationUtil.str(t.getExtensibility().name())));
        if (t.getContentDefinition() != null && !t.getContentDefinition().equals(""))
          elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition",
              serializationUtil.str(t.getContentDefinition().name())));

        nu.xom.Element elmValueSetDefinitions = null;
        if (t.getGroup() != null && !t.getGroup().equals("")) {
          elmValueSetDefinitions = valueSetDefinitionsMap.get(t.getGroup());
        } else {
          elmValueSetDefinitions = valueSetDefinitionsMap.get("NOGroup");
        }
        if (elmValueSetDefinitions == null) {
          elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");

          if (t.getGroup() != null && !t.getGroup().equals("")) {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", t.getGroup()));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", t.getOrder() + ""));
            valueSetDefinitionsMap.put(t.getGroup(), elmValueSetDefinitions);
          } else {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", "NOGroup"));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", "0"));
            valueSetDefinitionsMap.put("NOGroup", elmValueSetDefinitions);
          }

        }
        elmValueSetDefinitions.appendChild(elmValueSetDefinition);

        if (t.getCodes() != null) {
          for (Code c : t.getCodes()) {
            nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
            elmValueElement
                .addAttribute(new Attribute("Value", serializationUtil.str(c.getValue())));
            elmValueElement.addAttribute(
                new Attribute("DisplayName", serializationUtil.str(c.getLabel() + "")));
            if (c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
              elmValueElement.addAttribute(
                  new Attribute("CodeSystem", serializationUtil.str(c.getCodeSystem())));
            if (c.getCodeSystemVersion() != null && !c.getCodeSystemVersion().equals(""))
              elmValueElement.addAttribute(new Attribute("CodeSystemVersion",
                  serializationUtil.str(c.getCodeSystemVersion())));
            if (c.getCodeUsage() != null && !c.getCodeUsage().equals(""))
              elmValueElement
                  .addAttribute(new Attribute("Usage", serializationUtil.str(c.getCodeUsage())));
            if (c.getComments() != null && !c.getComments().equals(""))
              elmValueElement
                  .addAttribute(new Attribute("Comments", serializationUtil.str(c.getComments())));
            elmValueSetDefinition.appendChild(elmValueElement);
          }
        }
      }
    }

    elmTableLibrary.appendChild(elmMetaData);

    for (nu.xom.Element elmValueSetDefinitions : valueSetDefinitionsMap.values()) {
      elmTableLibrary.appendChild(elmValueSetDefinitions);
    }

    return elmTableLibrary.toXML();
  }

}
