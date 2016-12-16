package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeDatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeTableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
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
@Service public class SerializationServiceImpl implements SerializationService {

    @Autowired SerializationUtil serializationUtil;

    @Autowired SerializeMessageService serializeMessageService;

    @Autowired SerializeSegmentService serializeSegmentService;

    @Autowired SerializeDatatypeService serializeDatatypeService;

    @Autowired SerializeTableService serializeTableService;

    @Override public Document serializeDatatypeLibrary(IGDocument igDocument,
        boolean includeSegmentsInMessage) {
        return serializeIGDocument(igDocument, includeSegmentsInMessage);
    }

    @Override public Document serializeElement(SerializableElement element) {
        SerializableStructure serializableStructure = new SerializableStructure();
        serializableStructure.addSerializableElement(element);
        return serializableStructure.serializeStructure();
    }

    @Override public Document serializeIGDocument(IGDocument igDocument,
        boolean includeSegmentsInMessage) {
        SerializableStructure serializableStructure = new SerializableStructure();
        SerializableMetadata serializableMetadata =
            new SerializableMetadata(igDocument.getMetaData(),
                igDocument.getProfile().getMetaData(), igDocument.getDateUpdated());
        serializableStructure.addSerializableElement(serializableMetadata);
        SerializableSections serializableSections = new SerializableSections();
        String prefix = "";
        Integer depth = 1;
        serializationUtil.setSectionsPrefixes(igDocument.getChildSections(), prefix, depth,
            serializableSections.getRootSections());
        Profile profile = igDocument.getProfile();
        //Create base section node for the profile serialization
        String id = profile.getId();
        String position = String.valueOf(profile.getSectionPosition());
        prefix = String.valueOf(profile.getSectionPosition() + 1);
        String headerLevel = String.valueOf(1);
        String title = "";
        if (profile.getMessages().getSectionTitle() != null) {
            title = profile.getSectionTitle();
        }
        SerializableSection profileSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (profile.getSectionContents() != null && !profile.getSectionContents().isEmpty()) {
            profileSection.addSectionContent(profile.getSectionContents());
        }
        if (profile.getUsageNote() != null && !profile.getUsageNote().isEmpty()) {
            nu.xom.Element textElement = new nu.xom.Element("Text");
            if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
                nu.xom.Element usageNoteElement = new nu.xom.Element("UsageNote");
                usageNoteElement.appendChild(profile.getUsageNote());
                textElement.appendChild(usageNoteElement);
            }
            serializableSections.getRootSections().appendChild(textElement);
        }
        //Message Serialization
        SerializableSection messageSection = this.serializeMessages(profile,
            includeSegmentsInMessage);
        profileSection.addSection(messageSection);

        //Segments serialization
        SerializableSection segmentsSection = this.serializeSegments(profile);
        profileSection.addSection(segmentsSection);

        //Datatypes serialization
        SerializableSection datatypeSection = this.serializeDatatypes(profile);
        profileSection.addSection(datatypeSection);

        //Value sets serialization
        id = profile.getTableLibrary().getId();
        position = String.valueOf(profile.getTableLibrary().getSectionPosition());
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
            .valueOf(profile.getTableLibrary().getSectionPosition() + 1);
        headerLevel = String.valueOf(2);
        title = "";
        if (profile.getTableLibrary().getSectionTitle() != null) {
            title = profile.getTableLibrary().getSectionTitle();
        }
        SerializableSection valueSetsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (profile.getTableLibrary().getSectionContents() != null && !profile.getTableLibrary()
            .getSectionContents().isEmpty()) {
            valueSetsSection.addSectionContent(
                "<div class=\"fr-view\">" + profile.getTableLibrary().getSectionContents()
                    + "</div>");
        }
        List<TableLink> tableLinkList = new ArrayList<>(profile.getTableLibrary().getChildren());
        Collections.sort(tableLinkList);
        for (TableLink tableLink : tableLinkList) {
            SerializableTable serializableTable = serializeTableService.serializeTable(tableLink,
                prefix + "." + String.valueOf(tableLinkList.indexOf(tableLink) + 1),
                tableLinkList.indexOf(tableLink));
            valueSetsSection.addSection(serializableTable);
        }
        profileSection.addSection(valueSetsSection);

        SerializableSection constraintInformationSection =
            this.serializeConstraints(profile, messageSection.getSerializableSectionList(), segmentsSection.getSerializableSectionList(),
                datatypeSection.getSerializableSectionList());
        profileSection.addSection(constraintInformationSection);



