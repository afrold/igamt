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

		Element elmTableLibrary = (Element) tableLibraryDoc
				.getElementsByTagName("TableLibrary").item(0);
		tableLibrary
				.setDescription(elmTableLibrary.getAttribute("Description"));
		tableLibrary.setName(elmTableLibrary.getAttribute("Name"));
		tableLibrary.setOrganizationName(elmTableLibrary
				.getAttribute("OrganizationName"));
		tableLibrary.setStatus(elmTableLibrary.getAttribute("Status"));
		tableLibrary.setTableLibraryIdentifier(elmTableLibrary
				.getAttribute("TableLibraryIdentifier"));
		tableLibrary.setTableLibraryVersion(elmTableLibrary
				.getAttribute("TableLibraryVersion"));

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
		nu.xom.Element elmTableLibrary = new nu.xom.Element("TableLibrary");
		elmTableLibrary.setNamespaceURI("http://www.nist.gov/healthcare/data");
		elmTableLibrary.addAttribute(new Attribute("TableLibraryIdentifier",
				tableLibrary.getTableLibraryIdentifier()));
		elmTableLibrary.addAttribute(new Attribute("Status", tableLibrary
				.getStatus()));
		elmTableLibrary.addAttribute(new Attribute("TableLibraryVersion",
				tableLibrary.getTableLibraryVersion()));
		elmTableLibrary.addAttribute(new Attribute("OrganizationName",
				tableLibrary.getOrganizationName()));
		elmTableLibrary.addAttribute(new Attribute("Name", tableLibrary
				.getName()));
		elmTableLibrary.addAttribute(new Attribute("Description", tableLibrary
				.getDescription()));

		for (Table t : tableLibrary.getChildren()) {
			nu.xom.Element elmTableDefinition = new nu.xom.Element(
					"TableDefinition");
			elmTableDefinition.addAttribute(new Attribute("AlternateId", (t
					.getMappingAlternateId() == null) ? "" : t
					.getMappingAlternateId()));
			elmTableDefinition.addAttribute(new Attribute("Id", (t
					.getMappingId() == null) ? "" : t.getMappingId()));
			elmTableDefinition.addAttribute(new Attribute("Name",
					(t.getName() == null) ? "" : t.getName()));
			elmTableDefinition.addAttribute(new Attribute("Version", (t
					.getVersion() == null) ? "" : "" + t.getVersion()));
			elmTableDefinition.addAttribute(new Attribute("Codesys", (t
					.getCodesys() == null) ? "" : t.getCodesys()));
			elmTableDefinition.addAttribute(new Attribute("Oid",
					(t.getOid() == null) ? "" : t.getOid()));
			elmTableDefinition.addAttribute(new Attribute("Type", (t
					.getTableType() == null) ? "" : t.getTableType()));
			elmTableDefinition.addAttribute(new Attribute("Extensibility", (t
					.getExtensibility() == null) ? "" : t.getExtensibility()));
			elmTableDefinition.addAttribute(new Attribute("Stability", (t
					.getStability() == null) ? "" : t.getStability()));

			elmTableLibrary.appendChild(elmTableDefinition);

			if (t.getCodes() != null) {
				for (Code c : t.getCodes()) {
					nu.xom.Element elmTableElement = new nu.xom.Element(
							"TableElement");
					elmTableElement.addAttribute(new Attribute("Code", (c
							.getCode() == null) ? "" : c.getCode()));
					elmTableElement.addAttribute(new Attribute("DisplayName",
							(c.getLabel() == null) ? "" : c.getLabel()));
					elmTableElement.addAttribute(new Attribute("Codesys", (c
							.getCodesys() == null) ? "" : c.getCodesys()));
					elmTableElement.addAttribute(new Attribute("Source", (c
							.getSource() == null) ? "" : c.getSource()));
					elmTableElement.addAttribute(new Attribute("Usage", (c
							.getCodeUsage() == null) ? "" : c.getCodeUsage()));
					elmTableDefinition.appendChild(elmTableElement);
				}
			}

		}

		nu.xom.Document doc = new nu.xom.Document(elmTableLibrary);

		return doc;
	}

	private void deserializeXMLToTable(Element elmTableLibrary,
			Tables tableLibrary) {

		NodeList nodes = elmTableLibrary
				.getElementsByTagName("TableDefinition");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element elmTable = (Element) nodes.item(i);

			Table tableObj = new Table();

			tableObj.setCodesys(elmTable.getAttribute("Codesys"));
			tableObj.setMappingAlternateId(elmTable.getAttribute("AlternateId"));
			tableObj.setMappingId(elmTable.getAttribute("Id"));
			tableObj.setName(elmTable.getAttribute("Name"));
			tableObj.setOid(elmTable.getAttribute("Oid"));
			tableObj.setTableType(elmTable.getAttribute("Type"));
			if (elmTable.getAttribute("Version") != null
					&& !elmTable.getAttribute("Version").equals(""))
				tableObj.setVersion(elmTable.getAttribute("Version"));

			if (elmTable.getAttribute("Extensibility") != null
					&& !elmTable.getAttribute("Extensibility").equals("")) {
				tableObj.setExtensibility(elmTable
						.getAttribute("Extensibility"));
			} else {
				tableObj.setStability("Open");
			}

			if (elmTable.getAttribute("Stability") != null
					&& !elmTable.getAttribute("Stability").equals("")) {
				tableObj.setStability(elmTable.getAttribute("Stability"));
			} else {
				tableObj.setStability("Static");
			}

			this.deserializeXMLToCode(elmTable, tableObj);
			tableLibrary.addTable(tableObj);
		}

	}

	private void deserializeXMLToCode(Element elmTable, Table tableObj) {
		NodeList nodes = elmTable.getElementsByTagName("TableElement");

		for (int i = 0; i < nodes.getLength(); i++) {
			Element elmCode = (Element) nodes.item(i);

			Code codeObj = new Code();

			codeObj.setCode(elmCode.getAttribute("Code"));
			codeObj.setCodesys(elmCode.getAttribute("Codesys"));
			codeObj.setLabel(elmCode.getAttribute("DisplayName"));
			codeObj.setSource(elmCode.getAttribute("Source"));
			if (elmCode.getAttribute("Usage") != null
					&& !elmTable.getAttribute("Usage").equals("")) {
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
