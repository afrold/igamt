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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;

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

                tableLibrary.setName(elmTableLibrary.getAttribute("Name"));
                tableLibrary.setValueSetLibraryIdentifier(elmTableLibrary.getAttribute("ValueSetLibraryIdentifier"));

                if (elmTableLibrary.getAttribute("Description") != null && !elmTableLibrary.getAttribute("Description").equals("")) tableLibrary.setDescription(elmTableLibrary.getAttribute("Description"));
                if (elmTableLibrary.getAttribute("OrganizationName") != null && !elmTableLibrary.getAttribute("OrganizationName").equals("")) tableLibrary.setOrganizationName(elmTableLibrary.getAttribute("OrganizationName"));
                if (elmTableLibrary.getAttribute("ValueSetLibraryVersion") != null && !elmTableLibrary.getAttribute("ValueSetLibraryVersion").equals("")) tableLibrary.setValueSetLibraryVersion(elmTableLibrary.getAttribute("ValueSetLibraryVersion"));
                if (elmTableLibrary.getAttribute("Status") != null && !elmTableLibrary.getAttribute("Status").equals("")) tableLibrary.setStatus(elmTableLibrary.getAttribute("Status"));
                if (elmTableLibrary.getAttribute("DateCreated") != null && !elmTableLibrary.getAttribute("DateCreated").equals("")) tableLibrary.setDateCreated(elmTableLibrary.getAttribute("DateCreated"));

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
                elmTableLibrary.setNamespaceURI("http://www.nist.gov/healthcare/data");
                elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",tableLibrary.getValueSetLibraryIdentifier()));
                elmTableLibrary.addAttribute(new Attribute("Status", tableLibrary.getStatus()));
                elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryVersion", tableLibrary.getValueSetLibraryVersion()));
                elmTableLibrary.addAttribute(new Attribute("OrganizationName", tableLibrary.getOrganizationName()));
                elmTableLibrary.addAttribute(new Attribute("Name", tableLibrary.getName()));
                elmTableLibrary.addAttribute(new Attribute("Description", tableLibrary.getDescription()));

                nu.xom.Element elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");
                elmTableLibrary.appendChild(elmValueSetDefinitions);

                for (Table t : tableLibrary.getChildren()) {
                        nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
                        elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", (t.getBindingIdentifier() == null) ? "" : t.getBindingIdentifier()));
                        elmValueSetDefinition.addAttribute(new Attribute("Name", (t.getName() == null) ? "" : t.getName()));
                        elmValueSetDefinition.addAttribute(new Attribute("Description", (t.getDescription() == null) ? "" : t.getDescription()));
                        elmValueSetDefinition.addAttribute(new Attribute("Version", (t.getVersion() == null) ? "" : "" + t.getVersion()));
                        elmValueSetDefinition.addAttribute(new Attribute("Oid",(t.getOid() == null) ? "" : t.getOid()));
                        elmValueSetDefinition.addAttribute(new Attribute("Stability", (t.getStability() == null) ? "" : t.getStability()));
                        elmValueSetDefinition.addAttribute(new Attribute("Extensibility", (t.getExtensibility() == null) ? "" : t.getExtensibility()));
                        elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition", (t.getContentDefinition() == null) ? "" : t.getContentDefinition()));


                        elmValueSetDefinitions.appendChild(elmValueSetDefinition);

                        if (t.getCodes() != null) {
                                for (Code c : t.getCodes()) {
                                        nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
                                        elmValueElement.addAttribute(new Attribute("Value", (c.getValue() == null) ? "" : c.getValue()));
                                        elmValueElement.addAttribute(new Attribute("DisplayName",(c.getDisplayName() == null) ? "" : c.getDisplayName()));
                                        elmValueElement.addAttribute(new Attribute("CodeSystem", (c.getCodeSystem() == null) ? "" : c.getCodeSystem()));
                                        elmValueElement.addAttribute(new Attribute("CodeSystemVersion", (c.getCodeSystemVersion() == null) ? "" : c.getCodeSystemVersion()));
                                        elmValueElement.addAttribute(new Attribute("Usage", (c.getCodeUsage() == null) ? "" : c.getCodeUsage()));
                                        elmValueElement.addAttribute(new Attribute("Comments", (c.getComments() == null) ? "" : c.getComments()));
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
        	 		
        	 		String group = valueSetDefinitionsElement.getAttribute("Group");
        	 		int order = Integer.parseInt(valueSetDefinitionsElement.getAttribute("Order"));
        	 		
        	 		NodeList valueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ValueSetDefinition");
                    for (int j = 0; j < valueSetDefinitionNodes.getLength(); j++) {
                            Element elmTable = (Element) valueSetDefinitionNodes.item(j);

                            Table tableObj = new Table();

                            tableObj.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
                            tableObj.setName(elmTable.getAttribute("Name"));
                            tableObj.setGroup(group);
                            tableObj.setOrder(order);

                            if (elmTable.getAttribute("Description") != null && !elmTable.getAttribute("Description").equals("")) tableObj.setDescription(elmTable.getAttribute("Description"));
                            if (elmTable.getAttribute("Version") != null && !elmTable.getAttribute("Version").equals("")) tableObj.setVersion(elmTable.getAttribute("Version"));
                            if (elmTable.getAttribute("Oid") != null && !elmTable.getAttribute("Oid").equals("")) tableObj.setOid(elmTable.getAttribute("Oid"));

                            if (elmTable.getAttribute("Extensibility") != null && !elmTable.getAttribute("Extensibility").equals("")) {
                                    tableObj.setExtensibility(elmTable.getAttribute("Extensibility"));
                            } else {
                                    tableObj.setStability("Open");
                            }

                            if (elmTable.getAttribute("Stability") != null && !elmTable.getAttribute("Stability").equals("")) {
                                    tableObj.setStability(elmTable.getAttribute("Stability"));
                            } else {
                                    tableObj.setStability("Static");
                            }

                            if (elmTable.getAttribute("ContentDefinition") != null && !elmTable.getAttribute("ContentDefinition").equals("")) {
                                    tableObj.setContentDefinition(elmTable.getAttribute("ContentDefinition"));
                            } else {
                                    tableObj.setContentDefinition("Extensional");
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
                        codeObj.setDisplayName(elmCode.getAttribute("DisplayName"));

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