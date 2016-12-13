package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Attribute;
import nu.xom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
 * <p>
 * Created by Maxence Lefort on 12/7/16.
 */
@Service
public class SerializationServiceImpl implements SerializationService {

    @Autowired
    SerializationUtil serializationUtil;

    @Autowired
    SerializeMessageService serializeMessageService;

    @Override public Document serializeIGDocument(IGDocument igDocument) {
        SerializableStructure serializableStructure = new SerializableStructure();
        SerializableMetadata serializableMetadata = new SerializableMetadata(igDocument.getMetaData(),igDocument.getProfile().getMetaData(),igDocument.getDateUpdated());
        serializableStructure.addSerializableElement(serializableMetadata);
        SerializableSections serializableSections = new SerializableSections();
        String prefix = "";
        Integer depth = 1;
        serializationUtil.setSectionsPrefixes(igDocument.getChildSections(),prefix,depth,serializableSections.getRootSections());
        Profile profile = igDocument.getProfile();
        nu.xom.Element xsect = new nu.xom.Element("Section");
        xsect.addAttribute(new Attribute("id", profile.getId()));

        xsect.addAttribute(new Attribute("h", String.valueOf(1)));
        String id = profile.getId();
        String position = String.valueOf(profile.getSectionPosition());
        prefix = String.valueOf(profile.getSectionPosition() + 1);
        String title = "";
        if (profile.getSectionTitle() != null) {
            title = profile.getSectionTitle();
        }
        SerializableSection serializableSection = new SerializableSection(id,prefix,position,title);
        if (profile.getSectionContents() != null && !profile.getSectionContents().isEmpty()) {
            serializableSection.addSectionContent("<div class=\"fr-view\">" + profile.getSectionContents() + "</div>");
        }

        //TODO Check with Olivier if it's still used

        /*nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
        e.addAttribute(new Attribute("ID", profile.getId()));
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
        */

        if (profile.getUsageNote() != null && !profile.getUsageNote().isEmpty()) {
            nu.xom.Element textElement = new nu.xom.Element("Text");
            if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
                nu.xom.Element usageNoteElement = new nu.xom.Element("UsageNote");
                usageNoteElement.appendChild(profile.getUsageNote());
                textElement.appendChild(usageNoteElement);
            }
            serializableSections.getRootSections().appendChild(textElement);
        }

        id = profile.getMessages().getId();
        position = String.valueOf(profile.getMessages().getSectionPosition());
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
            + String.valueOf(profile.getMessages().getSectionPosition() + 1);
        if (profile.getMessages().getSectionTitle() != null) {
            title = profile.getMessages().getSectionTitle();
        } else {
            title = "";
        }
        SerializableSection serializableSectionMessages = new SerializableSection(id,prefix,position,title);
        if (profile.getMessages().getSectionContents() != null
            && !profile.getMessages().getSectionContents().isEmpty()) {
            serializableSectionMessages.addSectionContent("<div class=\"fr-view\">" + profile.getMessages().getSectionContents() + "</div>");
        }
        for (Message message : profile.getMessages().getChildren()) {
            SerializableMessage serializableMessage = serializeMessageService.serializeMessage(message,prefix)
            serializableSectionMessages.addSection(serializableMessage);
        }

