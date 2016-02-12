/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TableSerializationImpl implements TableSerialization {

        @Override
        public Tables deserializeXMLToTableLibrary(String xmlContents) {
                Document tableLibraryDoc = this.stringToDom(xmlContents);
                Tables tableLibrary = new Tables();
                Element elmTableLibrary = (Element) tableLibraryDoc.getElementsByTagName("ValueSetLibrary").item(0);
                tableLibrary.setValueSetLibraryIdentifier(elmTableLibrary.getAttribute("ValueSetLibraryIdentifier"));
                this.deserializeXMLToTable(elmTableLibrary, tableLibrary);

                return tableLibrary;
        }

        @Override
        public Tables deserializeXMLToTableLibrary(nu.xom.Document xmlDoc) {
                return deserializeXMLToTableLibrary(xmlDoc.toString());
        }

        @Override
        public String serializeTableLibraryToXML(Tables tableLibrary) {
                return this.serializeTableLibraryToDoc(tableLibrary).toXML();
        }

        @Override
        public nu.xom.Document serializeTableLibraryToDoc(Tables tableLibrary) {
                nu.xom.Element elmTableLibrary = new nu.xom.Element("ValueSetLibrary");
                elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",ExportUtil.str(tableLibrary.getValueSetLibraryIdentifier())));
                elmTableLibrary.addAttribute(new Attribute("Name", ExportUtil.str(tableLibrary.getName())));
                if(tableLibrary.getOrganizationName() != null && !tableLibrary.getOrganizationName().equals(""))
                elmTableLibrary.addAttribute(new Attribute("OrganizationName", ExportUtil.str(tableLibrary.getOrganizationName())));
                if(tableLibrary.getValueSetLibraryVersion() != null && !tableLibrary.getValueSetLibraryVersion().equals(""))
                elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryVersion", ExportUtil.str(tableLibrary.getValueSetLibraryVersion())));
                if(tableLibrary.getStatus() != null && !tableLibrary.getStatus().equals(""))
                elmTableLibrary.addAttribute(new Attribute("Status", ExportUtil.str(tableLibrary.getStatus())));
                if(tableLibrary.getDescription() != null && !tableLibrary.getDescription().equals(""))
                elmTableLibrary.addAttribute(new Attribute("Description", ExportUtil.str(tableLibrary.getDescription())));
                if(tableLibrary.getDateCreated() != null && !tableLibrary.getDateCreated().equals(""))
                elmTableLibrary.addAttribute(new Attribute("DateCreated", ExportUtil.str(tableLibrary.getDateCreated())));


                nu.xom.Element elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");
                elmTableLibrary.appendChild(elmValueSetDefinitions);

                for (Table t : tableLibrary.getChildren()) {
                        nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
                        elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", ExportUtil.str(t.getBindingIdentifier())));
                        elmValueSetDefinition.addAttribute(new Attribute("Name", ExportUtil.str(t.getName())));
                        if(t.getDescription() != null && !t.getDescription().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("Description",ExportUtil.str( t.getDescription())));
                        if(t.getVersion() != null && !t.getVersion().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("Version", ExportUtil.str(t.getVersion())));
                        if(t.getOid() != null && !t.getOid().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("Oid",ExportUtil.str(t.getOid())));
                        if(t.getStability() != null && !t.getStability().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("Stability", ExportUtil.str(t.getStability().value())));
                        if(t.getExtensibility() != null && !t.getExtensibility().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("Extensibility", ExportUtil.str(t.getExtensibility().value())));
                        if(t.getContentDefinition() != null && !t.getContentDefinition().equals(""))
                        	elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition", ExportUtil.str(t.getContentDefinition().value())));
                        

                        elmValueSetDefinitions.appendChild(elmValueSetDefinition);

                        if (t.getCodes() != null) {
                                for (Code c : t.getCodes()) {
                                        nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
                                        elmValueElement.addAttribute(new Attribute("Value", ExportUtil.str(c.getValue())));
                                        elmValueElement.addAttribute(new Attribute("DisplayName", ExportUtil.str(c.getLabel() + "")));
                                        if(c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
                                        	elmValueElement.addAttribute(new Attribute("CodeSystem", ExportUtil.str(c.getCodeSystem())));
                                        if(c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
                                        	elmValueElement.addAttribute(new Attribute("CodeSystemVersion", ExportUtil.str(c.getCodeSystemVersion())));
                                        if(c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
                                        	elmValueElement.addAttribute(new Attribute("Usage", ExportUtil.str(c.getCodeUsage())));
                                        if(c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
                                        	elmValueElement.addAttribute(new Attribute("Comments", ExportUtil.str(c.getComments())));
                                        elmValueSetDefinition.appendChild(elmValueElement);
                                }
                        }

                }
                nu.xom.Document doc = new nu.xom.Document(elmTableLibrary);

                return doc;
        }

        private void deserializeXMLToTable(Element elmTableLibrary, Tables tableLibrary) {
        	 	NodeList valueSetDefinitionsNode = elmTableLibrary.getElementsByTagName("ValueSetDefinitions");
        	 	for (int i = 0; i < valueSetDefinitionsNode.getLength(); i++) {
        	 		Element valueSetDefinitionsElement = (Element) valueSetDefinitionsNode.item(i);
        	 		NodeList valueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ValueSetDefinition");
                    for (int j = 0; j < valueSetDefinitionNodes.getLength(); j++) {
                            Element elmTable = (Element) valueSetDefinitionNodes.item(j);

                            Table tableObj = new Table();

                            tableObj.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
                            tableObj.setName(elmTable.getAttribute("Name"));
                            tableObj.setGroup(valueSetDefinitionsElement.getAttribute("Group"));
                            String orderStr = valueSetDefinitionsElement.getAttribute("Order");
                            if(orderStr != null && !orderStr.equals("")){
                            	tableObj.setOrder(Integer.parseInt(orderStr));
                	 		}

                            if (elmTable.getAttribute("Description") != null && !elmTable.getAttribute("Description").equals("")) tableObj.setDescription(elmTable.getAttribute("Description"));
                            if (elmTable.getAttribute("Version") != null && !elmTable.getAttribute("Version").equals("")) tableObj.setVersion(elmTable.getAttribute("Version"));
                            if (elmTable.getAttribute("Oid") != null && !elmTable.getAttribute("Oid").equals("")) tableObj.setOid(elmTable.getAttribute("Oid"));

                            if (elmTable.getAttribute("Extensibility") != null && !elmTable.getAttribute("Extensibility").equals("")) {
                                    tableObj.setExtensibility(Extensibility.fromValue(elmTable.getAttribute("Extensibility")));
                            } else {
                                    tableObj.setExtensibility(Extensibility.fromValue("Open"));
                            }

                            if (elmTable.getAttribute("Stability") != null && !elmTable.getAttribute("Stability").equals("")) {
                                    tableObj.setStability(Stability.fromValue(elmTable.getAttribute("Stability")));
                            } else {
                                    tableObj.setStability(Stability.fromValue("Static"));
                            }

                            if (elmTable.getAttribute("ContentDefinition") != null && !elmTable.getAttribute("ContentDefinition").equals("")) {
                                    tableObj.setContentDefinition(ContentDefinition.fromValue(elmTable.getAttribute("ContentDefinition")));
                            } else {
                                    tableObj.setContentDefinition(ContentDefinition.fromValue("Extensional"));
                            }

                            this.deserializeXMLToCode(elmTable, tableObj);
                            tableLibrary.addTable(tableObj);
                    }
        	 	}
        }

        private void deserializeXMLToCode(Element elmTable, Table tableObj) {
                NodeList nodes = elmTable.getElementsByTagName("ValueElement");

                for (int i = 0; i < nodes.getLength(); i++) {
                        Element elmCode = (Element) nodes.item(i);

                        Code codeObj = new Code();

                        codeObj.setValue(elmCode.getAttribute("Value"));
                        codeObj.setLabel(elmCode.getAttribute("DisplayName"));

                        if (elmCode.getAttribute("CodeSystem") != null && !elmTable.getAttribute("CodeSystem").equals("")) codeObj.setCodeSystem(elmTable.getAttribute("CodeSystem"));
                        if (elmCode.getAttribute("CodeSystemVersion") != null && !elmTable.getAttribute("CodeSystemVersion").equals("")) codeObj.setCodeSystemVersion(elmTable.getAttribute("CodeSystemVersion"));
                        if (elmCode.getAttribute("Comments") != null && !elmTable.getAttribute("Comments").equals("")) codeObj.setComments(elmTable.getAttribute("Comments"));

                        if (elmCode.getAttribute("Usage") != null && !elmTable.getAttribute("Usage").equals("")) {
                                codeObj.setCodeUsage(elmTable.getAttribute("Usage"));
                        } else {
                                codeObj.setCodeUsage("R");
                        }

                        tableObj.addCode(codeObj);
                }

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
        
     
}