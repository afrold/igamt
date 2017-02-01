package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override public Document serializeDatatypeLibrary(DatatypeLibraryDocument datatypeLibraryDocument) {
        SerializableStructure serializableStructure = new SerializableStructure();
        datatypeLibraryDocument.getMetaData().setHl7Version("");
        datatypeLibraryDocument.getDatatypeLibrary().setSectionTitle("Data Types");
        datatypeLibraryDocument.getDatatypeLibrary().setSectionContents("");
        datatypeLibraryDocument.getTableLibrary().setSectionContents("");
        datatypeLibraryDocument.getTableLibrary().setSectionTitle("Value Sets");
        SerializableMetadata serializableMetadata =
            new SerializableMetadata(datatypeLibraryDocument.getMetaData(), datatypeLibraryDocument.getDateUpdated());
        serializableStructure.addSerializableElement(serializableMetadata);
        SerializableSections serializableSections = new SerializableSections();
        SerializableSection datatypeSection = this.serializeDatatypes(datatypeLibraryDocument.getDatatypeLibrary(),1,true);
        //datatypeSection.setTitle("Data Types");
        serializableSections.addSection(datatypeSection);
        SerializableSection valueSetsSection = this.serializeValueSets(datatypeLibraryDocument.getTableLibrary(),2);
        //valueSetsSection.setTitle("Value Sets");
        serializableSections.addSection(valueSetsSection);
        serializableStructure.addSerializableElement(serializableSections);
        return serializableStructure.serializeStructure();
    }

    @Override public Document serializeElement(SerializableElement element) {
        SerializableStructure serializableStructure = new SerializableStructure();
        serializableStructure.addSerializableElement(element);
        return serializableStructure.serializeStructure();
    }

    @Override public Document serializeIGDocument(IGDocument igDocument,
        SerializationLayout serializationLayout) {
        SerializableStructure serializableStructure = new SerializableStructure();
        igDocument.getMetaData().setHl7Version(igDocument.getProfile().getMetaData().getHl7Version());
        SerializableMetadata serializableMetadata =
            new SerializableMetadata(igDocument.getMetaData(), igDocument.getDateUpdated());
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
        SerializableSection messageSection = this.serializeMessages(profile, serializationLayout,igDocument.getMetaData().getHl7Version());
        profileSection.addSection(messageSection);

        //Segments serialization
        SerializableSection segmentsSection = this.serializeSegments(profile);
        if(!serializationLayout.equals(SerializationLayout.VERBOSE)) {
            profileSection.addSection(segmentsSection);
        }

        //Datatypes serialization
        boolean serializeMaster = true;
        if(serializationLayout.equals(SerializationLayout.VERBOSE)) {
            serializeMaster = false;
        }
        SerializableSection datatypeSection = this.serializeDatatypes(profile.getDatatypeLibrary(),profile.getSectionPosition(),serializeMaster);
        profileSection.addSection(datatypeSection);

        //Value sets serialization
        SerializableSection valueSetsSection = this.serializeValueSets(profile.getTableLibrary(),profile.getSectionPosition());
        profileSection.addSection(valueSetsSection);

        SerializableSection constraintInformationSection =
            this.serializeConstraints(profile, messageSection.getSerializableSectionList(), segmentsSection.getSerializableSectionList(),
                datatypeSection.getSerializableSectionList());
        profileSection.addSection(constraintInformationSection);



        serializableSections.addSection(profileSection);
        serializableStructure.addSerializableElement(serializableSections);
        return serializableStructure.serializeStructure();
    }

    private SerializableSection serializeValueSets(TableLibrary tableLibrary,
        Integer sectionPosition) {
        String id = tableLibrary.getId();
        String position,prefix;
        if(tableLibrary.getSectionPosition()!=null) {
            position = String.valueOf(tableLibrary.getSectionPosition());
            prefix = String.valueOf(sectionPosition + 1) + "." + String
                .valueOf(tableLibrary.getSectionPosition() + 1);
        } else {
            position = String.valueOf(sectionPosition);
            prefix = String.valueOf(sectionPosition);
        }
        String headerLevel = String.valueOf(2);
        String title = "";
        if (tableLibrary.getSectionTitle() != null) {
            title = tableLibrary.getSectionTitle();
        }
        SerializableSection valueSetsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (tableLibrary.getSectionContents() != null && !tableLibrary
            .getSectionContents().isEmpty()) {
            valueSetsSection.addSectionContent(
                "<div class=\"fr-view\">" + tableLibrary.getSectionContents()
                    + "</div>");
        }
        List<TableLink> tableLinkList = new ArrayList<>(tableLibrary.getChildren());
        Collections.sort(tableLinkList);
        for (TableLink tableLink : tableLinkList) {
            SerializableTable serializableTable = serializeTableService.serializeTable(tableLink,
                prefix + "." + String.valueOf(tableLinkList.indexOf(tableLink) + 1),
                tableLinkList.indexOf(tableLink));
            valueSetsSection.addSection(serializableTable);
        }
        return valueSetsSection;
    }

    private SerializableSection serializeDatatypes(DatatypeLibrary datatypeLibrary, int sectionPosition, boolean serializeMaster) {
        String id = datatypeLibrary.getId();
        String position,prefix;
        if(datatypeLibrary.getSectionPosition()!=null) {
            position = String.valueOf(datatypeLibrary.getSectionPosition());
            prefix = String.valueOf(sectionPosition + 1) + "." + String
                .valueOf(datatypeLibrary.getSectionPosition() + 1);
        } else {
            position = String.valueOf(sectionPosition);
            prefix = String.valueOf(sectionPosition);
        }
        String headerLevel = String.valueOf(2);
        String title = "";
        if (datatypeLibrary.getSectionTitle() != null) {
            title = datatypeLibrary.getSectionTitle();
        }
        SerializableSection datatypeSection =
            new SerializableSection(id, prefix, position, headerLevel, title);
        if (datatypeLibrary.getSectionContents() != null && !datatypeLibrary.getSectionContents().isEmpty()) {
            datatypeSection.addSectionContent(
                "<div class=\"fr-view\">" + datatypeLibrary.getSectionContents() + "</div>");
        }
        List<DatatypeLink> datatypeLinkList =
            new ArrayList<>(datatypeLibrary.getChildren());
        Collections.sort(datatypeLinkList);
        for (DatatypeLink datatypeLink : datatypeLinkList) {
            SerializableDatatype serializableDatatype = serializeDatatypeService
                .serializeDatatype(datatypeLink,
                    prefix + "." + String.valueOf(datatypeLinkList.indexOf(datatypeLink) + 1),
                    datatypeLinkList.indexOf(datatypeLink));
            //This "if" is only useful if we want to display only user datatypes
            //if(serializeMaster||!(serializableDatatype.getDatatype().getScope().equals(Constant.SCOPE.HL7STANDARD))){
                datatypeSection.addSection(serializableDatatype);
            //}
        }
        return datatypeSection;
    }



    private SerializableSection serializeMessages(Profile profile, SerializationLayout serializationLayout, String hl7Version) {
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
                serializeMessageService.serializeMessage(message, prefix, serializationLayout,hl7Version);
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
        prefix = conformanceInformationSection.getPrefix() + "."
            + String.valueOf(1);
        headerLevel = String.valueOf(3);
        title = "Conformance statements";
        SerializableSection conformanceStatementsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = conformanceInformationSection.getPrefix() + "."
            + String.valueOf(2);
        headerLevel = String.valueOf(3);
        title = "Conditional predicates";
        SerializableSection conditionalPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(1);
        prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(1);
        headerLevel = String.valueOf(4);
        title = "Conformance profile level";
        SerializableSection profileLevelConformanceStatementsSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(1);
        prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(1);
        headerLevel = String.valueOf(4);
        title = "Conformance profile level";
        SerializableSection profileLevelPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(2);
        headerLevel = String.valueOf(4);
        title = "Segment level";
        SerializableSection segmentLevelConformanceStatementSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(2);
        headerLevel = String.valueOf(4);
        title = "Segment level";
        SerializableSection segmentLevelPredicatesSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(3);
        headerLevel = String.valueOf(4);
        title = "Datatype level";
        SerializableSection datatypeLevelConformanceStatementSection =
            new SerializableSection(id, prefix, position, headerLevel, title);

        id = UUID.randomUUID().toString();
        position = String.valueOf(2);
        prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(3);
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
                position = String.valueOf(
                    ((SerializableMessage) serializableMessageSection).getMessage().getPosition());
                prefix = profileLevelConformanceStatementsSection.getPrefix() + "." + String.valueOf(currentConformanceStatementPosition);
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
                prefix = profileLevelPredicatesSection.getPrefix() + "." + String.valueOf(currentPredicatePosition);
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
        for(SerializableSection serializableSegmentSection : segments){
            if(serializableSegmentSection.getSerializableSectionList().size()>0) {
                for (SerializableSection serializableSection : serializableSegmentSection
                    .getSerializableSectionList()) {
                    if (serializableSection instanceof SerializableSegment) {
                        SerializableSegment serializableSegment = (SerializableSegment) serializableSection;
                        if (serializableSegment.getConstraints().size() > 0) {
                            List<SerializableConstraint> segmentConformanceStatements = new ArrayList<>();
                            List<SerializableConstraint> segmentPredicates = new ArrayList<>();
                            for (SerializableConstraint serializableConstraint : serializableSegment
                                .getConstraints()) {
                                if (serializableConstraint.getConstraint() instanceof Predicate) {
                                    segmentPredicates.add(serializableConstraint);
                                } else if (serializableConstraint
                                    .getConstraint() instanceof ConformanceStatement) {
                                    segmentConformanceStatements.add(serializableConstraint);
                                }
                            }
                            if (segmentConformanceStatements.size() > 0) {
                                id = UUID.randomUUID().toString();
                                position = String.valueOf(currentConformanceStatementPosition);
                                prefix = segmentLevelConformanceStatementSection.getPrefix() + "." + currentConformanceStatementPosition;
                                headerLevel = String.valueOf(5);
                                title = serializableSegment.getSegment().getName() + " - " + serializableSegment.getSegment().getDescription();
                                SerializableSection
                                    conformanceStatementsSegmentLevelConformanceStatementsSection =
                                    new SerializableSection(id, prefix, position, headerLevel, title);
                                SerializableConstraints serializableConformanceStatementConstraints =
                                    new SerializableConstraints(segmentConformanceStatements, id, "", "",
                                        "ConformanceStatement");
                                conformanceStatementsSegmentLevelConformanceStatementsSection
                                    .addSection(serializableConformanceStatementConstraints);
                                currentConformanceStatementPosition += 1;
                                segmentLevelConformanceStatementSection.addSection(
                                    conformanceStatementsSegmentLevelConformanceStatementsSection);
                            }
                            if (segmentPredicates.size() > 0) {
                                id = UUID.randomUUID().toString();
                                position = String.valueOf(currentPredicatePosition);
                                prefix = segmentLevelPredicatesSection.getPrefix() + "." + currentPredicatePosition;
                                headerLevel = String.valueOf(5);
                                title = serializableSegment.getSegment().getName() + " - " + serializableSegment.getSegment().getDescription();
                                SerializableSection
                                    predicatesSegmentLevelConformanceStatementsSection =
                                    new SerializableSection(id, prefix, position, headerLevel, title);
                                SerializableConstraints serializablePredicateConstraints =
                                    new SerializableConstraints(segmentPredicates, id, "", "",
                                        "ConditionPredicate");
                                predicatesSegmentLevelConformanceStatementsSection
                                    .addSection(serializablePredicateConstraints);
                                currentPredicatePosition += 1;
                                segmentLevelPredicatesSection
                                    .addSection(predicatesSegmentLevelConformanceStatementsSection);
                            }

                        }
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
                        prefix = datatypeLevelConformanceStatementSection.getPrefix()+"."+String.valueOf(currentConformanceStatementPosition);
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
                        prefix = datatypeLevelPredicatesSection.getPrefix() + "." + String.valueOf(currentPredicatePosition);
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

        return conformanceInformationSection;
    }
}