        // nu.xom.Element ss = new nu.xom.Element("Segments");
        nu.xom.Element ss = new nu.xom.Element("Section");
        ss.addAttribute(new Attribute("id", profile.getSegmentLibrary().getId()));
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
        //TODO Refactor below
        // nu.xom.Element ds = new nu.xom.Element("Datatypes");
        nu.xom.Element ds = new nu.xom.Element("Section");
        ds.addAttribute(new Attribute("id", profile.getDatatypeLibrary().getId()));
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
        ts.addAttribute(new Attribute("id", profile.getTableLibrary().getId()));
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
        cnts.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        cnts.addAttribute(new Attribute("position", String.valueOf(5)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5);
        cnts.addAttribute(new Attribute("prefix", prefix));
        cnts.addAttribute(new Attribute("h", String.valueOf(2)));
        cnts.addAttribute(new Attribute("title", "Conformance information"));

        nu.xom.Element cs = new nu.xom.Element("Section");
        cs.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        cs.addAttribute(new Attribute("position", String.valueOf(1)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1);
        cs.addAttribute(new Attribute("prefix", prefix));
        cs.addAttribute(new Attribute("h", String.valueOf(3)));
        cs.addAttribute(new Attribute("title", "Conformance statements"));

        nu.xom.Element cp = new nu.xom.Element("Section");
        cp.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        cp.addAttribute(new Attribute("position", String.valueOf(2)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2);
        cp.addAttribute(new Attribute("prefix", prefix));
        cp.addAttribute(new Attribute("h", String.valueOf(3)));
        cp.addAttribute(new Attribute("title", "Conditional predicates"));

        // * Messages
        nu.xom.Element csmsg = new nu.xom.Element("Section");
        csmsg.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        csmsg.addAttribute(new Attribute("position", String.valueOf(3)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
            + "." + String.valueOf(profile.getMessages().getSectionPosition() + 1);
        csmsg.addAttribute(new Attribute("prefix", prefix));
        csmsg.addAttribute(new Attribute("h", String.valueOf(4)));
        csmsg.addAttribute(new Attribute("title", "Conformance profile level"));

        nu.xom.Element cpmsg = new nu.xom.Element("Section");
        cpmsg.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        cpmsg.addAttribute(new Attribute("position", String.valueOf(3)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(2)
            + "." + String.valueOf(profile.getMessages().getSectionPosition() + 1);
        cpmsg.addAttribute(new Attribute("prefix", prefix));
        cpmsg.addAttribute(new Attribute("h", String.valueOf(4)));
        cpmsg.addAttribute(new Attribute("title", "Conformance profile level"));

        for (Message m : profile.getMessages().getChildren()) {
            if (m.getChildren() != null) {

                nu.xom.Element csinfo = new nu.xom.Element("Constraints");
                csinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
                csinfo.addAttribute(new Attribute("position", String.valueOf(m.getPosition())));
                csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
                csinfo.addAttribute(new Attribute("title", m.getName()));
                csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

                nu.xom.Element cpinfo = new nu.xom.Element("Constraints");
                cpinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
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
        cssg.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        cssg.addAttribute(new Attribute("position", String.valueOf(3)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
            + "." + String.valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
        cssg.addAttribute(new Attribute("prefix", prefix));
        cssg.addAttribute(new Attribute("h", String.valueOf(4)));
        cssg.addAttribute(new Attribute("title", "Segment level"));

        nu.xom.Element cpsg = new nu.xom.Element("Section");
        cpsg.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
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
                    csinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
                    csinfo.addAttribute(new Attribute("position", ""));
                    csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
                    csinfo.addAttribute(new Attribute("title", sl.getLabel()));
                    csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

                    nu.xom.Element cpinfo = new nu.xom.Element("Constraints");
                    cpinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
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
        csdt.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
        csdt.addAttribute(new Attribute("position", String.valueOf(3)));
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "." + String.valueOf(1)
            + "." + String.valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
        csdt.addAttribute(new Attribute("prefix", prefix));
        csdt.addAttribute(new Attribute("h", String.valueOf(4)));
        csdt.addAttribute(new Attribute("title", "Datatype level"));

        nu.xom.Element cpdt = new nu.xom.Element("Section");
        cpdt.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
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
                    csinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
                    csinfo.addAttribute(new Attribute("position", ""));
                    csinfo.addAttribute(new Attribute("h", String.valueOf(3)));
                    csinfo.addAttribute(new Attribute("title", d.getLabel()));
                    csinfo.addAttribute(new Attribute("Type", "ConformanceStatement"));

                    nu.xom.Element cpdtinfo = new nu.xom.Element("Constraints");
                    cpdtinfo.addAttribute(new Attribute("id", UUID.randomUUID().toString()));
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

        return serializableStructure.serializeStructure();
    }

    @Override public Document serializeDatatypeLibrary(IGDocument igDocument) {
        return null;
    }

    @Override public Document serializeElement(SerializableElement element) {
        SerializableStructure serializableStructure = new SerializableStructure();
        serializableStructure.addSerializableElement(element);
        return serializableStructure.serializeStructure();
    }
}
