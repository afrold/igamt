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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementChange;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.HL7Version;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SchemaVersion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;

import nu.xom.Attribute;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.apache.commons.lang3.StringUtils;



public class ProfileDiffSerializationImpl {

	private ProfileDiffImpl diff;
	private Profile p1;
	private Profile p2;

	public ProfileDiffSerializationImpl(Profile p1, Profile p2, ProfileDiffImpl diff) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.diff = diff;
	}

	public File serializeDiffToFile() throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("ProfileTemp", ".xml");
			FileOutputStream outputStream = (FileOutputStream) Files.newOutputStream(out.toPath());
			Serializer ser;
			ser = new Serializer(outputStream, "UTF-8");
			ser.setIndent(4);
			ser.write(this.serializeDiffToDoc());
			return out;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public String serializeDiffToXML() {
		return this.serializeDiffToDoc().toXML();
	}

	public nu.xom.Document serializeDiffToDoc() {

		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", p1.getId() + ""));

		//TODO Add diff values for ProfileMetadata and ProfileInfo

		nu.xom.Element msgsd = new nu.xom.Element("MessagesDisplay");
		for (Message m : p1.getMessages().getChildren()) {
			nu.xom.Element msd = new nu.xom.Element("MessageDisplay");
			msd.addAttribute(new Attribute("StrucID", m.getStructID()));
			msd.addAttribute(new Attribute("Description", m.getDescription()));
			for (SegmentRefOrGroup srog: m.getChildren()){
				if (this.diff.findOneBySegmentRefOrGroupId(srog.getId()) != null){

					ElementChange ecsrog = this.diff.findOneBySegmentRefOrGroupId(srog.getId());
					nu.xom.Element elmsrog = new nu.xom.Element("Elt");

					if (ecsrog.getChangeType().equals("edit")){
						System.out.println("Diff=edit");
						elmsrog.addAttribute(new Attribute("Diff", "edit"));

						for (String field: ecsrog.getChange().keySet()){
							System.out.print(field +"=\"" + ecsrog.getChange().get(field).get("newvalue") + "\" ");
							elmsrog.addAttribute(new Attribute(field, ecsrog.getChange().get(field).get("newvalue")));
						}
					}
					if (ecsrog.getChangeType().equals("del")){
						System.out.println("Diff=del");
						System.out.println(ecsrog.getChange().get("deleted").get("basevalue"));
						elmsrog.addAttribute(new Attribute("Diff", "del"));
						elmsrog.addAttribute(new Attribute("Name", ecsrog.getChange().get("deleted").get("basevalue")));
					}
					msd.appendChild(elmsrog);					
				}
			}
			msgsd.appendChild(msd);
		}
		for (ElementChange ec: this.diff.findAddedSegmentRefOrGroups()){
			nu.xom.Element elmsrog = new nu.xom.Element("Elt");
			elmsrog.addAttribute(new Attribute("Diff", "add"));
			elmsrog.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));
			msgsd.appendChild(elmsrog);
		}
		e.appendChild(msgsd);

		// Segments 
		nu.xom.Element ss = new nu.xom.Element("Segments");

		for (Segment s1: p1.getSegments().getChildren()){
			if (this.diff.findOneBySegmentId(s1.getId()) != null 
					| this.diff.findOneByFieldParentId(s1.getId()) != null){

				nu.xom.Element elmsrog = this.serializeSegment(s1);

				if (this.diff.findOneByFieldParentId(s1.getId()) != null){
					for (ElementChange ec : this.diff.findFieldsByParentIdAndChangeType(s1.getId(), "*")){
						elmsrog.appendChild(this.serializeFields(ec));
					}
				}
				ss.appendChild(elmsrog);					
			}
		}
		for (ElementChange ec: this.diff.findAddedSegments()){
			nu.xom.Element elms = new nu.xom.Element("Segment");
			elms.addAttribute(new Attribute("Diff", "add"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));

			for (ElementChange ec2 : this.diff.findAddedFieldsByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("Field");
				elmf.addAttribute(new Attribute("Diff", "add"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("added").get("newvalue")));
				elms.appendChild(elmf);
			}
			ss.appendChild(elms);
		}
		for (ElementChange ec: this.diff.findDeletedSegments()){
			nu.xom.Element elms = new nu.xom.Element("Segment");
			elms.addAttribute(new Attribute("Diff", "del"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));

			for (ElementChange ec2 : this.diff.findDeletedFieldsByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("Field");
				elmf.addAttribute(new Attribute("Diff", "del"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("deleted").get("basevalue")));
				elms.appendChild(elmf);
			}
			ss.appendChild(elms);
		}
		e.appendChild(ss);


		//Datatypes
		nu.xom.Element ds = new nu.xom.Element("Datatypes");

		for (Datatype d : p1.getDatatypes().getChildren()) {
			//FIXME Check if necessary			if (d.getLabel().contains("_")) {
			if (this.diff.findOneByDatatypeId(d.getId()) != null |
					this.diff.findOneByComponentParentId(d.getId()) != null){

				nu.xom.Element elmd = this.serializeDatatype(d);

				if (this.diff.findOneByComponentParentId(d.getId()) != null){
					for (ElementChange ec : this.diff.findComponentsByParentIdAndChangeType(d.getId(), "*")){
						elmd.appendChild(this.serializeComponents(ec));
					}
				}
				ds.appendChild(elmd);
			}
			//				}
		}
		for (ElementChange ec: this.diff.findAddedDatatypes()){
			nu.xom.Element elms = new nu.xom.Element("Datatype");
			elms.addAttribute(new Attribute("Diff", "add"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));

			for (ElementChange ec2 : this.diff.findAddedComponentsByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("Component");
				elmf.addAttribute(new Attribute("Diff", "add"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("added").get("newvalue")));
				elms.appendChild(elmf);
			}
			ds.appendChild(elms);
		}
		for (ElementChange ec: this.diff.findDeletedDatatypes()){
			nu.xom.Element elms = new nu.xom.Element("Datatype");
			elms.addAttribute(new Attribute("Diff", "del"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));

			for (ElementChange ec2 : this.diff.findDeletedComponentsByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("Component");
				elmf.addAttribute(new Attribute("Diff", "del"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("deleted").get("basevalue")));
				elms.appendChild(elmf);
			}
			ds.appendChild(elms);
		}

		e.appendChild(ds);


		nu.xom.Element ts = new nu.xom.Element("Tables");
		for (Table t : p1.getTables().getChildren()) {

			if (this.diff.findOneByTableId(t.getId()) != null | 
					this.diff.findOneByCodeParentId(t.getId()) != null){

				nu.xom.Element elmt = this.serializeTable(t);

				if (this.diff.findOneByCodeParentId(t.getId()) != null){
					for (ElementChange ec : this.diff.findCodesByParentIdAndChangeType(t.getId(), "*")){
						elmt.appendChild(this.serializeCodes(ec));
					}
				}
				ts.appendChild(elmt);
			}
		}
		for (ElementChange ec: this.diff.findAddedTables()){
			nu.xom.Element elms = new nu.xom.Element("TableDefinition");
			elms.addAttribute(new Attribute("Diff", "add"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));

			for (ElementChange ec2 : this.diff.findAddedCodesByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("TableElement");
				elmf.addAttribute(new Attribute("Diff", "add"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("added").get("newvalue")));
				elms.appendChild(elmf);
			}
			ds.appendChild(elms);
		}
		for (ElementChange ec: this.diff.findDeletedTables()){
			nu.xom.Element elms = new nu.xom.Element("TableDefinition");
			elms.addAttribute(new Attribute("Diff", "del"));
			elms.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));

			for (ElementChange ec2 : this.diff.findDeletedCodesByParentId(ec.getId())){
				nu.xom.Element elmf = new nu.xom.Element("TableElement");
				elmf.addAttribute(new Attribute("Diff", "del"));				
				elmf.addAttribute(new Attribute("Name", ec2.getChange().get("deleted").get("basevalue")));
				elms.appendChild(elmf);
			}
			ds.appendChild(elms);
		}

		e.appendChild(ts);

		nu.xom.Document doc = new nu.xom.Document(e);
		return doc;
	}	



	private nu.xom.Element serializeSegment(Segment s1) {
		nu.xom.Element elmsrog = new nu.xom.Element("Segment");
		elmsrog.addAttribute(new Attribute("Name", s1.getName()));
		elmsrog.addAttribute(new Attribute("Description", s1.getDescription()));

		if (this.diff.findOneBySegmentId(s1.getId()) != null){

			ElementChange ecsrog = this.diff.findOneBySegmentId(s1.getId());

			if (ecsrog.getChangeType().equals("edit")){
				System.out.println("Diff=edit");
				elmsrog.addAttribute(new Attribute("Diff", "edit"));

				for (String field: ecsrog.getChange().keySet()){
					System.out.print(field +"=\"" + ecsrog.getChange().get(field).get("newvalue") + "\" ");
					elmsrog.addAttribute(new Attribute(field, ecsrog.getChange().get(field).get("newvalue")));
				}
			}
			if (ecsrog.getChangeType().equals("del")){
				System.out.println("Diff=del");
				System.out.println(ecsrog.getChange().get("deleted").get("basevalue"));
				elmsrog.addAttribute(new Attribute("Diff", "del"));
				elmsrog.addAttribute(new Attribute("Name", ecsrog.getChange().get("deleted").get("basevalue")));
			}
		}
		return elmsrog;
	}


	private nu.xom.Element serializeFields(ElementChange ec){

		nu.xom.Element elmf = new nu.xom.Element("Field");

		if (ec.getChangeType().equals("edit")){
			Field f = p1.getSegments().findOneField(ec.getId());
			elmf.addAttribute(new Attribute("Name", f.getName()));
			elmf.addAttribute(new Attribute("Position", String.valueOf(f.getPosition())));

			System.out.println("Diff=edit");
			elmf.addAttribute(new Attribute("Diff", "edit"));

			for (String field: ec.getChange().keySet()){
				System.out.print(field +"=\"" + ec.getChange().get(field).get("newvalue") + "\" ");
				elmf.addAttribute(new Attribute(field, ec.getChange().get(field).get("newvalue")));
			}
		}
		if (ec.getChangeType().equals("del")){
			Field f = p1.getSegments().findOneField(ec.getId());

			System.out.println("Diff=del");
			System.out.println(ec.getChange().get("deleted").get("basevalue"));
			elmf.addAttribute(new Attribute("Diff", "del"));
			elmf.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));
			elmf.addAttribute(new Attribute("Position", String.valueOf(f.getPosition())));
		}
		if (ec.getChangeType().equals("add")){
			Field f = p2.getSegments().findOneField(ec.getId());

			System.out.println("Diff=add");
			System.out.println(ec.getChange().get("added").get("newvalue"));
			elmf.addAttribute(new Attribute("Diff", "add"));
			elmf.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));
			elmf.addAttribute(new Attribute("Position", String.valueOf(f.getPosition())));
		}

		return elmf;

	}

	private nu.xom.Element serializeDatatype(Datatype d){
		nu.xom.Element elmd = new nu.xom.Element("Datatype");
		elmd.addAttribute(new Attribute("Name", d.getName()));
		elmd.addAttribute(new Attribute("Label", d.getLabel()));

		if (this.diff.findOneByDatatypeId(d.getId()) != null){

			ElementChange ec = this.diff.findOneByDatatypeId(d.getId());
			if (ec.getChangeType().equals("edit")){
				elmd.addAttribute(new Attribute("Diff", "edit"));

				for (String field: ec.getChange().keySet()){
					elmd.addAttribute(new Attribute(field, ec.getChange().get(field).get("newvalue")));
				}
			}
			if (ec.getChangeType().equals("del")){
				elmd.addAttribute(new Attribute("Diff", "del"));
				elmd.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));
			}
		}
		return elmd;
	}

	private nu.xom.Element serializeComponents(ElementChange ec){
		nu.xom.Element elmc = new nu.xom.Element("Component");


		if (ec.getChangeType().equals("edit")){
			Component c = p1.getDatatypes().findOneComponent(ec.getId());
			elmc.addAttribute(new Attribute("Name", c.getName()));
			elmc.addAttribute(new Attribute("Position", String.valueOf(c.getPosition())));
			elmc.addAttribute(new Attribute("Diff", "edit"));

			for (String field: ec.getChange().keySet()){
				System.out.print(field +"=\"" + ec.getChange().get(field).get("newvalue") + "\" ");
				elmc.addAttribute(new Attribute(field, ec.getChange().get(field).get("newvalue")));
			}
		}
		if (ec.getChangeType().equals("del")){
			Component c = p1.getDatatypes().findOneComponent(ec.getId());
			elmc.addAttribute(new Attribute("Name", c.getName()));
			elmc.addAttribute(new Attribute("Position", String.valueOf(c.getPosition())));
			elmc.addAttribute(new Attribute("Diff", "del"));
		}
		if (ec.getChangeType().equals("add")){
			Component c = p2.getDatatypes().findOneComponent(ec.getId());
			elmc.addAttribute(new Attribute("Name", c.getName()));
			elmc.addAttribute(new Attribute("Position", String.valueOf(c.getPosition())));
			elmc.addAttribute(new Attribute("Diff", "add"));
		}

		return elmc;
	}


	private nu.xom.Element serializeTable(Table t){
		nu.xom.Element elmt = new nu.xom.Element("TableDefinition");
		elmt.addAttribute(new Attribute("Id", t.getBindingIdentifier()));
		elmt.addAttribute(new Attribute("Name", t.getName()));

		if (this.diff.findOneByTableId(t.getId()) != null){

			ElementChange ec = this.diff.findOneByTableId(t.getId());

			if (ec.getChangeType().equals("edit")){
				elmt.addAttribute(new Attribute("Diff", "edit"));

				for (String field: ec.getChange().keySet()){
					elmt.addAttribute(new Attribute(field, ec.getChange().get(field).get("newvalue")));
				}
			}
			if (ec.getChangeType().equals("del")){
				elmt.addAttribute(new Attribute("Diff", "del"));
				elmt.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));
			}
		}
		return elmt;
	}

	private nu.xom.Element serializeCodes(ElementChange ec){
		nu.xom.Element elmc = new nu.xom.Element("TableElement");

		if (ec.getChangeType().equals("edit")){
			Code c = p1.getTables().findOneCodeById(ec.getId());
			elmc.addAttribute(new Attribute("Id", c.getId()));
			elmc.addAttribute(new Attribute("Diff", "edit"));

			for (String field: ec.getChange().keySet()){
				elmc.addAttribute(new Attribute(field, ec.getChange().get(field).get("newvalue")));
			}
		}
		if (ec.getChangeType().equals("del")){
			Code c = p1.getTables().findOneCodeById(ec.getId());
			elmc.addAttribute(new Attribute("Id", c.getId()));
			elmc.addAttribute(new Attribute("Diff", "del"));
			elmc.addAttribute(new Attribute("Name", ec.getChange().get("deleted").get("basevalue")));
		}
		if (ec.getChangeType().equals("add")){
			Code c = p2.getTables().findOneCodeById(ec.getId());
			elmc.addAttribute(new Attribute("Id", c.getId()));
			elmc.addAttribute(new Attribute("Name", ec.getChange().get("added").get("newvalue")));
			elmc.addAttribute(new Attribute("Diff", "add"));
		}

		return elmc;
	}

	public static void main(String[] args) throws IOException, ValidityException, ParsingException, TransformerConfigurationException {
		try {

			IGDocumentSerialization4ExportImpl test1 = new IGDocumentSerialization4ExportImpl();

			Profile p1 = test1.deserializeXMLToProfile(
					new String(Files.readAllBytes(Paths
							.get("src//main//resources//vxu//Profile.xml"))),
							new String(Files.readAllBytes(Paths
									.get("src//main//resources//vxu//ValueSets_all.xml"))),
									new String(Files.readAllBytes(Paths
											.get("src//main//resources//vxu//Constraints.xml"))));

			System.out.println(StringUtils.repeat("& * ", 25));
			ProfileMetaData metaData = p1.getMetaData();

			DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			Date date = new Date();
			metaData.setDate(dateFormat.format(date));
			metaData.setName("IZ_VXU");
			metaData.setOrgName("NIST");
			metaData.setSubTitle("Specifications");
			metaData.setVersion("1.0");

			metaData.setHl7Version(HL7Version.V2_7.value());
			metaData.setSchemaVersion(SchemaVersion.V1_0.value());
			metaData.setStatus("Draft");

			p1.setMetaData(metaData);

			Profile p2 = p1.clone();
			p1.setId("1");
			p2.setId("2");


			Message message = p2.getMessages().getChildren()
					.toArray(new Message[] {})[0];
			SegmentRef segmentRef = (SegmentRef) message.getChildren().get(0);
			Group group = (Group) message.getChildren().get(5);
			Segment segment = p2.getSegments().findOneSegmentById(segmentRef.getRef());
			Field field = segment.getFields().get(0);
			Datatype datatype = p2.getDatatypes().getChildren()
					.toArray(new Datatype[] {})[0];
			Component component = datatype.getComponents().get(0);
			Table table = p2.getTables().getChildren().toArray(new Table[] {})[0];
			Code code = table.getCodes().get(0);


			//Fake addition
			SegmentRef segmentRef3 = (SegmentRef) message.getChildren().get(4);
			Segment segment3 = p1.getSegments().findOneSegmentById(segmentRef3.getRef());
			p1.getSegments().delete(segment3.getId());


			segmentRef.setMin(456);
			segmentRef.setMax("94969");
			field.setComment("wawa");
			field.setName("new field name");
			field.setMax("3423542");
			field.setMin(234);
			group.setMax("*");
			group.setComment("new group comment");
			p2.getMetaData().setName(new String("IZ_VXU_X"));
			//			datatype.setComment("new dt comment");
			//			segment.setComment("<h2>Tqqqqqqqq</h2>");
			segment.setText1("<h2>Test format!</h2><p>textAngular WYSIWYG Text Editor</p><p><b>Features:</b></p><ol><li>Two-Way-Binding</li><li style=\"color: ;\"><b>Theming</b> Options</li><li>Simple Editor Instance Creation</li></ol><p><b>Link test:</b> <a href=\"https://github.com/fraywing/textAngular\">Here</a> </p>");
			component.setComment("new component comment");
			component.setMinLength(56346);
			table.setName("illegal name change-for test purpose");
			code.setLabel("illegal new label-for test purpose");

			ProfileDiffImpl diff = new ProfileDiffImpl();
			diff.compare(p1, p2);

			ProfileDiffSerializationImpl pds = new ProfileDiffSerializationImpl(p1, p2, diff);

			System.out.println(pds.serializeDiffToXML());
			System.out.println("done");
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}
}
