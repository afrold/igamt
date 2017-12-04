package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CodeUsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator.SegmentLinkComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableCompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableDatatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableMetadata;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSections;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DatatypeLibrarySerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DatatypeSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.IGDocumentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.MessageSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SegmentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeCompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeDatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeSegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeTableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Document;

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
public class SerializationServiceImpl implements SerializationService {

  SerializationUtil serializationUtil;

  SerializeMessageService serializeMessageService;

  SerializeSegmentService serializeSegmentService;

  SerializeDatatypeService serializeDatatypeService;

  SerializeCompositeProfileService serializeCompositeProfileService;

  SerializeProfileComponentService serializeProfileComponentService;

  SerializeTableService serializeTableService;

  SegmentService segmentService;

  CompositeProfileService compositeProfileService;

  TableService tableService;

  DatatypeService datatypeService;

  private ExportConfig exportConfig;

  private List<SegmentLink> bindedSegments;

  private List<DatatypeLink> bindedDatatypes;

  private Set<String> bindedTables;

  private List<CompositeProfile> compositeProfiles;

  private List<TableLink> unbindedTables;

  private Messages igDocumentMessages;

  static final Logger logger = LoggerFactory.getLogger(SerializationService.class);

  private List<String> bindedDatatypesId;

  private boolean doFilterValueSets = true;
  
  public SerializationServiceImpl(ApplicationContext applicationContext) {
	super();
	serializeCompositeProfileService = applicationContext.getBean(SerializeCompositeProfileService.class);
	serializationUtil = applicationContext.getBean(SerializationUtil.class);
	serializeMessageService = applicationContext.getBean(SerializeMessageService.class);
	serializeSegmentService = applicationContext.getBean(SerializeSegmentService.class);
	serializeDatatypeService = applicationContext.getBean(SerializeDatatypeService.class);
	serializeCompositeProfileService = applicationContext.getBean(SerializeCompositeProfileService.class);
	serializeProfileComponentService = applicationContext.getBean(SerializeProfileComponentService.class);
	serializeTableService = applicationContext.getBean(SerializeTableService.class);
	segmentService = applicationContext.getBean(SegmentService.class);
	compositeProfileService = applicationContext.getBean(CompositeProfileService.class);
	tableService = applicationContext.getBean(TableService.class);
	datatypeService = applicationContext.getBean(DatatypeService.class);
  }

@Override
  public Document serializeDatatypeLibrary(DatatypeLibraryDocument datatypeLibraryDocument,
      ExportConfig exportConfig) throws SerializationException {
    try {
      this.exportConfig = exportConfig;
      this.unbindedTables = null;
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
      this.bindedDatatypes = new ArrayList<>(datatypeLibraryDocument.getDatatypeLibrary().getChildren());
      this.bindedTables = new HashSet<>();
      for (TableLink tableLink : datatypeLibraryDocument.getTableLibrary().getChildren()) {
        doBindTable(tableLink.getId());
      }

      int datatypeSectionPosition = 1;
      if (datatypeLibraryDocument.getMetaData().getDescription() != null && !datatypeLibraryDocument
          .getMetaData().getDescription().trim().isEmpty()) {
        SerializableSection descriptionSection =
            new SerializableSection("descriptionSection", "1", "1", "1", "Description");
        descriptionSection.addSectionContent(datatypeLibraryDocument.getMetaData().getDescription());
        serializableSections.addSection(descriptionSection);
        datatypeSectionPosition++;
      }
      SerializableSection datatypeLibrarySection =
          new SerializableSection("datatypeLibrarySection", String.valueOf(datatypeSectionPosition),
              String.valueOf(datatypeSectionPosition), "1", "Datatype Library");
      SerializableSection datatypeSection =
          this.serializeDatatypes(datatypeLibraryDocument.getDatatypeLibrary(), 1, true);
      // datatypeSection.setTitle("Data Types");
      // SerializableSection valueSetsSection =
      // this.serializeValueSets(datatypeLibraryDocument.getTableLibrary(),2);
      // valueSetsSection.setTitle("Value Sets");
      datatypeLibrarySection.addSection(datatypeSection);
      // datatypeLibrarySection.addSection(valueSetsSection);
      serializableSections.addSection(datatypeLibrarySection);
      serializableStructure.addSerializableElement(serializableSections);
      return serializableStructure.serializeStructure();
    } catch (Exception e){
      throw new DatatypeLibrarySerializationException(e,datatypeLibraryDocument.getId());
    }
  }

  @Override
  public Document serializeElement(SerializableElement element) throws SerializationException {
    SerializableStructure serializableStructure = new SerializableStructure();
    serializableStructure.addSerializableElement(element);
    return serializableStructure.serializeStructure();
  }

