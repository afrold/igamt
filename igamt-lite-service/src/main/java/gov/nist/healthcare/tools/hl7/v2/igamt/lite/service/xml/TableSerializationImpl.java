package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Tables;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TableSerializationImpl implements TableSerialization{

	@Override
	public TableLibrary deserializeXMLToTableLibrary(String xmlContents) {
		Document tableLibraryDoc = this.stringToDom(xmlContents);
		TableLibrary tableLibrary = new TableLibrary();
		
		Element elmTableLibrary = (Element)tableLibraryDoc.getElementsByTagName("TableLibrary").item(0);
		tableLibrary.setDescription(elmTableLibrary.getAttribute("Description"));
		tableLibrary.setName(elmTableLibrary.getAttribute("Name"));
		tableLibrary.setOrganizationName(elmTableLibrary.getAttribute("OrganizationName"));
		tableLibrary.setStatus(elmTableLibrary.getAttribute("Status"));
		tableLibrary.setTableLibraryIdentifier(elmTableLibrary.getAttribute("TableLibraryIdentifier"));
		tableLibrary.setTableLibraryVersion(elmTableLibrary.getAttribute("TableLibraryVersion"));
		
		this.deserializeXMLToTable(elmTableLibrary, tableLibrary);
		
		return tableLibrary;
	}
	
	@Override
	public TableLibrary deserializeXMLToTableLibrary(nu.xom.Document xmlDoc) {
		return deserializeXMLToTableLibrary(xmlDoc.toString());
	}
	
	@Override
	public String serializeTableLibraryToXML(TableLibrary tableLibrary) {				
		return this.serializeTableLibraryToDoc(tableLibrary).toXML();
	}

	@Override
	public nu.xom.Document serializeTableLibraryToDoc(TableLibrary tableLibrary) {
		nu.xom.Element elmTableLibrary = new nu.xom.Element("TableLibrary");
		elmTableLibrary.setNamespaceURI("http://www.nist.gov/healthcare/data");
		elmTableLibrary.addAttribute(new Attribute("TableLibraryIdentifier", tableLibrary.getTableLibraryIdentifier()));
		elmTableLibrary.addAttribute(new Attribute("Status", tableLibrary.getStatus()));
		elmTableLibrary.addAttribute(new Attribute("TableLibraryVersion", tableLibrary.getTableLibraryVersion()));
		elmTableLibrary.addAttribute(new Attribute("OrganizationName", tableLibrary.getOrganizationName()));
		elmTableLibrary.addAttribute(new Attribute("Name", tableLibrary.getName()));
		elmTableLibrary.addAttribute(new Attribute("Description", tableLibrary.getDescription()));
		
		for(Table t: tableLibrary.getTables().getTables()){			
			nu.xom.Element elmTableDefinition = new nu.xom.Element("TableDefinition");
			elmTableDefinition.addAttribute(new Attribute("AlternateId", (t.getMappingAlternateId()==null)?"":t.getMappingAlternateId()));
			elmTableDefinition.addAttribute(new Attribute("Id", (t.getMappingId()==null)?"":t.getMappingId()));
			elmTableDefinition.addAttribute(new Attribute("Name", (t.getName()==null)?"":t.getName()));
			elmTableDefinition.addAttribute(new Attribute("Version", (t.getVersion()==0)?"":"" + t.getVersion()));
			elmTableDefinition.addAttribute(new Attribute("Codesys", (t.getCodesys()==null)?"":t.getCodesys()));
			elmTableDefinition.addAttribute(new Attribute("Oid", (t.getOid()==null)?"":t.getOid()));
			elmTableDefinition.addAttribute(new Attribute("Type", (t.getType()==null)?"":t.getType()));
			
			elmTableLibrary.appendChild(elmTableDefinition);
			
			if(t.getCodes() != null){
				for (Code c: t.getCodes()){
					nu.xom.Element elmTableElement = new nu.xom.Element("TableElement");
					elmTableElement.addAttribute(new Attribute("Code", (c.getCode()==null)?"":c.getCode()));
					elmTableElement.addAttribute(new Attribute("DisplayName", (c.getDisplayName()==null)?"":c.getDisplayName()));
					elmTableElement.addAttribute(new Attribute("Codesys", (c.getCodesys()==null)?"":c.getCodesys()));
					elmTableElement.addAttribute(new Attribute("Source", (c.getSource()==null)?"":c.getSource()));
					elmTableDefinition.appendChild(elmTableElement);
				}
			}
			
		}
		
		nu.xom.Document doc = new nu.xom.Document(elmTableLibrary);
				
		return doc;
	}
	
	private void deserializeXMLToTable(Element elmTableLibrary, TableLibrary tableLibrary) {
		Tables tables = new Tables();
		
		NodeList nodes = elmTableLibrary.getElementsByTagName("TableDefinition");
		
		for(int i=0; i<nodes.getLength(); i++){
			Element elmTable = (Element)nodes.item(i);
			
			Table tableObj = new Table();
			
			tableObj.setCodesys(elmTable.getAttribute("Codesys"));
			tableObj.setMappingAlternateId(elmTable.getAttribute("AlternateId"));
			tableObj.setMappingId(elmTable.getAttribute("Id"));
			tableObj.setName(elmTable.getAttribute("Name"));
			tableObj.setOid(elmTable.getAttribute("Oid"));
			tableObj.setType(elmTable.getAttribute("Type"));
			if(elmTable.getAttribute("Version") != null && !elmTable.getAttribute("Version").equals(""))
			tableObj.setVersion(Integer.parseInt(elmTable.getAttribute("Version")));
			this.deserializeXMLToCode(elmTable, tableObj);
			
			
			tables.addTable(tableObj);
		}
		
		tables.setTableLibrary(tableLibrary);
		tableLibrary.setTables(tables);
		
	}
	

	private void deserializeXMLToCode(Element elmTable, Table tableObj) {
		NodeList nodes = elmTable.getElementsByTagName("TableElement");
		
		for(int i=0; i<nodes.getLength(); i++){
			Element elmCode = (Element)nodes.item(i);
			
			Code codeObj = new Code();
			
			codeObj.setCode(elmCode.getAttribute("Code"));
			codeObj.setCodesys(elmCode.getAttribute("Codesys"));
			codeObj.setDisplayName(elmCode.getAttribute("DisplayName"));
			codeObj.setSource(elmCode.getAttribute("Source"));
			
			tableObj.getCodes().add(codeObj);
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
	
	public static void main(String[] args) throws IOException {
		TableSerializationImpl test = new TableSerializationImpl();
		TableLibrary tableLibrary = test.deserializeXMLToTableLibrary(new String(Files.readAllBytes(Paths.get("src//main//resources//VXU ValueSets.xml"))));
		
		System.out.println(tableLibrary.toString());
		
		System.out.println("----------------------------------------------------------------------------------------------------------");
		
		System.out.println(test.serializeTableLibraryToXML(tableLibrary));
	}

	
}
