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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CodeUsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ColumnsConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Comment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeMatrix;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFont;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.NameAndPositionAndPresence;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentAttributes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TargetType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedDataType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBindingStrength;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetMetadataConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VariesMapItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUSERColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUserColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsColumn;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeMatrixRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.NotificationsRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.UnchangedDataRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DeltaService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.TableUpdateStreamException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DataCorrectionSectionPosition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.DynTableDownloadService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.DynTableDownloadServiceImpl;

@Service
public class Bootstrap implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String, ArrayList<List<String>>> DatatypeMap = new HashMap<String, ArrayList<List<String>>>();
    private HashMap<String, Integer> Visited = new HashMap<String, Integer>();
    private boolean needUpdated;

    @Autowired
    ExportConfigRepository exportConfig;

    @Autowired
    ExportFontService exportFontService;

    @Autowired
    ExportFontConfigService exportFontConfigService;

    @Autowired
    UserService userService;
    

    @Autowired
    DynTableDownloadService dynTableDownloadService;
    

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ProfileService profileService;
    @Autowired
    DatatypeMatrixRepository matrix;
    @Autowired
    IGDocumentService documentService;
    @Autowired
    UnchangedDataRepository unchangedData;

    @Autowired
    MessageService messageService;

    @Autowired
    SegmentService segmentService;
    @Autowired
    DatatypeService datatypeService;
    @Autowired
    SegmentLibraryService segmentLibraryService;
    @Autowired
    DatatypeLibraryService datatypeLibraryService;
    @Autowired
    TableLibraryService tableLibraryService;

    @Autowired
    TableService tableService;
    @Autowired
    DataCorrectionSectionPosition dataCorrectionSectionPosition;
    @Autowired
    private ProfileComponentLibraryService profileComponentLibraryService;
    @Autowired
    private ProfileComponentService profileComponentService;
    @Autowired

    private CompositeProfileStructureService compositeProfileStructureService;
    @Autowired
    private TableLibraryRepository tableLibraryRepository;

    @Autowired
    private DatatypeLibraryRepository daatypeLibraryRepository;

    @Autowired
    private IGDocumentService iGDocumentService;

    @Autowired
    private DeltaService deltaService;

    @Autowired
    private ExportConfigRepository exportConfigRepository;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private ProfileSerialization profileSerialization;

    /*
     * 
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

	// Carefully use this. It will delete all of existing IGDocuments and
	// make new ones converted from the "igdocumentPreLibHL7",
	// "igdocumentPreLibPRELOADED" , and ""igdocumentPreLibUSER"
	// new IGDocumentConverterFromOldToNew().convert();
	// new DataCorrection().updateSegment();
	// new DataCorrection().updateDatatype();
	// new DataCorrection().updateSegmentLibrary();
	// new DataCorrection().updateDatatypeLibrary();
	// new DataCorrection().updateTableLibrary();
	// new DataCorrection().updateMessage();
	//
	// dataCorrectionSectionPosition.resetSectionPositions();
	// new DataCorrection().updateValueSetForSegment();
	// new DataCorrection().updateValueSetsForDT();

	// addVersionAndScopetoPRELOADEDIG();
	// addVersionAndScopetoHL7IG();
	/** to be runned one Time **/
	// CreateCollectionOfUnchanged();
	// AddVersionsToDatatypes();
	// addVersionAndScopetoUSERIG();
	// addScopeUserToOldClonedPRELOADEDIG();
	// changeTabletoTablesInNewHl7();
	// modifyCodeUsage();
	// modifyFieldUsage();
	// modifyComponentUsage();
	// [NOTE from Woo] I have checked all of Usage B/W in the message, but
	// nothing. So we don't need
	// to write a code for the message.

	// ===============Data Type Library=====================================

	// or unpublished
	// setTablesStatus(); // sets the status of all the tables to published
	// or unpublished
	// CreateCollectionOfUnchanged(); // group datatype by sets of versions
	// Colorate(); // genenerates the datatypes evolution matrix.
	//
	// // setDtsStatus();// sets the status of all the datatypes to
	// published
	// CreateIntermediateFromUnchanged();
	// MergeComponents();
	// setSegmentStatus();
	// //
	// ====================================================================*/
	// // this.modifyConstraint();
	// // this.modifyMSH2Constraint();
	// // createNewSectionIds();
	//
	//
	// correctProfileComp();
	// fixConfLengths();
	// fixUserPublishedData();
	// fixConstraints1();

	// createDefaultConfiguration("IG Style");
	// createDefaultConfiguration("Profile Style");
	// createDefaultConfiguration("Table Style");
	// createDefaultExportFonts();

	// createDefaultConfiguration("Datatype Library");

	// changeStatusofPHINVADSTables();

	// modifyCodeUsage();
	// fixMissingData();

	// To RUN on Production for 2.0.0-rc
	// CreateCollectionOfUnchanged(); // group datatype by sets of versions
	// Colorate(); // genenerates the datatypes evolution matrix.
	// CreateIntermediateFromUnchanged();
	// MergeComponents();
	// fixDatatypeRecursion();
	// fixDuplicateValueSets();
	// createDefaultExportFonts();
	// updateInitAndCreateBindingAndCommentsVSForDatatype();
	// updateInitAndCreateBindingAndCommentsVSForSegment();
	// updateInitAndCreateCommentsForMessage();
	// fixUserDatatypesScope();
	// updateDMofSegment();
	// updateProfileForMissingDTs();
	// DeleteProfileComponents();
	// fixValueSetNameAndDescription();
	// refactorCoConstrint();
	// updateUserExportConfigs();
	// hotfix();
	// // // Need to run ONE TIME
	// fixConfLength();
	// fixWrongConstraints();
	// updateSegmentDatatypeDescription();
	// updateGroupName();
	// fixProfielComponentConfLength();
	// updateGroupName();
	//
	// 2.0.5-beta
	// fixCodeSysLOINC();
	// fixAllConstraints();
	// setTablePreText();
	// addCodeSystemtoAllTables();
	// initializeAttributes();
	// changeCommentToAuthorNotes();
	// addInternal();

	// 2.0.0-beta6
	// fixCoConstraintsDTVS();
	// clearUserExportConfigurations();

	// 2.0.0-beta7
	// updateTableForNumOfCodesANDSourceType();
	// updateTableForNumOfCodesANDSourceType();
	// TODO Do not use updateTableForNumOfCodesANDSourceType function any
	// more. - Woo

	// This is just test.
	// testNotification();
	// 2.0.0-beta10
	// makePhinvadsExternal();
	// reMapp() //saves all datatypes and segments and tables to remove the
	// old to get rid of old attributes ;
	// addVersionToProfile();

	// investigateMutipleValueSets();

	// fixLibraries();
	// fixValueSetDataDB();
	// removeUnfoundBindingForDatatypes(); // beta16 03/20
	// removeUnfoundBindingForSegments();
	// fixSegmentStatus();

	// changeEmptyToNA();

	// removePreloadedIGs("CDC 2.5.1 Immunization Profile");
	// removePreloadedIGs("ONC Immunization Profile");
	// importXMLProfile("ONC-Profiles/Profiles/VXU-Z22_Profile.xml",
	// "ONC-Profiles/Tables/VXU-Z22_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/VXU-Z22_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z22");
	// importXMLProfile("ONC-Profiles/Profiles/ACK-Z23_Profile.xml",
	// "ONC-Profiles/Tables/ACK-Z23_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/ACK-Z23_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z23");
	// importXMLProfile("ONC-Profiles/Profiles/RSP-Z31_Profile.xml",
	// "ONC-Profiles/Tables/RSP-Z31_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/RSP-Z31_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z31");
	// importXMLProfile("ONC-Profiles/Profiles/RSP-Z32_Profile.xml",
	// "ONC-Profiles/Tables/RSP-Z32_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/RSP-Z32_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z32");
	// importXMLProfile("ONC-Profiles/Profiles/RSP-Z33_Profile.xml",
	// "ONC-Profiles/Tables/RSP-Z33_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/RSP-Z33_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z33");
	// importXMLProfile("ONC-Profiles/Profiles/QBP-Z34_Profile.xml",
	// "ONC-Profiles/Tables/QBP-Z34_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/QBP-Z34_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z34");
	// importXMLProfile("ONC-Profiles/Profiles/RSP-Z42_Profile.xml",
	// "ONC-Profiles/Tables/RSP-Z42_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/RSP-Z42_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z42");
	// importXMLProfile("ONC-Profiles/Profiles/QBP-Z44_Profile.xml",
	// "ONC-Profiles/Tables/QBP-Z44_ValueSetLibrary.xml",
	// "ONC-Profiles/Constraints/QBP-Z44_Constraints.xml", "CDC 2.5.1
	// Immunization Profile Z44");
	//
	//
	// correctSegmentCocon();

	// fixDuplicateValueSets();

	// fixValueSetDataDB();

	// removePreloadedIGs("CDC 2.5.1 Immunization Profile");
	// removePreloadedIGs("ONC Immunization Profile");
	// makePreloadedProfile("5b3103a984ae3d88f239fb8e");

	// removeWrongBindingData("5b21730984ae53d88a68bed2", new
	// String[]{"57e43a2b84ae7eaed5fbdf83", "57e624d684aea6fcfcde915f"});
	// removeWrongBindingData("5b352a9c84aeb042c3e441b9", new
	// String[]{"57e43a2b84ae7eaed5fbdf83", "57e624d684aea6fcfcde915f"});
	//
	// fixIgDocumentType();

	// 09/17/18
	// fixLengthAndConfLength();

	// 09/28/18
	// removeWrongBindingDataBySegmentName("PID", 5);
	// removeWrongBindingDataBySegmentName("NK1", 2);

	// 10/22/18
	// fixAccountIds();

	// 11/27/18
	// fixOBX2ValuesetMissingAndDuplicated();

