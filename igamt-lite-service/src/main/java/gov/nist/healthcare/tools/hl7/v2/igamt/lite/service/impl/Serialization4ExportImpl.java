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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CCValue;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsColumn;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

@Service
public class Serialization4ExportImpl implements IGDocumentSerialization {
	Logger logger = LoggerFactory.getLogger(Serialization4ExportImpl.class);

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private TableService tableService;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private ApplicationContext applicationContext;

	public File serializeProfileToFile(Profile profile) throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("ProfileTemp", ".xml");
			FileOutputStream outputStream = (FileOutputStream) Files.newOutputStream(out.toPath());
			Serializer ser;
			ser = new Serializer(outputStream, "UTF-8");
			ser.setIndent(4);
			ser.write(this.serializeProfileToDoc(profile));
			return out;
		} catch (IOException e1) {
			logger.warn("IO Exception");
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public String serializeProfileToXML(Profile profile) {
		return this.serializeProfileToDoc(profile).toXML();
	}

	@Override
	public File serializeSectionsToFile(IGDocument igdoc) throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("SectionsTemp", ".xml");
			FileOutputStream outputStream = new FileOutputStream(out);
			Serializer ser;
			ser = new Serializer(outputStream, "UTF-8");
			ser.setIndent(4);
			ser.write(new nu.xom.Document(this.serializeIGDocumentSectionsToDoc(igdoc)));
			outputStream.close();
			return out;
		} catch (IOException e1) {
			logger.warn("IO Exception");
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public String serializeSectionsToXML(IGDocument igdoc) {
		nu.xom.Element e = this.serializeIGDocumentSectionsToDoc(igdoc);
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc.toXML();
	}

	@Override
	public String serializeIGDocumentToXML(IGDocument igdoc) throws SerializationException {
		SerializationService serializationService = new SerializationServiceImpl(applicationContext);
		String xml = serializationService.serializeIGDocument(igdoc, SerializationLayout.IGDOCUMENT, ExportConfig.getBasicExportConfig(false)).toXML();
		return xml;
	}

	@Override
	public nu.xom.Document serializeIGDocumentToDoc(IGDocument igdoc) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");

		nu.xom.Element metadata = this.serializeIGDocumentMetadataToDoc(igdoc);
		metadata.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		nu.xom.Element rootSections = this.serializeIGDocumentSectionsToDoc(igdoc);
		rootSections.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		nu.xom.Element profileSections = this.serializeProfileToDoc(igdoc);
		profileSections.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		nu.xom.Document doc = new nu.xom.Document(e);
		e.appendChild(metadata);
		e.appendChild(rootSections);
		e.appendChild(profileSections);
		return doc;
	}

	@Override
	public String serializeDatatypesToXML(IGDocument igdoc) {
		return serializeDatatypesToDoc(igdoc).toXML();
	}

	public nu.xom.Document serializeDatatypesToDoc(IGDocument igdoc) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		nu.xom.Element datatypesSections = this.serializeDatatypesToElement(igdoc);
		nu.xom.Document doc = new nu.xom.Document(e);
		e.appendChild(datatypesSections);

		return doc;
	}

	@Override
	public String serializeTableToXML(TableLink tl) {
		nu.xom.Element e = serializeOneTable(tl);
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc.toXML();
	}

	@Override
	public String serializeDatatypeToXML(DatatypeLink dl) {
		nu.xom.Element e = serializeOneDatatype(dl);
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc.toXML();
	}

	@Override
	public String serializeSegmentToXML(SegmentLink sl) {
		nu.xom.Element e = serializeOneSegment(sl);
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc.toXML();
	}

	@Override
	public String serializeMessageToXML(Message m) {
		nu.xom.Element e = serializeOneMessage(m);
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc.toXML();
	}

	public nu.xom.Element serializeIGDocumentMetadataToDoc(IGDocument igdoc) {
		nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");

		if (igdoc.getMetaData() != null) {
			DocumentMetaData metaDataObj = igdoc.getMetaData();
			if (metaDataObj.getTitle() != null)
				elmMetaData.addAttribute(new Attribute("Name", metaDataObj.getTitle()));
			if (metaDataObj.getSubTitle() != null)
				elmMetaData.addAttribute(new Attribute("Subtitle", metaDataObj.getSubTitle()));
			if (metaDataObj.getVersion() != null)
				elmMetaData.addAttribute(new Attribute("DocumentVersion", metaDataObj.getVersion()));
			if (igdoc.getDateUpdated() != null)
				elmMetaData.addAttribute(new Attribute("Date", DateUtils.format(igdoc.getDateUpdated())));
			if (metaDataObj.getExt() != null)
				elmMetaData.addAttribute(new Attribute("Ext", metaDataObj.getExt()));
		}
		if (igdoc.getProfile().getMetaData() != null) {
			ProfileMetaData metaDataObj = igdoc.getProfile().getMetaData();
			if (metaDataObj.getOrgName() != null)
				elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj.getOrgName()));
			if (metaDataObj.getStatus() != null)
				elmMetaData.addAttribute(new Attribute("Status", metaDataObj.getStatus()));
			if (metaDataObj.getTopics() != null)
				elmMetaData.addAttribute(new Attribute("Topics", metaDataObj.getTopics()));
			if (metaDataObj.getHl7Version() != null)
				elmMetaData.addAttribute(new Attribute("HL7Version", metaDataObj.getHl7Version()));
		}
		return elmMetaData;
	}

	public nu.xom.Element serializeIGDocumentSectionsToDoc(IGDocument igdoc) {
		nu.xom.Element rootSections = new nu.xom.Element("Sections");
		addContents4Html(igdoc.getChildSections(), "", 1, rootSections);
		return rootSections;
	}

	private void addContents4Html(Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sect, String prefix,
			Integer depth, nu.xom.Element elt) {
		SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortedSections = sortSections(sect);
		for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section s : sortedSections) {
			nu.xom.Element xsect = new nu.xom.Element("Section");
			xsect.addAttribute(new Attribute("id", "ID_" + s.getId()));
			xsect.addAttribute(new Attribute("position", String.valueOf(s.getSectionPosition())));
			xsect.addAttribute(new Attribute("h", String.valueOf(depth)));
			if (s.getSectionTitle() != null)
				xsect.addAttribute(new Attribute("title", s.getSectionTitle()));

			if (s.getSectionContents() != null && !s.getSectionContents().isEmpty()) {
				nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
				sectCont.appendChild(cleanRichtext(s.getSectionContents()));
				xsect.appendChild(sectCont);
			}

			if (depth == 1) {
				xsect.addAttribute(new Attribute("prefix", String.valueOf(s.getSectionPosition())));
				addContents4Html(s.getChildSections(), String.valueOf(s.getSectionPosition()), depth + 1, xsect);
			} else {
				xsect.addAttribute(new Attribute("prefix", prefix + "." + String.valueOf(s.getSectionPosition())));
				addContents4Html(s.getChildSections(), prefix + "." + String.valueOf(s.getSectionPosition()), depth + 1,
						xsect);
			}
			elt.appendChild(xsect);
		}
	}

	private SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortSections(Set<Section> s) {
		SortedSet<Section> sortedSet = new TreeSet<Section>();
		Iterator<Section> setIt = s.iterator();
		while (setIt.hasNext()) {
			sortedSet.add(setIt.next());
		}
		return sortedSet;
	}

	public nu.xom.Element serializeProfileToDoc(IGDocument igdoc) {
		Profile profile = igdoc.getProfile();
		System.out.println(profile.getMessages().getChildren().size());
		nu.xom.Element xsect = new nu.xom.Element("Section");
		xsect.addAttribute(new Attribute("id", "ID_" + profile.getId()));
		xsect.addAttribute(new Attribute("position", String.valueOf(profile.getSectionPosition())));
		xsect.addAttribute(new Attribute("prefix", String.valueOf(profile.getSectionPosition() + 1)));
		xsect.addAttribute(new Attribute("h", String.valueOf(1)));
		if (profile.getSectionTitle() != null) {
			xsect.addAttribute(new Attribute("title", profile.getSectionTitle()));
		} else {
			xsect.addAttribute(new Attribute("title", ""));
		}
		if (profile.getSectionContents() != null && !profile.getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild("<div class=\"fr-view\">" + profile.getSectionContents() + "</div>");
			xsect.appendChild(sectCont);
		}

		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", "ID_" + profile.getId()));
		ProfileMetaData metaData = profile.getMetaData();
		if (metaData.getType() != null && !metaData.getType().isEmpty())
			e.addAttribute(new Attribute("Type", metaData.getType()));
		if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
			e.addAttribute(new Attribute("HL7Version", metaData.getHl7Version()));
		if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
			e.addAttribute(new Attribute("SchemaVersion", metaData.getSchemaVersion()));

		if (profile.getMetaData() != null) {
			nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
			ProfileMetaData metaDataObj = profile.getMetaData();
			if (metaDataObj.getName() != null)
				elmMetaData.addAttribute(new Attribute("Name", metaDataObj.getName()));
			if (metaDataObj.getOrgName() != null)
				elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj.getOrgName()));
			if (metaDataObj.getStatus() != null)
				elmMetaData.addAttribute(new Attribute("Status", metaDataObj.getStatus()));
			if (metaDataObj.getTopics() != null)
				elmMetaData.addAttribute(new Attribute("Topics", metaDataObj.getTopics()));
			if (metaDataObj.getSubTitle() != null)
				elmMetaData.addAttribute(new Attribute("Subtitle", metaDataObj.getSubTitle()));
			if (metaDataObj.getVersion() != null)
				elmMetaData.addAttribute(new Attribute("Version", metaDataObj.getVersion()));
			if (igdoc.getDateUpdated() != null)
				elmMetaData.addAttribute(new Attribute("Date", DateUtils.format(igdoc.getDateUpdated())));
			if (metaDataObj.getExt() != null)
				elmMetaData.addAttribute(new Attribute("Ext", metaDataObj.getExt()));
			if (profile.getComment() != null && !profile.getComment().equals("")) {
				elmMetaData.addAttribute(new Attribute("Comment", profile.getComment()));
			}

			e.appendChild(elmMetaData);

			if (profile.getMetaData().getEncodings() != null && profile.getMetaData().getEncodings().size() > 0) {
				nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
				for (String encoding : profile.getMetaData().getEncodings()) {
					nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
					elmEncoding.appendChild(encoding);
					elmEncodings.appendChild(elmEncoding);
				}
				e.appendChild(elmEncodings);
			}
		}

		if (profile.getUsageNote() != null && !profile.getUsageNote().isEmpty()) {
			nu.xom.Element ts = new nu.xom.Element("Text");
			if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
				nu.xom.Element elmUsageNote = new nu.xom.Element("UsageNote");
				elmUsageNote.appendChild(profile.getUsageNote());
				ts.appendChild(elmUsageNote);
			}
			e.appendChild(ts);
		}

		String prefix = "";

		// nu.xom.Element msd = new nu.xom.Element("MessagesDisplay");
		nu.xom.Element msd = new nu.xom.Element("Section");
		msd.addAttribute(new Attribute("id", "ID_" + profile.getMessages().getId()));
		msd.addAttribute(new Attribute("position", String.valueOf(profile.getMessages().getSectionPosition())));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
				+ String.valueOf(profile.getMessages().getSectionPosition() + 1);
		msd.addAttribute(new Attribute("prefix", prefix));
		msd.addAttribute(new Attribute("h", String.valueOf(2)));
		if (profile.getMessages().getSectionTitle() != null) {
			msd.addAttribute(new Attribute("title", profile.getMessages().getSectionTitle()));
		} else {
			msd.addAttribute(new Attribute("title", ""));
		}
		if (profile.getMessages().getSectionContents() != null
				&& !profile.getMessages().getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild("<div class=\"fr-view\">" + profile.getMessages().getSectionContents() + "</div>");
			msd.appendChild(sectCont);
		}

		// profile.getMessages().setPositionsOrder();
		// List<Message> msgList = new
		// ArrayList<>(profile.getMessages().getChildren());
		// Collections.sort(msgList);
		//
		// for (Message m : msgList) {
		for (Message m : profile.getMessages().getChildren()) {
			msd.appendChild(this.serializeMessageDisplay(m, profile.getSegmentLibrary(), prefix));
		}
		xsect.appendChild(msd);

		// nu.xom.Element ss = new nu.xom.Element("Segments");
		nu.xom.Element ss = new nu.xom.Element("Section");
		ss.addAttribute(new Attribute("id", "ID_" + profile.getSegmentLibrary().getId()));
		ss.addAttribute(new Attribute("position", String.valueOf(profile.getSegmentLibrary().getSectionPosition())));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
				+ String.valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
		ss.addAttribute(new Attribute("prefix", prefix));
		ss.addAttribute(new Attribute("h", String.valueOf(2)));
		if (profile.getSegmentLibrary().getSectionTitle() != null) {
			ss.addAttribute(new Attribute("title", profile.getSegmentLibrary().getSectionTitle()));
		} else {
			ss.addAttribute(new Attribute("title", ""));
		}
		if (profile.getSegmentLibrary().getSectionContents() != null
				&& !profile.getSegmentLibrary().getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild(
					"<div class=\"fr-view\">" + profile.getSegmentLibrary().getSectionContents() + "</div>");
			ss.appendChild(sectCont);
		}

		List<SegmentLink> sgtList = new ArrayList<SegmentLink>(profile.getSegmentLibrary().getChildren());
		Collections.sort(sgtList);
		for (SegmentLink link : sgtList) {
			this.serializeSegment(ss, link, profile.getTableLibrary(), profile.getDatatypeLibrary(),
					prefix + "." + String.valueOf(sgtList.indexOf(link) + 1), sgtList.indexOf(link));
		}
		xsect.appendChild(ss);

		// nu.xom.Element ds = new nu.xom.Element("Datatypes");
		nu.xom.Element ds = new nu.xom.Element("Section");
		ds.addAttribute(new Attribute("id", "ID_" + profile.getDatatypeLibrary().getId()));
		ds.addAttribute(new Attribute("position", String.valueOf(profile.getDatatypeLibrary().getSectionPosition())));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
				+ String.valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
		ds.addAttribute(new Attribute("prefix", prefix));
		ds.addAttribute(new Attribute("h", String.valueOf(2)));
		if (profile.getDatatypeLibrary().getSectionTitle() != null) {
			ds.addAttribute(new Attribute("title", profile.getDatatypeLibrary().getSectionTitle()));
		} else {
			ds.addAttribute(new Attribute("title", ""));
		}
		if (profile.getDatatypeLibrary().getSectionContents() != null
				&& !profile.getDatatypeLibrary().getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild(
					"<div class=\"fr-view\">" + profile.getDatatypeLibrary().getSectionContents() + "</div>");
			ds.appendChild(sectCont);
		}

		// profile.getDatatypeLibrary().setPositionsOrder();
		List<DatatypeLink> dtList = new ArrayList<DatatypeLink>(profile.getDatatypeLibrary().getChildren());
		Collections.sort(dtList);
		for (DatatypeLink dl : dtList) {
			// Old condition to serialize only flavoured datatypes
			// if (d.getLabel().contains("_")) {
			// ds.appendChild(this.serializeDatatype(d,
			// profile.getTableLibrary(),
			// profile.getDatatypeLibrary()));
			// }
			if (dl.getId() != null && datatypeService != null && datatypeService.findById(dl.getId()) != null) {
				ds.appendChild(this.serializeDatatype(dl, profile.getTableLibrary(), profile.getDatatypeLibrary(),
						prefix + "." + String.valueOf(dtList.indexOf(dl) + 1), dtList.indexOf(dl)));
			}
		}
		xsect.appendChild(ds);

		// nu.xom.Element ts = new nu.xom.Element("ValueSets");
		nu.xom.Element ts = new nu.xom.Element("Section");
		ts.addAttribute(new Attribute("id", "ID_" + profile.getTableLibrary().getId()));
		ts.addAttribute(new Attribute("position", String.valueOf(profile.getTableLibrary().getSectionPosition())));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
				+ String.valueOf(profile.getTableLibrary().getSectionPosition() + 1);
		ts.addAttribute(new Attribute("prefix", prefix));
		ts.addAttribute(new Attribute("h", String.valueOf(2)));
		if (profile.getTableLibrary().getSectionTitle() != null) {
			ts.addAttribute(new Attribute("title", profile.getTableLibrary().getSectionTitle()));
		} else {
			ts.addAttribute(new Attribute("title", ""));
		}
		if (profile.getTableLibrary().getSectionContents() != null
				&& !profile.getTableLibrary().getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild("<div class=\"fr-view\">" + profile.getTableLibrary().getSectionContents() + "</div>");
			ts.appendChild(sectCont);
		}

		// profile.getTableLibrary().setPositionsOrder();
		List<TableLink> tables = new ArrayList<TableLink>(profile.getTableLibrary().getChildren());
		// TODO Need check Sort
		Collections.sort(tables);
		for (TableLink link : tables) {
			if (tableService != null) {
				if (tableService.findById(link.getId()) != null) {
					ts.appendChild(this.serializeTable(link, prefix + "." + String.valueOf(tables.indexOf(link)),
							tables.indexOf(link)));
				}
			}
		}
		xsect.appendChild(ts);

		nu.xom.Element cnts = new nu.xom.Element("Section");
		cnts.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cnts.addAttribute(new Attribute("position", String.valueOf(5)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5);
		cnts.addAttribute(new Attribute("prefix", prefix));
		cnts.addAttribute(new Attribute("h", String.valueOf(2)));
		cnts.addAttribute(new Attribute("title", "Conformance information"));

		nu.xom.Element cs = new nu.xom.Element("Section");
		cs.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cs.addAttribute(new Attribute("position", String.valueOf(1)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1);
		cs.addAttribute(new Attribute("prefix", prefix));
		cs.addAttribute(new Attribute("h", String.valueOf(3)));
		cs.addAttribute(new Attribute("title", "Conformance statements"));

		nu.xom.Element cp = new nu.xom.Element("Section");
		cp.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cp.addAttribute(new Attribute("position", String.valueOf(2)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2);
		cp.addAttribute(new Attribute("prefix", prefix));
		cp.addAttribute(new Attribute("h", String.valueOf(3)));
		cp.addAttribute(new Attribute("title", "Conditional predicates"));

		// * Messages
		nu.xom.Element csmsg = new nu.xom.Element("Section");
		csmsg.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		csmsg.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
				+ "." + String.valueOf(profile.getMessages().getSectionPosition() + 1);
		csmsg.addAttribute(new Attribute("prefix", prefix));
		csmsg.addAttribute(new Attribute("h", String.valueOf(4)));
		csmsg.addAttribute(new Attribute("title", "Conformance profile level"));

		nu.xom.Element cpmsg = new nu.xom.Element("Section");
		cpmsg.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cpmsg.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2)
				+ "." + String.valueOf(profile.getMessages().getSectionPosition() + 1);
		cpmsg.addAttribute(new Attribute("prefix", prefix));
		cpmsg.addAttribute(new Attribute("h", String.valueOf(4)));
		cpmsg.addAttribute(new Attribute("title", "Conformance profile level"));

		for (Message m : profile.getMessages().getChildren()) {
			if (m.getChildren() != null) {

				nu.xom.Element csinfo = new nu.xom.Element("Constraints");
				csinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
				csinfo.addAttribute(new Attribute("position", String.valueOf(m.getPosition())));
				csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
				csinfo.addAttribute(new Attribute("title", m.getName()));
				csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

				nu.xom.Element cpinfo = new nu.xom.Element("Constraints");
				cpinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
				cpinfo.addAttribute(new Attribute("position", ""));
				cpinfo.addAttribute(new Attribute("h", String.valueOf(3)));
				cpinfo.addAttribute(new Attribute("title", m.getName()));
				cpinfo.addAttribute(new Attribute("Type", "ConditionPredicate"));

				// Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
				// new HashMap<Integer, SegmentRefOrGroup>();
				//
				// for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
				// segmentRefOrGroups.put(segmentRefOrGroup.getPosition(),
				// segmentRefOrGroup);
				// }
				
				serializeMessageConstraints(m, csinfo, cpinfo);

				List<SegmentRefOrGroup> children = m.getChildren();
				for (int i = 0; i < children.size(); i++) {
					SegmentRefOrGroup segmentRefOrGroup = children.get(i);

					String prefixcp = String.valueOf(profile.getSectionPosition() + 1) + "5.1.3";
					String prefixcs = String.valueOf(profile.getSectionPosition() + 1) + "5.2.3";

					this.serializeSegmentRefOrGroupConstraint(segmentRefOrGroup.getPosition(), segmentRefOrGroup,
							csinfo, cpinfo, prefixcs, prefixcp);

				}
				cpmsg.appendChild(cpinfo);
				csmsg.appendChild(csinfo);
			}
		}

		cp.appendChild(cpmsg);
		cs.appendChild(csmsg);

		// Constraints for segments
		nu.xom.Element cssg = new nu.xom.Element("Section");
		cssg.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cssg.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
				+ "." + String.valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
		cssg.addAttribute(new Attribute("prefix", prefix));
		cssg.addAttribute(new Attribute("h", String.valueOf(4)));
		cssg.addAttribute(new Attribute("title", "Segment level"));

		nu.xom.Element cpsg = new nu.xom.Element("Section");
		cpsg.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cpsg.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2)
				+ "." + String.valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
		cpsg.addAttribute(new Attribute("prefix", prefix));
		cpsg.addAttribute(new Attribute("h", String.valueOf(4)));
		cpsg.addAttribute(new Attribute("title", "Segment level"));

		for (SegmentLink sl : profile.getSegmentLibrary().getChildren()) {
			if (sl.getId() != null && segmentService != null && segmentService.findById(sl.getId()) != null) {
				Segment s = segmentService.findById(sl.getId());
				if (s.getFields() != null) {

					nu.xom.Element csinfo = new nu.xom.Element("Constraints");
					csinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
					csinfo.addAttribute(new Attribute("position", ""));
					csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
					csinfo.addAttribute(new Attribute("title", sl.getLabel()));
					csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

					nu.xom.Element cpinfo = new nu.xom.Element("Constraints");
					cpinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
					cpinfo.addAttribute(new Attribute("position", ""));
					cpinfo.addAttribute(new Attribute("h", String.valueOf(3)));
					cpinfo.addAttribute(new Attribute("title", s.getLabel()));
					cpinfo.addAttribute(new Attribute("Type", "ConditionPredicate"));

					// Map<Integer, Field> fields = new HashMap<Integer,
					// Field>();

					// for (Field f : s.getFields()) {
					// fields.put(f.getPosition(), f);
					// }

					List<Field> children = s.getFields();
					for (int i = 0; i < children.size(); i++) {
						List<Constraint> constraints = findConstraints(children.get(i).getPosition(), s.getPredicates(),
								s.getConformanceStatements());
						if (!constraints.isEmpty()) {
							for (Constraint constraint : constraints) {
								nu.xom.Element elmConstraint = serializeConstraintToElement(constraint,
										s.getName() + "-");
								if (constraint instanceof Predicate) {
									prefix = String.valueOf(profile.getSectionPosition() + 1) + "5.1.3";
									cpinfo.addAttribute(new Attribute("prefix", prefix));
									cpinfo.appendChild(elmConstraint);
								} else if (constraint instanceof ConformanceStatement) {
									prefix = String.valueOf(profile.getSectionPosition() + 1) + "5.2.3";
									csinfo.addAttribute(new Attribute("prefix", prefix));
									csinfo.appendChild(elmConstraint);
								}
							}
						}
					}
					cpsg.appendChild(cpinfo);
					cssg.appendChild(csinfo);
				}
			}
		}

		cp.appendChild(cpsg);
		cs.appendChild(cssg);

		// Constraints for datatypes
		nu.xom.Element csdt = new nu.xom.Element("Section");
		csdt.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		csdt.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
				+ "." + String.valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
		csdt.addAttribute(new Attribute("prefix", prefix));
		csdt.addAttribute(new Attribute("h", String.valueOf(4)));
		csdt.addAttribute(new Attribute("title", "Datatype level"));

		nu.xom.Element cpdt = new nu.xom.Element("Section");
		cpdt.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
		cpdt.addAttribute(new Attribute("position", String.valueOf(3)));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2)
				+ "." + String.valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
		cpdt.addAttribute(new Attribute("prefix", prefix));
		cpdt.addAttribute(new Attribute("h", String.valueOf(4)));
		cpdt.addAttribute(new Attribute("title", "Datatype level"));

		for (DatatypeLink dl : profile.getDatatypeLibrary().getChildren()) {
			if (datatypeService != null && dl.getId() != null && datatypeService.findById(dl.getId()) != null) {
				Datatype d = datatypeService.findById(dl.getId());
				if (d.getComponents() != null && d.getComponents().size() > 0) {

					nu.xom.Element csinfo = new nu.xom.Element("Constraints");
					csinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
					csinfo.addAttribute(new Attribute("position", ""));
					csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
					csinfo.addAttribute(new Attribute("title", d.getLabel()));
					csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

					nu.xom.Element cpdtinfo = new nu.xom.Element("Constraints");
					cpdtinfo.addAttribute(new Attribute("id", "ID_" + UUID.randomUUID().toString().replaceAll("-", "")));
					cpdtinfo.addAttribute(new Attribute("position", ""));
					cpdtinfo.addAttribute(new Attribute("h", String.valueOf(3)));
					cpdtinfo.addAttribute(new Attribute("title", d.getLabel()));
					cpdtinfo.addAttribute(new Attribute("Type", "ConditionPredicate"));
					//
					// Map<Integer, Component> components = new HashMap<Integer,
					// Component>();
					// for (Component c : d.getComponents()) {
					// components.put(c.getPosition(), c);
					// }
					for (int i = 0; i < d.getComponents().size(); i++) {
						// Component c = components.get(i);
						List<Constraint> constraints = findConstraints(d.getComponents().get(i).getPosition(),
								d.getPredicates(), d.getConformanceStatements());
						if (!constraints.isEmpty()) {
							for (Constraint constraint : constraints) {
								nu.xom.Element elmConstraint = serializeConstraintToElement(constraint,
										d.getName() + ".");
								if (constraint instanceof Predicate) {
									prefix = String.valueOf(profile.getSectionPosition() + 1) + "5.1.3";
									cpdtinfo.addAttribute(new Attribute("prefix", prefix));
									cpdtinfo.appendChild(elmConstraint);
								} else if (constraint instanceof ConformanceStatement) {
									prefix = String.valueOf(profile.getSectionPosition() + 1) + "5.2.3";
									csinfo.addAttribute(new Attribute("prefix", prefix));
									csinfo.appendChild(elmConstraint);
								}
							}
						}
					}
					cpdt.appendChild(cpdtinfo);
					csdt.appendChild(csinfo);
				}
			}
		}

		cp.appendChild(cpdt);
		cs.appendChild(csdt);

		cnts.appendChild(cp);
		cnts.appendChild(cs);

		xsect.appendChild(cnts);

		xsect.appendChild(e);
		return xsect;
	}

	public nu.xom.Element serializeConstraintToElement(Constraint constraint, String locationName) {
		nu.xom.Element elmConstraint = new nu.xom.Element("Constraint");
		elmConstraint.addAttribute(
				new Attribute("Id", constraint.getConstraintId() == null ? "" : constraint.getConstraintId()));
			if(null!=constraint.getConstraintTarget()&&constraint.getConstraintTarget().length()>"[".length()) {
					elmConstraint.addAttribute(new Attribute("Location", constraint.getConstraintTarget()
							.substring(0, constraint.getConstraintTarget().indexOf('['))));
			} else {
					//TODO report the error correctly
					elmConstraint.addAttribute(new Attribute("Location","!!DEBUG : ERROR!!"));
			}
		elmConstraint.addAttribute(new Attribute("LocationName", locationName));
		elmConstraint.appendChild(constraint.getDescription());
		if (constraint instanceof Predicate) {
			elmConstraint.addAttribute(new Attribute("Type", "pre"));
			elmConstraint.addAttribute(new Attribute("Usage", "C(" + ((Predicate) constraint).getTrueUsage() + "/"
					+ ((Predicate) constraint).getFalseUsage() + ")"));
		} else if (constraint instanceof ConformanceStatement) {
			elmConstraint.addAttribute(new Attribute("Type", "cs"));
			elmConstraint.addAttribute(new Attribute("Classification",
					constraint.getConstraintClassification() == null ? "" : constraint.getConstraintClassification()));
		}
		return elmConstraint;
	}
	
	private void serializeMessageConstraints(Message m, nu.xom.Element csinfo, nu.xom.Element cpinfo){
		List<ConformanceStatement> conformances = m.getConformanceStatements();
		if (conformances != null && !conformances.isEmpty()) {
			for (Constraint constraint : conformances) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				csinfo.appendChild(elmConstraint);
			}
		}
		List<Predicate> predicates = m.getPredicates();
		if (predicates != null && !predicates.isEmpty()) {
			for (Constraint constraint : predicates) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				cpinfo.appendChild(elmConstraint);
			}
		}
		
	}

	private void serializeSegmentRefOrGroupConstraint(Integer i, SegmentRefOrGroup segmentRefOrGroup,
			nu.xom.Element csinfo, nu.xom.Element cpinfo, String prefixcs, String prefixcp) {
		List<Constraint> constraints = findConstraints(i, segmentRefOrGroup.getPredicates(),
				segmentRefOrGroup.getConformanceStatements());
		if (!constraints.isEmpty()) {
			for (Constraint constraint : constraints) {
				String locationName = segmentRefOrGroup instanceof Group ? ((Group) segmentRefOrGroup).getName()
						: ((SegmentRef) segmentRefOrGroup).getRef().getName();
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, locationName + "-");
				if (constraint instanceof Predicate) {
					cpinfo.addAttribute(new Attribute("prefix", prefixcp));
					cpinfo.appendChild(elmConstraint);
				} else if (constraint instanceof ConformanceStatement) {
					csinfo.addAttribute(new Attribute("prefix", prefixcs));
					csinfo.appendChild(elmConstraint);
				}
			}
		}

		if (segmentRefOrGroup instanceof Group) {
			// Map<Integer, SegmentRefOrGroup> segmentRefOrGroups =
			// new HashMap<Integer, SegmentRefOrGroup>();
			//
			// for (SegmentRefOrGroup srog : ((Group)
			// segmentRefOrGroup).getChildren()) {
			// segmentRefOrGroups.put(srog.getPosition(), srog);
			// }

			List<SegmentRefOrGroup> children = ((Group) segmentRefOrGroup).getChildren();
			for (int j = 0; j < children.size(); j++) {
				SegmentRefOrGroup srog = children.get(j);
				this.serializeSegmentRefOrGroupConstraint(srog.getPosition(), srog, csinfo, cpinfo, prefixcs, prefixcp);
			}
		}
	}

	public nu.xom.Element serializeDatatypesToElement(IGDocument igdoc) {
		Profile profile = igdoc.getProfile();
		nu.xom.Element xsect = new nu.xom.Element("Section");
		xsect.addAttribute(new Attribute("id", "ID_" + profile.getId()));
		xsect.addAttribute(new Attribute("position", String.valueOf(profile.getSectionPosition())));
		xsect.addAttribute(new Attribute("prefix", String.valueOf(profile.getSectionPosition() + 1)));
		xsect.addAttribute(new Attribute("h", String.valueOf(1)));
		if (profile.getSectionTitle() != null) {
			xsect.addAttribute(new Attribute("title", profile.getSectionTitle()));
		} else {
			xsect.addAttribute(new Attribute("title", ""));
		}

		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", "ID_" + profile.getId()));
		ProfileMetaData metaData = profile.getMetaData();
		if (metaData.getType() != null && !metaData.getType().isEmpty())
			e.addAttribute(new Attribute("Type", metaData.getType()));
		if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
			e.addAttribute(new Attribute("HL7Version", metaData.getHl7Version()));
		if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
			e.addAttribute(new Attribute("SchemaVersion", metaData.getSchemaVersion()));

		String prefix = "";

		// nu.xom.Element ds = new nu.xom.Element("Datatypes");
		nu.xom.Element ds = new nu.xom.Element("Section");
		ds.addAttribute(new Attribute("id", "ID_" + profile.getDatatypeLibrary().getId()));
		ds.addAttribute(new Attribute("position", String.valueOf(profile.getDatatypeLibrary().getSectionPosition())));
		prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
				+ String.valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
		ds.addAttribute(new Attribute("prefix", prefix));
		ds.addAttribute(new Attribute("h", String.valueOf(2)));
		if (profile.getDatatypeLibrary().getSectionTitle() != null) {
			ds.addAttribute(new Attribute("title", profile.getDatatypeLibrary().getSectionTitle()));
		} else {
			ds.addAttribute(new Attribute("title", ""));
		}
		// TODO check setPositionsOrder
		// profile.getDatatypeLibrary().setPositionsOrder();
		List<DatatypeLink> dtList = new ArrayList<>(profile.getDatatypeLibrary().getChildren());
		// TODO check sort
		// Collections.sort(dtList);
		for (DatatypeLink dl : dtList) {
			// Old condition to serialize only flavoured datatypes
			// if (d.getLabel().contains("_")) {
			// ds.appendChild(this.serializeDatatype(d,
			// profile.getTableLibrary(),
			// profile.getDatatypeLibrary()));
			// }
			ds.appendChild(this.serializeDatatype(dl, profile.getTableLibrary(), profile.getDatatypeLibrary(),
					prefix + "." + String.valueOf(dtList.indexOf(dl) + 1), dtList.indexOf(dl)));
		}
		xsect.appendChild(ds);

		xsect.appendChild(e);
		return xsect;
	}

	@Override
	public nu.xom.Document serializeProfileToDoc(Profile profile) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", "ID_" + profile.getId() + ""));
		ProfileMetaData metaData = profile.getMetaData();
		if (metaData.getType() != null && !metaData.getType().equals(""))
			e.addAttribute(new Attribute("Type", metaData.getType()));
		if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
			e.addAttribute(new Attribute("HL7Version", metaData.getHl7Version()));
		if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
			e.addAttribute(new Attribute("SchemaVersion", metaData.getSchemaVersion()));

		if (profile.getMetaData() != null) {
			nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
			ProfileMetaData metaDataObj = profile.getMetaData();
			elmMetaData.addAttribute(new Attribute("Name", metaDataObj.getName() + ""));
			elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj.getOrgName()));
			if (metaDataObj.getStatus() != null)
				elmMetaData.addAttribute(new Attribute("Status", metaDataObj.getStatus()));
			if (metaDataObj.getTopics() != null)
				elmMetaData.addAttribute(new Attribute("Topics", metaDataObj.getTopics()));
			if (metaDataObj.getSubTitle() != null)
				elmMetaData.addAttribute(new Attribute("Subtitle", metaDataObj.getSubTitle()));
			if (metaDataObj.getVersion() != null)
				elmMetaData.addAttribute(new Attribute("Version", metaDataObj.getVersion()));
			if (profile.getDateUpdated() != null)
				elmMetaData.addAttribute(new Attribute("Date", DateUtils.format(profile.getDateUpdated())));
			if (metaDataObj.getExt() != null)
				elmMetaData.addAttribute(new Attribute("Ext", metaDataObj.getExt()));
			if (profile.getComment() != null && !profile.getComment().equals("")) {
				elmMetaData.addAttribute(new Attribute("Comment", profile.getComment()));
			}

			e.appendChild(elmMetaData);

			if (profile.getMetaData().getEncodings() != null && profile.getMetaData().getEncodings().size() > 0) {
				nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
				for (String encoding : profile.getMetaData().getEncodings()) {
					nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
					elmEncoding.appendChild(encoding);
					elmEncodings.appendChild(elmEncoding);
				}
				e.appendChild(elmEncodings);
			}

		}

		if (profile.getUsageNote() != null) {
			nu.xom.Element ts = new nu.xom.Element("Text");
			if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
				nu.xom.Element elmUsageNote = new nu.xom.Element("UsageNote");
				elmUsageNote.appendChild(profile.getUsageNote());
				ts.appendChild(elmUsageNote);
			}
			e.appendChild(ts);
		}

		nu.xom.Element msd = new nu.xom.Element("MessagesDisplay");
		List<Message> msgList = new ArrayList<Message>(profile.getMessages().getChildren());
		// Collections.sort(msgList);

		for (Message m : msgList) {
			msd.appendChild(this.serializeMessageDisplay(m, profile.getSegmentLibrary(), ""));
		}
		e.appendChild(msd);

		nu.xom.Element ss = new nu.xom.Element("Segments");
		List<SegmentLink> sgtList = new ArrayList<>(profile.getSegmentLibrary().getChildren());
		// TODO Check Sort
		// Collections.sort(sgtList);
		for (SegmentLink sl : sgtList) {
			this.serializeSegment(ss, sl, profile.getTableLibrary(), profile.getDatatypeLibrary(), "",
					sgtList.indexOf(sl));
		}
		e.appendChild(ss);

		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		List<DatatypeLink> dtList = new ArrayList<>(profile.getDatatypeLibrary().getChildren());
		Collections.sort(dtList);
		for (DatatypeLink dl : dtList) {
			// Old condition to serialize only flavoured datatypes
			// if (d.getLabel().contains("_")) {
			// ds.appendChild(this.serializeDatatype(d,
			// profile.getTableLibrary(),
			// profile.getDatatypeLibrary()));
			// }
			ds.appendChild(this.serializeDatatype(dl, profile.getTableLibrary(), profile.getDatatypeLibrary(), "",
					dtList.indexOf(dl)));

		}
		e.appendChild(ds);

		nu.xom.Element ts = new nu.xom.Element("ValueSets");
		List<TableLink> tables = new ArrayList<TableLink>(profile.getTableLibrary().getChildren());
		Collections.sort(tables);
		for (TableLink tl : tables) {
			ts.appendChild(this.serializeTable(tl, "", tables.indexOf(ts)));
		}
		e.appendChild(ts);

		nu.xom.Document doc = new nu.xom.Document(e);

		return doc;
	}

	private nu.xom.Element serializeTable(TableLink tl, String prefix, Integer position) {
		if (tl.getId() != null) {
			Table t = tableService.findById(tl.getId());
			if (t != null) {
				nu.xom.Element sect = new nu.xom.Element("Section");
				sect.addAttribute(new Attribute("id", "ID_" + t.getId()));
				sect.addAttribute(new Attribute("prefix", prefix));
				sect.addAttribute(new Attribute("position", String.valueOf(position)));
				sect.addAttribute(new Attribute("h", String.valueOf(3)));
				sect.addAttribute(new Attribute("title", t.getBindingIdentifier() + " - " + t.getDescription()));

				nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
				elmTableDefinition.addAttribute(
						new Attribute("Id", (t.getBindingIdentifier() == null) ? "" : t.getBindingIdentifier()));
				elmTableDefinition.addAttribute(new Attribute("BindingIdentifier",
						(tl.getBindingIdentifier() == null) ? "" : tl.getBindingIdentifier()));
				elmTableDefinition.addAttribute(new Attribute("Name", (t.getName() == null) ? "" : t.getName()));
				elmTableDefinition.addAttribute(
						new Attribute("Description", (t.getDescription() == null) ? "" : t.getDescription()));
				elmTableDefinition
						.addAttribute(new Attribute("Version", (t.getVersion() == null) ? "" : "" + t.getVersion()));
				elmTableDefinition.addAttribute(new Attribute("Oid", (t.getOid() == null) ? "" : t.getOid()));
				elmTableDefinition.addAttribute(
						new Attribute("Stability", (t.getStability() == null) ? "" : t.getStability().name()));
				elmTableDefinition.addAttribute(new Attribute("Extensibility",
						(t.getExtensibility() == null) ? "" : t.getExtensibility().name()));
				elmTableDefinition.addAttribute(new Attribute("ContentDefinition",
						(t.getContentDefinition() == null) ? "" : t.getContentDefinition().name()));
				elmTableDefinition.addAttribute(new Attribute("id", "ID_" + t.getId()));
				elmTableDefinition.addAttribute(new Attribute("position", ""));
				elmTableDefinition.addAttribute(new Attribute("prefix", prefix));

				if (t.getCodes() != null) {
					for (Code c : t.getCodes()) {
						nu.xom.Element elmTableElement = new nu.xom.Element("ValueElement");
						elmTableElement
								.addAttribute(new Attribute("Value", (c.getValue() == null) ? "" : c.getValue()));
						elmTableElement
								.addAttribute(new Attribute("Label", (c.getLabel() == null) ? "" : c.getLabel()));
						elmTableElement.addAttribute(
								new Attribute("CodeSystem", (c.getCodeSystem() == null) ? "" : c.getCodeSystem()));
						elmTableElement.addAttribute(
								new Attribute("Usage", (c.getCodeUsage() == null) ? "" : c.getCodeUsage()));
						elmTableElement.addAttribute(
								new Attribute("Comments", (c.getComments() == null) ? "" : c.getComments()));
						elmTableDefinition.appendChild(elmTableElement);
					}
				}
					if ((t != null && !t.getDefPreText().isEmpty()) || (t != null && !t.getDefPostText().isEmpty())) {
							if (t.getDefPreText() != null && !t.getDefPreText().isEmpty()) {
									elmTableDefinition.appendChild(this.serializeRichtext("DefPreText", t.getDefPreText()));
							}
							if (t.getDefPostText() != null && !t.getDefPostText().isEmpty()) {
									elmTableDefinition.appendChild(this.serializeRichtext("DefPostText", t.getDefPostText()));
							}
					}
				sect.appendChild(elmTableDefinition);
				return sect;
			} else {
				logger.error("ValueSet serialization: No table found with id " + tl.getId());

				nu.xom.Element sect = new nu.xom.Element("Section");
				sect.addAttribute(new Attribute("id", "ID_" + tl.getId()));
				sect.addAttribute(new Attribute("prefix", prefix));
				sect.addAttribute(new Attribute("position", String.valueOf(position)));
				sect.addAttribute(new Attribute("h", String.valueOf(3)));
				sect.addAttribute(new Attribute("title", tl.getBindingIdentifier()));

				nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
				elmTableDefinition.addAttribute(new Attribute("Id", (tl.getId() == null) ? "! DEBUG: COULD NOT FIND id"
						: "! DEBUG: COULD NOT FIND id " + tl.getId()));
				elmTableDefinition.addAttribute(new Attribute("BindingIdentifier",
						(tl.getBindingIdentifier() == null) ? "" : tl.getBindingIdentifier()));
				sect.appendChild(elmTableDefinition);
				return sect;
			}
		} else {
			logger.error("ValueSet serialization: Null id");
			nu.xom.Element sect = new nu.xom.Element("Section");
			sect.addAttribute(new Attribute("id", "NULL"));
			sect.addAttribute(new Attribute("prefix", prefix));
			sect.addAttribute(new Attribute("position", String.valueOf(position)));
			sect.addAttribute(new Attribute("h", String.valueOf(3)));
			sect.addAttribute(new Attribute("title", "! DEBUG: COULD NOT FIND null id"));
			nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
			elmTableDefinition.addAttribute(new Attribute("Id", "null"));
			elmTableDefinition.addAttribute(new Attribute("BindingIdentifier", "null"));
			sect.appendChild(elmTableDefinition);
			return sect;
		}
	}

	private nu.xom.Element serializeOneTable(TableLink tl) {
		if (tl.getId() != null) {
			Table t = tableService.findById(tl.getId());
			nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
			if (t != null) {

				elmTableDefinition.addAttribute(
						new Attribute("Id", (t.getBindingIdentifier() == null) ? "" : t.getBindingIdentifier()));
				elmTableDefinition.addAttribute(new Attribute("BindingIdentifier",
						(tl.getBindingIdentifier() == null) ? "" : tl.getBindingIdentifier()));
				elmTableDefinition.addAttribute(new Attribute("Name", (t.getName() == null) ? "" : t.getName()));
				elmTableDefinition.addAttribute(
						new Attribute("Description", (t.getDescription() == null) ? "" : t.getDescription()));
				elmTableDefinition
						.addAttribute(new Attribute("Version", (t.getVersion() == null) ? "" : "" + t.getVersion()));
				elmTableDefinition.addAttribute(new Attribute("Oid", (t.getOid() == null) ? "" : t.getOid()));
				elmTableDefinition.addAttribute(
						new Attribute("Stability", (t.getStability() == null) ? "" : t.getStability().name()));
				elmTableDefinition.addAttribute(new Attribute("Extensibility",
						(t.getExtensibility() == null) ? "" : t.getExtensibility().name()));
				elmTableDefinition.addAttribute(new Attribute("ContentDefinition",
						(t.getContentDefinition() == null) ? "" : t.getContentDefinition().name()));
				elmTableDefinition.addAttribute(new Attribute("id", "ID_" + t.getId()));

				if (t.getCodes() != null) {
					for (Code c : t.getCodes()) {
						nu.xom.Element elmTableElement = new nu.xom.Element("ValueElement");
						elmTableElement
								.addAttribute(new Attribute("Value", (c.getValue() == null) ? "" : c.getValue()));
						elmTableElement
								.addAttribute(new Attribute("Label", (c.getLabel() == null) ? "" : c.getLabel()));
						elmTableElement.addAttribute(
								new Attribute("CodeSystem", (c.getCodeSystem() == null) ? "" : c.getCodeSystem()));
						elmTableElement.addAttribute(
								new Attribute("Usage", (c.getCodeUsage() == null) ? "" : c.getCodeUsage()));
						elmTableElement.addAttribute(
								new Attribute("Comments", (c.getComments() == null) ? "" : c.getComments()));
						elmTableDefinition.appendChild(elmTableElement);
					}
				}

				if ((t != null && !t.getDefPreText().isEmpty()) || (t != null && !t.getDefPostText().isEmpty())) {
					if (t.getDefPreText() != null && !t.getDefPreText().isEmpty()) {
						elmTableDefinition.appendChild(this.serializeRichtext("DefPreText", t.getDefPreText()));
					}
					if (t.getDefPostText() != null && !t.getDefPostText().isEmpty()) {
						elmTableDefinition.appendChild(this.serializeRichtext("DefPostText", t.getDefPostText()));
					}
				}

			} else {
				logger.error("ValueSet serialization: No table found with id " + tl.getId());
				elmTableDefinition.addAttribute(new Attribute("Id", "ID_" + tl.getId()));
				elmTableDefinition.addAttribute(new Attribute("BindingIdentifier",
						(tl.getBindingIdentifier() == null) ? "" : tl.getBindingIdentifier()));
				elmTableDefinition.addAttribute(
						new Attribute("Description", "! DEBUG: COULD NOT FIND table with id" + tl.getId()));
				return elmTableDefinition;
			}
			return elmTableDefinition;
		} else {
			logger.error("ValueSet serialization: No table found with null id");
			nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
			elmTableDefinition.addAttribute(new Attribute("Id", "null"));
			elmTableDefinition.addAttribute(new Attribute("BindingIdentifier", "! DEBUG: COULD NOT FIND null id"));
			return elmTableDefinition;
		}
	}

	private nu.xom.Element serializeMessageDisplay(Message m, SegmentLibrary segments, String prefix) {
		nu.xom.Element sect = new nu.xom.Element("Section");
		sect.addAttribute(new Attribute("id", "ID_" + m.getId()));
		sect.addAttribute(new Attribute("prefix", prefix + "." + String.valueOf(m.getPosition())));
		sect.addAttribute(new Attribute("position", String.valueOf(m.getPosition() + 1)));
		sect.addAttribute(new Attribute("h", String.valueOf(3)));
		String title = m.getName() != null ? m.getName()
				: m.getMessageType() + "^" + m.getEvent() + "^" + m.getStructID();
		sect.addAttribute(new Attribute("title", title + " - " + m.getIdentifier() + " - " + m.getDescription()));

		nu.xom.Element elmMessage = new nu.xom.Element("MessageDisplay");
		elmMessage.addAttribute(new Attribute("ID", "ID_" + m.getId()));
		elmMessage.addAttribute(new Attribute("Name", m.getName() + ""));
		elmMessage.addAttribute(new Attribute("Type", m.getMessageType()));
		elmMessage.addAttribute(new Attribute("Event", m.getEvent()));
		elmMessage.addAttribute(new Attribute("StructID", m.getStructID()));
		elmMessage.addAttribute(new Attribute("position", m.getPosition() + ""));

		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", m.getDescription()));
		if (m.getComment() != null && !m.getComment().isEmpty()) {
			elmMessage.addAttribute(new Attribute("Comment", m.getComment()));
		}
		// elmMessage.addAttribute(new Attribute("Position",
		// m.getPosition().toString()));
		// N elmMessage.addAttribute(new Attribute("Position",
		// String.valueOf(m.getSectionPosition()+1)));
		if (m.getUsageNote() != null && !m.getUsageNote().isEmpty()) {
			elmMessage.appendChild(this.serializeRichtext("UsageNote", m.getUsageNote()));
		}

		if ((m != null && !m.getDefPreText().isEmpty()) || (m != null && !m.getDefPostText().isEmpty())) {
				if (m.getDefPreText() != null && !m.getDefPreText().isEmpty()) {
					elmMessage.appendChild(this.serializeRichtext("DefPreText", m.getDefPreText()));
				}
				if (m.getDefPostText() != null && !m.getDefPostText().isEmpty()) {
					elmMessage.appendChild(this.serializeRichtext("DefPostText", m.getDefPostText()));
				}
		}

		List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
		for (SegmentRefOrGroup srog : segRefOrGroups) {
			if (srog instanceof SegmentRef) {
				this.serializeSegmentRefDisplay(elmMessage, (SegmentRef) srog, segments, 0);
			} else if (srog instanceof Group) {
				this.serializeGroupDisplay(elmMessage, (Group) srog, segments, 0);
			}
		}

		// Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new
		// HashMap<Integer,
		// SegmentRefOrGroup>();
		// for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
		// segmentRefOrGroups.put(segmentRefOrGroup.getPosition(),
		// segmentRefOrGroup);
		// }
		//
		// for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
		// SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
		// if (segmentRefOrGroup instanceof SegmentRef) {
		// this.serializeSegmentRefDisplay(elmMessage, (SegmentRef)
		// segmentRefOrGroup, segments, 0);
		// } else if (segmentRefOrGroup instanceof Group) {
		// this.serializeGroupDisplay(elmMessage, (Group) segmentRefOrGroup,
		// segments, 0);
		// }
		// }

		List<ConformanceStatement> conformances = m.getConformanceStatements();
		if (conformances != null && !conformances.isEmpty()) {
			for (Constraint constraint : conformances) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				elmMessage.appendChild(elmConstraint);
			}
		}
		List<Predicate> predicates = m.getPredicates();
		if (predicates != null && !predicates.isEmpty()) {
			for (Constraint constraint : predicates) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				elmMessage.appendChild(elmConstraint);
			}
		}
		
		sect.appendChild(elmMessage);
		return sect;
	}

	private void serializeGroupDisplay(nu.xom.Element elmDisplay, Group group, SegmentLibrary segments, Integer depth) {
		nu.xom.Element elmGroup = new nu.xom.Element("Elt");
		elmGroup.addAttribute(new Attribute("IdGpe", "ID_" + group.getId()));
		elmGroup.addAttribute(new Attribute("Name", group.getName()));
		elmGroup.addAttribute(new Attribute("Description", "BEGIN " + group.getName() + " GROUP"));
		elmGroup.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup.addAttribute(new Attribute("Max", group.getMax()));
		elmGroup.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "["));
		elmGroup.addAttribute(new Attribute("Comment", group.getComment()));
		elmGroup.addAttribute(new Attribute("Position", group.getPosition().toString()));
		elmDisplay.appendChild(elmGroup);

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			if (segmentRefOrGroup instanceof SegmentRef) {
				this.serializeSegmentRefDisplay(elmDisplay, (SegmentRef) segmentRefOrGroup, segments, depth + 1);
			} else if (segmentRefOrGroup instanceof Group) {
				this.serializeGroupDisplay(elmDisplay, (Group) segmentRefOrGroup, segments, depth + 1);
			}
		}
		nu.xom.Element elmGroup2 = new nu.xom.Element("Elt");
		elmGroup2.addAttribute(new Attribute("IdGpe", "ID_" + group.getId()));
		elmGroup2.addAttribute(new Attribute("Name", "END " + group.getName() + " GROUP"));
		elmGroup2.addAttribute(new Attribute("Description", "END " + group.getName() + " GROUP"));
		elmGroup2.addAttribute(new Attribute("Usage", ""));
		elmGroup2.addAttribute(new Attribute("Min", ""));
		elmGroup2.addAttribute(new Attribute("Max", ""));
		elmGroup2.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "]"));
		elmGroup2.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmGroup2.addAttribute(new Attribute("Position", group.getPosition().toString()));

			List<ConformanceStatement> conformanceStatements = group.getConformanceStatements();
			if (conformanceStatements != null && !conformanceStatements.isEmpty()) {
					for (Constraint constraint : conformanceStatements) {
							nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, group.getName() + ".");
							elmGroup2.appendChild(elmConstraint);
					}
			}
			List<Predicate> predicates = group.getPredicates();
			if (predicates != null && !predicates.isEmpty()) {
					for (Constraint constraint : predicates) {
							nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, group.getName() + ".");
							elmGroup2.appendChild(elmConstraint);
					}
			}

			elmDisplay.appendChild(elmGroup2);




	}

	private void serializeSegmentRefDisplay(nu.xom.Element elmDisplay, SegmentRef segmentRef, SegmentLibrary segments,
			Integer depth) {
		nu.xom.Element elmSegment = new nu.xom.Element("Elt");
		elmSegment.addAttribute(new Attribute("IDRef", "ID_" + segmentRef.getId()));
		elmSegment.addAttribute(new Attribute("IDSeg", "ID_" + segmentRef.getRef().getId()));

		// TODO Check segments vs sgtService if (segmentRef.getRef() != null &&
		// segments.findOneSegmentById(segmentRef.getRef()) != null &&
		// segmentService.findById(segmentRef.getRef()).getName() != null) {
		if (segmentRef.getRef() != null && segments.findOneSegmentById(segmentRef.getRef().getId()) != null
				&& segments.findOneSegmentById(segmentRef.getRef().getId()).getName() != null) {
			elmSegment.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth)
					+ segments.findOneSegmentById(segmentRef.getRef().getId()).getName()));
			String label = (segmentRef.getRef().getExt() == null || segmentRef.getRef().getExt().isEmpty())
					? segmentRef.getRef().getName() : segmentRef.getRef().getLabel();
			elmSegment.addAttribute(new Attribute("Label", label));
			elmSegment.addAttribute(new Attribute("Description",
					segmentService.findById(segmentRef.getRef().getId()).getDescription()));

		}
		elmSegment.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().value()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax() + ""));
		if (segmentRef.getComment() != null)
			elmSegment.addAttribute(new Attribute("Comment", segmentRef.getComment()));
		elmSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
		elmDisplay.appendChild(elmSegment);
	}

	private nu.xom.Element serializeOneMessage(Message m) {
		// nu.xom.Element sect = new nu.xom.Element("Section");
		// sect.addAttribute(new Attribute("id", m.getId()));
		// sect.addAttribute(new Attribute("position",
		// String.valueOf(m.getPosition() + 1)));
		// sect.addAttribute(new Attribute("h", String.valueOf(3)));
		// String title = m.getName() != null ? m.getName() :
		// m.getMessageType()+ "^"+m.getEvent()+"^" +
		// m.getStructID();
		// sect.addAttribute(new Attribute("title", title + " - " +
		// m.getIdentifier() + " - " +
		// m.getDescription()));

		nu.xom.Element elmMessage = new nu.xom.Element("MessageDisplay");
		elmMessage.addAttribute(new Attribute("ID", "ID_" + m.getId()));

		elmMessage.addAttribute(new Attribute("position", String.valueOf(m.getPosition())));
		elmMessage.addAttribute(new Attribute("Name", m.getName() + ""));
		elmMessage.addAttribute(new Attribute("Type", m.getMessageType()));
		elmMessage.addAttribute(new Attribute("Event", m.getEvent()));
		elmMessage.addAttribute(new Attribute("StructID", m.getStructID()));
		String title = m.getName() != null ? m.getName()
				: m.getMessageType() + "^" + m.getEvent() + "^" + m.getStructID();
		elmMessage.addAttribute(new Attribute("Label", title + " - " + m.getIdentifier() + ""));

		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", m.getDescription()));
		if (m.getComment() != null && !m.getComment().isEmpty()) {
			elmMessage.addAttribute(new Attribute("Comment", m.getComment()));
		}
		if (m.getUsageNote() != null && !m.getUsageNote().isEmpty()) {
			elmMessage.appendChild(this.serializeRichtext("UsageNote", m.getUsageNote()));
		}

		List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
		for (SegmentRefOrGroup srog : segRefOrGroups) {
			if (srog instanceof SegmentRef) {
				this.serializeOneSegmentRef(elmMessage, (SegmentRef) srog, 0, m);
			} else if (srog instanceof Group) {
				this.serializeOneGroup(elmMessage, (Group) srog, 0, m);
			}
		}
		// List<Constraint> constraints =
		// findConstraints(i, d.getPredicates(), d.getConformanceStatements());
		// if (!constraints.isEmpty()) {
		// for (Constraint constraint : constraints) {
		// nu.xom.Element elmConstraint =
		// serializeConstraintToElement(constraint, d.getName() + ".");
		// elmComponent.appendChild(elmConstraint);
		// }
		// }
		List<ConformanceStatement> confromances = m.getConformanceStatements();
		if (confromances != null && !confromances.isEmpty()) {
			for (Constraint constraint : confromances) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				elmMessage.appendChild(elmConstraint);
			}
		}
		List<Predicate> predicates = m.getPredicates();
		if (predicates != null && !predicates.isEmpty()) {
			for (Constraint constraint : predicates) {
				nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, m.getName() + ".");
				elmMessage.appendChild(elmConstraint);
			}
		}

		return elmMessage;

		// sect.appendChild(elmMessage);
		// return sect;
	}

	private void serializeOneGroup(nu.xom.Element elmDisplay, Group group, Integer depth, Message m) {
		nu.xom.Element elmGroup = new nu.xom.Element("Elt");
		elmGroup.addAttribute(new Attribute("IdGpe", "ID_" + group.getId()));
		elmGroup.addAttribute(new Attribute("Name", group.getName()));
		elmGroup.addAttribute(new Attribute("Description", "BEGIN " + group.getName() + " GROUP"));
		elmGroup.addAttribute(new Attribute("Usage", depth.toString()));
		elmGroup.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup.addAttribute(new Attribute("Max", group.getMax()));
		elmGroup.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "["));
		elmGroup.addAttribute(new Attribute("Comment", group.getComment()));
		elmGroup.addAttribute(new Attribute("Position", group.getPosition().toString()));
		elmDisplay.appendChild(elmGroup);

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			if (segmentRefOrGroup instanceof SegmentRef) {
				this.serializeOneSegmentRef(elmDisplay, (SegmentRef) segmentRefOrGroup, depth + 1, m);
			} else if (segmentRefOrGroup instanceof Group) {
				this.serializeOneGroup(elmDisplay, (Group) segmentRefOrGroup, depth + 1, m);
			}
		}
		nu.xom.Element elmGroup2 = new nu.xom.Element("Elt");
		elmGroup2.addAttribute(new Attribute("IdGpe", "ID_" + group.getId()));
		elmGroup2.addAttribute(new Attribute("Name", "END " + group.getName() + " GROUP"));
		elmGroup2.addAttribute(new Attribute("Description", "END " + group.getName() + " GROUP"));
		elmGroup2.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup2.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup2.addAttribute(new Attribute("Max", group.getMax()));
		elmGroup2.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth) + "]"));
		elmGroup2.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmGroup2.addAttribute(new Attribute("Position", group.getPosition().toString()));
		elmDisplay.appendChild(elmGroup2);

	}

	private void serializeOneSegmentRef(nu.xom.Element elmDisplay, SegmentRef segmentRef, Integer depth, Message m) {

		nu.xom.Element elmSegment = new nu.xom.Element("Elt");
		elmSegment.addAttribute(new Attribute("IDRef", "ID_" + segmentRef.getId()));
		elmSegment.addAttribute(new Attribute("IDSeg", "ID_" + segmentRef.getRef().getId()));

		if (segmentRef.getRef() != null && segmentService.findById(segmentRef.getRef().getId()) != null
				&& segmentService.findById(segmentRef.getRef().getId()).getName() != null) {
			elmSegment.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4 * depth)
					+ segmentService.findById(segmentRef.getRef().getId()).getName()));
			String label = (segmentRef.getRef().getExt() == null || segmentRef.getRef().getExt().isEmpty())
					? segmentRef.getRef().getName() : segmentRef.getRef().getLabel();
			elmSegment.addAttribute(new Attribute("Label", label));
			elmSegment.addAttribute(new Attribute("Description",
					segmentService.findById(segmentRef.getRef().getId()).getDescription()));

		}
		elmSegment.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().toString()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax() + ""));
		if (segmentRef.getComment() != null)
			elmSegment.addAttribute(new Attribute("Comment", segmentRef.getComment()));
		elmSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
		elmDisplay.appendChild(elmSegment);
	}

	private nu.xom.Element serializeRichtext(String attribute, String richtext) {
		nu.xom.Element elmText1 = new nu.xom.Element("Text");
		elmText1.addAttribute(new Attribute("Type", attribute));
		elmText1.appendChild(cleanRichtext(richtext));
		return elmText1;
	}

	private String cleanRichtext(String richtext) {
		richtext = richtext.replace("<br>", "<br></br>");
		richtext = richtext.replace("<p style=\"\"><br></p>", "<p></p>");
		org.jsoup.nodes.Document doc = Jsoup.parse(richtext);
		Elements elements1 = doc.select("h1");
		elements1.tagName("p").attr("style",
				"display: block;font-size: 18.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
		// elements1.after("<hr />");
		Elements elements2 = doc.select("h2");
		elements2.tagName("p").attr("style",
				"display: block;font-size: 16.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
		Elements elements3 = doc.select("h3");
		elements3.tagName("p").attr("style",
				"display: block;font-size: 14.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
		Elements elements4 = doc.select("h4");
		elements4.tagName("p").attr("style",
				"display: block;font-size: 12.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
		Elements elementsPre = doc.select("pre");
		elementsPre.tagName("span").attr("style",
				"display: block; font-family: monospace; background-color: rgb(209, 213, 216);");
				// froala css => "white-space: pre-wrap; word-wrap: break-word;"
		
		for (org.jsoup.nodes.Element elementImg : doc.select("img")) {
			try {
				if (elementImg.attr("src") != null && !"".equals(elementImg.attr("src"))) {
					InputStream imgis = null;
					String ext = null;
					byte[] bytes = null;
					if (elementImg.attr("src").indexOf("name=") != -1) {
						String filename = elementImg.attr("src").substring(elementImg.attr("src").indexOf("name=") + 5);
						ext = FilenameUtils.getExtension(filename);
						GridFSDBFile dbFile = fileStorageService.findOneByFilename(filename);
						if (dbFile != null) {
							imgis = dbFile.getInputStream();
							bytes = IOUtils.toByteArray(imgis);
						}
					} else {
						String filename = elementImg.attr("src");
						ext = FilenameUtils.getExtension(filename);
						URL url = new URL(filename);
						bytes = IOUtils.toByteArray(url);
					}
					if (bytes != null && bytes.length > 0) {
						String imgEnc = Base64.encodeBase64String(bytes);
						String texEncImg = "data:image/" + ext + ";base64," + imgEnc;
						elementImg.attr("src", texEncImg);
					}
				}
				if (elementImg.attr("alt") == null || elementImg.attr("alt").isEmpty()){
					elementImg.attr("alt", ".");
				}
				String imgStyle = elementImg.attr("style");
				elementImg.attr("style", imgStyle.replace("px;", ";"));
//				style="width: 300px;

			} catch (RuntimeException e) {
				e.printStackTrace(); // If error, we leave the original document
										// as is.
			} catch (Exception e) {
				e.printStackTrace(); // If error, we leave the original document
				// as is.
			}
		}

		for (org.jsoup.nodes.Element elementTbl : doc.select("table")) {
			if (elementTbl.attr("summary") == null || elementTbl.attr("summary").isEmpty()) {
				elementTbl.attr("summary", ".");
			}
		}
				
		//Renaming strong to work as html4 
		doc.select("strong").tagName("b");

		// Tidy tidy = new Tidy();
		// tidy.setWraplen(Integer.MAX_VALUE);
		// tidy.setXHTML(true);
		// tidy.setShowWarnings(false); // to hide errors
		// tidy.setQuiet(true); // to hide warning
		// tidy.setMakeClean(true);
		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// tidy.parseDOM(IOUtils.toInputStream("<div class=\"fr-view\">" +
		// doc.html() + "</div>"), outputStream);
		// return outputStream.toString();
		return "<div class=\"fr-view\">" + doc.body().html() + "</div>";
	}

	private void serializeSegment(nu.xom.Element parentElement, SegmentLink sl, TableLibrary tables,
			DatatypeLibrary datatypes, String prefix, Integer position) {
		nu.xom.Element sect = new nu.xom.Element("Section");
		// if (sl.getId() != null && segmentService != null &&
		// segmentService.findById(sl.getId()) !=
		// null){
		if (sl.getId() != null) {
			Segment s = segmentService.findById(sl.getId());

			sect.addAttribute(new Attribute("id", "ID_" + s.getId()));
			sect.addAttribute(new Attribute("prefix", prefix));
			sect.addAttribute(new Attribute("position", String.valueOf(position)));
			sect.addAttribute(new Attribute("h", String.valueOf(3)));
			sect.addAttribute(new Attribute("title", sl.getLabel() + " - " + s.getDescription()));

			nu.xom.Element elmSegment = serializeOneSegment(sl);

			elmSegment.addAttribute(new Attribute("prefix", prefix));
			elmSegment.addAttribute(new Attribute("position", ""));

			sect.appendChild(elmSegment);
			parentElement.appendChild(sect);
		}
	}

	private nu.xom.Element serializeOneSegment(SegmentLink sl) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");

		if (sl.getId() != null && segmentService.findById(sl.getId()) != null) {
			Segment s = segmentService.findById(sl.getId());
			if (s != null) {
				elmSegment.addAttribute(new Attribute("ID", "ID_" + s.getId()));
				elmSegment.addAttribute(new Attribute("id", "ID_" + s.getId()));
				elmSegment.addAttribute(new Attribute("Name", sl.getName()));
				elmSegment.addAttribute(new Attribute("Label",
						sl.getExt() == null || sl.getExt().isEmpty() ? sl.getName() : sl.getLabel()));
				elmSegment.addAttribute(new Attribute("Position", ""));
				elmSegment.addAttribute(new Attribute("Description", s.getDescription()));
				if (s.getComment() != null && !s.getComment().isEmpty()) {
					elmSegment.addAttribute(new Attribute("Comment", s.getComment()));
				}

				if ((s.getText1() != null && !s.getText1().isEmpty())
						|| (s.getText2() != null && !s.getText2().isEmpty())) {
					if (s.getText1() != null && !s.getText1().isEmpty()) {
						elmSegment.appendChild(this.serializeRichtext("DefPreText", s.getText1()));
					}
					if (s.getText2() != null && !s.getText2().isEmpty()) {
						elmSegment.appendChild(this.serializeRichtext("DefPostText", s.getText2()));
					}
				}

				for (int i = 0; i < s.getFields().size(); i++) {
					Field f = s.getFields().get(i);
					nu.xom.Element elmField = new nu.xom.Element("Field");
					elmField.addAttribute(new Attribute("Name", f.getName()));
					elmField.addAttribute(new Attribute("Usage", getFullUsage(s, i).toString()));
					if (f.getDatatype() != null && datatypeService.findById(f.getDatatype().getId()) != null) {
						Datatype data = datatypeService.findById(f.getDatatype().getId());
						elmField.addAttribute(new Attribute("Datatype", data.getLabel()));
					} else {
						elmField.addAttribute(new Attribute("Datatype",
								f.getDatatype() != null
										? "! DEBUG: COULD NOT FIND datatype " + f.getDatatype().getLabel()
										: "! DEBUG: COULD NOT FIND datatype with null id"));
					}
					// Following line means that there are no conformance length
					// for a complex datatype
					if (f.getConfLength() != null && !f.getConfLength().equals("")) {
						if (f.getDatatype() != null) {
							Datatype d = datatypeService.findById(f.getDatatype().getId());
							if (d != null) {
								if (d.getComponents().size() > 0) {
									elmField.addAttribute(new Attribute("ConfLength", ""));
									elmField.addAttribute(new Attribute("MinLength", ""));
									if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
										elmField.addAttribute(new Attribute("MaxLength", ""));
								} else {
									elmField.addAttribute(new Attribute("ConfLength", f.getConfLength()));
									elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
									if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
										elmField.addAttribute(new Attribute("MaxLength", f.getMaxLength()));
								}
							}
						}
					}

					elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
					elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
					if (f.getTables() != null && !f.getTables().isEmpty()) {
						String temp = "";
						if (f.getTables().size() > 1) {
							for (TableLink t : f.getTables()) {
								String bdInd = tableService.findById(t.getId()) == null ? null
										: tableService.findById(t.getId()).getBindingIdentifier();
								temp += (bdInd != null && !bdInd.equals("")) ? "," + bdInd
										: ", ! DEBUG: COULD NOT FIND binding identifier " + t.getBindingIdentifier();
							}
						} else {
							String bdInd = tableService.findById(f.getTables().get(0).getId()) == null ? null
									: tableService.findById(f.getTables().get(0).getId()).getBindingIdentifier();
							temp = (bdInd != null && !bdInd.equals("")) ? bdInd
									: "! DEBUG: COULD NOT FIND binding identifier "
											+ f.getTables().get(0).getBindingIdentifier();
						}
						elmField.addAttribute(new Attribute("Binding", temp));
					}
					if (f.getItemNo() != null && !f.getItemNo().equals(""))
						elmField.addAttribute(new Attribute("ItemNo", f.getItemNo()));
					if (f.getComment() != null && !f.getComment().isEmpty())
						elmField.addAttribute(new Attribute("Comment", f.getComment()));
					elmField.addAttribute(new Attribute("Position", String.valueOf(f.getPosition())));

					if (f.getText() != null && !f.getText().isEmpty()) {
						elmField.appendChild(this.serializeRichtext("Text", f.getText()));
					}

					List<Constraint> constraints = findConstraints(i, s.getPredicates(), s.getConformanceStatements());
					if (!constraints.isEmpty()) {
						for (Constraint constraint : constraints) {
							nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, s.getName() + "-");
							elmField.appendChild(elmConstraint);
						}
					}

					elmSegment.appendChild(elmField);
				}
				CoConstraints coconstraints = s.getCoConstraints();
				if (coconstraints.getConstraints().size() != 0) {
					nu.xom.Element ccts = new Element("coconstraints");
					nu.xom.Element htmlTable = new nu.xom.Element("table");
					htmlTable.addAttribute(new Attribute("cellpadding", "1"));
					htmlTable.addAttribute(new Attribute("cellspacing", "0"));
					htmlTable.addAttribute(new Attribute("border", "1"));
					htmlTable.addAttribute(new Attribute("width", "100%"));

					nu.xom.Element thead = new nu.xom.Element("thead");
					thead.addAttribute(new Attribute("style", "background:#F0F0F0; color:#B21A1C; align:center"));
					nu.xom.Element tr = new nu.xom.Element("tr");
					for (CoConstraintsColumn ccc : coconstraints.getColumnList()) {
						nu.xom.Element th = new nu.xom.Element("th");
						th.appendChild(sl.getName() + "-" + ccc.getField().getPosition());
						tr.appendChild(th);
					}

					nu.xom.Element thd = new nu.xom.Element("th");
					thd.appendChild("Description");
					tr.appendChild(thd);

					nu.xom.Element thc = new nu.xom.Element("th");
					thc.appendChild("Comments");
					tr.appendChild(thc);
					thead.appendChild(tr);
					htmlTable.appendChild(thead);

					nu.xom.Element tbody = new nu.xom.Element("tbody");
					tbody.addAttribute(new Attribute("style", "background-color:white;text-decoration:normal"));
					for (CoConstraint cct : coconstraints.getConstraints()) {

						tr = new nu.xom.Element("tr");
						for (CCValue ccv : cct.getValues()) {
							nu.xom.Element td = new nu.xom.Element("td");
							if (ccv != null) {
								if (coconstraints.getColumnList().get(cct.getValues().indexOf(ccv)).getConstraintType()
										.equals("v")) {
									td.appendChild(ccv.getValue());
								} else {
									if (ccv.getValue() != null && ccv.getValue().equals("")) {
										td.appendChild("N/A");
									} else {
										if (tableService.findById(ccv.getValue()) != null) {
											td.appendChild(
													tableService.findById(ccv.getValue()).getBindingIdentifier());
										} else {
											td.appendChild("");
										}
									}
								}
							} else {
								td.appendChild("");
							}
							tr.appendChild(td);
						}
						nu.xom.Element td = new nu.xom.Element("td");
						td.appendChild(cct.getDescription());
						tr.appendChild(td);
						td = new nu.xom.Element("td");
						td.appendChild(cct.getComments());
						tr.appendChild(td);
						tbody.appendChild(tr);
					}
					htmlTable.appendChild(tbody);
					ccts.appendChild(htmlTable);
					elmSegment.appendChild(ccts);
				}
			}
		} else {
			elmSegment.addAttribute(new Attribute("ID", "ID_" + sl.getId()));
			elmSegment.addAttribute(new Attribute("Name", sl.getName() + ""));
			elmSegment.addAttribute(new Attribute("Label",
					sl.getExt() == null || sl.getExt().isEmpty() ? sl.getName() : sl.getLabel() + ""));
			elmSegment.addAttribute(new Attribute("Description", "Error"));
			elmSegment.addAttribute(new Attribute("Comment", "! DEBUG: COULD NOT FIND id" + sl.getId()));
		}
		return elmSegment;
	}

	private List<Constraint> findConstraints(Integer target, List<Predicate> predicates,
			List<ConformanceStatement> conformanceStatements) {
		// TODO Add case for root level constraints
		List<Constraint> constraints = new ArrayList<>();
		for (Predicate pre : predicates) {
			if (pre.getConstraintTarget().indexOf('[') != -1) {
				if (target == Integer
						.parseInt(pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))) {
					constraints.add(pre);
				}
			}
		}
		for (ConformanceStatement conformanceStatement : conformanceStatements) {
			if (conformanceStatement.getConstraintTarget().indexOf('[') != -1) {
				if (target == Integer.parseInt(conformanceStatement.getConstraintTarget().substring(0,
						conformanceStatement.getConstraintTarget().indexOf('[')))) {
					constraints.add(conformanceStatement);
				}
			}
		}
		return constraints;
	}

	private List<Predicate> findPredicate(Integer target, List<Predicate> predicates) {
		// TODO Add case for root level constraints
		List<Predicate> constraints = new ArrayList<>();
		for (Predicate pre : predicates) {
			if (pre.getConstraintTarget().indexOf('[') != -1) {
				if (target == Integer
						.parseInt(pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))) {
					constraints.add(pre);
				}
			}
		}

		return constraints;
	}

	private nu.xom.Element serializeDatatype(DatatypeLink dl, TableLibrary tables, DatatypeLibrary datatypes,
			String prefix, Integer position) {
		nu.xom.Element sect = new nu.xom.Element("Section");

		if (dl.getId() != null && datatypeService.findById(dl.getId()) != null) {
			Datatype d = datatypeService.findById(dl.getId());
			sect.addAttribute(new Attribute("id", "ID_" + d.getId()));
			sect.addAttribute(new Attribute("title", d.getLabel() + " - " + d.getDescription()));
		} else if (dl.getId() != null) {
			sect.addAttribute(new Attribute("id", "NULL"));
			sect.addAttribute(new Attribute("title", "NULL"));
		} else if (datatypeService.findById(dl.getId()) != null) {
			sect.addAttribute(new Attribute("id", dl.getId()));
			sect.addAttribute(new Attribute("title", "! Unfound id " + dl.getId()));
		}
		sect.addAttribute(new Attribute("prefix", prefix));
		sect.addAttribute(new Attribute("position", String.valueOf(position)));
		sect.addAttribute(new Attribute("h", String.valueOf(3)));

		nu.xom.Element elmDatatype = serializeOneDatatype(dl);
		elmDatatype.addAttribute(new Attribute("prefix", prefix));
		elmDatatype.addAttribute(new Attribute("position", ""));

		sect.appendChild(elmDatatype);
		return sect;
	}

	private nu.xom.Element serializeOneDatatype(DatatypeLink dl) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		if (dl != null && dl.getId() != null) {
			Datatype d = datatypeService.findById(dl.getId());
			if (d != null) {
				elmDatatype.addAttribute(new Attribute("ID", "ID_" + d.getId()));
				elmDatatype.addAttribute(new Attribute("Name", d.getName()));
				elmDatatype.addAttribute(new Attribute("Label", d.getLabel()));
				elmDatatype.addAttribute(new Attribute("Description", d.getDescription()));
				elmDatatype.addAttribute(new Attribute("PurposeAndUse", d.getPurposeAndUse()));
				elmDatatype.addAttribute(new Attribute("Comment", d.getComment()));
				elmDatatype
						.addAttribute(new Attribute("Hl7Version", d.getHl7Version() == null ? "" : d.getHl7Version()));// TODO
				// Check do
				// we want
				// here?
				elmDatatype.addAttribute(new Attribute("id", "ID_" + d.getId()));
				List<ConformanceStatement> confromances = d.getConformanceStatements();

				if (confromances != null && !confromances.isEmpty()) {
					for (Constraint constraint : confromances) {
						nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, d.getName() + ".");
						elmDatatype.appendChild(elmConstraint);
					}
				}

				List<Predicate> predicates = d.getPredicates();
				if (predicates != null && !predicates.isEmpty()) {
					for (Constraint constraint : predicates) {
						nu.xom.Element elmConstraint = serializeConstraintToElement(constraint, d.getName() + ".");
						elmDatatype.appendChild(elmConstraint);
					}
				}
				if (d.getComponents() != null) {
					for (int i = 0; i < d.getComponents().size(); i++) {
						Component c = d.getComponents().get(i);
						nu.xom.Element elmComponent = new nu.xom.Element("Component");
						elmComponent.addAttribute(new Attribute("Name", c.getName()));
						elmComponent.addAttribute(new Attribute("Usage", getFullUsage(d, i)));
						if (c.getDatatype() != null && datatypeService.findById(c.getDatatype().getId()) != null) {
							elmComponent.addAttribute(new Attribute("Datatype",
									datatypeService.findById(c.getDatatype().getId()).getLabel()));
						} else {
							elmComponent.addAttribute(new Attribute("Datatype",
									c.getDatatype() != null
											? "! DEBUG: COULD NOT FIND datatype " + c.getDatatype().getLabel()
											: "! DEBUG: COULD NOT FIND datatype with null id"));
						}
						if (c.getDatatype() != null && datatypeService.findById(c.getDatatype().getId()) != null) {
							Datatype sub = datatypeService.findById(c.getDatatype().getId());
							if (sub != null) {
								if (sub.getComponents().size() == 0) {
									elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
									if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
										elmComponent.addAttribute(new Attribute("MaxLength", c.getMaxLength()));
									if (c.getConfLength() != null && !c.getConfLength().equals(""))
										elmComponent.addAttribute(new Attribute("ConfLength", c.getConfLength()));
								} else {
									elmComponent.addAttribute(new Attribute("MinLength", ""));
									elmComponent.addAttribute(new Attribute("MaxLength", ""));
									elmComponent.addAttribute(new Attribute("ConfLength", ""));
								}
							}
						}
						if (c.getComment() != null && !c.getComment().equals(""))
							elmComponent.addAttribute(new Attribute("Comment", c.getComment()));
						elmComponent.addAttribute(new Attribute("Position", c.getPosition().toString()));
						if (c.getText() != null & !c.getText().isEmpty()) {
							elmComponent.appendChild(this.serializeRichtext("Text", c.getText()));
						}

						if (c.getTables() != null && (c.getTables().size() > 0)) {
							String temp = "";
							for (TableLink t : c.getTables()) {
								if (t.getId() != null && tableService.findById(t.getId()) != null) {
									String bdInd = tableService.findById(t.getId()).getBindingIdentifier();
									temp = !temp.equals("") ? temp + "," + bdInd : bdInd;
								}
							}
							elmComponent.addAttribute(new Attribute("Binding", temp));
						}

						elmDatatype.appendChild(elmComponent);
					}
					if (d.getComponents().size() == 0) {
						nu.xom.Element elmComponent = new nu.xom.Element("Component");
						elmComponent.addAttribute(new Attribute("Name", d.getName()));
						elmComponent.addAttribute(new Attribute("Position", "1"));
						elmDatatype.appendChild(elmComponent);
					}

					if ((d != null && !d.getDefPreText().isEmpty()) || (d != null && !d.getDefPostText().isEmpty())) {
						if (d.getDefPreText() != null && !d.getDefPreText().isEmpty()) {
							elmDatatype.appendChild(this.serializeRichtext("DefPreText", d.getDefPreText()));
						}
						if (d.getDefPostText() != null && !d.getDefPostText().isEmpty()) {
							elmDatatype.appendChild(this.serializeRichtext("DefPostText", d.getDefPostText()));
						}
					}
					if (d.getUsageNote() != null && !d.getUsageNote().isEmpty()) {
						elmDatatype.appendChild(this.serializeRichtext("UsageNote", d.getUsageNote()));
					}
				}
			} else {
				elmDatatype.addAttribute(new Attribute("ID", "ID_" + dl.getId()));
				elmDatatype.addAttribute(new Attribute("Name", dl.getName() + ""));
				elmDatatype.addAttribute(new Attribute("Label", dl.getLabel() + ""));
				elmDatatype.addAttribute(new Attribute("Description", "Error"));
				elmDatatype.addAttribute(new Attribute("Comment", "! DEBUG: COULD NOT FIND id " + dl.getId()));
			}
		}
		return elmDatatype;
	}

	private String getFullUsage(Datatype d, int i) {
		List<Predicate> predicates = findPredicate(i + 1, d.getPredicates());

		if (predicates == null || predicates.isEmpty()) {
			return d.getComponents().get(i).getUsage().toString();
		} else {
			Predicate p = predicates.get(0);

			return d.getComponents().get(i).getUsage().toString() + "(" + p.getTrueUsage() + "/" + p.getFalseUsage()
					+ ")";
		}

	}

	private String getFullUsage(Segment s, int i) {
		List<Predicate> predicates = findPredicate(i + 1, s.getPredicates());

		if (predicates == null || predicates.isEmpty()) {
			return s.getFields().get(i).getUsage().toString();
		} else {
			Predicate p = predicates.get(0);

			return s.getFields().get(i).getUsage().toString() + "(" + p.getTrueUsage() + "/" + p.getFalseUsage() + ")";
		}

	}

	// private String getFullUsage(Message m, int i, String target) {
	// List<Predicate> predicates = findPredicate(i+1,m.getPredicates(),target);
	//
	// if(predicates==null||predicates.isEmpty()){
	// return m.getChildren().get(i).getUsage().toString();
	// }else{
	// Predicate p=predicates.get(0);
	//
	// return
	// m.getChildren().get(i).getUsage().toString()+"("+p.getTrueUsage()+"/"+p.getFalseUsage()+")";
	// }
	//
	// }

	public File serializeProfileToZipFile(Profile profile) throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("Profile_" + profile.getId(), ".zip");
			FileOutputStream outputStream = (FileOutputStream) Files.newOutputStream(out.toPath());

			int read = 0;
			byte[] bytes = new byte[1024];

			InputStream is = this.serializeProfileToZip(profile);
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			is.close();
			outputStream.close();

			return out;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

	}

	@Override
	public InputStream serializeProfileToZip(Profile profile) throws IOException {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		this.generateProfileIS(out, this.serializeProfileToXML(profile));
		this.generateValueSetIS(out, new TableSerializationImpl().serializeTableLibraryToXML(profile.getTableLibrary(),
				new DocumentMetaData(), profile.getDateUpdated()));
		this.generateConstraintsIS(out, new ConstraintsSerializationImpl().serializeConstraintsToXML(profile,
				new DocumentMetaData(), profile.getDateUpdated()));

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
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
		out.putNextEntry(new ZipEntry("ValueSet.xml"));
		InputStream inValueSet = IOUtils.toInputStream(valueSetXML);
		int lenTP;
		while ((lenTP = inValueSet.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inValueSet.close();
	}

	private void generateConstraintsIS(ZipOutputStream out, String constraintsXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Profile.xml"));
		InputStream inConstraints = IOUtils.toInputStream(constraintsXML);
		int lenTP;
		while ((lenTP = inConstraints.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inConstraints.close();
	}

	/**
	 * Encodes the byte array into base64 string
	 *
	 * @param imageByteArray
	 *            - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray) {
		return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	/**
	 * Decodes the base64 string into byte array
	 *
	 * @param imageDataString
	 *            - a {@link java.lang.String}
	 * @return byte array
	 */
	public static byte[] decodeImage(String imageDataString) {
		return Base64.decodeBase64(imageDataString);
	}

	@Override
	public InputStream serializeProfileToZip(Profile profile, String[] ids)
			throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream serializeDatatypeToZip(DatatypeLibrary datatypeLibrary) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serializeDatatypeLibraryDocumentToXML(DatatypeLibraryDocument datatypeLibraryDocument) {
		nu.xom.Document doc = serializeDatatypeLibraryDocumentToDoc(datatypeLibraryDocument);
		return doc.toXML();
	}

	@Override
	public nu.xom.Document serializeDatatypeLibraryDocumentToDoc(DatatypeLibraryDocument datatypeLibraryDocument) {
		// Create the root node (datatype library document node) that will
		// contain the datatype and
		// value sets libraries
		nu.xom.Element datatypeLibraryDocumentNode = new nu.xom.Element("ConformanceProfile");
		// Add the metadatas to the datatype library document node
		// TODO check if it shouldn't be a DatatypeLibraryDocumentMetaData
		// object in the model
		DatatypeLibraryMetaData datatypeLibraryMetadata = datatypeLibraryDocument.getMetaData();
		datatypeLibraryDocumentNode.addAttribute(new Attribute("Hl7Version",
				datatypeLibraryMetadata.getHl7Version() == null ? "" : datatypeLibraryMetadata.getHl7Version()));
		nu.xom.Element metaDataNode = new nu.xom.Element("MetaData");
		metaDataNode.addAttribute(new Attribute("Name",
				datatypeLibraryMetadata.getName() == null ? "" : datatypeLibraryMetadata.getName()));
		metaDataNode.addAttribute(new Attribute("OrgName",
				datatypeLibraryMetadata.getOrgName() == null ? "" : datatypeLibraryMetadata.getOrgName()));
		metaDataNode.addAttribute(new Attribute("Version",
				datatypeLibraryMetadata.getVersion() == null ? "" : datatypeLibraryMetadata.getVersion()));
		metaDataNode.addAttribute(new Attribute("Date",
				datatypeLibraryMetadata.getDate() == null ? "" : datatypeLibraryMetadata.getDate()));
		metaDataNode.addAttribute(
				new Attribute("Ext", datatypeLibraryMetadata.getExt() == null ? "" : datatypeLibraryMetadata.getExt()));
		datatypeLibraryDocumentNode.appendChild(metaDataNode);
		// Create the datatype library node that will contain the datatype nodes
		// Element datatypeLibraryNode = new nu.xom.Element("Datatypes");
		Element datatypeLibraryNode = new nu.xom.Element("Section");
		// Add attributes to the datatypeLibraryNode
		datatypeLibraryNode.addAttribute(new Attribute("id", "ID_" + datatypeLibraryDocument.getDatatypeLibrary().getId()));
		datatypeLibraryNode.addAttribute(new Attribute("position",
				String.valueOf(datatypeLibraryDocument.getDatatypeLibrary().getSectionPosition())));
		if (datatypeLibraryDocument.getSectionPosition() != null
				&& datatypeLibraryDocument.getDatatypeLibrary().getSectionPosition() != null) {
			String prefix = String.valueOf(datatypeLibraryDocument.getSectionPosition() + 1) + "."
					+ String.valueOf(datatypeLibraryDocument.getDatatypeLibrary().getSectionPosition() + 1);
			datatypeLibraryNode.addAttribute(new Attribute("prefix", prefix));
		}
		datatypeLibraryNode.addAttribute(new Attribute("h", String.valueOf(2)));
		datatypeLibraryNode.addAttribute(new Attribute("title", "Datatypes"));
		// Fetch all the Datatypes and create a node for each of them
		List<DatatypeLink> datattypeLinkList = new ArrayList<>(
				datatypeLibraryDocument.getDatatypeLibrary().getChildren());
		Collections.sort(datattypeLinkList);
		for (DatatypeLink dataTypeLink : datattypeLinkList) {
			// Serialize the datatype
			Element dataTypeNode = serializeDatatype(dataTypeLink, datatypeLibraryDocument.getTableLibrary(),
					datatypeLibraryDocument.getDatatypeLibrary(), "", datattypeLinkList.indexOf(dataTypeLink));
			if (dataTypeLink.getId() != null) {
				Datatype datatype = datatypeService.findById(dataTypeLink.getId());
				if (datatype != null && datatype.getScope().equals(Constant.SCOPE.MASTER)) {
					dataTypeNode.addAttribute(new Attribute("scope", "MASTER"));
				}
			}
			// Add the datatype node to the children of the datatype library
			// node
			datatypeLibraryNode.appendChild(dataTypeNode);
		}
		// Create the value sets library node that will contain the value set
		// nodes
		// Element tableLibraryNode = new nu.xom.Element("ValueSets");
		Element tableLibraryNode = new nu.xom.Element("Section");
		tableLibraryNode.addAttribute(new Attribute("id", "ID_" + datatypeLibraryDocument.getTableLibrary().getId()));
		tableLibraryNode.addAttribute(new Attribute("position",
				String.valueOf(datatypeLibraryDocument.getTableLibrary().getSectionPosition())));
		if (datatypeLibraryDocument.getSectionPosition() != null
				&& datatypeLibraryDocument.getTableLibrary().getSectionPosition() != null) {
			String prefix = String.valueOf(datatypeLibraryDocument.getSectionPosition() + 1) + "."
					+ String.valueOf(datatypeLibraryDocument.getTableLibrary().getSectionPosition() + 1);
			tableLibraryNode.addAttribute(new Attribute("prefix", prefix));
		}
		tableLibraryNode.addAttribute(new Attribute("h", String.valueOf(2)));
		tableLibraryNode.addAttribute(new Attribute("title", "Value Sets"));
		if (datatypeLibraryDocument.getTableLibrary().getSectionContents() != null
				&& !datatypeLibraryDocument.getTableLibrary().getSectionContents().isEmpty()) {
			nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
			sectCont.appendChild("<div class=\"fr-view\">"
					+ datatypeLibraryDocument.getTableLibrary().getSectionContents() + "</div>");
			tableLibraryNode.appendChild(sectCont);
		}
		// Fetch all the Value sets and create a node for each of them
		List<TableLink> tableLinkList = new ArrayList<>(datatypeLibraryDocument.getTableLibrary().getChildren());
		Collections.sort(tableLinkList);
		for (TableLink tableLink : tableLinkList) {
			// Serialize the value set
			Element tableLinkNode = serializeTable(tableLink, "", tableLinkList.indexOf(tableLink));
			// Add the value set node to the children of the value sets library
			// node
			if (tableLinkNode != null) {
				tableLibraryNode.appendChild(tableLinkNode);
			}
		}
		// Add the datatypes to the root node
		datatypeLibraryDocumentNode.appendChild(datatypeLibraryNode);
		// Add the value sets to the root node
		datatypeLibraryDocumentNode.appendChild(tableLibraryNode);
		// Create the document with the root node
		nu.xom.Document doc = new nu.xom.Document(datatypeLibraryDocumentNode);
		return doc;
	}

	@Override
	public String serializeDatatypeLibraryToXML(DatatypeLibrary datatypeLibrary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public nu.xom.Document serializeDatatypeLibraryToDoc(DatatypeLibrary datatypeLibrary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream serializeProfileDisplayToZip(Profile original, String[] ids)
			throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream serializeProfileGazelleToZip(Profile original, String[] ids)
			throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlConstraints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Profile deserializeXMLToProfile(Document docProfile, Document docValueSet, Document docConstraints) {
		// TODO Auto-generated method stub
		return null;
	}
}