  @Override
  public Document serializeIGDocument(IGDocument igDocument,
      SerializationLayout serializationLayout, ExportConfig exportConfig) throws SerializationException {
    try {
      this.exportConfig = exportConfig;
      igDocumentMessages = igDocument.getProfile().getMessages();
      this.bindedDatatypes = new ArrayList<>();
      this.bindedDatatypesId = new ArrayList<>();
      if (igDocument.getProfile().getTableLibrary().getExportConfig() != null
          && igDocument.getProfile().getTableLibrary().getExportConfig().getInclude() != null) {
        this.bindedTables = igDocument.getProfile().getTableLibrary().getExportConfig().getInclude();
        this.doFilterValueSets = false;
      } else {
        bindedTables = new HashSet<>();
      }
      this.bindedSegments = new ArrayList<>();
      this.unbindedTables = new ArrayList<>(igDocument.getProfile().getTableLibrary().getTables());
      this.compositeProfiles = new ArrayList<>();
      if (igDocument.getProfile().getCompositeProfiles() != null && !igDocument.getProfile().getCompositeProfiles().getChildren().isEmpty() && exportConfig.isIncludeCompositeProfileTable()) {
        for (CompositeProfileStructure compositeProfileStructure : igDocument.getProfile().getCompositeProfiles().getChildren()) {
          CompositeProfile compositeProfile = null;
          try {
            compositeProfile = compositeProfileService.buildCompositeProfile(compositeProfileStructure);
          } catch (Exception e) {
            logger.error("Unable to build the composite profile from the structure " + compositeProfileStructure.getName());
          }
          if (compositeProfile != null) {
            compositeProfiles.add(compositeProfile);
            for (SegmentRefOrGroup segmentRefOrGroup : compositeProfile.getChildren()) {
              if (ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), this.exportConfig.getSegmentORGroupsCompositeProfileExport())) {
                identifyBindedItemsFromSegmentOrGroup(segmentRefOrGroup, compositeProfile);
              } else {
                identifyUnbindedValueSetsFromSegmentOrGroup(segmentRefOrGroup, compositeProfile);
              }
            }
          }
        }
      }
      for (Message message : igDocument.getProfile().getMessages().getChildren()) {
        for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
          if (ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), this.exportConfig.getSegmentORGroupsMessageExport())) {
            identifyBindedItemsFromSegmentOrGroup(segmentRefOrGroup);
          } else {
            identifyUnbindedValueSetsFromSegmentOrGroup(segmentRefOrGroup, null);
          }
        }
      }
      // IGDocument igDocument = filterIgDocumentMessages(originIgDocument, exportConfig);
      SerializableStructure serializableStructure = new SerializableStructure();
      igDocument.getMetaData().setHl7Version(igDocument.getProfile().getMetaData().getHl7Version());
      SerializableMetadata serializableMetadata =
          new SerializableMetadata(igDocument.getMetaData(), igDocument.getDateUpdated());
      serializableStructure.addSerializableElement(serializableMetadata);
      SerializableSections serializableSections = new SerializableSections();
      String prefix = "";
      Integer depth = 1;
      if (!serializationLayout.equals(SerializationLayout.TABLES)) {
        serializationUtil.setSectionsPrefixes(igDocument.getChildSections(), prefix, depth,
            serializableSections.getRootSections());
      }
      Profile profile = igDocument.getProfile();
      // Create base section node for the profile serialization
      String id = profile.getId();
      String position = String.valueOf(igDocument.getChildSections().size() + 1);
      // String position = String.valueOf(profile.getSectionPosition());
      prefix = String.valueOf(profile.getSectionPosition() + 1);
      String headerLevel = String.valueOf(1);
      String title = "";
      if (profile.getMessages().getSectionTitle() != null) {
        title = profile.getSectionTitle();
      }
      SerializableSection profileSection =
          new SerializableSection(id, prefix, position, headerLevel, title);
      if (profile.getSectionContents() != null && !profile.getSectionContents().isEmpty()) {
        profileSection.addSectionContent(serializationUtil.cleanRichtext(profile.getSectionContents()));
      }
      if (profile.getUsageNote() != null && !profile.getUsageNote().isEmpty()) {
        nu.xom.Element textElement = new nu.xom.Element("Text");
        if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
          nu.xom.Element usageNoteElement = new nu.xom.Element("UsageNote");
          usageNoteElement.appendChild(serializationUtil.cleanRichtext(profile.getUsageNote()));
          textElement.appendChild(usageNoteElement);
        }
        serializableSections.getRootSections().appendChild(textElement);
      }
      int currentPosition = 1;
      // Profile Component serialization
      if (exportConfig.isIncludeProfileComponentTable()) {
        SerializableSection profileComponentSection =
            this.serializeProfileComponent(profile.getProfileComponentLibrary(), currentPosition);
        if (profileComponentSection != null) {
          profileSection.addSection(profileComponentSection);
          currentPosition++;
        }
      }

      // Message Serialization
      SerializableSection messageSection = this.serializeMessages(profile, serializationLayout,
          igDocument.getMetaData().getHl7Version(), currentPosition);
      if (exportConfig.isIncludeMessageTable() && messageSection != null) {
        profileSection.addSection(messageSection);
        currentPosition++;
      }

      // Composite profiles serialization
      SerializableSection compositeProfilesSection = null;
      if (exportConfig.isIncludeCompositeProfileTable()){
    	  compositeProfilesSection =
	          this.serializeCompositeProfiles(profile, serializationLayout, igDocument.getMetaData().getHl7Version(), currentPosition);
	      if (compositeProfilesSection != null) {
	        profileSection.addSection(compositeProfilesSection);
	        currentPosition++;
	      }
      }

      // Segments serialization
      UsageConfig fieldsUsageConfig = exportConfig.getFieldsExport();
      SerializableSection segmentsSection =
          this.serializeSegments(profile, fieldsUsageConfig, serializationLayout, currentPosition,
              exportConfig.isDuplicateOBXDataTypeWhenFlavorNull());
      if (exportConfig.isIncludeSegmentTable() && !serializationLayout.equals(SerializationLayout.PROFILE) && segmentsSection != null) {
        profileSection.addSection(segmentsSection);
        currentPosition++;
      }

      // Datatypes serialization
      boolean serializeMaster = true;
      if (serializationLayout.equals(SerializationLayout.PROFILE)) {
        serializeMaster = false;
      }
      SerializableSection datatypeSection =
          this.serializeDatatypes(profile.getDatatypeLibrary(), currentPosition, serializeMaster);
      if (exportConfig.isIncludeDatatypeTable() && datatypeSection != null) {
        profileSection.addSection(datatypeSection);
        currentPosition++;
      }

      // Value sets serialization
      SerializableSection valueSetsSection =
          this.serializeValueSets(profile.getTableLibrary(), currentPosition);
      if (exportConfig.isIncludeValueSetsTable() && valueSetsSection != null) {
        profileSection.addSection(valueSetsSection);
        currentPosition++;
      }
      List<SerializableSection> compositeProfileSections = null;
      if (compositeProfilesSection != null) {
        compositeProfileSections = compositeProfilesSection.getSerializableSectionList();
      }
      SerializableSection constraintInformationSection =
          this.serializeConstraints(profile, messageSection.getSerializableSectionList(),
              compositeProfileSections, segmentsSection.getSerializableSectionList(),
              datatypeSection.getSerializableSectionList(), currentPosition);
      if (constraintInformationSection != null) {
        profileSection.addSection(constraintInformationSection);
        currentPosition++;
      }


      serializableSections.addSection(profileSection);
      serializableStructure.addSerializableElement(serializableSections);
      return serializableStructure.serializeStructure();
    } catch (Exception e){
      throw new IGDocumentSerializationException(e,igDocument.getId());
    }
  }

  private SerializableSection serializeProfileComponent(
      ProfileComponentLibrary profileComponentLibrary, Integer sectionPosition) {
    if (profileComponentLibrary.getChildren() != null
        && !profileComponentLibrary.getChildren().isEmpty()) {
      String id = profileComponentLibrary.getId();
      String position, prefix;
      if (profileComponentLibrary.getSectionPosition() != null) {
        position = String.valueOf(profileComponentLibrary.getSectionPosition());
        prefix = String.valueOf(sectionPosition + 1) + "."
            + String.valueOf(profileComponentLibrary.getSectionPosition() + 1);
      } else {
        position = String.valueOf(sectionPosition);
        prefix = String.valueOf(sectionPosition);
      }
      String headerLevel = String.valueOf(2);
      String title = "";
      if (profileComponentLibrary.getSectionTitle() != null) {
        title = profileComponentLibrary.getSectionTitle();
      } else {
        title = "Profile Components";
      }
      SerializableSection profileComponentSection =
          new SerializableSection(id, prefix, position, headerLevel, title);
      if (profileComponentLibrary.getSectionContents() != null
          && !profileComponentLibrary.getSectionContents().isEmpty()) {
        profileComponentSection.addSectionContent(
            serializationUtil.cleanRichtext(profileComponentLibrary.getSectionContents()));
      }
      int currentPosition = 1;
      for (ProfileComponentLink profileComponentLink : profileComponentLibrary.getChildren()) {
        SerializableSection serializableProfileComponentSection =
            serializeProfileComponentService.serializeProfileComponent(profileComponentLink,
                currentPosition, exportConfig.getProfileComponentItemsExport());
        if (serializableProfileComponentSection != null) {
          profileComponentSection.addSection(serializableProfileComponentSection);
          currentPosition++;
        }
      }
      if (profileComponentSection != null) {
        return profileComponentSection;
      }
    }
    return null;
  }

  private IGDocument filterIgDocumentMessages(IGDocument igDocument, ExportConfig exportConfig) {
    if (exportConfig == null) {
      return igDocument;
    } else {
      Profile profile = igDocument.getProfile();
      // Filter messages' segments and groups
      Messages messages = profile.getMessages();
      UsageConfig segmentORGroupsMessageUsageConfig =
          exportConfig.getSegmentORGroupsMessageExport();
      UsageConfig segmentORGroupsCompositeProfileUsageConfig =
          exportConfig.getSegmentORGroupsCompositeProfileExport();
      for (Message message : messages.getChildren()) {
        List<SegmentRefOrGroup> finalSegmentRefOrGroupList = new ArrayList<>();
        for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
          SegmentRefOrGroup finalSegmentRefOrGroup =
              filterSegmentRefOrGroup(segmentRefOrGroup, segmentORGroupsMessageUsageConfig);
          if (finalSegmentRefOrGroup != null) {
            finalSegmentRefOrGroupList.add(finalSegmentRefOrGroup);
          }
        }
        message.setChildren(finalSegmentRefOrGroupList);
      }
      if (this.compositeProfiles != null && !this.compositeProfiles.isEmpty()) {
        for (CompositeProfile compositeProfile : this.compositeProfiles) {
          List<SegmentRefOrGroup> finalSegmentRefOrGroupList = new ArrayList<>();
          for (SegmentRefOrGroup segmentRefOrGroup : compositeProfile.getChildren()) {
            SegmentRefOrGroup finalSegmentRefOrGroup = filterSegmentRefOrGroup(segmentRefOrGroup,
                segmentORGroupsCompositeProfileUsageConfig);
            if (finalSegmentRefOrGroup != null) {
              finalSegmentRefOrGroupList.add(finalSegmentRefOrGroup);
            }
          }
          compositeProfile.setChildren(finalSegmentRefOrGroupList);
        }
      }
      return igDocument;
    }
  }

  private SegmentRefOrGroup filterSegmentRefOrGroup(SegmentRefOrGroup segmentRefOrGroup,
      UsageConfig segmentORGroupsUsageConfig) {
    if (segmentRefOrGroup instanceof SegmentRef) {
      if (ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), segmentORGroupsUsageConfig)) {
        return segmentRefOrGroup;
      }
    } else if (segmentRefOrGroup instanceof Group) {
      Group group = (Group) segmentRefOrGroup;
      if (ExportUtil.diplayUsage(group.getUsage(), segmentORGroupsUsageConfig)) {
        List<SegmentRefOrGroup> toBeRemovedList = new ArrayList<>();
        for (SegmentRefOrGroup groupSegmentRefOrGroup : group.getChildren()) {
          if (filterSegmentRefOrGroup(groupSegmentRefOrGroup, segmentORGroupsUsageConfig) == null) {
            toBeRemovedList.add(groupSegmentRefOrGroup);
          }
        }
        for (SegmentRefOrGroup toBeRemoved : toBeRemovedList) {
          group.getChildren().remove(toBeRemoved);
        }
        return segmentRefOrGroup;
      }
    }
    return null;
  }

  private SerializableSection serializeValueSets(TableLibrary tableLibrary,
      Integer sectionPosition) throws TableSerializationException {
    String id = tableLibrary.getId();
    String position, prefix;
    if (tableLibrary.getSectionPosition() != null) {
      prefix = String.valueOf(sectionPosition + 1) + "."
          + String.valueOf(tableLibrary.getSectionPosition() + 1);
    } else {
      prefix = String.valueOf(sectionPosition);
    }
    position = String.valueOf(sectionPosition);
    String headerLevel = String.valueOf(2);
    String title = "";
    if (tableLibrary.getSectionTitle() != null) {
      title = tableLibrary.getSectionTitle();
    }
    SerializableSection valueSetsSection =
        new SerializableSection(id, prefix, position, headerLevel, title);
    if (tableLibrary.getSectionContents() != null && !tableLibrary.getSectionContents().isEmpty()) {
      valueSetsSection
          .addSectionContent(serializationUtil.cleanRichtext(tableLibrary.getSectionContents()));
    }
    List<TableLink> tableLinkList = new ArrayList<>(tableLibrary.getChildren());
    Collections.sort(tableLinkList);
    CodeUsageConfig valueSetCodesUsageConfig = this.exportConfig.getCodesExport();
    if (bindedTables != null && !bindedTables.isEmpty()) {
      for (String tableId : bindedTables) {
        TableLink tableLink = findTableLinkById(tableId, tableLinkList);
        if (tableLink != null) {
          SerializableTable serializableTable = serializeTableService.serializeTable(tableLink,
              prefix + "." + String.valueOf(tableLinkList.indexOf(tableLink) + 1),
              tableLinkList.indexOf(tableLink), valueSetCodesUsageConfig,
              exportConfig.getValueSetsMetadata(), exportConfig.getMaxCodeNumber(),tableLibrary.getCodePresence());
          valueSetsSection.addSection(serializableTable);
        }
      }
    }
    if (doFilterValueSets && unbindedTables != null && !unbindedTables.isEmpty() && (exportConfig.isUnboundCustom() || exportConfig.isUnboundCustom())) {
      for (TableLink tableLink : this.unbindedTables) {
	      Table table = tableService.findById(tableLink.getId());
	      if (table != null && ExportUtil.displayUnbindedTable(exportConfig, table)) {
	        SerializableTable serializableTable = serializeTableService.serializeTable(tableLink,
	            prefix + "." + String.valueOf(tableLinkList.indexOf(tableLink) + 1),
	            tableLinkList.indexOf(tableLink), valueSetCodesUsageConfig,
	            exportConfig.getValueSetsMetadata(), exportConfig.getMaxCodeNumber(),tableLibrary.getCodePresence());
	        valueSetsSection.addSection(serializableTable);
	      }
      }
    }
    return valueSetsSection;
  }

  private TableLink findTableLinkById(String tableId, List<TableLink> tableLinkList) {
    if (tableLinkList != null && !tableLinkList.isEmpty()) {
      for (TableLink tableLink : tableLinkList) {
        if (tableLink != null && tableLink.getId() != null && tableLink.getId().equals(tableId)) {
          return tableLink;
        }
      }
    }
    return null;
  }

  private SerializableSection serializeDatatypes(DatatypeLibrary datatypeLibrary,
      int sectionPosition, boolean serializeMaster) throws DatatypeSerializationException {
    String id = datatypeLibrary.getId();
    String position, prefix;
    if (datatypeLibrary.getSectionPosition() != null) {
      prefix = String.valueOf(sectionPosition) + "."
          + String.valueOf(datatypeLibrary.getSectionPosition() + 1);
    } else {
      prefix = String.valueOf(sectionPosition);
    }
    position = String.valueOf(sectionPosition);
    String headerLevel = String.valueOf(2);
    String title = "";
    if (datatypeLibrary.getSectionTitle() != null) {
      title = datatypeLibrary.getSectionTitle();
    }
    SerializableSection datatypeSection =
        new SerializableSection(id, prefix, position, headerLevel, title);
    if (datatypeLibrary.getSectionContents() != null
        && !datatypeLibrary.getSectionContents().isEmpty()) {
      datatypeSection
          .addSectionContent(serializationUtil.cleanRichtext(datatypeLibrary.getSectionContents()));
    }
    List<DatatypeLink> datatypeLinkList = new ArrayList<>(datatypeLibrary.getChildren());
    Collections.sort(datatypeLinkList);
    UsageConfig datatypeComponentsUsageConfig = this.exportConfig.getComponentExport();
    if (bindedDatatypes != null && !bindedDatatypes.isEmpty()) {
      Iterator<DatatypeLink> itr = bindedDatatypes.iterator();
      while (itr.hasNext()) {
        DatatypeLink entry = itr.next();
        if (entry.getName().toLowerCase().equals("varies") && !exportConfig.isIncludeVaries()) {
          itr.remove();
        } else {
          CompositeProfile compositeProfile = getDatatypeCompositeProfile(entry);
          SerializableDatatype serializableDatatype = null;
          if (compositeProfile != null) {
            serializableDatatype = serializeDatatypeService.serializeDatatype(entry,
                prefix + "." + String.valueOf(datatypeLinkList.indexOf(entry) + 1),
                datatypeLinkList.indexOf(entry), datatypeComponentsUsageConfig,
                compositeProfile.getDatatypesMap());
          } else {
            serializableDatatype = serializeDatatypeService.serializeDatatype(entry,
                prefix + "." + String.valueOf(datatypeLinkList.indexOf(entry) + 1),
                datatypeLinkList.indexOf(entry), datatypeComponentsUsageConfig);
          }
          // This "if" is only useful if we want to display only user datatypes
          // if(serializeMaster||!(serializableDatatype.getDatatype().getScope().equals(Constant.SCOPE.HL7STANDARD))){
          if (serializableDatatype != null) {
            datatypeSection.addSection(serializableDatatype);
          }

        }

      }

    }
    return datatypeSection;
  }



  private SerializableSection serializeMessages(Profile profile,
      SerializationLayout serializationLayout, String hl7Version, int position) throws
      MessageSerializationException {
    String id = profile.getMessages().getId();
    String sectionPosition = String.valueOf(position);
    String prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
        + String.valueOf(profile.getMessages().getSectionPosition() + 1);
    String headerLevel = String.valueOf(2);
    String title = "";
    if (profile.getMessages().getSectionTitle() != null) {
      title = profile.getMessages().getSectionTitle();
    }
    SerializableSection messageSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
    if (profile.getMessages().getSectionContents() != null
        && !profile.getMessages().getSectionContents().isEmpty()) {
      messageSection.addSectionContent(
          serializationUtil.cleanRichtext(profile.getMessages().getSectionContents()));
    }
    for (Message message : this.igDocumentMessages.getChildren()) {
      SerializableMessage serializableMessage = serializeMessageService.serializeMessage(message,
          prefix, String.valueOf(3), serializationLayout, hl7Version, this.exportConfig);
      messageSection.addSection(serializableMessage);
    }
    return messageSection;
  }

  private SerializableSection serializeCompositeProfiles(Profile profile,
      SerializationLayout serializationLayout, String hl7Version, int position)
      throws SerializationException {
    if (profile.getCompositeProfiles() != null
        && !profile.getCompositeProfiles().getChildren().isEmpty()) {
      String id = profile.getCompositeProfiles().getId();
      String sectionPosition = String.valueOf(position);
      String prefix = "";
      String headerLevel = String.valueOf(2);
      String title = "";
      if (profile.getCompositeProfiles().getSectionTitle() != null) {
        title = profile.getCompositeProfiles().getSectionTitle();
      } else {
        title = "Composite Profiles";
      }
      SerializableSection compositeProfileSection =
          new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
      if (profile.getCompositeProfiles().getSectionContents() != null
          && !profile.getCompositeProfiles().getSectionContents().isEmpty()) {
        compositeProfileSection.addSectionContent(
            serializationUtil.cleanRichtext(profile.getCompositeProfiles().getSectionContents()));
      }
      for (CompositeProfile compositeProfile : this.compositeProfiles) {
        SerializableCompositeProfile serializableCompositeProfile =
            serializeCompositeProfileService.serializeCompositeProfile(compositeProfile, prefix,
                serializationLayout, hl7Version, this.exportConfig);
        if (serializableCompositeProfile != null) {
          compositeProfileSection.addSection(serializableCompositeProfile);
        }
      }
      return compositeProfileSection;
    }
    return null;
  }

  private void identifyBindedItemsFromSegmentOrGroup(SegmentRefOrGroup segmentRefOrGroup) {
    identifyBindedItemsFromSegmentOrGroup(segmentRefOrGroup, null);
  }

  private void identifyBindedItemsFromSegmentOrGroup(SegmentRefOrGroup segmentRefOrGroup,
      CompositeProfile compositeProfile) {
    if (segmentRefOrGroup instanceof SegmentRef) {
      if (ExportUtil.diplayUsage(segmentRefOrGroup.getUsage(), exportConfig.getSegmentsExport())) {
        if (!this.bindedSegments.contains(((SegmentRef) segmentRefOrGroup).getRef())) {
          this.bindedSegments.add(((SegmentRef) segmentRefOrGroup).getRef());
        }
      }
      Segment segment = null;
      if (compositeProfile != null) {
        segment = compositeProfile.getSegmentsMap()
            .get(((SegmentRef) segmentRefOrGroup).getRef().getId());
      } else {
        segment = segmentService.findById(((SegmentRef) segmentRefOrGroup).getRef().getId());
      }
      if (segment != null) {
        Map<String, Usage> fieldLocationUsageMap = new HashMap<>();
        for (Field field : segment.getFields()) {
          fieldLocationUsageMap.put(String.valueOf(field.getPosition()), field.getUsage());
          if (!bindedDatatypes.contains(field.getDatatype())
              && ExportUtil.diplayUsage(field.getUsage(), this.exportConfig.getDatatypesExport())) {
            doBindDatatype(field.getDatatype(), null);
          }
        }
        for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : segment
            .getValueSetBindings()) {
          if (valueSetOrSingleCodeBinding instanceof ValueSetBinding) {
            if (fieldLocationUsageMap.containsKey(valueSetOrSingleCodeBinding.getLocation())) {
              if (ExportUtil.diplayUsage(
                  fieldLocationUsageMap.get(valueSetOrSingleCodeBinding.getLocation()),
                  this.exportConfig.getValueSetsExport())) {
                doBindTable(valueSetOrSingleCodeBinding.getTableId());
              }
            }

            removeFromUnbindedTables(valueSetOrSingleCodeBinding.getTableId());
          }
        }
        if (segment.getDynamicMappingDefinition() != null
            && segment.getDynamicMappingDefinition().getDynamicMappingItems() != null) {
          for (DynamicMappingItem dynamicMappingItem : segment.getDynamicMappingDefinition()
              .getDynamicMappingItems()) {
            if (dynamicMappingItem.getDatatypeId() != null) {
              this.doBindDatatype(dynamicMappingItem.getDatatypeId());
            }
          }
        }

        if (segment.getCoConstraintsTable() != null
            && segment.getCoConstraintsTable().getThenMapData() != null) {
          for (String key : segment.getCoConstraintsTable().getThenMapData().keySet()) {
            if (segment.getCoConstraintsTable().getThenMapData().get(key) != null) {
              for (CoConstraintTHENColumnData coConstraintTHENColumnData : segment
                  .getCoConstraintsTable().getThenMapData().get(key)) {
                if (coConstraintTHENColumnData != null) {
                  if (coConstraintTHENColumnData.getDatatypeId() != null) {
                    doBindDatatype(coConstraintTHENColumnData.getDatatypeId());
                  } else if (coConstraintTHENColumnData.getValueSets() != null
                      && !coConstraintTHENColumnData.getValueSets().isEmpty()) {
                    for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
                      doBindTable(valueSetData.getTableId());
                    }
                  }
                }
              }
            }
          }
        }
      }
    } else if (segmentRefOrGroup instanceof Group) {
      for (SegmentRefOrGroup children : ((Group) segmentRefOrGroup).getChildren()) {
        if ((compositeProfile != null && ExportUtil.diplayUsage(children.getUsage(),
            this.exportConfig.getSegmentORGroupsMessageExport()))
            || (compositeProfile == null && ExportUtil.diplayUsage(children.getUsage(),
                this.exportConfig.getSegmentORGroupsCompositeProfileExport()))) {
          identifyBindedItemsFromSegmentOrGroup(children, compositeProfile);
        } else {
          identifyUnbindedValueSetsFromSegmentOrGroup(children, compositeProfile);
        }
      }
    }
  }

  private void bindTablesFromValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      Map<String, Usage> componentLocationUsageMap) {
    for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : valueSetBindings) {
      if (valueSetOrSingleCodeBinding instanceof ValueSetBinding) {
        if (componentLocationUsageMap.containsKey(valueSetOrSingleCodeBinding.getLocation())) {
          if (ExportUtil.diplayUsage(
              componentLocationUsageMap.get(valueSetOrSingleCodeBinding.getLocation()),
              this.exportConfig.getValueSetsExport())) {
            doBindTable(valueSetOrSingleCodeBinding.getTableId());
          }
        }
        removeFromUnbindedTables(valueSetOrSingleCodeBinding.getTableId());
      }
    }
  }

  private void doBindTable(String tableId) {
    if (this.doFilterValueSets && !bindedTables.contains(tableId)) {
      bindedTables.add(tableId);
    }
  }

  private void identifyBindedItemsFromDatatype(Datatype datatype) {
    Map<String, Usage> componentLocationUsageMap = new HashMap<>();
    for (Component component : datatype.getComponents()) {
      if (component != null && component.getDatatype() != null) {
        componentLocationUsageMap.put(String.valueOf(component.getPosition()),
            component.getUsage());
        if (ExportUtil.diplayUsage(component.getUsage(), exportConfig.getDatatypesExport())) {
          doBindDatatype(component.getDatatype(), null);
        }
      }
    }
    bindTablesFromValueSetBindings(datatype.getValueSetBindings(), componentLocationUsageMap);
  }

  private void doBindDatatype(DatatypeLink datatypeLink, Datatype datatype) {
    if (!this.bindedDatatypesId.contains(datatypeLink.getId())) {
      this.bindedDatatypesId.add(datatypeLink.getId());
      this.bindedDatatypes.add(datatypeLink);
      if (datatype == null) {
        datatype = datatypeService.findById(datatypeLink.getId());
      }
      if (datatype != null) {
        identifyBindedItemsFromDatatype(datatype);
      }
    }
  }

  private void doBindDatatype(String datatypeId) {
    Datatype datatype = datatypeService.findById(datatypeId);
    DatatypeLink datatypeLink =
        new DatatypeLink(datatype.getId(), datatype.getName(), datatype.getExt());
    doBindDatatype(datatypeLink, datatype);
  }

  private void identifyUnbindedValueSetsFromSegmentOrGroup(SegmentRefOrGroup segmentRefOrGroup,
      CompositeProfile compositeProfile) {
    if (segmentRefOrGroup instanceof SegmentRef) {
      Segment segment = null;
      if (compositeProfile != null) {
        segment = compositeProfile.getSegmentsMap()
            .get(((SegmentRef) segmentRefOrGroup).getRef().getId());
      } else {
        segment = segmentService.findById(((SegmentRef) segmentRefOrGroup).getRef().getId());
      }
      if (segment != null) {
        for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : segment
            .getValueSetBindings()) {
          if (valueSetOrSingleCodeBinding instanceof ValueSetBinding) {
            removeFromUnbindedTables(valueSetOrSingleCodeBinding.getTableId());
          }
        }
      }
    } else if (segmentRefOrGroup instanceof Group) {
      for (SegmentRefOrGroup children : ((Group) segmentRefOrGroup).getChildren()) {
        identifyUnbindedValueSetsFromSegmentOrGroup(children, compositeProfile);
      }
    }
  }


  private void removeFromUnbindedTables(String tableId) {
    if (tableId != null) {
      for (TableLink tableLink : this.unbindedTables) {
        if (tableLink.getId() != null && tableLink.getId().equals(tableId)) {
          this.unbindedTables.remove(tableLink);
          break;
        }
      }
    }
  }

  private SerializableSection serializeSegments(Profile profile, UsageConfig fieldsUsageConfig,
      SerializationLayout serializationLayout, int position,
      Boolean duplicateOBXDataTypeWhenFlavorNull) throws SegmentSerializationException {
    String id = profile.getSegmentLibrary().getId();
    String sectionPosition = String.valueOf(position);
    String prefix = String.valueOf(profile.getSectionPosition() + 1) + "."
        + String.valueOf(profile.getSegmentLibrary().getSectionPosition() + 1);
    String headerLevel = String.valueOf(2);
    String title = "";
    if (profile.getSegmentLibrary().getSectionTitle() != null) {
      title = profile.getSegmentLibrary().getSectionTitle();
    }
    SerializableSection segmentsSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
    if (profile.getSegmentLibrary().getSectionContents() != null
        && !profile.getSegmentLibrary().getSectionContents().isEmpty()) {
      segmentsSection.addSectionContent(
          serializationUtil.cleanRichtext(profile.getSegmentLibrary().getSectionContents()));
    }
    if (serializationLayout.equals(SerializationLayout.IGDOCUMENT)) {
      this.bindedSegments.sort(new SegmentLinkComparator());
    }
    for (SegmentLink segmentLink : this.bindedSegments) {
      if (segmentLink.getId() != null) {
        CompositeProfile compositeProfile = getSegmentCompositeProfile(segmentLink);
        if (compositeProfile != null) {
          segmentsSection.addSection(serializeSegmentService.serializeSegment(segmentLink,
              prefix + "." + String.valueOf(this.bindedSegments.indexOf(segmentLink) + 1),
              bindedSegments.indexOf(segmentLink), 3, fieldsUsageConfig,
              compositeProfile.getSegmentsMap(), compositeProfile.getDatatypesMap(),
              compositeProfile.getTablesMap(), duplicateOBXDataTypeWhenFlavorNull));
        } else {
          segmentsSection.addSection(serializeSegmentService.serializeSegment(segmentLink,
              prefix + "." + String.valueOf(bindedSegments.indexOf(segmentLink) + 1),
              bindedSegments.indexOf(segmentLink), 3, fieldsUsageConfig,
              duplicateOBXDataTypeWhenFlavorNull));
        }
      }
    }
    return segmentsSection;
  }

  private CompositeProfile getSegmentCompositeProfile(SegmentLink segmentLink) {
    if (this.compositeProfiles != null && !this.compositeProfiles.isEmpty()) {
      for (CompositeProfile compositeProfile : this.compositeProfiles) {
        if (compositeProfile.getSegmentsMap().containsKey(segmentLink.getId())) {
          return compositeProfile;
        }
      }
    }
    return null;
  }

  private CompositeProfile getDatatypeCompositeProfile(DatatypeLink datatypeLink) {
    if (this.compositeProfiles != null && !this.compositeProfiles.isEmpty()) {
      for (CompositeProfile compositeProfile : this.compositeProfiles) {
        if (compositeProfile.getDatatypesMap().containsKey(datatypeLink.getId())) {
          return compositeProfile;
        }
      }
    }
    return null;
  }

  private CompositeProfile getTableCompositeProfile(String tableId) {
    if (this.compositeProfiles != null && !this.compositeProfiles.isEmpty()) {
      for (CompositeProfile compositeProfile : this.compositeProfiles) {
        if (compositeProfile.getTablesMap().containsKey(tableId)) {
          return compositeProfile;
        }
      }
    }
    return null;
  }

  private SerializableSection serializeConstraints(Profile profile,
      List<SerializableSection> messages, List<SerializableSection> compositeProfile,
      List<SerializableSection> segments, List<SerializableSection> datatypes, int position) {

    String id = UUID.randomUUID().toString();
    String prefix = String.valueOf(profile.getSectionPosition() + 1) + "." + String.valueOf(5);
    String headerLevel = String.valueOf(2);
    String title = "Conformance information";
    SerializableSection conformanceInformationSection =
        new SerializableSection(id, prefix, String.valueOf(position), headerLevel, title);

    id = UUID.randomUUID().toString();
    String sectionPosition = String.valueOf(1);
    prefix = conformanceInformationSection.getPrefix() + "." + String.valueOf(1);
    headerLevel = String.valueOf(3);
    title = "Conformance statements";
    SerializableSection conformanceStatementsSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(2);
    prefix = conformanceInformationSection.getPrefix() + "." + String.valueOf(2);
    headerLevel = String.valueOf(3);
    title = "Conditional predicates";
    SerializableSection conditionalPredicatesSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(1);
    prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(1);
    headerLevel = String.valueOf(4);
    title = "Conformance profile level";
    SerializableSection profileLevelConformanceStatementsSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(1);
    prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(1);
    headerLevel = String.valueOf(4);
    title = "Conformance profile level";
    SerializableSection profileLevelPredicatesSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(1);
    prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(4);
    headerLevel = String.valueOf(4);
    title = "Composite profile level";
    SerializableSection compositeProfileLevelConformanceStatementsSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(1);
    prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(4);
    headerLevel = String.valueOf(4);
    title = "Composite profile level";
    SerializableSection compositeProfilePredicatesSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(2);
    prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(2);
    headerLevel = String.valueOf(4);
    title = "Segment level";
    SerializableSection segmentLevelConformanceStatementSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(2);
    prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(2);
    headerLevel = String.valueOf(4);
    title = "Segment level";
    SerializableSection segmentLevelPredicatesSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(2);
    prefix = conformanceStatementsSection.getPrefix() + "." + String.valueOf(3);
    headerLevel = String.valueOf(4);
    title = "Datatype level";
    SerializableSection datatypeLevelConformanceStatementSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);

    id = UUID.randomUUID().toString();
    sectionPosition = String.valueOf(2);
    prefix = conditionalPredicatesSection.getPrefix() + "." + String.valueOf(3);
    headerLevel = String.valueOf(4);
    title = "Datatype level";
    SerializableSection datatypeLevelPredicatesSection =
        new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
    Integer currentConformanceStatementPosition = 1;
    Integer currentPredicatePosition = 1;
    for (SerializableSection serializableMessageSection : messages) {
      if (serializableMessageSection instanceof SerializableMessage) {
        SerializableMessage serializableMessage = (SerializableMessage) serializableMessageSection;
        id = UUID.randomUUID().toString();
        sectionPosition = String
            .valueOf(((SerializableMessage) serializableMessageSection).getMessage().getPosition());
        prefix = profileLevelConformanceStatementsSection.getPrefix() + "."
            + String.valueOf(currentConformanceStatementPosition);
        headerLevel = String.valueOf(5);
        title = serializableMessage.getMessage().getName();
        SerializableConstraints serializableConformanceStatement =
            serializableMessage.getSerializableConformanceStatements();
        if (serializableConformanceStatement.getConstraints().size() > 0) {
          SerializableSection conformanceStatementsProfileLevelConformanceStatementsSection =
              new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
          serializableConformanceStatement.setTitle("");
          conformanceStatementsProfileLevelConformanceStatementsSection
              .addSection(serializableConformanceStatement);
          profileLevelConformanceStatementsSection
              .addSection(conformanceStatementsProfileLevelConformanceStatementsSection);
          currentConformanceStatementPosition += 1;
        }
        id = UUID.randomUUID().toString();
        sectionPosition = String.valueOf(currentPredicatePosition);
        prefix = profileLevelPredicatesSection.getPrefix() + "."
            + String.valueOf(currentPredicatePosition);
        headerLevel = String.valueOf(5);
        title = serializableMessage.getMessage().getName();
        SerializableConstraints serializablePredicate =
            serializableMessage.getSerializablePredicates();
        if (serializablePredicate.getConstraints().size() > 0) {
          SerializableSection predicatesProfileLevelConformanceStatementsSection =
              new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
          serializablePredicate.setTitle("");
          predicatesProfileLevelConformanceStatementsSection.addSection(serializablePredicate);
          profileLevelPredicatesSection
              .addSection(predicatesProfileLevelConformanceStatementsSection);
        }
      }
    }
    if (compositeProfile != null) {
      for (SerializableSection serializableCompositeProfileSection : compositeProfile) {
        if (serializableCompositeProfileSection instanceof SerializableCompositeProfile) {
          SerializableCompositeProfile serializableCompositeProfile =
              (SerializableCompositeProfile) serializableCompositeProfileSection;
          id = UUID.randomUUID().toString();
          sectionPosition =
              String.valueOf(((SerializableCompositeProfile) serializableCompositeProfileSection)
                  .getCompositeProfile().getPosition());
          prefix = compositeProfileLevelConformanceStatementsSection.getPrefix() + "."
              + String.valueOf(currentConformanceStatementPosition);
          headerLevel = String.valueOf(5);
          title = serializableCompositeProfile.getCompositeProfile().getName();
          SerializableConstraints serializableConformanceStatement =
              serializableCompositeProfile.getSerializableConformanceStatements();
          if (serializableConformanceStatement.getConstraints().size() > 0) {
            SerializableSection conformanceStatementsCompositeProfileLevelConformanceStatementsSection =
                new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
            serializableConformanceStatement.setTitle("");
            conformanceStatementsCompositeProfileLevelConformanceStatementsSection
                .addSection(serializableConformanceStatement);
            compositeProfileLevelConformanceStatementsSection
                .addSection(conformanceStatementsCompositeProfileLevelConformanceStatementsSection);
            currentConformanceStatementPosition += 1;
          }
          id = UUID.randomUUID().toString();
          sectionPosition = String.valueOf(currentPredicatePosition);
          prefix = compositeProfilePredicatesSection.getPrefix() + "."
              + String.valueOf(currentPredicatePosition);
          headerLevel = String.valueOf(5);
          title = serializableCompositeProfile.getCompositeProfile().getName();
          SerializableConstraints serializablePredicate =
              serializableCompositeProfile.getSerializablePredicates();
          if (serializablePredicate.getConstraints().size() > 0) {
            SerializableSection predicatesCompositeProfileLevelConformanceStatementsSection =
                new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
            serializablePredicate.setTitle("");
            predicatesCompositeProfileLevelConformanceStatementsSection
                .addSection(serializablePredicate);
            compositeProfilePredicatesSection
                .addSection(predicatesCompositeProfileLevelConformanceStatementsSection);
          }
        }
      }
    }

    currentConformanceStatementPosition = 1;
    currentPredicatePosition = 1;
    for (SerializableSection serializableSegmentSection : segments) {
      if (serializableSegmentSection != null
          && serializableSegmentSection.getSerializableSectionList().size() > 0) {
        for (SerializableSection serializableSection : serializableSegmentSection
            .getSerializableSectionList()) {
          if (serializableSection != null && serializableSection instanceof SerializableSegment) {
            SerializableSegment serializableSegment = (SerializableSegment) serializableSection;
            if (serializableSegment != null && serializableSegment.getConstraints().size() > 0) {
              List<SerializableConstraint> segmentConformanceStatements = new ArrayList<>();
              List<SerializableConstraint> segmentPredicates = new ArrayList<>();
              for (SerializableConstraint serializableConstraint : serializableSegment
                  .getConstraints()) {
                if (serializableConstraint != null) {
                  if (serializableConstraint.getConstraint() instanceof Predicate) {
                    segmentPredicates.add(serializableConstraint);
                  } else if (serializableConstraint
                      .getConstraint() instanceof ConformanceStatement) {
                    segmentConformanceStatements.add(serializableConstraint);
                  }
                }
              }
              if (segmentConformanceStatements.size() > 0) {
                id = UUID.randomUUID().toString();
                sectionPosition = String.valueOf(currentConformanceStatementPosition);
                prefix = segmentLevelConformanceStatementSection.getPrefix() + "."
                    + currentConformanceStatementPosition;
                headerLevel = String.valueOf(5);
                title = serializableSegment.getSegment().getLabel() + " - "
                    + serializableSegment.getSegment().getDescription();
                SerializableSection conformanceStatementsSegmentLevelConformanceStatementsSection =
                    new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
                SerializableConstraints serializableConformanceStatementConstraints =
                    new SerializableConstraints(segmentConformanceStatements, id, "", "",
                        "ConformanceStatement");
                conformanceStatementsSegmentLevelConformanceStatementsSection
                    .addSection(serializableConformanceStatementConstraints);
                currentConformanceStatementPosition += 1;
                segmentLevelConformanceStatementSection
                    .addSection(conformanceStatementsSegmentLevelConformanceStatementsSection);
              }
              if (segmentPredicates.size() > 0) {
                id = UUID.randomUUID().toString();
                sectionPosition = String.valueOf(currentPredicatePosition);
                prefix = segmentLevelPredicatesSection.getPrefix() + "." + currentPredicatePosition;
                headerLevel = String.valueOf(5);
                title = serializableSegment.getSegment().getLabel() + " - "
                    + serializableSegment.getSegment().getDescription();
                SerializableSection predicatesSegmentLevelConformanceStatementsSection =
                    new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
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
    for (SerializableSection serializableSection : datatypes) {
      if (serializableSection instanceof SerializableDatatype) {
        SerializableDatatype serializableDatatype = (SerializableDatatype) serializableSection;
        if (serializableDatatype.getConstraints().size() > 0) {
          List<SerializableConstraint> datatypeConformanceStatements = new ArrayList<>();
          List<SerializableConstraint> datatypePredicates = new ArrayList<>();
          for (SerializableConstraint serializableConstraint : serializableDatatype
              .getConstraints()) {
            if (serializableConstraint.getConstraint() instanceof Predicate) {
              datatypePredicates.add(serializableConstraint);
            } else if (serializableConstraint.getConstraint() instanceof ConformanceStatement) {
              datatypeConformanceStatements.add(serializableConstraint);
            }
          }
          if (datatypeConformanceStatements.size() > 0) {
            id = UUID.randomUUID().toString();
            sectionPosition = String.valueOf(currentConformanceStatementPosition);
            prefix = datatypeLevelConformanceStatementSection.getPrefix() + "."
                + String.valueOf(currentConformanceStatementPosition);
            headerLevel = String.valueOf(5);
            title = serializableDatatype.getDatatype().getName() + " - "
                + serializableDatatype.getDatatype().getDescription();
            SerializableSection conformanceStatementsDatatypeLevelConformanceStatementsSection =
                new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
            SerializableConstraints serializableConformanceStatementConstraints =
                new SerializableConstraints(datatypeConformanceStatements, id, "", "",
                    "ConformanceStatement");
            conformanceStatementsDatatypeLevelConformanceStatementsSection
                .addSection(serializableConformanceStatementConstraints);
            currentConformanceStatementPosition += 1;
            datatypeLevelConformanceStatementSection
                .addSection(conformanceStatementsDatatypeLevelConformanceStatementsSection);
          }
          if (datatypePredicates.size() > 0) {
            id = UUID.randomUUID().toString();
            sectionPosition = String.valueOf(currentPredicatePosition);
            prefix = datatypeLevelPredicatesSection.getPrefix() + "."
                + String.valueOf(currentPredicatePosition);
            headerLevel = String.valueOf(5);
            title = serializableDatatype.getDatatype().getName() + " - "
                + serializableDatatype.getDatatype().getDescription();
            SerializableSection predicatesDatatypeLevelConformanceStatementsSection =
                new SerializableSection(id, prefix, sectionPosition, headerLevel, title);
            SerializableConstraints serializablePredicateConstraints =
                new SerializableConstraints(datatypePredicates, id, "", "", "ConditionPredicate");
            predicatesDatatypeLevelConformanceStatementsSection
                .addSection(serializablePredicateConstraints);
            currentPredicatePosition += 1;
            datatypeLevelPredicatesSection
                .addSection(predicatesDatatypeLevelConformanceStatementsSection);
          }

        }
      }
    }
    if (this.exportConfig.isIncludeMessageTable()
        && !profileLevelConformanceStatementsSection.getSerializableSectionList().isEmpty()) {
      conformanceStatementsSection.addSection(profileLevelConformanceStatementsSection);
    }
    if (this.exportConfig.isIncludeCompositeProfileTable()
        && !compositeProfileLevelConformanceStatementsSection.getSerializableSectionList()
            .isEmpty()) {
      conformanceStatementsSection.addSection(compositeProfileLevelConformanceStatementsSection);
    }
    if (this.exportConfig.isIncludeSegmentTable()
        && !segmentLevelConformanceStatementSection.getSerializableSectionList().isEmpty()) {
      conformanceStatementsSection.addSection(segmentLevelConformanceStatementSection);
    }
    if (this.exportConfig.isIncludeDatatypeTable()
        && !datatypeLevelConformanceStatementSection.getSerializableSectionList().isEmpty()) {
      conformanceStatementsSection.addSection(datatypeLevelConformanceStatementSection);
    }
    if (this.exportConfig.isIncludeMessageTable()
        && !profileLevelPredicatesSection.getSerializableSectionList().isEmpty()) {
      conditionalPredicatesSection.addSection(profileLevelPredicatesSection);
    }
    if (this.exportConfig.isIncludeCompositeProfileTable()
        && !compositeProfilePredicatesSection.getSerializableSectionList().isEmpty()) {
      conditionalPredicatesSection.addSection(compositeProfilePredicatesSection);
    }
    if (this.exportConfig.isIncludeSegmentTable()
        && !segmentLevelPredicatesSection.getSerializableSectionList().isEmpty()) {
      conditionalPredicatesSection.addSection(segmentLevelPredicatesSection);
    }
    if (this.exportConfig.isIncludeDatatypeTable()
        && !datatypeLevelPredicatesSection.getSerializableSectionList().isEmpty()) {
      conditionalPredicatesSection.addSection(datatypeLevelPredicatesSection);
    }
    if (!conformanceStatementsSection.getSerializableSectionList().isEmpty()) {
      conformanceInformationSection.addSection(conformanceStatementsSection);
    }
    if (!conditionalPredicatesSection.getSerializableSectionList().isEmpty()) {
      conformanceInformationSection.addSection(conditionalPredicatesSection);
    }
    if (!conformanceInformationSection.getSerializableSectionList().isEmpty()) {
      return conformanceInformationSection;
    }
    return null;
  }

  @Override
  public Document serializeDataModel(Object dataModel, String host)
      throws SerializationException {
    SerializableStructure serializableStructure = new SerializableStructure();
    SerializableElement serializableElement = null;
    if (dataModel instanceof Datatype) {
      serializableElement = serializeDatatypeService.serializeDatatype((Datatype) dataModel, host);
    } else if (dataModel instanceof Table) {
      serializableElement = serializeTableService.serializeTable((Table) dataModel);
    } else if (dataModel instanceof Segment) {
      serializableElement = serializeSegmentService.serializeSegment((Segment) dataModel, host);
    } else if (dataModel instanceof Message) {
      serializableElement = serializeMessageService.serializeMessage((Message) dataModel, host);
    } else if (dataModel instanceof ProfileComponent) {
      serializableElement = serializeProfileComponentService
          .serializeProfileComponent((ProfileComponent) dataModel, host);
    }
    if (serializableElement != null) {
      serializableStructure.addSerializableElement(serializableElement);
    }
    return serializableStructure.serializeStructure();
  }
  
  
  
  
  
}