//	createDynTable0396();
    	iGDocumentService.makePreloaded("57a9f1c384ae90ce1244d327");
    }

    private void createDynTable0396() throws IOException {
	try {
	    Table table = tableService.findDynamicTable0396();
	    if (table == null) {
		// copy from table 0396 in version 2.8.2
		Table t = tableService.findByScopeAndVersionAndBindingIdentifier(SCOPE.HL7STANDARD, "2.8.2", "0396");
		if (t != null) {
		    table = new Table();
		    table.setBindingIdentifier(t.getBindingIdentifier());
		    table.setDescription(t.getDefPostText());
		    table.setDefPreText(t.getDefPreText());
		    table.setName(t.getName());
		    table.setOid(t.getOid());
		    table.setVersion(null);
		    table.setExtensibility(Extensibility.Open);
		    table.setId(null);
		    table.setLibIds(new HashSet<String>());
		    table.setScope(SCOPE.HL7STANDARD);
		    table.setStability(Stability.Dynamic);
		    table.setStatus(STATUS.PUBLISHED);
		    table.setType(Constant.TABLE);
		    table.setComment(t.getComment());
		    table.setCodeSystems(new HashSet<>(Arrays.asList(new String[] { "HL70396" })));
		    table.setHl7Version("Dyn");
		    table.setContentDefinition(ContentDefinition.Extensional);
		    table.setReferenceUrl(DynTableDownloadServiceImpl.TABLE_0396_URL);
		}
		InputStream io = dynTableDownloadService.downloadExcelFile();
		table = tableService.updateTable(table, io);
	    }
	} catch (TableUpdateStreamException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    

    /**
    * 
    */
    private void fixOBX2ValuesetMissingAndDuplicated() {
	List<Segment> segments = this.segmentService.findAll();

	for (Segment s : segments) {
	    if (s.getName().equals("OBX")) {
		boolean isMissing = this.checkOBX2Binding(s);
		if (isMissing) {
		    Table t0125 = this.tableService.findByScopeAndVersionAndBindingIdentifier(SCOPE.HL7STANDARD,
			    s.getHl7Version(), "0125");
		    if (t0125 != null) {
			ValueSetBinding vsb = new ValueSetBinding();
			vsb.setLocation("2");
			vsb.setTableId(t0125.getId());
			vsb.setBindingStrength(ValueSetBindingStrength.R);
			s.addValueSetBinding(vsb);
			this.segmentService.save(s);
		    }
		}
		int count = 0;
		Set<ValueSetOrSingleCodeBinding> toBeDeletedBindings = new HashSet<ValueSetOrSingleCodeBinding>();
		for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
		    if (binding instanceof ValueSetBinding) {
			ValueSetBinding vsb = (ValueSetBinding) binding;
			if (vsb.getLocation().equals("2")) {
			    count = count + 1;
			    if (count > 1)
				toBeDeletedBindings.add(vsb);
			}
		    }
		}
		if (count > 1) {
		    for (ValueSetOrSingleCodeBinding d : toBeDeletedBindings) {
			s.getValueSetBindings().remove(d);
		    }
		    this.segmentService.save(s);
		}
	    }
	}
    }

    /**
     * @param s
     * @return
     */
    private boolean checkOBX2Binding(Segment s) {
	for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
	    if (binding instanceof ValueSetBinding) {
		ValueSetBinding vsb = (ValueSetBinding) binding;
		if (vsb.getLocation().equals("2")) {
		    if (this.tableService.findById(vsb.getTableId()) != null)
			return false;
		}
	    }
	}
	return true;
    }

    /**
     * Add missing accountIds
     */
    private void fixAccountIds() {
	List<IGDocument> docs = iGDocumentService.findAll();
	for (IGDocument igDoc : docs) {
	    for (SegmentLink sl : igDoc.getProfile().getSegmentLibrary().getChildren()) {
		Segment s = this.segmentService.findById(sl.getId());
		if (s != null && s.getScope().equals(SCOPE.USER)) {
		    s.setAccountId(igDoc.getAccountId());
		    this.segmentService.save(s);
		}
	    }
	    for (DatatypeLink dl : igDoc.getProfile().getDatatypeLibrary().getChildren()) {
		Datatype d = this.datatypeService.findById(dl.getId());
		if (d != null && d.getScope().equals(SCOPE.USER)) {
		    d.setAccountId(igDoc.getAccountId());
		    this.datatypeService.save(d);
		}
	    }

	    if (igDoc.getProfile().getTableLibrary() != null
		    && igDoc.getProfile().getTableLibrary().getChildren() != null) {

		for (TableLink dl : igDoc.getProfile().getTableLibrary().getChildren()) {
		    Table d = this.tableService.findById(dl.getId());

		    if (d != null && d.getScope().equals(SCOPE.USER)) {
			d.setAccountId(igDoc.getAccountId());
			this.tableService.save(d);
		    }
		}

	    }

	    if (igDoc.getProfile().getMessages() != null && igDoc.getProfile().getMessages().getChildren() != null) {

		for (Message dl : igDoc.getProfile().getMessages().getChildren()) {
		    Message d = this.messageService.findById(dl.getId());
		    if (d != null) {
			d.setAccountId(igDoc.getAccountId());
			this.messageService.save(d);
		    }
		}
	    }

	    if (igDoc.getProfile().getProfileComponentLibrary() != null
		    && igDoc.getProfile().getProfileComponentLibrary().getChildren() != null) {
		for (ProfileComponentLink dl : igDoc.getProfile().getProfileComponentLibrary().getChildren()) {
		    ProfileComponent d = this.profileComponentService.findById(dl.getId());
		    if (d != null) {
			d.setAccountId(igDoc.getAccountId());
			this.profileComponentService.save(d);
		    }
		  }
	    }

	    if (igDoc.getProfile().getCompositeProfiles() != null
		    && igDoc.getProfile().getCompositeProfiles().getChildren() != null) {
		for (CompositeProfileStructure dl : igDoc.getProfile().getCompositeProfiles().getChildren()) {
		    CompositeProfileStructure d = this.compositeProfileStructureService.findById(dl.getId());
		    if (d != null) {
			d.setAccountId(igDoc.getAccountId());
			this.compositeProfileStructureService.save(d);
		    }
		  }
	    }

	  }

    }

    /**
     * @param igId
     * @param i
     */
    private void removeWrongBindingDataBySegmentName(String sementName, int location) {
	List<Segment> segments = this.segmentService.findAll();

	for (Segment s : segments) {
	    if (s.getName().equals(sementName)) {
		List<ValueSetOrSingleCodeBinding> bindings = s.getValueSetBindings();
		if (bindings != null) {
		    Set<ValueSetOrSingleCodeBinding> toBeDeletedBindings = new HashSet<ValueSetOrSingleCodeBinding>();

		    for (ValueSetOrSingleCodeBinding b : bindings) {
			if (b instanceof ValueSetBinding) {
			    ValueSetBinding vsb = (ValueSetBinding) b;
			    if (vsb.getLocation().equals(location + ""))
				toBeDeletedBindings.add(vsb);
			}
		    }

		    if (toBeDeletedBindings.size() > 0) {
			for (ValueSetOrSingleCodeBinding binding : toBeDeletedBindings) {
			    s.getValueSetBindings().remove(binding);
			}
			this.segmentService.save(s);
			System.out.println("[[INFO]" + sementName + "'s location " + location + " binding is removed!");
		    }
		}

	    }
	}

    }

    private void removeWrongBindingData(String igId, String[] valueSetIds) {

	IGDocument igDoc = this.iGDocumentService.findOne(igId);

	if (igDoc != null) {
	    for (SegmentLink sl : igDoc.getProfile().getSegmentLibrary().getChildren()) {
		Segment s = this.segmentService.findById(sl.getId());
		Set<ValueSetOrSingleCodeBinding> toBeDeletedBindings = new HashSet<ValueSetOrSingleCodeBinding>();
		if (s != null && s.getValueSetBindings() != null) {
		    for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
			if (binding instanceof ValueSetBinding) {
			    ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
			    if (Arrays.asList(valueSetIds).contains(valueSetBinding.getTableId())) {
				toBeDeletedBindings.add(valueSetBinding);
			    }
			}
		    }
		    for (ValueSetOrSingleCodeBinding binding : toBeDeletedBindings) {
			s.getValueSetBindings().remove(binding);
		    }
		}

		this.segmentService.save(s);
	    }

	    for (DatatypeLink dl : igDoc.getProfile().getDatatypeLibrary().getChildren()) {
		Datatype d = this.datatypeService.findById(dl.getId());
		Set<ValueSetOrSingleCodeBinding> toBeDeletedBindings = new HashSet<ValueSetOrSingleCodeBinding>();
		if (d != null && d.getValueSetBindings() != null) {
		    for (ValueSetOrSingleCodeBinding binding : d.getValueSetBindings()) {
			if (binding instanceof ValueSetBinding) {
			    ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
			    if (Arrays.asList(valueSetIds).contains(valueSetBinding.getTableId())) {
				toBeDeletedBindings.add(valueSetBinding);
			    }
			}
		    }

		    for (ValueSetOrSingleCodeBinding binding : toBeDeletedBindings) {
			d.getValueSetBindings().remove(binding);
		    }
		}

		this.datatypeService.save(d);
	    }
	}
    }

    private Component findComponentByPositon(List<Component> components, int position) {
	for (Component component : components) {
	    if (component.getPosition() == position) {
		return component;
	    }
	}
	return null;
    }

    private Field findFieldByPositon(List<Field> fields, int position) {
	for (Field field : fields) {
	    if (field.getPosition() == position) {
		return field;
	    }
	}
	return null;
    }

    private void fixLengthAndConfLength() {
	List<String> versions = new ArrayList<String>();
	versions.add("2.5.1");
	versions.add("2.6");
	versions.add("2.7");
	versions.add("2.7.1");
	versions.add("2.8");
	versions.add("2.8.1");
	versions.add("2.8.2");

	for (int i = 1; i < versions.size(); i++) {
	    String version = versions.get(i);

	    // datatypes
	    List<Datatype> datatypes = datatypeService.findByScopeAndVersion(SCOPE.HL7STANDARD.toString(), version);
	    for (Datatype datatype : datatypes) {
		Datatype prevVersionDatatype = datatypeService.findByNameAndVesionAndScope(datatype.getName(),
			versions.get(i - 1), SCOPE.HL7STANDARD.toString());
		if (prevVersionDatatype != null) {
		    if (datatype.getComponents() != null && !datatype.getComponents().isEmpty()) {
			boolean changed = false;
			for (Component component : datatype.getComponents()) {
			    Component prevComponent = findComponentByPositon(datatype.getComponents(),
				    component.getPosition());
			    if (prevComponent != null) {
				if (component.getConfLength() != null
					&& DataElement.LENGTH_NA.equals(component.getConfLength())
					&& prevComponent.getConfLength() != null
					&& !prevComponent.getConfLength().equals(DataElement.LENGTH_NA)) {
				    component.setConfLength(prevComponent.getConfLength());
				    changed = true;
				}
				if ((component.getMinLength() != null
					&& DataElement.LENGTH_NA.equals(component.getMinLength())
					&& prevComponent.getMinLength() != null
					&& !prevComponent.getMinLength().equals(DataElement.LENGTH_NA))
					|| (component.getMaxLength() != null
						&& DataElement.LENGTH_NA.equals(component.getMaxLength())
						&& prevComponent.getMaxLength() != null
						&& !prevComponent.getMaxLength().equals(DataElement.LENGTH_NA))) {
				    component.setMinLength(prevComponent.getMinLength());
				    component.setMaxLength(prevComponent.getMaxLength());
				    changed = true;
				}
			    }
			}
			if (changed) {
			    datatypeService.save(datatype);
			}
		    }
		}
	    }

	    // segments
	    List<Segment> segments = segmentService.findByScopeAndVersion(SCOPE.HL7STANDARD.toString(), version);
	    for (Segment segment : segments) {
		Segment prevVersionSegment = segmentService.findByNameAndVersionAndScope(segment.getName(),
			versions.get(i - 1), SCOPE.HL7STANDARD.toString());
		if (prevVersionSegment != null) {
		    if (segment.getFields() != null && !segment.getFields().isEmpty()) {
			boolean changed = false;
			for (Field field : segment.getFields()) {
			    Field prevField = findFieldByPositon(segment.getFields(), field.getPosition());
			    if (prevField != null) {
				if (field.getConfLength() != null && DataElement.LENGTH_NA.equals(field.getConfLength())
					&& prevField.getConfLength() != null
					&& !field.getConfLength().equals(DataElement.LENGTH_NA)) {
				    field.setConfLength(prevField.getConfLength());
				    changed = true;
				}
				if ((field.getMinLength() != null && DataElement.LENGTH_NA.equals(field.getMinLength())
					&& prevField.getMinLength() != null
					&& !prevField.getMinLength().equals(DataElement.LENGTH_NA))
					|| (field.getMaxLength() != null
						&& DataElement.LENGTH_NA.equals(field.getMaxLength())
						&& prevField.getMaxLength() != null
						&& !prevField.getMaxLength().equals(DataElement.LENGTH_NA))) {
				    field.setMinLength(prevField.getMinLength());
				    field.setMaxLength(prevField.getMaxLength());
				    changed = true;
				}
			    }
			}
			if (changed) {
			    segmentService.save(segment);
			}
		    }
		}
	    }
	}
    }

    /**
     * @param string
     * @throws IGDocumentException
     */
    private void makePreloadedProfile(String id) throws IGDocumentException {
	IGDocument igDocument = this.iGDocumentService.findOne(id);
	igDocument.setAccountId(null);
	igDocument.setDateUpdated(new Date());
	igDocument.setScope(IGDocumentScope.PRELOADED);
	DocumentMetaData metaData = new DocumentMetaData();
	igDocument.setMetaData(metaData);
	this.iGDocumentService.save(igDocument);
    }

    void fixIgDocumentType() {
	List<IGDocument> allIgs = this.iGDocumentService.findAll();
	for (IGDocument ig : allIgs) {
	    if (!ig.getType().equals(Constant.Document)) {
		ig.setType(Constant.Document);
		try {
		    this.iGDocumentService.save(ig);
		} catch (IGDocumentException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
    }

    public boolean exist(List<ValueSetOrSingleCodeBinding> bindings, ValueSetBinding binding) {
	boolean found = false;
	for (ValueSetOrSingleCodeBinding b : bindings) {
	    if (b instanceof ValueSetBinding && b.getTableId().equals(binding.getTableId())) {
		found = true;
	    }
	}
	return found;
    }

    public void fixDuplicateValueSets() {
	List<Segment> segments = this.segmentService.findByScope("HL7STANDARD");
	for (Segment s : segments) {
	    List<ValueSetOrSingleCodeBinding> valueSetBindings = s.getValueSetBindings();
	    List<ValueSetOrSingleCodeBinding> newValueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();
	    if (valueSetBindings != null) {
		for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
		    if (binding instanceof ValueSetBinding) {
			ValueSetBinding vs = (ValueSetBinding) binding;
			if (!exist(newValueSetBindings, vs)) {
			    newValueSetBindings.add(vs);
			}
		    } else {
			newValueSetBindings.add(binding);
		    }
		}
		s.setValueSetBindings(newValueSetBindings);
		segmentService.save(s);

	    }
	}

	List<Datatype> datatypes = this.datatypeService.findByScope("HL7STANDARD");
	for (Datatype s : datatypes) {
	    List<ValueSetOrSingleCodeBinding> valueSetBindings = s.getValueSetBindings();
	    List<ValueSetOrSingleCodeBinding> newValueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();
	    if (valueSetBindings != null) {
		for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
		    if (binding instanceof ValueSetBinding) {
			ValueSetBinding vs = (ValueSetBinding) binding;
			if (!exist(newValueSetBindings, vs)) {
			    newValueSetBindings.add(vs);
			}
		    } else {
			newValueSetBindings.add(binding);
		    }
		}
		s.setValueSetBindings(newValueSetBindings);
		datatypeService.save(s);
	    }
	}

    }

    /**
     * 
     */
    private void correctSegmentCocon() {
	List<Segment> userSegs = this.segmentService.findByScope("USER");

	for (Segment s : userSegs) {
	    if (s.getName().equals("OBX")) {
		CoConstraintsTable coConstraintsTable = s.getCoConstraintsTable();

		if (coConstraintsTable != null) {

		    if (coConstraintsTable.getRowSize() != coConstraintsTable.getIfColumnData().size()) {
			List<CoConstraintIFColumnData> newIFData = new ArrayList<CoConstraintIFColumnData>();

			for (CoConstraintIFColumnData ifData : coConstraintsTable.getIfColumnData()) {
			    if (ifData != null)
				newIFData.add(ifData);
			}

			coConstraintsTable.setIfColumnData(newIFData);
			Map<String, List<CoConstraintTHENColumnData>> newTHENColumnData = new HashMap<String, List<CoConstraintTHENColumnData>>();
			for (String key : coConstraintsTable.getThenMapData().keySet()) {
			    List<CoConstraintTHENColumnData> newThenData = new ArrayList<CoConstraintTHENColumnData>();
			    List<CoConstraintTHENColumnData> thenData = coConstraintsTable.getThenMapData().get(key);
			    for (CoConstraintTHENColumnData data : thenData) {
				if (data != null)
				    newThenData.add(data);
			    }
			    newTHENColumnData.put(key, newThenData);
			}
			coConstraintsTable.setThenMapData(newTHENColumnData);

			System.out.println("-------------FOUND NULL DATA------------");
			System.out.println("ID:" + s.getId());
			System.out.println("Label:" + s.getLabel());
			System.out.println("rowSize1:" + coConstraintsTable.getRowSize());
			System.out.println("rowSize2:" + coConstraintsTable.getIfColumnData().size());

			this.segmentService.save(s);

		    }

		}

	    }
	}
    }

    private void removePreloadedIGs(String s) {

	List<IGDocument> allPreloadeds = this.iGDocumentService.findAllPreloaded();
	for (IGDocument ig : allPreloadeds) {
	    if (ig.getMetaData().getTitle().contains(s))
		this.iGDocumentService.delete(ig.getId());
	}

    }

    void importXMLProfile(String profilePath, String valuesetPath, String constraintPath, String title)
	    throws IOException, IGDocumentException, ProfileException {

	ClassPathResource pResource = new ClassPathResource(profilePath);
	ClassPathResource vResource = new ClassPathResource(valuesetPath);
	ClassPathResource cResource = new ClassPathResource(constraintPath);

	IGDocument igd = new IGDocument();
	Profile profile = this.profileSerialization.deserializeXMLToProfile(
		FileUtils.readFileToString(pResource.getFile()), FileUtils.readFileToString(vResource.getFile()),
		FileUtils.readFileToString(cResource.getFile()));
	profile.setDateUpdated(new Date());
	profile.setScope(IGDocumentScope.USER);
	profile.setSectionTitle("Profile");
	profile.setSectionContents("Contents");
	profile.setSectionDescription("DESC");
	profile.setSectionPosition(3);

	ProfileComponentLibrary profileComponentLibrary = new ProfileComponentLibrary();
	profileComponentLibrary.setSectionTitle("profileComponentLibrary");
	profileComponentLibrary.setSectionContents("Contents");
	profileComponentLibrary.setSectionDescription("DESC");
	profileComponentLibrary.setSectionPosition(1);
	profileComponentLibraryService.save(profileComponentLibrary);
	profile.setProfileComponentLibrary(profileComponentLibrary);

	igd.addProfile(profile);

	igd.setAccountId((long) 10);
	Date date = new Date();
	igd.setDateUpdated(new Date());
	igd.setScope(IGDocumentScope.PRELOADED);
	igd.setComment("Created " + date.toString());

	DocumentMetaData metaData = new DocumentMetaData();
	metaData.setSubTitle("Imported from XML files");
	metaData.setTitle(title);
	metaData.setHl7Version(profile.getMetaData().getHl7Version());
	igd.setMetaData(metaData);

	iGDocumentService.save(igd);
    }

    void changeEmptyToNA() {

	List<Datatype> dts = datatypeService.findAll();
	for (Datatype d : dts) {
	    if (d.getComponents() != null) {
		for (Component c : d.getComponents()) {
		    if (c.getConfLength() == null || c.getConfLength().isEmpty()) {
			c.setConfLength(DataElement.LENGTH_NA);
		    }
		    if (c.getMinLength() == null || c.getMinLength().isEmpty()) {
			c.setMinLength(DataElement.LENGTH_NA);
			c.setMaxLength(DataElement.LENGTH_NA);

		    }
		    if (c.getMaxLength() == null || c.getMaxLength().isEmpty()) {
			c.setMinLength(DataElement.LENGTH_NA);
			c.setMaxLength(DataElement.LENGTH_NA);

		    }
		}
	    }
	    datatypeService.save(d);

	}

	//
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    if (s.getFields() != null) {
		for (Field f : s.getFields()) {
		    if (f.getConfLength() == null || f.getConfLength().isEmpty()) {
			f.setConfLength(DataElement.LENGTH_NA);
		    }
		    if (f.getMinLength() == null || f.getMinLength().isEmpty()) {
			f.setMinLength(DataElement.LENGTH_NA);
			f.setMaxLength(DataElement.LENGTH_NA);

		    }
		    if (f.getMaxLength() == null || f.getMaxLength().isEmpty()) {
			f.setMinLength(DataElement.LENGTH_NA);
			f.setMaxLength(DataElement.LENGTH_NA);

		    }
		}
	    }
	    segmentService.save(s);
	}

    }

    private void fixValueSetDataDB() {
	List<Table> tables = tableService.findByScope("PHINVADS");

	for (Table t : tables) {
	    String description = t.getDescription();
	    if (description == null)
		description = "";
	    else {
		description = description.replaceAll("\u0019s", " ");
		description = description.replaceAll("“", "&quot;");
		description = description.replaceAll("”", "&quot;");
		description = description.replaceAll("\"", "&quot;");
	    }
	    String defPostText = t.getDefPostText();
	    if (defPostText == null)
		defPostText = "";
	    else {
		defPostText = defPostText.replaceAll("\u0019s", " ");
		defPostText = defPostText.replaceAll("“", "&quot;");
		defPostText = defPostText.replaceAll("”", "&quot;");
		defPostText = defPostText.replaceAll("\"", "&quot;");
	    }
	    String defPreText = t.getDefPreText();
	    if (defPreText == null)
		defPreText = "";
	    else {
		defPreText = defPreText.replaceAll("\u0019s", " ");
		defPreText = defPreText.replaceAll("“", "&quot;");
		defPreText = defPreText.replaceAll("”", "&quot;");
		defPreText = defPreText.replaceAll("\"", "&quot;");
	    }

	    tableService.updateAllDescription(t.getId(), description, defPostText, defPreText);
	}

    }

    private void removeUnfoundBindingForDatatypes() {
	List<Datatype> hl7Dts = datatypeService.findByScope(SCOPE.HL7STANDARD.toString());
	for (Datatype d : hl7Dts) {
	    if (d.getValueSetBindings() != null) {
		List<ValueSetOrSingleCodeBinding> finalList = new ArrayList<ValueSetOrSingleCodeBinding>();
		for (ValueSetOrSingleCodeBinding binding : d.getValueSetBindings()) {
		    if (binding instanceof ValueSetBinding) {
			if (tableService.findOneShortById(binding.getTableId()) != null) {
			    finalList.add(binding);
			}
		    }
		}

		d.setValueSetBindings(finalList);
		datatypeService.save(d);
	    }
	}
    }

    private void removeUnfoundBindingForSegments() {
	List<Segment> hl7Segments = segmentService.findByScope(SCOPE.HL7STANDARD.toString());
	for (Segment s : hl7Segments) {
	    if (s.getValueSetBindings() != null) {
		List<ValueSetOrSingleCodeBinding> finalList = new ArrayList<ValueSetOrSingleCodeBinding>();
		for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
		    if (binding instanceof ValueSetBinding) {
			if (tableService.findById(binding.getTableId()) != null) {
			    finalList.add(binding);
			}
		    }
		}

		s.setValueSetBindings(finalList);
		segmentService.save(s);
	    }
	}
    }

    private void addVersionToProfile() {
	List<Message> messages = messageService.findAll();
	for (Message m : messages) {
	    if (m.getHl7Version() == null || m.getHl7Version().isEmpty()) {
		m.setHl7Version(findMessageVersion(m));
		messageService.save(m);
	    }
	}
    }

    private String findMessageVersion(Message m) {
	// TODO Auto-generated method stub
	for (SegmentRefOrGroup sog : m.getChildren()) {
	    String v = segmentOrgroupVersion(sog);
	    if (v != null) {
		return segmentOrgroupVersion(sog);
	    }
	}
	return null;
    }

    private String segmentOrgroupVersion(SegmentRefOrGroup sog) {
	if (sog instanceof SegmentRef) {
	    SegmentRef ref = (SegmentRef) sog;
	    if (ref.getRef().getId() != null) {
		Segment s = segmentService.findById(ref.getRef().getId());
		if (s != null && s.getHl7Version() != null) {
		    return s.getHl7Version();

		}
	    }

	} else if (sog instanceof Group) {
	    Group ref = (Group) sog;
	    for (SegmentRefOrGroup child : ref.getChildren()) {
		segmentOrgroupVersion(child);
	    }
	}
	return null;
	// TODO Auto-generated method stub
    }

    private void investigateMutipleValueSets() {
	List<IGDocument> allIGs = documentService.findAll();
	String report = "";
	for (IGDocument ig : allIGs) {

	    if (ig.getScope().equals(IGDocumentScope.USER)) {
		Profile p = ig.getProfile();
		for (Message m : p.getMessages().getChildren()) {
		    if (m.getValueSetBindings() != null) {
			Set<String> valueSetLocation = new HashSet<String>();
			for (ValueSetOrSingleCodeBinding vsscb : m.getValueSetBindings()) {
			    if (vsscb instanceof ValueSetBinding) {
				ValueSetBinding vsb = (ValueSetBinding) vsscb;

				if (valueSetLocation.contains(vsb.getLocation())) {
				    report = report + "##########FOUND###########\n";
				    report = report + "IG Document ID: " + ig.getId() + "\n";
				    report = report + "IG Document Account ID: " + ig.getAccountId() + "\n";
				    // if(ig.getAccountId() != null){
				    // Account account =
				    // accountRepository.findOne(ig.getAccountId());
				    // if(account != null){
				    // report = report + "IG Document Account
				    // email: " + account.getEmail() + "\n";
				    // }else {
				    // report = report + "Account Info is not
				    // FOUND!!!!" + "\n";
				    // }
				    // }else {
				    // report = report + "Account ID is
				    // null!!!!" + "\n";
				    // }
				    report = report + "Message ID: " + m.getId() + "\n";
				    report = report + "Message Name: " + m.getName() + "\n";
				    report = report + "Message Identifier: " + m.getIdentifier() + "\n";
				    report = report + "Multiple Value Set Location: " + vsb.getLocation() + "\n";
				    Table table = tableService.findById(vsb.getTableId());
				    if (table != null) {
					report = report + "Multiple Value Set Id: " + vsb.getTableId() + "\n";
					report = report + "Multiple Value Set Binding Identifier: "
						+ table.getBindingIdentifier() + "\n";
				    } else {
					report = report + "Table is not FOUND!!!!" + "\n";
				    }
				    report = report + "##########END###########" + "\n";

				} else {
				    valueSetLocation.add(vsb.getLocation());
				}
			    }
			}
		    }
		}
		for (SegmentLink sl : p.getSegmentLibrary().getChildren()) {
		    if (sl.getId() != null) {
			Segment s = segmentService.findById(sl.getId());
			if (s != null) {
			    if (s.getValueSetBindings() != null) {
				Set<String> valueSetLocation = new HashSet<String>();
				for (ValueSetOrSingleCodeBinding vsscb : s.getValueSetBindings()) {
				    if (vsscb instanceof ValueSetBinding) {
					ValueSetBinding vsb = (ValueSetBinding) vsscb;

					if (valueSetLocation.contains(vsb.getLocation())) {

					    report = report + "##########FOUND###########" + "\n";
					    report = report + "IG Document ID: " + ig.getId() + "\n";
					    report = report + "IG Document Account ID: " + ig.getAccountId() + "\n";

					    // if(ig.getAccountId() != null){
					    // Account account =
					    // accountRepository.findOne(ig.getAccountId());
					    // if(account != null){
					    // report = report + "IG Document
					    // Account email: " +
					    // account.getEmail() + "\n";
					    // }else {
					    // report = report + "Account Info
					    // is not FOUND!!!!" + "\n";
					    // }
					    // }else {
					    // report = report + "Account ID is
					    // null!!!!" + "\n";
					    // }

					    report = report + "Segment ID: " + s.getId() + "\n";
					    report = report + "Segment Label: " + s.getLabel() + "\n";
					    report = report + "Multiple Value Set Location: " + vsb.getLocation()
						    + "\n";
					    Table table = tableService.findById(vsb.getTableId());
					    if (table != null) {
						report = report + "Multiple Value Set Id: " + vsb.getTableId() + "\n";
						report = report + "Multiple Value Set Binding Identifier: "
							+ table.getBindingIdentifier() + "\n";
					    } else {
						report = report + "Table is not FOUND!!!!" + "\n";
					    }
					    report = report + "##########END###########" + "\n";

					} else {
					    valueSetLocation.add(vsb.getLocation());
					}
				    }
				}
			    }
			}
		    } else {
			report = report + "SegmentLink ID is null. IG id :" + ig.getId() + "\n";
		    }

		}
		for (DatatypeLink dl : p.getDatatypeLibrary().getChildren()) {
		    if (dl.getId() != null) {
			Datatype d = datatypeService.findById(dl.getId());

			if (d != null) {
			    if (d.getValueSetBindings() != null) {
				Set<String> valueSetLocation = new HashSet<String>();
				for (ValueSetOrSingleCodeBinding vsscb : d.getValueSetBindings()) {
				    if (vsscb instanceof ValueSetBinding) {
					ValueSetBinding vsb = (ValueSetBinding) vsscb;

					if (valueSetLocation.contains(vsb.getLocation())) {
					    report = report + "##########FOUND###########" + "\n";
					    report = report + "IG Document ID: " + ig.getId() + "\n";
					    report = report + "IG Document Account ID: " + ig.getAccountId() + "\n";
					    // if(ig.getAccountId() != null){
					    // Account account =
					    // accountRepository.findOne(ig.getAccountId());
					    // if(account != null){
					    // report = report + "IG Document
					    // Account email: " +
					    // account.getEmail() + "\n";
					    // }else {
					    // report = report + "Account Info
					    // is not FOUND!!!!" + "\n";
					    // }
					    // }else {
					    // report = report + "Account ID is
					    // null!!!!" + "\n";
					    // }
					    report = report + "Datatype ID: " + d.getId() + "\n";
					    report = report + "Datatype Label: " + d.getLabel() + "\n";
					    report = report + "Multiple Value Set Location: " + vsb.getLocation()
						    + "\n";
					    Table table = tableService.findById(vsb.getTableId());
					    if (table != null) {
						report = report + "Multiple Value Set Id: " + vsb.getTableId() + "\n";
						report = report + "Multiple Value Set Binding Identifier: "
							+ table.getBindingIdentifier() + "\n";
					    } else {
						report = report + "Table is not FOUND!!!!" + "\n";
					    }
					    report = report + "##########END###########" + "\n";

					} else {
					    valueSetLocation.add(vsb.getLocation());
					}
				    }
				}
			    }
			}
		    } else {
			report = report + "DatatypeLink ID is null. IG id :" + ig.getId() + "\n";
		    }
		}
	    }

	}
	report = report + "##################" + "END MultipleValue Set binding investigation" + "\n";

	System.out.println(report);
    }

    private void testNotification() {
	Notification item = new Notification();

	item.setByWhom("JY Woo");
	item.setChangedDate(new Date());
	item.setTargetType(TargetType.Valueset);
	item.setTargetId("57ee310484ae2aadc10efcca");

	Notification item2 = new Notification();
	item2.setByWhom("JY Woo2");
	item2.setChangedDate(new Date());
	item2.setTargetType(TargetType.Valueset);
	item2.setTargetId("57f0e74684ae7a55c2410d22");

	Notifications notifications = new Notifications();
	notifications.setIgDocumentId("5a149844512c91633456205e");
	notifications.addItem(item);
	notifications.addItem(item2);
	notificationsRepository.save(notifications);

    }

    private void reMapp() {
	List<Segment> list = segmentService.findAll();
	segmentService.save(list);
	List<Datatype> dts = datatypeService.findAll();
	datatypeService.save(dts);
    }

    private void updateTableForNumOfCodesANDSourceType() {
	List<Table> allTables = tableService.findAll();
	// String largeTableLISTCSV = "\"ID\"," + "\"Binding Identifier\"," +
	// "\"Name\"," + "\"Code
	// Size\"," + "\"SCOPE\"," + "\"HL7 Version\"\n";
	// String IGUsedLargeTableLISTCSV = "\"ID\"," + "\"IG Name\"," + "\"IG
	// Title\"," + "\"IG
	// SubTitle\"," + "\"IG status\"," + "\"Account Id\"," + "\"User Id\","
	// + "\"User email\"," +
	// "\"User FullName\"," + "\"Binding Identifier\"," + "\"VS Name\"," +
	// "\"Code Size\"," +
	// "\"SCOPE\"," + "\"HL7 Version\"\n";

	// Map<String,Table> largeTable = new HashMap<String, Table> ();

	for (Table t : allTables) {
	    int numberOfCodes = t.getCodes().size();
	    if (!t.getScope().equals(SCOPE.PHINVADS)) {
		tableService.updateAttributes(t.getId(), "numberOfCodes", numberOfCodes);
	    }
	    if (t.getScope().equals(SCOPE.PHINVADS) || numberOfCodes > 500) {
		tableService.updateAttributes(t.getId(), "sourceType", SourceType.EXTERNAL);
		tableService.updateAttributes(t.getId(), "managedBy", Constant.External);
	    }
	    if (numberOfCodes > 500) {
		tableService.updateAttributes(t.getId(), "codes", new ArrayList<Code>());
	    }
	}

	/*
	 * List<IGDocument> allUserIGs =
	 * documentService.findAllByScope(IGDocumentScope.USER); for(IGDocument
	 * ig : allUserIGs){ TableLibrary tLib =
	 * ig.getProfile().getTableLibrary();
	 * 
	 * for(TableLink tl : tLib.getChildren()){ Table found =
	 * largeTable.get(tl.getId());
	 * 
	 * if(found != null){ Account account =
	 * accountRepository.findOne(ig.getAccountId());
	 * 
	 * if(account == null) { account = new Account();
	 * account.setUsername("Unknown"); account.setEmail("Unknown");
	 * account.setFullName("Unknown"); }
	 * 
	 * IGUsedLargeTableLISTCSV = IGUsedLargeTableLISTCSV + "\"" + ig.getId()
	 * + "\"," + "\"" + ig.getMetaData().getName() + "\"," + "\"" +
	 * ig.getMetaData().getTitle() + "\"," + "\"" +
	 * ig.getMetaData().getSubTitle() + "\"," + "\"" +
	 * ig.getMetaData().getStatus() + "\"," +
	 * 
	 * "\"" + ig.getAccountId()+ "\"," + "\"" + account.getUsername() +
	 * "\"," + "\"" + account.getEmail() + "\"," + "\"" +
	 * account.getFullName() + "\"," +
	 * 
	 * "\"" + found.getId() + "\"," + "\"" + found.getBindingIdentifier() +
	 * "\"," + "\"" + found.getName() + "\"," + "\"" +
	 * found.getCodes().size() + "\"," + "\"" + found.getScope() + "\"," +
	 * "\"" + found.getHl7Version() + "\"\n"; } } }
	 */

	// System.out.println(largeTableLISTCSV);
	// System.out.println(IGUsedLargeTableLISTCSV);
    }

    private void fixLibraries() {
	List<IGDocument> igDocuments = iGDocumentService.findAll();
	Set<Segment> segments;
	Set<DatatypeLink> datatypes;
	Set<TableLink> valueSets;
	for (IGDocument igDocument : igDocuments) {
	    segments = new HashSet<>();
	    datatypes = new HashSet<>();
	    valueSets = new HashSet<>();
	    for (Message message : igDocument.getProfile().getMessages().getChildren()) {
		for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
		    identifyBindedItemsFromSegmentOrGroup(segmentRefOrGroup, segments, datatypes, valueSets);
		}
	    }
	    boolean changed = false;
	    for (Segment segment : segments) {
		if (!igDocument.getProfile().getSegmentLibrary().contains(segment.getId())) {
		    changed = true;
		    igDocument.getProfile().getSegmentLibrary().addSegment(segment);
		}
	    }
	    if (changed)
		segmentLibraryService.save(igDocument.getProfile().getSegmentLibrary());
	    changed = false;
	    for (DatatypeLink datatypeLink : datatypes) {
		if (!igDocument.getProfile().getDatatypeLibrary().contains(datatypeLink)) {
		    changed = true;
		    igDocument.getProfile().getDatatypeLibrary().addDatatype(datatypeLink);
		}
	    }
	    if (changed)
		datatypeLibraryService.save(igDocument.getProfile().getDatatypeLibrary());
	    changed = false;
	    for (TableLink tableLink : valueSets) {
		if (!igDocument.getProfile().getTableLibrary().contains(tableLink)) {
		    changed = true;
		    igDocument.getProfile().getTableLibrary().addTable(tableLink);
		    tableLibraryService.save(igDocument.getProfile().getTableLibrary());
		}
	    }
	    if (changed)
		tableLibraryService.save(igDocument.getProfile().getTableLibrary());
	    changed = false;
	}
    }

    private void identifyBindedItemsFromSegmentOrGroup(SegmentRefOrGroup segmentRefOrGroup, Set<Segment> segments,
	    Set<DatatypeLink> datatypes, Set<TableLink> valueSets) {
	if (segmentRefOrGroup instanceof SegmentRef) {
	    Segment segment = segmentService.findById(((SegmentRef) segmentRefOrGroup).getRef().getId());
	    if (!segments.contains(segment)) {
		segments.add(segment);
	    }
	    if (segment != null) {
		for (Field field : segment.getFields()) {
		    if (field.getDatatype() != null && !datatypes.contains(field.getDatatype())) {
			datatypes.add(field.getDatatype());
			Datatype datatype = datatypeService.findById(field.getDatatype().getId());
			for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : datatype.getValueSetBindings()) {
			    if (valueSetOrSingleCodeBinding instanceof ValueSetBinding
				    && valueSetOrSingleCodeBinding.getTableId() != null
				    && !valueSets.contains(valueSetOrSingleCodeBinding.getTableId())) {
				Table table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
				if (table.getScope().equals(SCOPE.ARCHIVED)
					&& !table.getStatus().equals(STATUS.PUBLISHED)) {

				    table.setScope(SCOPE.USER);
				    tableService.updateAttributes(table.getId(), "scope", SCOPE.USER);

				}
				if (table != null) {
				    TableLink tableLink = new TableLink(table.getId(), table.getBindingIdentifier());
				    valueSets.add(tableLink);
				}
			    }
			}
			if (datatype != null) {
			    for (Component component : datatype.getComponents()) {
				if (component.getDatatype() != null && !datatypes.contains(component.getDatatype())) {
				    datatypes.add(component.getDatatype());
				}

			    }
			}
		    }
		}
		for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : segment.getValueSetBindings()) {
		    if (valueSetOrSingleCodeBinding instanceof ValueSetBinding
			    && valueSetOrSingleCodeBinding.getTableId() != null
			    && !valueSets.contains(valueSetOrSingleCodeBinding.getTableId())) {
			Table table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
			if (table != null) {
			    TableLink tableLink = new TableLink(table.getId(), table.getBindingIdentifier());
			    valueSets.add(tableLink);
			}
		    }
		}
		if (segment.getDynamicMappingDefinition() != null
			&& segment.getDynamicMappingDefinition().getDynamicMappingItems() != null) {
		    for (DynamicMappingItem dynamicMappingItem : segment.getDynamicMappingDefinition()
			    .getDynamicMappingItems()) {
			if (dynamicMappingItem.getDatatypeId() != null) {
			    Datatype datatype = datatypeService.findById(dynamicMappingItem.getDatatypeId());
			    if (datatype != null) {
				DatatypeLink datatypeLink = new DatatypeLink(datatype.getId(), datatype.getName(),
					datatype.getExt());
				datatypes.add(datatypeLink);
			    }
			}
		    }
		}

		if (segment.getCoConstraintsTable() != null
			&& segment.getCoConstraintsTable().getThenMapData() != null) {
		    for (String key : segment.getCoConstraintsTable().getThenMapData().keySet()) {
			if (segment.getCoConstraintsTable().getThenMapData().get(key) != null) {
			    for (CoConstraintTHENColumnData coConstraintTHENColumnData : segment.getCoConstraintsTable()
				    .getThenMapData().get(key)) {
				if (coConstraintTHENColumnData != null) {
				    if (coConstraintTHENColumnData.getDatatypeId() != null) {
					Datatype datatype = datatypeService
						.findById(coConstraintTHENColumnData.getDatatypeId());
					if (datatype != null) {
					    DatatypeLink datatypeLink = new DatatypeLink(datatype.getId(),
						    datatype.getName(), datatype.getExt());
					    datatypes.add(datatypeLink);
					}
				    } else if (coConstraintTHENColumnData.getValueSets() != null
					    && !coConstraintTHENColumnData.getValueSets().isEmpty()) {
					for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
					    Table table = tableService.findById(valueSetData.getTableId());
					    if (table != null) {
						TableLink tableLink = new TableLink(table.getId(),
							table.getBindingIdentifier());
						valueSets.add(tableLink);
					    }
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
		identifyBindedItemsFromSegmentOrGroup(children, segments, datatypes, valueSets);
	    }
	}
    }

    private void makePhinvadsExternal() {
	List<Table> allPhvs = tableService.findByScope(SCOPE.PHINVADS.name());

	for (Table t : allPhvs) {
	    tableService.updateAttributes(t.getId(), "sourceType", SourceType.EXTERNAL);

	}
    };

    private void setCodePresence() {
	List<TableLibrary> tbls = tableLibraryService.findAll();
	for (TableLibrary tbl : tbls) {
	    tbl.setCodePresence(new HashMap<String, Boolean>());
	    for (TableLink link : tbl.getChildren()) {
		if (link.getId() != null) {
		    System.out.println(link);
		    tbl.getCodePresence().put(link.getId(), true);

		}

	    }
	    tableLibraryService.save(tbl);
	}
    };

    private void clearUserExportConfigurations() {
	exportConfigRepository.deleteAll();
    }

    private void fixCoConstraintsDTVS() {
	List<IGDocument> allIGs = documentService.findAll();
	int count = 0;
	for (IGDocument ig : allIGs) {
	    System.out.println("-----------" + count++);
	    Profile p = ig.getProfile();

	    DatatypeLibrary dtLib = p.getDatatypeLibrary();
	    TableLibrary tLib = p.getTableLibrary();
	    SegmentLibrary sLib = p.getSegmentLibrary();

	    for (SegmentLink sl : sLib.getChildren()) {
		if (sl != null && sl.getId() != null) {
		    boolean flag = false;
		    Segment seg = segmentService.findById(sl.getId());
		    if (seg != null && seg.getName().equals("OBX")) {
			if (seg.getCoConstraintsTable() != null) {

			    CoConstraintsTable coConstraintsTable = seg.getCoConstraintsTable();
			    List<CoConstraintColumnDefinition> thenDefinitions = coConstraintsTable
				    .getThenColumnDefinitionList();
			    for (CoConstraintColumnDefinition thenDefinition : thenDefinitions) {
				if (thenDefinition != null) {
				    Map<String, List<CoConstraintTHENColumnData>> thenMapData = coConstraintsTable
					    .getThenMapData();
				    if (thenMapData != null) {
					List<CoConstraintTHENColumnData> coConstraintTHENColumnData = thenMapData
						.get(thenDefinition.getId());

					for (CoConstraintTHENColumnData thenData : coConstraintTHENColumnData) {

					    if (thenData != null) {
						if (thenData.getDatatypeId() != null) {
						    DatatypeLink dl = dtLib.findOne(thenData.getDatatypeId());

						    if (dl == null) {
							System.out.println("==================>" + "found missing DT");
							thenData.setDatatypeId(null);

							flag = true;

						    } else {
							System.out.println("==================>" + "Good DT");
						    }
						}

						if (thenData.getValueSets() != null) {
						    List<ValueSetData> toBeDeletedVSDataList = new ArrayList<ValueSetData>();
						    for (ValueSetData vsData : thenData.getValueSets()) {
							if (vsData != null) {
							    if (vsData.getTableId() != null) {
								TableLink tl = tLib
									.findOneTableById(vsData.getTableId());

								if (tl == null) {
								    System.out.println(
									    "==================>" + "found missing VS");
								    toBeDeletedVSDataList.add(vsData);
								    flag = true;
								} else {
								    System.out
									    .println("==================>" + "Good VS");
								}
							    }
							}
						    }

						    for (ValueSetData toBeDeletedVSData : toBeDeletedVSDataList) {
							thenData.getValueSets().remove(toBeDeletedVSData);
						    }

						    thenData.getValueSets().remove(null);
						}
					    }
					}
				    }
				}
			    }
			}

			if (flag)
			    segmentService.save(seg);
		    }
		}
	    }
	}
    }

    private void addInternal() {
	List<Table> all = tableService.findAll();
	for (Table t : all) {
	    tableService.updateAttributes(t.getId(), "sourceType", SourceType.INTERNAL);
	}
    }

    private void setTablePreText() {
	List<Table> all = tableService.findAll();
	for (Table t : all) {
	    t.setDefPreText(t.getDescription());
	    tableService.updateDescription(t.getId(), t.getDescription());
	}
	// tableService.save(allPhinVades);
    }

    private void changeCommentToAuthorNotes() {
	// for messages
	List<Message> messages = messageService.findByScope(SCOPE.USER.toString());
	for (Message m : messages) {
	    if (m.getComment() != null) {
		messageService.updateAttribute(m.getId(), "authorNotes", m.getComment());
	    }
	}
	// for Segment
	List<Segment> segments = segmentService.findByScope(SCOPE.USER.toString());
	for (Segment s : segments) {
	    if (s.getComment() != null) {
		segmentService.updateAttribute(s.getId(), "authorNotes", s.getComment());
	    }
	}
	// for Datatypes
	List<Datatype> datatypes = datatypeService.findAll();
	for (Datatype d : datatypes) {
	    if (d.getStatus().equals(STATUS.UNPUBLISHED) && d.getComment() != null) {
		datatypeService.updateAttribute(d.getId(), "authorNotes", d.getComment());
	    }
	}
	// profile Components
	List<ProfileComponent> profileComponents = profileComponentService.findAll();
	for (ProfileComponent pc : profileComponents) {

	    if (pc.getComment() != null) {
		profileComponentService.updateAttribute(pc.getId(), "authorNotes", pc.getComment());
	    } else {
		profileComponentService.updateAttribute(pc.getId(), "authorNotes", "");
	    }
	}
	List<CompositeProfileStructure> compositesPCs = compositeProfileStructureService.findAll();
	for (CompositeProfileStructure c : compositesPCs) {
	    if (c.getComment() != null) {

		compositeProfileStructureService.updateAttribute(c.getId(), "authorNotes", c.getComment());
	    } else {
		compositeProfileStructureService.updateAttribute(c.getId(), "authorNotes", "");

	    }
	}
    }

    private void initializeAttributes() {

	List<Table> allPH = tableService.findByScope(SCOPE.PHINVADS.toString());
	for (Table t : allPH) {

	    tableService.updateAttributes(t.getId(), "stability", Stability.fromValue("Undefined"));
	    tableService.updateAttributes(t.getId(), "contentDefinition", ContentDefinition.fromValue("Undefined"));
	    tableService.updateAttributes(t.getId(), "extensibility", Extensibility.fromValue("Undefined"));

	}
	List<Table> allHl7 = tableService.findByScope(SCOPE.HL7STANDARD.toString());
	for (Table t : allHl7) {

	    tableService.updateAttributes(t.getId(), "stability", Stability.fromValue("Undefined"));
	    tableService.updateAttributes(t.getId(), "contentDefinition", ContentDefinition.fromValue("Undefined"));
	    tableService.updateAttributes(t.getId(), "extensibility", Extensibility.fromValue("Undefined"));

	}

    }

    private void addCodeSystemtoAllTables() {
	List<Table> allTables = tableService.findAll();
	for (Table t : allTables) {
	    addCodeSystemToTable(t);
	}
    }

    private void addCodeSystemToTable(Table t) {
	Set<String> codesSystemtoAdd = new HashSet<String>();

	for (Code c : t.getCodes()) {
	    if (c.getCodeSystem() != null && !c.getCodeSystem().isEmpty()) {
		codesSystemtoAdd.add(c.getCodeSystem());

	    }
	}
	tableService.updateCodeSystem(t.getId(), codesSystemtoAdd);
    }

    private void fixAllConstraints() {
	List<Message> messages = messageService.findAll();
	for (Message m : messages) {
	    for (ConformanceStatement cs : m.getConformanceStatements()) {
		if (cs != null && cs.getDescription() != null) {
		    cs.setDescription(cs.getDescription().replace("[1]", ""));
		}
	    }
	    for (Predicate p : m.getPredicates()) {
		if (p != null && p.getDescription() != null) {
		    p.setDescription(p.getDescription().replace("[1]", ""));
		}
	    }
	    messageService.save(m);
	}

	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    for (ConformanceStatement cs : s.getConformanceStatements()) {
		if (cs != null && cs.getDescription() != null) {
		    cs.setDescription(cs.getDescription().replace("[1]", ""));
		}
	    }
	    for (Predicate p : s.getPredicates()) {
		if (p != null && p.getDescription() != null) {
		    p.setDescription(p.getDescription().replace("[1]", ""));
		}
	    }
	    segmentService.save(s);
	}

	List<Datatype> datatypes = datatypeService.findAll();
	for (Datatype d : datatypes) {
	    for (ConformanceStatement cs : d.getConformanceStatements()) {
		if (cs != null && cs.getDescription() != null) {
		    cs.setDescription(cs.getDescription().replace("[1]", ""));
		}
	    }
	    for (Predicate p : d.getPredicates()) {
		if (p != null && p.getDescription() != null) {
		    p.setDescription(p.getDescription().replace("[1]", ""));
		}
	    }
	    datatypeService.save(d);
	}

    }

    private void fixCodeSysLOINC() {
	List<Table> tables = tableService.findAll();

	for (Table t : tables) {
	    boolean isChanged = false;

	    for (Code c : t.getCodes()) {
		if (c.getCodeSystem() != null && c.getCodeSystem().toLowerCase().equals("loinc")) {
		    isChanged = true;
		    c.setCodeSystem("LN");
		}
	    }
	    if (isChanged)
		tableService.save(t);
	}

    }

    private void updateGroupName() {
	List<Message> messages = messageService.findAll();

	for (Message m : messages) {
	    needUpdated = false;
	    for (SegmentRefOrGroup srog : m.getChildren()) {
		if (srog instanceof Group) {
		    Group g = (Group) srog;
		    visitGroup(g, m);
		}
	    }

	    if (needUpdated) {
		System.out.println("-------Message Updated----");
		messageService.save(m);
	    }
	}

    }

    private void visitGroup(Group g, Message m) {
	if (g.getName().contains(" ") || g.getName().contains(".")) {
	    System.out.println("-------FOUND Group Name with Space----");
	    System.out.println(m.getScope());
	    System.out.println(g.getName());
	    System.out.println(g.getStatus());
	    System.out.println(g.getCreatedFrom());
	    System.out.println(g.getHl7Version());
	    String newGroupName = g.getName();
	    newGroupName = newGroupName.replaceAll(" ", "_");
	    String[] groupNameSplit = newGroupName.split("\\.");
	    newGroupName = groupNameSplit[groupNameSplit.length - 1];
	    g.setName(newGroupName);
	    System.out.println(g.getName());
	    needUpdated = true;
	}

	for (SegmentRefOrGroup srog : g.getChildren()) {
	    if (srog instanceof Group) {
		Group child = (Group) srog;
		visitGroup(child, m);
	    }
	}
    }

    private void fixProfielComponentConfLength() {
	List<ProfileComponent> profileComponents = profileComponentService.findAll();
	for (ProfileComponent s : profileComponents) {
	    List<SubProfileComponent> children = s.getChildren();
	    if (children != null && !children.isEmpty()) {
		for (SubProfileComponent field : children) {
		    SubProfileComponentAttributes attributes = field.getAttributes();
		    if (attributes != null) {
			if ("0".equals(attributes.getOldConfLength())) {
			    attributes.setOldConfLength(DataElement.LENGTH_NA);
			}
			if ("0".equals(attributes.getConfLength())) {
			    attributes.setConfLength(DataElement.LENGTH_NA);
			}
		    }
		}
		profileComponentService.save(s);
	    }
	}

    }

    private void updateSegmentDatatypeDescription() {
	List<Segment> segments = segmentService.findAll();

	for (Segment s : segments) {
	    if (s.getScope().equals(SCOPE.HL7STANDARD)) {
		if (s.getDescription() == null || s.getDescription().equals("")) {
		    if (s.getName().equals("UB1")) {
			s.setDescription("UB82");
			segmentService.save(s);
		    }
		}
	    }
	}

	List<Datatype> dts = datatypeService.findAll();

	for (Datatype d : dts) {
	    if (d.getScope().equals(SCOPE.HL7STANDARD)) {
		if (d.getDescription() == null || d.getDescription().equals("")) {
		    d.setDescription("No Description");
		    datatypeService.save(d);
		}
	    }
	}
    }

    private void fixWrongConstraints() {
	List<Datatype> datatypes = datatypeService.findAll();
	for (Datatype d : datatypes) {
	    for (ConformanceStatement cs : d.getConformanceStatements()) {
		if (cs != null && cs.getAssertion() != null) {
		    if (cs.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong CS for DT-------");
			System.out.println(d.getLabel());

			String newAssertion = cs.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			cs.setAssertion(newAssertion);

			System.out.println(cs.getAssertion());

			datatypeService.save(d);
		    }
		}

	    }

	    for (Predicate p : d.getPredicates()) {
		if (p != null && p.getAssertion() != null) {
		    if (p.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong Predicate for DT-------");
			System.out.println(d.getLabel());

			String newAssertion = p.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			p.setAssertion(newAssertion);

			System.out.println(p.getAssertion());

			datatypeService.save(d);
		    }
		}
	    }
	}

	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    for (ConformanceStatement cs : s.getConformanceStatements()) {
		if (cs != null && cs.getAssertion() != null) {
		    if (cs.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong CS for Segment-------");
			System.out.println(s.getLabel());

			String newAssertion = cs.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			cs.setAssertion(newAssertion);

			System.out.println(cs.getAssertion());

			segmentService.save(s);
		    }
		}

	    }

	    for (Predicate p : s.getPredicates()) {
		if (p != null && p.getAssertion() != null) {
		    if (p.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong Predicate for Segment-------");
			System.out.println(s.getLabel());

			String newAssertion = p.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			p.setAssertion(newAssertion);

			System.out.println(p.getAssertion());

			segmentService.save(s);
		    }
		}
	    }
	}

	List<Message> messages = messageService.findAll();
	for (Message m : messages) {
	    for (ConformanceStatement cs : m.getConformanceStatements()) {
		if (cs != null && cs.getAssertion() != null) {
		    if (cs.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong CS for MEssage-------");
			System.out.println(m.getName());

			String newAssertion = cs.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			cs.setAssertion(newAssertion);

			System.out.println(cs.getAssertion());

			messageService.save(m);
		    }
		}

	    }

	    for (Predicate p : m.getPredicates()) {
		if (p != null && p.getAssertion() != null) {
		    if (p.getAssertion().contains("<IFTHEN>")) {
			System.out.println("---------FOUND Wrong Predicate for MEssage-------");
			System.out.println(m.getName());

			String newAssertion = p.getAssertion().replaceAll("<IFTHEN>", "<IMPLY>");
			newAssertion = newAssertion.replaceAll("</IFTHEN>", "</IMPLY>");

			p.setAssertion(newAssertion);

			System.out.println(p.getAssertion());

			messageService.save(m);
		    }
		}
	    }
	}

    }

    private void hotfix() {
	List<Segment> segments = segmentService.findAll();

	for (Segment s : segments) {
	    if (s.getName().equals("PID")) {
		List<ValueSetOrSingleCodeBinding> vsbList = s.getValueSetBindings();

		ValueSetOrSingleCodeBinding tobeDel = null;
		for (ValueSetOrSingleCodeBinding vsb : vsbList) {
		    if (vsb.getLocation().equals("5")) {
			tobeDel = vsb;
		    }
		}

		if (tobeDel != null) {
		    vsbList.remove(tobeDel);
		    segmentService.save(s);
		}
	    }
	}
    }

    // private void fixNullLengthAndConfLength() {
    // List<Segment> segments = segmentService.findAll();
    // for (Segment s : segments) {
    // List<Field> fields = s.getFields();
    // if (fields != null && !fields.isEmpty()) {
    // for (Field field : fields) {
    // String maxLength = field.getMaxLength();
    // String minLength = field.getMaxLength();
    // String confLength = field.getConfLength();
    // if (confLength == null || StringUtils.isEmpty(confLength)
    // || StringUtils.isBlank(confLength)) {
    // field.setConfLength(DataElement.LENGTH_NA);
    // }
    // if (minLength == null || StringUtils.isEmpty(minLength)
    // || StringUtils.isBlank(minLength)) {
    // field.setMinLength(DataElement.LENGTH_NA);
    // }
    // if (maxLength == null || StringUtils.isEmpty(maxLength)
    // || StringUtils.isBlank(maxLength)) {
    // field.setMinLength(DataElement.LENGTH_NA);
    // }
    // }
    // segmentService.save(s);
    // }
    // }
    // }

    private void fixConfLength() {
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    List<Field> fields = s.getFields();
	    if (fields != null && !fields.isEmpty()) {
		for (Field field : fields) {
		    String confLength = field.getConfLength();
		    if (confLength.equals("0")) {
			field.setConfLength(DataElement.LENGTH_NA);
		    }
		}
		segmentService.save(s);
	    }
	}

	List<Datatype> datatypes = datatypeService.findAll();
	for (Datatype s : datatypes) {
	    List<Component> components = s.getComponents();
	    if (components != null && !components.isEmpty()) {
		for (Component comp : components) {
		    String confLength = comp.getConfLength();
		    if (confLength.equals("0")) {
			comp.setConfLength(DataElement.LENGTH_NA);
		    }
		}
		datatypeService.save(s);
	    }
	}

    }

    private void refactorCoConstrint() {
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    if (s.getName().equals("OBX")) {
		if (s.getCoConstraints() != null) {
		    CoConstraints oldCoConstraints = s.getCoConstraints();
		    if (oldCoConstraints.getColumnList().size() > 0 && oldCoConstraints.getConstraints().size() > 0) {

			CoConstraintsTable newCoConstraintsTable = new CoConstraintsTable();

			newCoConstraintsTable.setRowSize(oldCoConstraints.getConstraints().size());

			CoConstraintColumnDefinition newIfColumnDefinition = new CoConstraintColumnDefinition();
			CoConstraintsColumn oldIFColumnDefinition = oldCoConstraints.getColumnList().get(0);
			Datatype oldChildId = datatypeService
				.findById(oldIFColumnDefinition.getField().getDatatype().getId());

			newIfColumnDefinition.setConstraintPath(oldIFColumnDefinition.getField().getPosition() + "[1]");
			newIfColumnDefinition.setPath(oldIFColumnDefinition.getField().getPosition() + "");
			newIfColumnDefinition.setType("field");
			newIfColumnDefinition.setName(oldChildId.getName());
			newIfColumnDefinition.setdMReference(false);
			newIfColumnDefinition.setDtId(oldIFColumnDefinition.getField().getDatatype().getId());
			newIfColumnDefinition.setId(ObjectId.get().toString());

			if (oldChildId.getComponents() != null && oldChildId.getComponents().size() > 0) {
			    newIfColumnDefinition.setPrimitive(false);
			} else {
			    newIfColumnDefinition.setPrimitive(true);
			}

			if (oldIFColumnDefinition.getConstraintType().equals("vs")) {
			    newIfColumnDefinition.setConstraintType("valueset");
			} else {
			    newIfColumnDefinition.setConstraintType("value");
			}
			newIfColumnDefinition.setUsage(oldIFColumnDefinition.getField().getUsage());

			newCoConstraintsTable.setIfColumnDefinition(newIfColumnDefinition);

			List<CoConstraintUserColumnDefinition> newUserColumnDefinitionList = new ArrayList<CoConstraintUserColumnDefinition>();

			CoConstraintUserColumnDefinition newDescriptionColumnDefinition = new CoConstraintUserColumnDefinition();
			CoConstraintUserColumnDefinition newCommentsColumnDefinition = new CoConstraintUserColumnDefinition();

			newDescriptionColumnDefinition.setId("Description");
			newDescriptionColumnDefinition.setTitle("Description");
			newCommentsColumnDefinition.setId("Comments");
			newCommentsColumnDefinition.setTitle("Comments");

			newUserColumnDefinitionList.add(newDescriptionColumnDefinition);
			newUserColumnDefinitionList.add(newCommentsColumnDefinition);

			newCoConstraintsTable.setUserColumnDefinitionList(newUserColumnDefinitionList);

			List<CoConstraintIFColumnData> ifColumnData = new ArrayList<CoConstraintIFColumnData>();
			Map<String, List<CoConstraintUSERColumnData>> userMapData = new HashMap<String, List<CoConstraintUSERColumnData>>();
			List<CoConstraintUSERColumnData> newDescriptionColumnData = new ArrayList<CoConstraintUSERColumnData>();
			List<CoConstraintUSERColumnData> newCommentsColumnData = new ArrayList<CoConstraintUSERColumnData>();

			for (CoConstraint cc : oldCoConstraints.getConstraints()) {
			    CoConstraintIFColumnData newCoConstraintIFColumnData = new CoConstraintIFColumnData();
			    ValueData valueData = new ValueData();
			    valueData.setValue(cc.getValues().get(0).getValue());
			    newCoConstraintIFColumnData.setValueData(valueData);
			    ifColumnData.add(newCoConstraintIFColumnData);

			    CoConstraintUSERColumnData newCoConstraintDESCColumnData = new CoConstraintUSERColumnData();
			    newCoConstraintDESCColumnData.setText(cc.getDescription());
			    newDescriptionColumnData.add(newCoConstraintDESCColumnData);

			    CoConstraintUSERColumnData newCoConstraintCOMMENTSColumnData = new CoConstraintUSERColumnData();
			    newCoConstraintCOMMENTSColumnData.setText(cc.getComments());
			    newCommentsColumnData.add(newCoConstraintCOMMENTSColumnData);
			}

			userMapData.put("Description", newDescriptionColumnData);
			userMapData.put("Comments", newCommentsColumnData);
			newCoConstraintsTable.setIfColumnData(ifColumnData);
			newCoConstraintsTable.setUserMapData(userMapData);

			List<CoConstraintColumnDefinition> thenColumnDefinitionList = new ArrayList<CoConstraintColumnDefinition>();
			Map<String, List<CoConstraintTHENColumnData>> thenMapData = new HashMap<String, List<CoConstraintTHENColumnData>>();

			for (int i = 1; i < oldCoConstraints.getColumnList().size(); i++) {
			    CoConstraintColumnDefinition newTHENColumnDefinition = new CoConstraintColumnDefinition();
			    CoConstraintsColumn oldTHENColumnDefinition = oldCoConstraints.getColumnList().get(i);

			    Datatype oldChildIdOfTHEN = datatypeService
				    .findById(oldTHENColumnDefinition.getField().getDatatype().getId());

			    newTHENColumnDefinition
				    .setConstraintPath(oldTHENColumnDefinition.getField().getPosition() + "[1]");
			    newTHENColumnDefinition.setPath(oldTHENColumnDefinition.getField().getPosition() + "");
			    newTHENColumnDefinition.setType("field");
			    newTHENColumnDefinition.setName(oldChildIdOfTHEN.getName());
			    if (oldTHENColumnDefinition.getField().getPosition().equals(2)) {
				newTHENColumnDefinition.setdMReference(true);
				newTHENColumnDefinition.setConstraintType("dmr");
			    } else {
				newTHENColumnDefinition.setdMReference(false);
				if (oldTHENColumnDefinition.getConstraintType().equals("vs")) {
				    newTHENColumnDefinition.setConstraintType("valueset");
				} else {
				    newTHENColumnDefinition.setConstraintType("value");
				}
			    }

			    newTHENColumnDefinition.setDtId(oldTHENColumnDefinition.getField().getDatatype().getId());
			    newTHENColumnDefinition.setId(ObjectId.get().toString());

			    if (oldChildIdOfTHEN.getComponents() != null
				    && oldChildIdOfTHEN.getComponents().size() > 0) {
				newTHENColumnDefinition.setPrimitive(false);
			    } else {
				newTHENColumnDefinition.setPrimitive(true);
			    }

			    newTHENColumnDefinition.setUsage(oldTHENColumnDefinition.getField().getUsage());

			    thenColumnDefinitionList.add(newTHENColumnDefinition);
			    List<CoConstraintTHENColumnData> thenData = new ArrayList<CoConstraintTHENColumnData>();

			    for (CoConstraint cc : oldCoConstraints.getConstraints()) {
				String value = cc.getValues().get(oldTHENColumnDefinition.getColumnPosition())
					.getValue();
				CoConstraintTHENColumnData newCoConstraintTHENColumnData = new CoConstraintTHENColumnData();

				if (oldTHENColumnDefinition.getConstraintType().equals("vs")) {
				    List<ValueSetData> valueSets = new ArrayList<ValueSetData>();
				    Table t = tableService.findById(value);
				    if (t != null) {
					ValueSetData valueSetData = new ValueSetData();
					valueSetData.setTableId(value);
					valueSets.add(valueSetData);
				    }
				    newCoConstraintTHENColumnData.setValueSets(valueSets);
				} else {
				    ValueData valueData = new ValueData();
				    valueData.setValue(value);
				    newCoConstraintTHENColumnData.setValue(valueData);
				}
				thenData.add(newCoConstraintTHENColumnData);
			    }
			    thenMapData.put(newTHENColumnDefinition.getId(), thenData);
			}

			newCoConstraintsTable.setThenColumnDefinitionList(thenColumnDefinitionList);
			newCoConstraintsTable.setThenMapData(thenMapData);

			s.setCoConstraintsTable(newCoConstraintsTable);
			segmentService.save(s);
		    }
		}
	    }
	}
    }

    private void DeleteProfileComponents() throws IGDocumentException {
	List<IGDocument> igDocuments = documentService.findAll();
	List<ProfileComponentLibrary> pcLibs = profileComponentLibraryService.findAll();
	profileComponentLibraryService.delete(pcLibs);
	for (IGDocument igd : igDocuments) {
	    if (igd.getProfile().getProfileComponentLibrary() != null
		    && igd.getProfile().getProfileComponentLibrary().getId() != null) {
		// igd.getProfile().setProfileComponentLibrary(null);
		igd.getProfile().getProfileComponentLibrary()
			.deleteAll(igd.getProfile().getProfileComponentLibrary().getChildren());
		profileComponentLibraryService.save(igd.getProfile().getProfileComponentLibrary());
		// profileComponentLibraryService.save(igd.getProfile().getProfileComponentLibrary());
	    }
	}

	List<ProfileComponent> pcs = profileComponentService.findAll();
	profileComponentService.delete(pcs);
	documentService.save(igDocuments);
    }

    private void updateProfileForMissingDTs() throws Exception {

	List<IGDocument> preloadedDocs = documentService.findAllPreloaded();
	List<IGDocument> usersDocs = documentService.findAllUser();

	List<IGDocument> preloadedAndUserDocs = new ArrayList<IGDocument>();
	preloadedAndUserDocs.addAll(preloadedDocs);
	preloadedAndUserDocs.addAll(usersDocs);

	for (IGDocument doc : preloadedAndUserDocs) {
	    Profile p = doc.getProfile();
	    boolean isChanged = false;
	    DatatypeLibrary datatypeLibrary = p.getDatatypeLibrary();
	    TableLibrary tableLibrary = p.getTableLibrary();
	    for (SegmentLink sl : p.getSegmentLibrary().getChildren()) {
		if (sl != null && sl.getId() != null) {
		    Segment s = segmentService.findById(sl.getId());

		    DynamicMappingDefinition dmd = s.getDynamicMappingDefinition();

		    if (dmd != null) {
			for (DynamicMappingItem dmi : dmd.getDynamicMappingItems()) {
			    if (datatypeLibrary.findOne(dmi.getDatatypeId()) == null) {
				System.out.println("----A. Found missing DT by DMD--------");
				Datatype dt = datatypeService.findById(dmi.getDatatypeId());
				if (dt != null) {
				    addDT(dt, datatypeLibrary, tableLibrary);
				}
				System.out.println("----B. Added missing DT by DMD--------");
				isChanged = true;
			    }
			}

			/*
			 * String vsId =
			 * dmd.getMappingStructure().getReferenceValueSetId();
			 * if (vsId != null) { Table t =
			 * tableService.findById(vsId);
			 * 
			 * if (tableLibrary.findOneTableById(vsId) == null) {
			 * System.out.println(
			 * "----H. Found missing Table by DMD--------");
			 * TableLink tl = new TableLink(); tl.setId(t.getId());
			 * tl.setBindingIdentifier(t.getBindingIdentifier());
			 * tableLibrary.addTable(tl); System.out.println(
			 * "----I. Added missing Table by DMD--------");
			 * isChanged = true; }
			 * 
			 * 
			 * for (Code c : t.getCodes()) { String dtName =
			 * c.getValue(); String hl7Version = null; hl7Version =
			 * t.getHl7Version(); if (hl7Version == null) hl7Version
			 * = s.getHl7Version(); if (hl7Version == null)
			 * hl7Version = doc.getMetaData().getHl7Version(); if
			 * (hl7Version == null) hl7Version = "2.8.2";
			 * 
			 * Datatype dt =
			 * datatypeService.findByNameAndVesionAndScope(dtName,
			 * hl7Version, "HL7STANDARD"); if (dt == null) {
			 * System.out.println(
			 * "-----------------ERROR--------DT NULL-------");
			 * System.out.println("dtName:" + dtName);
			 * System.out.println("hl7Version:" + hl7Version);
			 * System.out.println("TableID:" + t.getId()); } else {
			 * if (datatypeLibrary.findOne(dt.getId()) == null) {
			 * System.out.println(
			 * "----A-1. Found missing DT by Default--------");
			 * addDT(dt, datatypeLibrary, tableLibrary);
			 * System.out.println(
			 * "----B-1. Added missing DT by Default--------");
			 * isChanged = true; } } } }
			 */
		    }
		}
	    }

	    if (isChanged) {
		System.out.println("----Z. Profile changed and Saved--------");
		datatypeLibraryService.save(datatypeLibrary);
		tableLibraryService.save(tableLibrary);
		p.setDatatypeLibrary(datatypeLibrary);
		p.setTableLibrary(tableLibrary);
		doc.setProfile(p);
		profileService.save(p);
	    }

	}
    }

    private void addDT(Datatype dt, DatatypeLibrary datatypeLibrary, TableLibrary tableLibrary) {
	datatypeLibrary.addDatatype(dt);
	for (ValueSetOrSingleCodeBinding vsb : dt.getValueSetBindings()) {
	    Table t = tableService.findById(vsb.getTableId());
	    if (t != null) {
		tableLibrary.addTable(t);
	    }
	}
	for (Component c : dt.getComponents()) {
	    Datatype childDt = datatypeService.findById(c.getDatatype().getId());
	    addDT(childDt, datatypeLibrary, tableLibrary);
	}
    }

    private void updateUserExportConfigs() {
	List<ExportConfig> exportConfigs = exportConfig.findAll();
	for (ExportConfig exportConfig : exportConfigs) {
	    if (exportConfig != null) {
		ExportConfig defaultConfig = ExportConfig.getBasicExportConfig(false);
		if (exportConfig.getCodesExport() == null) {
		    exportConfig.setCodesExport(defaultConfig.getCodesExport());
		}
		if (exportConfig.getComponentExport() == null) {
		    exportConfig.setComponentExport(defaultConfig.getComponentExport());
		}
		if (exportConfig.getCompositeProfileColumn() == null) {
		    exportConfig.setCompositeProfileColumn(defaultConfig.getCompositeProfileColumn());
		}
		if (exportConfig.getDatatypeColumn() == null) {
		    exportConfig.setDatatypeColumn(defaultConfig.getDatatypeColumn());
		}
		if (exportConfig.getDatatypesExport() == null) {
		    exportConfig.setDatatypesExport(defaultConfig.getDatatypesExport());
		}
		if (exportConfig.getFieldsExport() == null) {
		    exportConfig.setFieldsExport(defaultConfig.getFieldsExport());
		}
		if (exportConfig.getMessageColumn() == null) {
		    exportConfig.setMessageColumn(defaultConfig.getMessageColumn());
		}
		if (exportConfig.getProfileComponentColumn() == null) {
		    exportConfig.setProfileComponentColumn(defaultConfig.getProfileComponentColumn());
		}
		if (exportConfig.getProfileComponentItemsExport() == null) {
		    exportConfig.setProfileComponentItemsExport(defaultConfig.getProfileComponentItemsExport());
		}
		if (exportConfig.getSegmentColumn() == null) {
		    exportConfig.setSegmentColumn(defaultConfig.getSegmentColumn());
		}
		if (exportConfig.getSegmentORGroupsCompositeProfileExport() == null) {
		    exportConfig.setSegmentORGroupsCompositeProfileExport(
			    defaultConfig.getSegmentORGroupsCompositeProfileExport());
		}
		if (exportConfig.getSegmentORGroupsMessageExport() == null) {
		    exportConfig.setSegmentORGroupsMessageExport(defaultConfig.getSegmentORGroupsMessageExport());
		}
		if (exportConfig.getSegmentsExport() == null) {
		    exportConfig.setSegmentsExport(defaultConfig.getSegmentsExport());
		}
		if (exportConfig.getValueSetColumn() == null) {
		    exportConfig.setValueSetColumn(defaultConfig.getValueSetColumn());
		}
		if (exportConfig.getValueSetsExport() == null) {
		    exportConfig.setValueSetsExport(defaultConfig.getValueSetsExport());
		}
		if (exportConfig.getValueSetsMetadata() == null) {
		    exportConfig.setValueSetsMetadata(defaultConfig.getValueSetsMetadata());
		}
		this.exportConfig.save(exportConfig);
	    }
	}
    }

    private void updateDMofSegment() {
	WebBeanConfig config = new WebBeanConfig();
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {

	    VariesMapItem item = this.findVariesMapItem(s.getName(), s.getHl7Version(),
		    config.igDocumentConfig().getVariesMapItems());
	    System.out.println("----0. ITEM--------" + item);
	    System.out.println("s.getName():::" + s.getName());
	    System.out.println("s.getHl7Version():::" + s.getHl7Version());

	    if (item != null) {
		System.out.println("----1. Found DM Segment--------");
		String valueSetId = this.findValueSetID(s.getValueSetBindings(), item.getReferenceLocation());
		System.out.println("----2. Found Referece ValusSetID--------::" + valueSetId);
		DynamicMappingDefinition dynamicMappingDefinition = new DynamicMappingDefinition();
		if (valueSetId == null)
		    System.out.println("-----------------ERROR--------valueSetId NULL-------");
		VariesMapItem mappingStructure = new VariesMapItem();
		List<DynamicMappingItem> dynamicMappingItems = new ArrayList<DynamicMappingItem>();
		mappingStructure.setHl7Version(item.getHl7Version());
		mappingStructure.setReferenceLocation(item.getReferenceLocation());
		mappingStructure.setSegmentName(item.getSegmentName());
		mappingStructure.setTargetLocation(item.getTargetLocation());

		DynamicMapping dm = s.getDynamicMapping();
		for (Mapping m : dm.getMappings()) {
		    if (m.getSecondReference() != null) {
			System.out.println("--------------CHECK----------------" + m.getSecondReference());
			if (m.getSecondReference().equals(3))
			    mappingStructure.setSecondRefereceLocation(m.getSecondReference() + ".1");
			else
			    mappingStructure.setSecondRefereceLocation(m.getSecondReference() + "");
		    }

		    for (Case c : m.getCases()) {
			if (c.getValue() != null && c.getDatatype() != null) {
			    DynamicMappingItem dmi = new DynamicMappingItem();
			    dmi.setFirstReferenceValue(c.getValue());
			    dmi.setDatatypeId(c.getDatatype());
			    if (m.getSecondReference() != null && c.getSecondValue() != null) {
				dmi.setSecondReferenceValue(c.getSecondValue());
			    }
			    dynamicMappingItems.add(dmi);
			}
		    }
		}

		dynamicMappingDefinition.setDynamicMappingItems(dynamicMappingItems);
		dynamicMappingDefinition.setMappingStructure(mappingStructure);
		s.setDynamicMappingDefinition(dynamicMappingDefinition);

		segmentService.save(s);
	    } else {
		if (s.getName().equals("OBX") || s.getName().equals("MFA") || s.getName().equals("MFE"))
		    System.out.println("-----------------ERROR--------ITEM NULL-------");
	    }
	}
    }

    private String findValueSetID(List<ValueSetOrSingleCodeBinding> valueSetBindings, String referenceLocation) {
	for (ValueSetOrSingleCodeBinding vsb : valueSetBindings) {
	    if (vsb.getLocation().equals(referenceLocation))
		return vsb.getTableId();
	}
	return null;
    }

    private VariesMapItem findVariesMapItem(String segmentName, String hl7Version, Set<VariesMapItem> variesMapItems) {
	for (VariesMapItem item : variesMapItems) {
	    if (item.getSegmentName().equals(segmentName) && item.getHl7Version().equals(hl7Version))
		return item;
	}
	return null;
    }

    private void fixUserDatatypesScope() throws IGDocumentException {
	List<Datatype> datatypes = datatypeService.findByScope(SCOPE.USER.toString());
	for (Datatype datatype : datatypes) {
	    if (datatype.getPublicationVersion() == 0) {
		datatype.setStatus(STATUS.UNPUBLISHED);
		datatypeService.save(datatype);
	    }
	}
    }

    private void fixValueSetNameAndDescription() {
	List<Table> allTables = tableService.findAll();
	for (Table t : allTables) {
	    if (null != t && !t.getScope().equals(SCOPE.PHINVADS)) {
		if (t.getDescription() != null) {
		    t.setName(t.getDescription());
		}
		t.setDescription(null);
		tableService.save(t);
	    }
	}
    }

    private void fixDatatypeRecursion(IGDocument document) throws IGDocumentException {
	DatatypeLibrary datatypeLibrary = document.getProfile().getDatatypeLibrary();
	Datatype withdrawn = getWithdrawnDatatype(document.getProfile().getMetaData().getHl7Version());
	DatatypeLink withdrawnLink = new DatatypeLink(withdrawn.getId(), withdrawn.getName(), withdrawn.getExt());
	Set<String> datatypeIds = new HashSet<String>();
	for (DatatypeLink datatypeLink : datatypeLibrary.getChildren()) {
	    datatypeIds.add(datatypeLink.getId());
	}
	List<Datatype> datatypes = datatypeService.findByIds(datatypeIds);
	for (Datatype datatype : datatypes) {
	    if (datatype != null) {
		List<Component> components = datatype.getComponents();
		if (components != null && !components.isEmpty()) {
		    for (Component component : components) {
			DatatypeLink componentDatatypeLink = component.getDatatype();
			if (componentDatatypeLink != null && componentDatatypeLink.getId() != null
				&& componentDatatypeLink.getId().equals(datatype.getId())) {
			    component.setDatatype(withdrawnLink);
			    if (!contains(withdrawnLink, datatypeLibrary)) {
				datatypeLibrary.addDatatype(withdrawnLink);
			    }
			}
		    }
		}
	    }
	}
	datatypeService.save(datatypes);
	daatypeLibraryRepository.save(datatypeLibrary);
    }

    private Datatype getWithdrawnDatatype(String hl7Version) throws IGDocumentException {
	List<Datatype> datatypes = datatypeService.findByNameAndVersionAndScope("-", hl7Version,
		SCOPE.HL7STANDARD.toString());
	Datatype dt = datatypes != null && !datatypes.isEmpty() ? datatypes.get(0) : null;
	if (dt == null) {
	    dt = new Datatype();
	    dt.setName("-");
	    dt.setDescription("withdrawn");
	    dt.setHl7versions(Arrays.asList(new String[] { hl7Version }));
	    dt.setHl7Version(hl7Version);
	    dt.setScope(SCOPE.HL7STANDARD);
	    dt.setDateUpdated(DateUtils.getCurrentDate());
	    dt.setStatus(STATUS.PUBLISHED);
	    dt.setPrecisionOfDTM(3);
	    datatypeService.save(dt);
	}
	return dt;
    }

    private void fixDatatypeRecursion() throws IGDocumentException {
	List<IGDocument> igDocuments = documentService.findAll();
	for (IGDocument document : igDocuments) {
	    fixDatatypeRecursion(document);
	}
    }

    private TableLink findTableLink(Set<TableLink> tableLinks, TableLink link) {
	if (tableLinks != null && !tableLinks.isEmpty() && link != null && link.getId() != null) {
	    Iterator<TableLink> it = tableLinks.iterator();
	    while (it.hasNext()) {
		TableLink tableLink = it.next();
		if (tableLink.getId() != null && tableLink.getId().equals(link.getId())) {
		    return tableLink;
		}
	    }
	}
	return null;
    }

    private boolean isTableDuplicated(TableLink tableLink, List<TableLink> tableLinks) {
	if (tableLink != null && tableLink.getId() != null && tableLinks != null && !tableLinks.isEmpty()) {
	    Table table = tableService.findOneShortById(tableLink.getId());
	    if (table != null) {
		for (int i = 0; i < tableLinks.size(); i++) {
		    TableLink link = tableLinks.get(i);
		    if (link != null && link.getBindingIdentifier() != null
			    && link.getBindingIdentifier().equals(tableLink.getBindingIdentifier())
			    && !tableLink.getId().equals(link.getId()) && sameScope(table, link)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    private boolean sameScope(Table table, TableLink link) {
	Table table2 = tableService.findOneShortById(link.getId());
	return table2 != null && table.getScope().equals(table2.getScope());
    }

    private void updateInitAndCreateCommentsForMessage() {
	List<Message> allMsgs = messageService.findAll();
	for (Message m : allMsgs) {
	    m.setComments(new ArrayList<Comment>());
	    for (SegmentRefOrGroup child : m.getChildren()) {
		updateCommentForSegRefOrGroup(m, child, null);
	    }

	    messageService.save(m);
	}
    }

    private void updateCommentForSegRefOrGroup(Message m, SegmentRefOrGroup srog, String parentPath) {
	String currentPath = null;
	if (parentPath == null)
	    currentPath = srog.getPosition() + "";
	else
	    currentPath = parentPath + "." + srog.getPosition();

	if (srog.getComment() != null && !srog.getComment().equals("")) {
	    Comment comment = new Comment();
	    comment.setDescription(srog.getComment());
	    comment.setLastUpdatedDate(new Date());
	    comment.setLocation(currentPath);

	    m.addComment(comment);
	    System.out.println("FOUND!!!!!!!!" + comment.getDescription());
	    // srog.setComment(null);
	}
	if (srog instanceof Group) {
	    for (SegmentRefOrGroup child : ((Group) srog).getChildren()) {
		updateCommentForSegRefOrGroup(m, child, currentPath);
	    }
	}
    }

    // private void updateInitAndCreateBindingAndCommentsVSForSegment() {
    // List<Segment> allSegs = segmentService.findAll();
    // for (Segment s : allSegs) {
    // s.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
    // s.setComments(new ArrayList<Comment>());
    // for (Field f : s.getFields()) {
    // if (f.getTables() != null) {
    // for (TableLink tl : f.getTables()) {
    // Table t = tableService.findById(tl.getId());
    // if (t != null) {
    // ValueSetBinding vsb = new ValueSetBinding();
    // vsb.setBindingLocation(tl.getBindingLocation());
    // vsb.setBindingStrength(tl.getBindingStrength());
    // vsb.setLocation(f.getPosition() + "");
    // vsb.setTableId(t.getId());
    // vsb.setUsage(f.getUsage());
    //
    // s.addValueSetBinding(vsb);
    // }
    // }
    // // f.setTables(null);
    // }
    // if (f.getComment() != null && !f.getComment().equals("")) {
    // Comment comment = new Comment();
    // comment.setDescription(f.getComment());
    // comment.setLastUpdatedDate(new Date());
    // comment.setLocation(f.getPosition() + "");
    //
    // s.addComment(comment);
    // // s.setComment(null);
    // }
    // }
    //
    // segmentService.save(s);
    // }
    // }

    // private void updateInitAndCreateBindingAndCommentsVSForDatatype() {
    // List<Datatype> allDts = datatypeService.findAll();
    // for (Datatype d : allDts) {
    // d.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
    // d.setComments(new ArrayList<Comment>());
    // for (Component c : d.getComponents()) {
    // if (c.getTables() != null) {
    // for (TableLink tl : c.getTables()) {
    // Table t = tableService.findById(tl.getId());
    // if (t != null) {
    // ValueSetBinding vsb = new ValueSetBinding();
    // vsb.setBindingLocation(tl.getBindingLocation());
    // vsb.setBindingStrength(tl.getBindingStrength());
    // vsb.setLocation(c.getPosition() + "");
    // vsb.setUsage(c.getUsage());
    // vsb.setTableId(t.getId());
    //
    // d.addValueSetBinding(vsb);
    // }
    // }
    // // c.setTables(null);
    // }
    // if (c.getComment() != null && !c.getComment().equals("")) {
    // Comment comment = new Comment();
    // comment.setDescription(c.getComment());
    // comment.setLastUpdatedDate(new Date());
    // comment.setLocation(c.getPosition() + "");
    //
    // d.addComment(comment);
    // // c.setComment(null);
    // }
    // }
    // datatypeService.save(d);
    // }
    // }

    private void fixMissingData(List<TableLink> tableLinks, TableLibrary tableLibrary) {
	if (tableLinks != null && !tableLinks.isEmpty()) {
	    for (TableLink tableLink : tableLinks) {
		if (tableLink != null && tableLink.getId() != null && !contains(tableLink, tableLibrary)) {
		    tableLibrary.addTable(tableLink);
		}
	    }
	}
    }

    private boolean contains(TableLink link, TableLibrary tableLibrary) {
	if (tableLibrary.getChildren() != null) {
	    for (TableLink tableLink : tableLibrary.getChildren()) {
		if (tableLink.getId() != null && tableLink.getId().equals(link.getId())) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean contains(DatatypeLink link, DatatypeLibrary datatypeLibrary) {
	if (datatypeLibrary.getChildren() != null) {
	    for (DatatypeLink datatypeLink : datatypeLibrary.getChildren()) {
		if (datatypeLink.getId() != null && datatypeLink.getId().equals(link.getId())) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * 
     */
    private void createDefaultConfiguration(String type) {
	ExportConfig defaultConfiguration = new ExportConfig();
	defaultConfiguration.setDefaultType(true);
	defaultConfiguration.setAccountId(null);
	defaultConfiguration.setIncludeMessageTable(true);
	defaultConfiguration.setIncludeSegmentTable(true);
	defaultConfiguration.setIncludeDatatypeTable(true);
	defaultConfiguration.setIncludeValueSetsTable(true);
	defaultConfiguration.setIncludeCompositeProfileTable(true);
	defaultConfiguration.setIncludeProfileComponentTable(true);
	// Default Usages
	UsageConfig displayAll = new UsageConfig();
	UsageConfig displaySelectives = new UsageConfig();
	displaySelectives.setC(true);
	displaySelectives.setX(false);
	displaySelectives.setO(false);
	displaySelectives.setR(true);
	displaySelectives.setRe(true);
	CodeUsageConfig codeUsageExport = new CodeUsageConfig();
	codeUsageExport.setE(false);
	codeUsageExport.setP(true);
	codeUsageExport.setR(true);

	displayAll.setC(true);
	displayAll.setRe(true);
	displayAll.setX(true);
	displayAll.setO(true);
	displayAll.setR(true);

	defaultConfiguration.setSegmentORGroupsMessageExport(displayAll);
	defaultConfiguration.setSegmentORGroupsCompositeProfileExport(displayAll);

	defaultConfiguration.setComponentExport(displayAll);

	defaultConfiguration.setFieldsExport(displayAll);
	defaultConfiguration.setProfileComponentItemsExport(displayAll);

	defaultConfiguration.setCodesExport(codeUsageExport);

	defaultConfiguration.setDatatypesExport(displaySelectives);
	defaultConfiguration.setSegmentsExport(displaySelectives);

	defaultConfiguration.setValueSetsExport(displaySelectives);

	ValueSetMetadataConfig valueSetMetadataConfig = new ValueSetMetadataConfig(true, true, true, true, true);
	defaultConfiguration.setValueSetsMetadata(valueSetMetadataConfig);

	// Default column
	ArrayList<NameAndPositionAndPresence> messageColumnsDefaultList = new ArrayList<NameAndPositionAndPresence>();

	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Segment", 1, true, true));
	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Flavor", 2, true, true));
	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Element Name", 3, true, true));
	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Cardinality", 4, true, false));
	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 5, true, false));
	messageColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 6, true, false));

	ArrayList<NameAndPositionAndPresence> segmentColumnsDefaultList = new ArrayList<NameAndPositionAndPresence>();
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Conformance Length", 2, false, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Data Type", 3, true, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Cardinality", 5, true, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Length", 6, false, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Value Set", 7, true, false));
	segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 8, true, false));

	ArrayList<NameAndPositionAndPresence> dataTypeColumnsDefaultList = new ArrayList<NameAndPositionAndPresence>();

	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Conformance Length", 2, false, false));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Data Type", 3, true, false));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, false));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Length", 5, false, false));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Value Set", 6, true, false));
	dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 7, true, false));

	defaultConfiguration.setDatatypeColumn(new ColumnsConfig(dataTypeColumnsDefaultList));
	defaultConfiguration.setSegmentColumn(new ColumnsConfig(segmentColumnsDefaultList));
	defaultConfiguration.setProfileComponentColumn(new ColumnsConfig(segmentColumnsDefaultList));
	defaultConfiguration.setMessageColumn(new ColumnsConfig(messageColumnsDefaultList));
	defaultConfiguration.setCompositeProfileColumn(new ColumnsConfig(messageColumnsDefaultList));

	ArrayList<NameAndPositionAndPresence> valueSetsDefaultList = new ArrayList<NameAndPositionAndPresence>();

	valueSetsDefaultList.add(new NameAndPositionAndPresence("Value", 1, true, true));
	valueSetsDefaultList.add(new NameAndPositionAndPresence("Code System", 2, true, true));
	valueSetsDefaultList.add(new NameAndPositionAndPresence("Usage", 3, false, false));
	valueSetsDefaultList.add(new NameAndPositionAndPresence("Description", 4, true, true));
	valueSetsDefaultList.add(new NameAndPositionAndPresence("Comment", 5, false, false));

	defaultConfiguration.setValueSetColumn(new ColumnsConfig(valueSetsDefaultList));

	exportConfig.save(defaultConfiguration);

    }

    private void createDefaultExportFonts() throws Exception {
	ExportFont exportFont = new ExportFont("'Arial Narrow',sans-serif", "'Arial Narrow',sans-serif;");
	exportFontService.save(exportFont);
	ExportFontConfig defaultExportFontConfig = exportFontConfigService.getDefaultExportFontConfig();
	if (defaultExportFontConfig != null) {
	    exportFontConfigService.delete(defaultExportFontConfig);
	}
	defaultExportFontConfig = new ExportFontConfig(exportFont, 10, true);
	exportFontConfigService.save(defaultExportFontConfig);
	exportFont = new ExportFont("\"Palatino Linotype\", \"Book Antiqua\", Palatino, serif",
		"\"Palatino Linotype\", \"Book Antiqua\", Palatino, serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Times New Roman\", Times, serif", "\"Times New Roman\", Times, serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("Georgia, serif", "Georgia, serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Comic Sans MS\", cursive, sans-serif",
		"\"Comic Sans MS\", cursive, sans-serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif",
		"\"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("Tahoma, Geneva, sans-serif", "Tahoma, Geneva, sans-serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Trebuchet MS\", Helvetica, sans-serif",
		"\"Trebuchet MS\", Helvetica, sans-serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("Verdana, Geneva, sans-serif", "Verdana, Geneva, sans-serif;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Courier New\", Courier, monospace", "\"Courier New\", Courier, monospace;");
	exportFontService.save(exportFont);
	exportFont = new ExportFont("\"Lucida Console\", Monaco, monospace", "\"Lucida Console\", Monaco, monospace;");
	exportFontService.save(exportFont);
    }

    private void changeStatusofPHINVADSTables() {
	List<Table> allTables = tableService.findAll();

	for (Table t : allTables) {
	    if (null != t && null != t.getScope()) {
		if (t.getScope().equals(SCOPE.PHINVADS) && STATUS.UNPUBLISHED.equals(t.getStatus())) {
		    tableService.updateStatus(t.getId(), STATUS.PUBLISHED);
		}
	    }
	}
    }

    private void fixMissingCodes(String sourceTableLibId, String targetTableLibId) {
	TableLibrary sourceLib = tableLibraryRepository.findById(sourceTableLibId);
	TableLibrary tagertLib = tableLibraryRepository.findById(targetTableLibId);

	for (TableLink targetLink : tagertLib.getChildren()) {
	    Table targetTable = tableService.findById(targetLink.getId());
	    if (targetTable.getScope().equals(SCOPE.USER)
		    && (targetTable.getCodes() == null || targetTable.getCodes().isEmpty())) {
		TableLink sourceLink = findTableLink(sourceLib, targetTable.getBindingIdentifier());
		if (sourceLink != null) {
		    Table sourceTable = tableService.findById(sourceLink.getId());
		    if (sourceTable != null) {
			targetTable.setCodes(sourceTable.getCodes());
			tableService.save(targetTable);
		    }
		}
	    }
	}
    }

    private void fixSegmentStatus() {
	List<Segment> allsegs = segmentService.findAll();
	for (Segment s : allsegs) {
	    if (null != s && null != s.getScope()) {
		if (s.getScope().equals(SCOPE.USER) && STATUS.PUBLISHED.equals(s.getStatus())) {
		    segmentService.updateStatus(s.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}
    }

    private TableLink findTableLink(TableLibrary library, String bindingIdentifier) {
	for (TableLink targetLink : library.getChildren()) {
	    if (targetLink.getBindingIdentifier() != null
		    && targetLink.getBindingIdentifier().equals(bindingIdentifier)) {
		return targetLink;
	    }
	}
	return null;
    }

    private void fixConstraints1() {
	List<Datatype> allDts = datatypeService.findAll();
	for (Datatype d : allDts) {
	    fixConstraint1(d.getConformanceStatements(), d.getPredicates());
	    datatypeService.save(d);
	}
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    fixConstraint1(s.getConformanceStatements(), s.getPredicates());
	    segmentService.save(s);
	}
    }

    private void fixConstraint1(List<ConformanceStatement> cs, List<Predicate> ps) {
	if (cs != null) {
	    for (ConformanceStatement c : cs) {
		if (c.getAssertion() != null && c.getAssertion().startsWith("<Assertion><IFTHEN>")) {
		    c.setAssertion(c.getAssertion().replaceAll(Pattern.quote("IFTHEN>"), "IMPLY>"));
		}
	    }
	}
	if (ps != null) {
	    for (Predicate p : ps) {
		if (p.getAssertion() != null && p.getAssertion().startsWith("<Assertion><IFTHEN>")) {
		    p.setAssertion(p.getAssertion().replaceAll(Pattern.quote("IFTHEN>"), "IMPLY>"));
		}
	    }
	}
    }

    private void fixUserPublishedData() {
	List<Table> allTables = tableService.findAll();
	for (Table t : allTables) {
	    if (null != t && null != t.getScope()) {
		if (t.getScope().equals(SCOPE.USER) && STATUS.PUBLISHED.equals(t.getStatus())) {
		    tableService.updateStatus(t.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}

	List<Datatype> allDts = datatypeService.findAll();
	for (Datatype d : allDts) {
	    if (null != d && null != d.getScope()) {
		if (d.getScope().equals(SCOPE.USER) && STATUS.PUBLISHED.equals(d.getStatus())) {
		    datatypeService.updateStatus(d.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}

	List<Segment> allsegs = segmentService.findAll();
	for (Segment s : allsegs) {
	    if (null != s && null != s.getScope()) {
		if (s.getScope().equals(SCOPE.USER) && STATUS.PUBLISHED.equals(s.getStatus())) {
		    segmentService.updateStatus(s.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}

	List<Message> allMessages = messageService.findAll();
	for (Message s : allMessages) {
	    if (null != s && null != s.getScope()) {
		if (s.getScope().equals(SCOPE.USER) && STATUS.PUBLISHED.equals(s.getStatus())) {
		    segmentService.updateStatus(s.getId(), STATUS.UNPUBLISHED);
		} else if (s.getScope().equals(SCOPE.HL7STANDARD) && STATUS.UNPUBLISHED.equals(s.getStatus())) {
		    segmentService.updateStatus(s.getId(), STATUS.PUBLISHED);
		}
	    }
	}
    }

    private void fixConfLengths() {
	List<Segment> segments = segmentService.findAll();
	for (Segment s : segments) {
	    List<Field> fields = s.getFields();
	    for (Field f : fields) {
		if ("-1".equals(f.getConfLength())) {
		    f.setConfLength("");
		}
	    }
	}
	segmentService.save(segments);

	List<Datatype> datatypes = datatypeService.findAll();
	for (Datatype s : datatypes) {
	    List<Component> components = s.getComponents();
	    for (Component f : components) {
		if ("-1".equals(f.getConfLength())) {
		    f.setConfLength("");
		}
	    }
	}
	datatypeService.save(datatypes);
    }

    private void modifyCodeUsage() {
	List<Table> allTables = tableService.findAll();

	for (Table t : allTables) {
	    boolean isChanged = false;
	    for (Code c : t.getCodes()) {
		if (c.getCodeUsage() == null) {
		    c.setCodeUsage("P");
		    isChanged = true;
		} else if (!c.getCodeUsage().equals("R") && !c.getCodeUsage().equals("P")
			&& !c.getCodeUsage().equals("E")) {
		    c.setCodeUsage("P");
		    isChanged = true;
		}
	    }
	    if (isChanged) {
		tableService.save(t);
		logger.info("Table " + t.getId() + " has been updated by the codeusage issue.");
	    }
	}
    }

    private void setTablesStatus() {
	List<Table> allTables = tableService.findAll();
	for (Table t : allTables) {
	    if (null != t && null != t.getScope()) {
		if (t.getScope().equals(SCOPE.HL7STANDARD) || t.getScope().equals(SCOPE.PRELOADED)) {
		    tableService.updateStatus(t.getId(), STATUS.PUBLISHED);
		} else if (!STATUS.PUBLISHED.equals(t.getStatus())) {
		    tableService.updateStatus(t.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}
    }

    private void setDtsStatus() {
	List<Datatype> allDts = datatypeService.findAll();
	for (Datatype d : allDts) {
	    if (null != d && null != d.getScope()) {
		if (d.getScope().equals(SCOPE.HL7STANDARD) || d.getScope().equals(SCOPE.PRELOADED)) {
		    datatypeService.updateStatus(d.getId(), STATUS.PUBLISHED);
		} else if (!STATUS.PUBLISHED.equals(d.getStatus())) {
		    datatypeService.updateStatus(d.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}
    }

    private void setSegmentStatus() {
	List<Segment> allsegs = segmentService.findAll();
	for (Segment s : allsegs) {
	    if (null != s && null != s.getScope()) {
		if (s.getScope().equals(SCOPE.HL7STANDARD) || s.getScope().equals(SCOPE.PRELOADED)) {
		    segmentService.updateStatus(s.getId(), STATUS.PUBLISHED);
		} else if (!STATUS.PUBLISHED.equals(s.getStatus())) {
		    segmentService.updateStatus(s.getId(), STATUS.UNPUBLISHED);
		}
	    }
	}
    }

    private void modifyFieldUsage() {
	List<Segment> allSegments = segmentService.findAll();

	for (Segment s : allSegments) {
	    boolean isChanged = false;
	    for (Field f : s.getFields()) {
		if (f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)) {
		    f.setUsage(Usage.X);
		    isChanged = true;
		}
	    }
	    if (isChanged) {
		segmentService.save(s);
		logger.info("Segment " + s.getId() + " has been updated by the usage W/B issue.");
	    }
	}
    }

    private void createNewSectionIds() throws IGDocumentException {
	List<IGDocument> igs = documentService.findAll();
	for (IGDocument ig : igs) {
	    if (ig.getChildSections() != null && !ig.getChildSections().isEmpty()) {
		for (Section s : ig.getChildSections()) {
		}
	    }
	    documentService.save(ig);
	}

	setUpdatedDates(); // Run only once.

    }

    // correctProfileComp(); }

    private void correctProfileComp() throws IGDocumentException {

	List<IGDocument> igDocuments = documentService.findAll();
	for (IGDocument igd : igDocuments) {
	    Messages msgs = igd.getProfile().getMessages();
	    if (igd.getProfile().getProfileComponentLibrary().getId() == null) {

		profileComponentLibraryService.save(igd.getProfile().getProfileComponentLibrary());
	    }

	}
	documentService.save(igDocuments);
    }

    @SuppressWarnings("deprecation")
    private void setUpdatedDates() throws IGDocumentException {

	List<Datatype> datatypes = datatypeService.findAll();
	boolean changed = false;
	if (datatypes != null) {
	    for (Datatype d : datatypes) {
		if (d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) && d.getDate() != null) {
		    try {
			Date dateUpdated = parseDate(d.getDate());
			datatypeService.updateDate(d.getId(), dateUpdated);
		    } catch (ParseException e) {
			logger.info("Failed to parse date of datatype with id=" + d.getId() + ", Date=" + d.getDate());
		    }
		}
	    }
	}

	changed = false;
	List<Segment> segments = segmentService.findAll();
	if (segments != null) {
	    for (Segment d : segments) {
		if (d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) && d.getDate() != null) {
		    Date dateUpdated = null;
		    try {
			dateUpdated = parseDate(d.getDate());
			segmentService.updateDate(d.getId(), dateUpdated);
		    } catch (ParseException e) {
			logger.info("Failed to parse date of segment with id=" + d.getId() + ", Date=" + d.getDate());
		    }
		}
	    }
	}

	changed = false;
	List<Table> tables = tableService.findAll();
	if (tables != null) {
	    for (Table d : tables) {
		if (d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) && d.getDate() != null) {
		    Date dateUpdated = null;
		    try {
			dateUpdated = parseDate(d.getDate());
			tableService.updateDate(d.getId(), dateUpdated);
		    } catch (ParseException e) {
			logger.info("Failed to parse date of table with id=" + d.getId() + ", Date=" + d.getDate());
		    }
		}
	    }
	}

	changed = false;
	List<Message> messages = messageService.findAll();
	if (messages != null) {
	    for (Message d : messages) {
		if (d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) && d.getDate() != null) {
		    Date dateUpdated = null;
		    try {
			dateUpdated = parseDate(d.getDate());
			messageService.updateDate(d.getId(), dateUpdated);
		    } catch (ParseException e) {
			logger.info("Failed to parse date of message with id=" + d.getId() + ", Date=" + d.getDate());
		    }
		}
	    }

	}

	changed = false;
	List<IGDocument> documents = documentService.findAll();
	if (documents != null) {
	    for (IGDocument d : documents) {
		if (d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD)
			&& d.getMetaData().getDate() != null) {
		    Date dateUpdated = null;
		    try {
			dateUpdated = parseDate(d.getMetaData().getDate());
			documentService.updateDate(d.getId(), dateUpdated);
		    } catch (ParseException e) {
			logger.info("Failed to parse date of table with id=" + d.getId() + ", Date="
				+ d.getMetaData().getDate());
		    }
		}
	    }
	}

    }

    private Date parseDate(String dateString) throws ParseException {
	Date dateUpdated = parseDate(dateString, DateUtils.FORMAT);

	if (dateUpdated == null) {
	    dateUpdated = parseDate(dateString, "EEE MMM d HH:mm:ss zzz yyyy");
	}

	if (dateUpdated == null) {
	    dateUpdated = parseDate(dateString, Constant.mdy.toPattern());
	}

	if (dateUpdated != null)
	    return dateUpdated;

	throw new ParseException("", 1);
    }

    private Date parseDate(String dateString, String format) {
	Date dateUpdated = null;
	try {
	    dateUpdated = new SimpleDateFormat(format).parse(dateString);
	} catch (ParseException e) {
	    dateUpdated = null;
	}
	return dateUpdated;
    }

    //
    // private void modifyCodeUsage() {
    // List<Table> allTables = tableService.findAll();
    //
    // for (Table t : allTables) {
    // boolean isChanged = false;
    // for (Code c : t.getCodes()) {
    // if (c.getCodeUsage() == null) {
    // c.setCodeUsage("P");
    // isChanged = true;
    // } else if (!c.getCodeUsage().equals("R") && !c.getCodeUsage().equals("P")
    // && !c.getCodeUsage().equals("E")) {
    // c.setCodeUsage("P");
    // isChanged = true;
    // }
    // }
    // if (isChanged) {
    // tableService.save(t);
    // logger.info("Table " + t.getId() + " has been updated by the codeusage
    // issue.");
    // }
    // }
    // }
    //
    // private void modifyFieldUsage() {
    // List<Segment> allSegments = segmentService.findAll();
    //
    // for (Segment s : allSegments) {
    // boolean isChanged = false;
    // for (Field f : s.getFields()) {
    // if (f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)) {
    // f.setUsage(Usage.X);
    // isChanged = true;
    // }
    // }
    // if (isChanged) {
    // segmentService.save(s);
    // logger.info("Segment " + s.getId() + " has been updated by the usage W/B
    // issue.");
    // }
    // }
    // }
    // private void createNewSectionIds() throws IGDocumentException{
    // List<IGDocument> igs=documentService.findAll();
    // for(IGDocument ig: igs){
    // if(ig.getChildSections()!=null&& !ig.getChildSections().isEmpty()){
    // for(Section s : ig.getChildSections()){
    // ChangeIdInside(s);
    // }
    // }
    // documentService.save(ig);
    // }
    // }
    //
    // private void ChangeIdInside(Section s) {
    // s.setId(ObjectId.get().toString());
    // if(s.getChildSections()!=null&& !s.getChildSections().isEmpty()){
    // for(Section sub : s.getChildSections()){
    //
    // ChangeIdInside(sub);
    // }
    // }
    // }
    //
    // private void modifyMSH2Constraint(){
    // List<Segment> allSegments = segmentService.findAll();
    //
    // for (Segment s : allSegments) {
    // if(s.getName().equals("MSH")){
    // boolean isChanged = false;
    // for(ConformanceStatement cs: s.getConformanceStatements()){
    // if(cs.getConstraintTarget().equals("2[1]")){
    // cs.setDescription("The value of MSH.2 (Encoding Characters) SHALL be
    // '^~\\&'.");
    // cs.setAssertion("<Assertion><PlainText IgnoreCase=\"false\" Path=\"2[1]\"
    // Text=\"^~\\&amp;\"/></Assertion>");
    // isChanged = true;
    // }
    // }
    //
    // if (isChanged) {
    // segmentService.save(s);
    // logger.info("Segment " + s.getId() + " has been updated by CS issue");
    // }
    // }
    // }
    //
    // }
    //
    // private void modifyConstraint(){
    // List<Segment> allSegments = segmentService.findAll();
    //
    // for (Segment s : allSegments) {
    // if(!s.getName().equals("MSH")){
    // boolean isChanged = false;
    // ConformanceStatement wrongCS = null;
    // for(ConformanceStatement cs: s.getConformanceStatements()){
    // if(cs.getConstraintTarget().equals("2[1]")){
    // if(cs.getDescription().startsWith("The value of MSH.2")){
    // wrongCS = cs;
    // isChanged = true;
    // }
    // }
    // }
    //
    // if (isChanged) {
    // s.getConformanceStatements().remove(wrongCS);
    // segmentService.save(s);
    // logger.info("Segment " + s.getId() + " has been updated by CS issue");
    // }
    // }
    // }
    //
    // }
    //
    private void modifyComponentUsage() {
	List<Datatype> allDatatypes = datatypeService.findAll();

	for (Datatype d : allDatatypes) {
	    boolean isChanged = false;
	    for (Component c : d.getComponents()) {
		if (c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)) {
		    c.setUsage(Usage.X);
		    isChanged = true;
		}
	    }
	    if (isChanged) {
		datatypeService.save(d);
		logger.info("Datatype " + d.getId() + " has been updated by the usage W/B issue.");
	    }
	}
    }

    // private void changeTabletoTablesInNewHl7() {
    // List<String> hl7Versions = new ArrayList<String>();
    // hl7Versions.add("2.7.1");
    // hl7Versions.add("2.8");
    // hl7Versions.add("2.8.1");
    // hl7Versions.add("2.8.2");
    // List<IGDocument> igDocuments =
    // documentService.findByScopeAndVersionsInIg(IGDocumentScope.HL7STANDARD,
    // hl7Versions);
    // for (IGDocument igd : igDocuments) {
    // Set<String> usedSegsId = new HashSet<String>();
    // SegmentLibrary segmentLib = igd.getProfile().getSegmentLibrary();
    // for (SegmentLink segLink : segmentLib.getChildren()) {
    // usedSegsId.add(segLink.getId());
    // }
    // List<Segment> usedSegs = segmentService.findByIds(usedSegsId);
    // for (Segment usedSeg : usedSegs) {
    // for (Field fld : usedSeg.getFields()) {
    // if (fld.getTable() != null) {
    // fld.getTables().add(fld.getTable());
    // System.out.println("Field Table Added=" + fld.getTable());
    // }
    // }
    // }
    // segmentService.save(usedSegs);
    // Set<String> usedDtsId = new HashSet<String>();
    // DatatypeLibrary datatypeLib = igd.getProfile().getDatatypeLibrary();
    // for (DatatypeLink dtLink : datatypeLib.getChildren()) {
    // usedSegsId.add(dtLink.getId());
    // }
    // List<Datatype> usedDts = datatypeService.findByIds(usedDtsId);
    // for (Datatype usedDt : usedDts) {
    // for (Component comp : usedDt.getComponents()) {
    // if (comp.getTable() != null) {
    // comp.getTables().add(comp.getTable());
    // System.out.println("Component Table Added=" + comp.getTable());
    // }
    // }
    // }
    // datatypeService.save(usedDts);
    // }
    //
    // }

    private void loadPreloadedIGDocuments() throws Exception {
	IGDocument d = new IGDocument();

	String p = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Profile.xml"));
	String v = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_ValueSetLibrary.xml"));
	String c = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Constraints.xml"));
	Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);

	profile.setScope(IGDocumentScope.PRELOADED);

	d.addProfile(profile);

	boolean existPreloadedDocument = false;

	String documentID = d.getMetaData().getIdentifier();
	String documentVersion = d.getMetaData().getVersion();

	List<IGDocument> igDocuments = documentService.findAll();

	for (IGDocument igd : igDocuments) {
	    if (igd.getScope().equals(IGDocumentScope.PRELOADED) && documentID.equals(igd.getMetaData().getIdentifier())
		    && documentVersion.equals(igd.getMetaData().getVersion())) {
		existPreloadedDocument = true;
	    }
	}
	if (!existPreloadedDocument)
	    documentService.save(d);
    }

    private void initMAp() {
	List<SCOPE> scopes = new ArrayList<SCOPE>();
	scopes.add(SCOPE.HL7STANDARD);
	List<Datatype> dataInit = datatypeService.findByScopesAndVersion(scopes, "2.3.1");
	for (Datatype dt : dataInit) {
	    ArrayList<List<String>> temp = new ArrayList<List<String>>();
	    List<String> version1 = new ArrayList<String>();
	    version1.add("2.3.1");
	    temp.add(version1);
	    DatatypeMap.put(dt.getName(), temp);

	}

    }

    private void AddVersiontoMap(String version) {
	Visited = new HashMap<String, Integer>();
	List<SCOPE> scopes = new ArrayList<SCOPE>();
	scopes.add(SCOPE.HL7STANDARD);
	List<Datatype> datatypesToAdd = datatypeService.findByScopesAndVersion(scopes, version);

	for (Datatype dt : datatypesToAdd) {
	    ArrayList<List<String>> temp = new ArrayList<List<String>>();
	    List<String> version2 = new ArrayList<String>();
	    version2.add(version);
	    temp.add(version2);
	    if (!DatatypeMap.containsKey(dt.getName())) {
		DatatypeMap.put(dt.getName(), temp);
	    } else {
		for (int i = 0; i < DatatypeMap.get(dt.getName()).size(); i++) {
		    List<Datatype> datatypes = datatypeService.findByNameAndVersionAndScope(dt.getName(),
			    DatatypeMap.get(dt.getName()).get(i).get(0), "HL7STANDARD");
		    Datatype d = null;
		    if (datatypes != null && !datatypes.isEmpty()) {
			d = datatypes.get(0);
		    }
		    if (d != null && !Visited.containsKey(dt.getName())) {
			if (deltaService.CompareDatatypes(d, dt)) {
			    DatatypeMap.get(dt.getName()).get(i).add(version);

			    System.out.println("FOUND IDENTIQUE");
			    Visited.put(dt.getName(), 1);
			}
		    }
		}
		if (!Visited.containsKey(dt.getName())) {
		    List<String> version2Add = new ArrayList<String>();
		    version2Add.add(version);
		    DatatypeMap.get(dt.getName()).add(version2Add);
		    Visited.put(dt.getName(), 1);
		}
	    }
	}
    }

    public void addAllVersions() {
	initMAp();
	String[] versions = { "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.7.1", "2.8", "2.8.1", "2.8.2" };
	// String[] versions = {"2.2","2.3"};
	for (int i = 0; i < versions.length; i++) {
	    AddVersiontoMap(versions[i]);
	}
    }

    public void CreateCollectionOfUnchanged() {
	addAllVersions();

	for (Entry<String, ArrayList<List<String>>> e : DatatypeMap.entrySet()) {
	    String name = e.getKey();
	    ArrayList<List<String>> values = e.getValue();
	    for (List<String> versions : values) {
		UnchangedDataType unchanged = new UnchangedDataType();
		unchanged.setName(name);
		unchanged.setVersions(versions);
		unchangedData.insert(unchanged);

	    }
	}
    }

    public void CreateIntermediateFromUnchanged() throws CloneNotSupportedException {
	List<UnchangedDataType> unchanged = unchangedData.findAll();
	for (UnchangedDataType dt : unchanged) {
	    List<Datatype> datatypes = datatypeService.findByNameAndVersionAndScope(dt.getName(),
		    dt.getVersions().get(dt.getVersions().size() - 1), SCOPE.HL7STANDARD.toString());
	    Datatype d = datatypes != null && !datatypes.isEmpty() ? datatypes.get(0) : null;
	    Datatype newDatatype = d.clone();
	    newDatatype.setId(null);
	    newDatatype.setHl7Version("*");
	    newDatatype.setScope(SCOPE.INTERMASTER);
	    newDatatype.setHl7versions(dt.getVersions());
	    datatypeService.save(newDatatype);

	}

	// List<Datatype> Inter = datatypeService.findByScope("INTERMASTER");
	// for (Datatype d : Inter) {
	// if (d.getComponents().size() != 0) {
	// MergeComponent(d);
	// datatypeService.save(d);
	// }
	//
	// }

    }

    /**
     * @param d
     * @throws Exception
     */
    private void MergeComponents() throws Exception {
	// TODO Auto-generated method stub
	List<Datatype> BeforeMerge = datatypeService.findByScope("INTERMASTER");
	for (Datatype dt : BeforeMerge) {
	    if (dt.getComponents().size() != 0) {
		for (Component c : dt.getComponents()) {
		    if (c.getDatatype() != null) {
			Datatype current = datatypeService.findById(c.getDatatype().getId());
			Datatype temp = datatypeService.findByCompatibleVersion(current.getName(),
				current.getHl7Version(), "INTERMASTER");
			c.getDatatype().setId(temp.getId());

		    }
		}
	    }
	    datatypeService.save(dt);
	}

    }

    public void Colorate() {
	addAllVersions();

	for (Entry<String, ArrayList<List<String>>> e : DatatypeMap.entrySet()) {
	    String name = e.getKey();
	    DatatypeMatrix dt = new DatatypeMatrix();
	    dt.setName(name);
	    HashMap<String, Integer> links = new HashMap<String, Integer>();

	    ArrayList<List<String>> values = e.getValue();
	    for (int i = 0; i < values.size(); i++) {
		for (String version : values.get(i)) {

		    links.put(version.replace(".", ""), i);
		}
	    }
	    dt.setLinks(links);
	    if (!dt.getName().equals("-")) {
		matrix.insert(dt);
	    }
	}
    }

    // NOTE:ADD version to preloaded segs,dts,vs
    private void addVersionAndScopetoHL7IG() {
	List<String> hl7Versions = new ArrayList<String>();

	hl7Versions.add("2.3.1");
	hl7Versions.add("2.4");
	hl7Versions.add("2.5");
	hl7Versions.add("2.5.1");
	hl7Versions.add("2.6");
	hl7Versions.add("2.7");

	List<IGDocument> igDocuments = documentService.findByScopeAndVersions(IGDocumentScope.HL7STANDARD, hl7Versions);
	for (IGDocument igd : igDocuments) {
	    Messages msgs = igd.getProfile().getMessages();
	    System.out.println(msgs.getChildren().size());
	    for (Message msg : msgs.getChildren()) {
		msg.setScope(SCOPE.HL7STANDARD);
		msg.setHl7Version(igd.getMetaData().getHl7Version());

	    }
	    messageService.save(msgs.getChildren());

	}
    }

    // private void addVersionAndScopetoPRELOADEDIG() {
    // List<String> hl7Versions = new ArrayList<String>();
    // hl7Versions.add("2.1");
    // hl7Versions.add("2.2");
    // hl7Versions.add("2.3");
    // hl7Versions.add("2.3.1");
    // hl7Versions.add("2.4");
    // hl7Versions.add("2.5");
    // hl7Versions.add("2.5.1");
    // hl7Versions.add("2.6");
    // hl7Versions.add("2.7");
    //
    // List<IGDocument> igDocuments =
    // documentService.findByScopeAndVersions(IGDocumentScope.PRELOADED,
    // hl7Versions);
    // Set<String> segIds = new HashSet<String>();
    // for (IGDocument igd : igDocuments) {
    // Messages msgs = igd.getProfile().getMessages();
    // for (Message msg : msgs.getChildren()) {
    // msg.setScope(SCOPE.PRELOADED);
    // for (SegmentRefOrGroup segRef : msg.getChildren()) {
    //
    // if (segRef instanceof SegmentRef) {
    // segIds.add(((SegmentRef) segRef).getRef().getId());
    // } else if (segRef instanceof Group) {
    // segIds.addAll(processGrp((Group) segRef));
    // }
    // }
    // private void addScopeUserToOldClonedPRELOADEDIG() {
    // List<String> hl7Versions = new ArrayList<String>();
    // // hl7Versions.add("2.1");
    // // hl7Versions.add("2.2");
    // // hl7Versions.add("2.3");
    // // hl7Versions.add("2.3.1");
    // // hl7Versions.add("2.4");
    // // hl7Versions.add("2.5");
    // hl7Versions.add("2.5.1");
    // // hl7Versions.add("2.6");
    // // hl7Versions.add("2.7");
    //
    // List<IGDocument> igDocuments =
    // documentService.findByScopeAndVersionsInIg(IGDocumentScope.USER,
    // hl7Versions);
    // for (IGDocument igd : igDocuments) {
    // Messages msgs = igd.getProfile().getMessages();
    // for (Message msg : msgs.getChildren()) {
    // if (SCOPE.USER.equals(msg.getScope()) ||
    // SCOPE.PRELOADED.equals(msg.getScope())) {
    // msg.setScope(SCOPE.USER);
    // }
    // List<Segment> preSegs = segmentService.findByIds(segIds);
    // Set<String> preDtsId = new HashSet<String>();
    // Set<String> preVssId = new HashSet<String>();
    // List<Segment> segToSave = new ArrayList<Segment>();
    // List<Datatype> dtToSave = new ArrayList<Datatype>();
    // List<Table> tableToSave = new ArrayList<Table>();
    // for (Segment seg : preSegs) {
    // if (seg.getScope() == SCOPE.USER) {
    // seg.setScope(SCOPE.PRELOADED);
    // for (Field fld : seg.getFields()) {
    // preDtsId.add(fld.getDatatype().getId());
    // for (TableLink t : fld.getTables()) {
    // preVssId.add(t.getId());
    // }
    // }
    // List<Datatype> preDts = datatypeService.findByIds(preDtsId);
    // // List<Table> preVss=tableService.findAllByIds(preVssId);
    // for (Datatype dt : preDts) {
    // if (dt.getScope() == SCOPE.USER) {
    // for (Component comp : dt.getComponents()) {
    // for (TableLink t : comp.getTables()) {
    // preVssId.add(t.getId());
    // }
    // }
    // dt.setScope(SCOPE.PRELOADED);
    // dtToSave.add(dt);
    //
    // }
    // }
    // Set<String> preDtsIdInComp = new HashSet<String>();
    // for (Datatype dtInComp : dtToSave) {
    // for (Component comp : dtInComp.getComponents()) {
    //
    // preDtsIdInComp.add(comp.getDatatype().getId());
    // }
    //
    // }
    // List<Datatype> preDtsInComp = datatypeService.findByIds(preDtsIdInComp);
    // for (Datatype dt : preDtsInComp) {
    // if (dt.getScope() == SCOPE.USER) {
    // for (Component comp : dt.getComponents()) {
    // for (TableLink t : comp.getTables()) {
    // preVssId.add(t.getId());
    // }
    // }
    // dt.setScope(SCOPE.PRELOADED);
    // dtToSave.add(dt);
    //
    // }
    // }
    // List<Table> preVs = tableService.findAllByIds(preVssId);
    //
    // for (Table preTable : preVs) {
    // if (preTable.getScope() == SCOPE.USER) {
    // preTable.setScope(SCOPE.PRELOADED);
    // tableToSave.add(preTable);
    // }
    // }
    // System.out.println(dtToSave);
    // segToSave.add(seg);
    //
    // }
    // Set<String> usedSegsId = new HashSet<String>();
    // SegmentLibrary segmentLib = igd.getProfile().getSegmentLibrary();
    // for (SegmentLink segLink : segmentLib.getChildren()) {
    // usedSegsId.add(segLink.getId());
    // }
    // List<Segment> usedSegs = segmentService.findByIds(usedSegsId);
    // for (Segment usedSeg : usedSegs) {
    // if (SCOPE.PRELOADED.equals(usedSeg.getScope())) {
    // usedSeg.setScope(SCOPE.USER);
    // }
    // segmentService.save(segToSave);
    // datatypeService.save(dtToSave);
    // tableService.save(tableToSave);
    //
    // msg.setHl7Version(igd.getMetaData().getHl7Version());
    // Set<String> usedDtsId = new HashSet<String>();
    // DatatypeLibrary datatypeLib = igd.getProfile().getDatatypeLibrary();
    // for (DatatypeLink dtLink : datatypeLib.getChildren()) {
    // usedDtsId.add(dtLink.getId());
    // }
    // List<Datatype> usedDts = datatypeService.findByIds(usedDtsId);
    // for (Datatype usedDt : usedDts) {
    // if (SCOPE.PRELOADED.equals((usedDt.getScope()))) {
    // usedDt.setScope(SCOPE.USER);
    // }
    // }
    // datatypeService.save(usedDts);
    //
    //
    // Set<String> usedTbsId = new HashSet<String>();
    // TableLibrary tableLib = igd.getProfile().getTableLibrary();
    // for (TableLink dtLink : tableLib.getChildren()) {
    // usedTbsId.add(dtLink.getId());
    // }
    // List<Table> usedTbs = tableService.findAllByIds(usedTbsId);
    // for (Table usedDt : usedTbs) {
    // if (SCOPE.PRELOADED.equals(usedDt.getScope())) {
    // usedDt.setScope(SCOPE.USER);
    // }
    //
    // }
    // messageService.save(msgs.getChildren());
    //
    //
    // }
    // }

    // private Set<String> SegIdsInMsg(Message msg, Set<String> result){
    // for(SegmentRefOrGroup segRef: msg.getChildren()){
    //
    // }
    // }
    private Set<String> processGrp(Group grp) {
	Set<String> result = new HashSet<String>();
	for (SegmentRefOrGroup segOrGrp : grp.getChildren()) {
	    if (segOrGrp instanceof SegmentRef) {
		result.add(((SegmentRef) segOrGrp).getRef().getId());
	    } else if (segOrGrp instanceof Group) {
		result.addAll(processGrp((Group) segOrGrp));
	    }
	}
	return result;

    }

    private void checkTableNameForAllIGDocuments() throws IGDocumentException {

	List<IGDocument> igDocuments = documentService.findAll();

	for (IGDocument igd : igDocuments) {
	    boolean ischanged = false;
	    TableLibrary tables = igd.getProfile().getTableLibrary();

	    for (TableLink tl : tables.getChildren()) {
		// if (t.getName() == null || t.getName().equals("")) {
		// if (t.getDescription() != null) {
		// t.setName(t.getDescription());
		// ischanged = true;
		// } else
		// t.setName("NONAME");
		// }
	    }

	    if (ischanged)
		documentService.apply(igd);
	}
    }

    private void AddVersionsToDatatypes() {
	List<Datatype> dts = datatypeService.findAll();
	for (Datatype d : dts) {
	    if (d.getHl7versions() != null && d.getHl7versions().isEmpty()) {
		if (!d.getScope().equals(SCOPE.MASTER)) {

		    d.getHl7versions().add(d.getHl7Version());
		    datatypeService.save(d);

		}
	    }
	}
    }

}