        serializableSections.addSection(profileSection);
        serializableStructure.addSerializableElement(serializableSections);
        return serializableStructure.serializeStructure();
    }

    private SerializableSection serializeDatatypes(Profile profile) {
        String id = profile.getDatatypeLibrary().getId();
        String position = String.valueOf(profile.getDatatypeLibrary().getSectionPosition());
        String prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
            .valueOf(profile.getDatatypeLibrary().getSectionPosition() + 1);
        String headerLevel = String.valueOf(2);
        String title = "";
        if (profile.getDatatypeLibrary().getSectionTitle() != null) {
            title = profile.getDatatypeLibrary().getSectionTitle();
        }
        SerializableSection datatypeSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (profile.getDatatypeLibrary().getSectionContents() != null && !profile
            .getDatatypeLibrary().getSectionContents().isEmpty()) {
            datatypeSection.addSectionContent(
                "<div class=\"fr-view\">" + profile.getDatatypeLibrary().getSectionContents()
                    + "</div>");
        }
        List<DatatypeLink> datatypeLinkList =
            new ArrayList<>(profile.getDatatypeLibrary().getChildren());
        Collections.sort(datatypeLinkList);
        for (DatatypeLink datatypeLink : datatypeLinkList) {
            SerializableDatatype serializableDatatype = serializeDatatypeService
                .serializeDatatype(datatypeLink,
                    prefix + "." + String.valueOf(datatypeLinkList.indexOf(datatypeLink) + 1),
                    datatypeLinkList.indexOf(datatypeLink));
            datatypeSection.addSection(serializableDatatype);
        }
        return datatypeSection;
    }



    private SerializableSection serializeMessages(Profile profile, boolean includeSegmentsInMessage) {
        String id = profile.getMessages().getId();
        String position = String.valueOf(profile.getMessages().getSectionPosition());
        String prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
            .valueOf(profile.getMessages().getSectionPosition() + 1);
        String headerLevel = String.valueOf(2);
        String title = "";
        if (profile.getMessages().getSectionTitle() != null) {
            title = profile.getMessages().getSectionTitle();
        }
        SerializableSection messageSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (profile.getMessages().getSectionContents() != null && !profile.getMessages()
            .getSectionContents().isEmpty()) {
            messageSection.addSectionContent(
                "<div class=\"fr-view\">" + profile.getMessages().getSectionContents() + "</div>");
        }

        for (Message message : profile.getMessages().getChildren()) {
            SerializableMessage serializableMessage =
                serializeMessageService.serializeMessage(message, prefix, includeSegmentsInMessage);
            messageSection.addSection(serializableMessage);
        }
        return messageSection;
    }

    private SerializableSection serializeSegments(Profile profile) {
        String id = profile.getSegmentLibrary().getId();
        String position = String.valueOf(profile.getSegmentLibrary().getSectionPosition());
        String prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
            .valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
        String headerLevel = String.valueOf(2);
        String title = "";
        if (profile.getSegmentLibrary().getSectionTitle() != null) {
            title = profile.getSegmentLibrary().getSectionTitle();
        }
        SerializableSection segmentsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (profile.getSegmentLibrary().getSectionContents() != null && !profile.getSegmentLibrary()
            .getSectionContents().isEmpty()) {
            segmentsSection.addSectionContent(
                "<div class=\"fr-view\">" + profile.getSegmentLibrary().getSectionContents()
                    + "</div>");
        }

        List<SegmentLink> segmentLinkList =
            new ArrayList<>(profile.getSegmentLibrary().getChildren());
        Collections.sort(segmentLinkList);
        for (SegmentLink segmentLink : segmentLinkList) {
            if (segmentLink.getId() != null) {
                segmentsSection.addSection(serializeSegmentService.serializeSegment(segmentLink,
                    prefix + "." + String.valueOf(segmentLinkList.indexOf(segmentLink) + 1),
                    segmentLinkList.indexOf(segmentLink), 3));

            }
        }
        return segmentsSection;
    }

    private SerializableSection serializeConstraints(Profile profile,
        List<SerializableSection> messages, List<SerializableSection> segments,
        List<SerializableSection> datatypes) {

        String id = UUID.randomUUID().toString();
        String position = String.valueOf(5);
        String prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5);
        String headerLevel = String.valueOf(2);
        String title = "Conformance information";
        SerializableSection conformanceInformationSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(1);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(1);
        headerLevel = String.valueOf(3);
        title = "Conformance statements";
        SerializableSection conformanceStatementsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(2);
        headerLevel = String.valueOf(3);
        title = "Conditional predicates";
        SerializableSection conditionalPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(1);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(1) + "." + String.valueOf(1);
        headerLevel = String.valueOf(4);
        title = "Conformance profile level";
        SerializableSection profileLevelConformanceStatementsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(1);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(2) + "." + String.valueOf(1);
        headerLevel = String.valueOf(4);
        title = "Conformance profile level";
        SerializableSection profileLevelPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(1) + "." + String.valueOf(2);
        headerLevel = String.valueOf(4);
        title = "Segment level";
        SerializableSection segmentLevelConformanceStatementSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(2) + "." + String.valueOf(2);
        headerLevel = String.valueOf(4);
        title = "Segment level";
        SerializableSection segmentLevelPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(1) + "." + String.valueOf(3);
        headerLevel = String.valueOf(4);
        title = "Datatype level";
        SerializableSection datatypeLevelConformanceStatementSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
            + String.valueOf(2) + "." + String.valueOf(3);
        headerLevel = String.valueOf(4);
        title = "Datatype level";
        SerializableSection datatypeLevelPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        Integer currentConformanceStatementPosition = 1;
        Integer currentPredicatePosition = 1;
        for (SerializableSection serializableMessageSection : messages) {
            if (serializableMessageSection instanceof SerializableMessage) {
                SerializableMessage serializableMessage =
                    (SerializableMessage) serializableMessageSection;
                id = UUID.randomUUID().toString();
                position = String.valueOf(currentConformanceStatementPosition);
                prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
                    + String.valueOf(1) + "." + String.valueOf(1) + "." + String.valueOf(currentConformanceStatementPosition);
                headerLevel = String.valueOf(5);
                title = serializableMessage.getMessage().getName();
                SerializableConstraints serializableConformanceStatement = serializableMessage.getSerializableConformanceStatements();
                if(serializableConformanceStatement.getConstraints().size()>0) {
                    SerializableSection
                        conformanceStatementsProfileLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                    serializableConformanceStatement.setTitle("");
                    conformanceStatementsProfileLevelConformanceStatementsSection
                        .addSection(serializableConformanceStatement);
                    profileLevelConformanceStatementsSection
                        .addSection(conformanceStatementsProfileLevelConformanceStatementsSection);
                    currentConformanceStatementPosition+=1;
                }
                id = UUID.randomUUID().toString();
                position = String.valueOf(currentPredicatePosition);
                prefix =
                    String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5) + "."
                        + String.valueOf(1) + "." + String.valueOf(2) + "." + String.valueOf(currentPredicatePosition);
                headerLevel = String.valueOf(5);
                title = serializableMessage.getMessage().getName();
                SerializableConstraints serializablePredicate = serializableMessage.getSerializablePredicates();
                if(serializablePredicate.getConstraints().size()>0) {
                    SerializableSection predicatesProfileLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                    serializablePredicate.setTitle("");
                    predicatesProfileLevelConformanceStatementsSection
                        .addSection(serializablePredicate);
                    profileLevelPredicatesSection
                        .addSection(predicatesProfileLevelConformanceStatementsSection);
                }
            }
        }

        currentConformanceStatementPosition = 1;
        currentPredicatePosition = 1;
        for(SerializableSection serializableSection : segments){
            if(serializableSection instanceof SerializableSegment) {
                SerializableSegment serializableSegment = (SerializableSegment) serializableSection;
                if (serializableSegment.getConstraints().size() > 0) {
                    List<SerializableConstraint> segmentConformanceStatements = new ArrayList<>();
                    List<SerializableConstraint> segmentPredicates = new ArrayList<>();
                    for(SerializableConstraint serializableConstraint : serializableSegment.getConstraints()){
                        if(serializableConstraint.getConstraint() instanceof Predicate){
                            segmentPredicates.add(serializableConstraint);
                        } else if(serializableConstraint.getConstraint() instanceof ConformanceStatement){
                            segmentConformanceStatements.add(serializableConstraint);
                        }
                    }
                    if(segmentConformanceStatements.size()>0) {
                        id = UUID.randomUUID().toString();
                        position = String.valueOf(currentConformanceStatementPosition);
                        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
                            .valueOf(5) + "." + String.valueOf(2) + "." + String.valueOf(1) + "." + String.valueOf(currentConformanceStatementPosition);
                        headerLevel = String.valueOf(5);
                        title = serializableSegment.getSegment().getName() + " - " + serializableSegment.getSegment().getDescription();
                        SerializableSection
                            conformanceStatementsSegmentLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                        SerializableConstraints serializableConformanceStatementConstraints =
                            new SerializableConstraints(segmentConformanceStatements, id, "", "",
                                "ConformanceStatement");
                        conformanceStatementsSegmentLevelConformanceStatementsSection
                            .addSection(serializableConformanceStatementConstraints);
                        currentConformanceStatementPosition+=1;
                        segmentLevelConformanceStatementSection.addSection(conformanceStatementsSegmentLevelConformanceStatementsSection);
                    }
                    if(segmentPredicates.size()>0) {
                        id = UUID.randomUUID().toString();
                        position = String.valueOf(currentPredicatePosition);
                        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
                            .valueOf(5) + "." + String.valueOf(2) + "." + String.valueOf(2) + "." + String.valueOf(currentPredicatePosition);
                        headerLevel = String.valueOf(5);
                        title = serializableSegment.getSegment().getName() + " - " + serializableSegment.getSegment().getDescription();
                        SerializableSection
                            predicatesSegmentLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                        SerializableConstraints serializablePredicateConstraints =
                            new SerializableConstraints(segmentPredicates, id, "", "",
                                "ConditionPredicate");
                        predicatesSegmentLevelConformanceStatementsSection
                            .addSection(serializablePredicateConstraints);
                        currentPredicatePosition+=1;
                        segmentLevelPredicatesSection.addSection(predicatesSegmentLevelConformanceStatementsSection);
                    }

                }
            }
        }
        currentConformanceStatementPosition = 1;
        currentPredicatePosition = 1;
        for(SerializableSection serializableSection : datatypes){
            if(serializableSection instanceof SerializableDatatype) {
                SerializableDatatype serializableDatatype = (SerializableDatatype) serializableSection;
                if (serializableDatatype.getConstraints().size() > 0) {
                    List<SerializableConstraint> datatypeConformanceStatements = new ArrayList<>();
                    List<SerializableConstraint> datatypePredicates = new ArrayList<>();
                    for(SerializableConstraint serializableConstraint : serializableDatatype.getConstraints()){
                        if(serializableConstraint.getConstraint() instanceof Predicate){
                            datatypePredicates.add(serializableConstraint);
                        } else if(serializableConstraint.getConstraint() instanceof ConformanceStatement){
                            datatypeConformanceStatements.add(serializableConstraint);
                        }
                    }
                    if(datatypeConformanceStatements.size()>0) {
                        id = UUID.randomUUID().toString();
                        position = String.valueOf(currentConformanceStatementPosition);
                        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
                            .valueOf(5) + "." + String.valueOf(2) + "." + String.valueOf(1) + "." + String.valueOf(currentConformanceStatementPosition);
                        headerLevel = String.valueOf(5);
                        title = serializableDatatype.getDatatype().getName() + " - "+serializableDatatype.getDatatype().getDescription();
                        SerializableSection
                            conformanceStatementsDatatypeLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                        SerializableConstraints serializableConformanceStatementConstraints =
                            new SerializableConstraints(datatypeConformanceStatements, id, "", "",
                                "ConformanceStatement");
                        conformanceStatementsDatatypeLevelConformanceStatementsSection
                            .addSection(serializableConformanceStatementConstraints);
                        currentConformanceStatementPosition+=1;
                        datatypeLevelConformanceStatementSection.addSection(conformanceStatementsDatatypeLevelConformanceStatementsSection);
                    }
                    if(datatypePredicates.size()>0) {
                        id = UUID.randomUUID().toString();
                        position = String.valueOf(currentPredicatePosition);
                        prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String
                            .valueOf(5) + "." + String.valueOf(2) + "." + String.valueOf(2) + "." + String.valueOf(currentPredicatePosition);
                        headerLevel = String.valueOf(5);
                        title = serializableDatatype.getDatatype().getName() + " - "+serializableDatatype.getDatatype().getDescription();
                        SerializableSection
                            predicatesDatatypeLevelConformanceStatementsSection = new SerializableSection(id, prefix, position, headerLevel, title);
                        SerializableConstraints serializablePredicateConstraints =
                            new SerializableConstraints(datatypePredicates, id, "", "",
                                "ConditionPredicate");
                        predicatesDatatypeLevelConformanceStatementsSection
                            .addSection(serializablePredicateConstraints);
                        currentPredicatePosition+=1;
                        datatypeLevelPredicatesSection.addSection(predicatesDatatypeLevelConformanceStatementsSection);
                    }

                }
            }
        }

        conformanceStatementsSection.addSection(profileLevelConformanceStatementsSection);
        conformanceStatementsSection.addSection(segmentLevelConformanceStatementSection);
        conformanceStatementsSection.addSection(datatypeLevelConformanceStatementSection);
        conditionalPredicatesSection.addSection(profileLevelPredicatesSection);
        conditionalPredicatesSection.addSection(segmentLevelPredicatesSection);
        conditionalPredicatesSection.addSection(datatypeLevelPredicatesSection);

        conformanceInformationSection.addSection(conformanceStatementsSection);
        conformanceInformationSection.addSection(conditionalPredicatesSection);


        /*for (Message m : profile.getMessages().getChildren()) {
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

        xsect.appendChild(cnts);*/
        return conformanceInformationSection;
    }
}
