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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeMatrix;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedDataType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeMatrixRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.UnchangedDataRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DataCorrectionSectionPosition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

@Service
public class Bootstrap implements InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final HashMap<String, ArrayList<List<String>>> DatatypeMap =
      new HashMap<String, ArrayList<List<String>>>();
  private HashMap<String, Integer> Visited = new HashMap<String, Integer>();

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
  TableService tableService;
  @Autowired
  DataCorrectionSectionPosition dataCorrectionSectionPosition;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
   }
  
  @SuppressWarnings("deprecation")
private void setUpdatedDates() throws IGDocumentException {
	  	
	    List<Datatype> datatypes = datatypeService.findAll();
	    boolean changed = false;
	    if(datatypes != null) {
	    for (Datatype d : datatypes) {
	       if(d.getScope() != null &&!d .getScope().equals(SCOPE.HL7STANDARD) && d.getDate() != null){
	    	   try {
				Date dateUpdated = parseDate(d.getDate());
 		    	datatypeService.updateDate(d.getId(),dateUpdated);
 				} catch (ParseException e) {
					 logger.info("Failed to parse date of datatype with id=" + d.getId()  + ", Date=" + d.getDate());
				}
	       }
	    }
	    }
	   
	    changed = false;	    
	    List<Segment> segments = segmentService.findAll();
	    if(segments != null) {
	    for (Segment d : segments) {
	       if(d.getScope() != null &&!d .getScope().equals(SCOPE.HL7STANDARD) &&   d.getDate() != null){
	    	   Date dateUpdated = null;
	    	   try {
				  dateUpdated = parseDate(d.getDate());
 				  segmentService.updateDate(d.getId(),dateUpdated);
 				} catch (ParseException e) {
					 logger.info("Failed to parse date of segment with id=" + d.getId()  + ", Date=" + d.getDate());
	 			}
	       }
	    }
	    } 
	    
	    changed = false;	    
	    List<Table> tables = tableService.findAll();
	    if(tables != null) {
	    for (Table d : tables) {
	       if(d.getScope() != null &&!d .getScope().equals(SCOPE.HL7STANDARD) &&  d.getDate() != null){
	    	   Date dateUpdated = null;
	    	   try {
				  dateUpdated = parseDate(d.getDate());
 				  tableService.updateDate(d.getId(),dateUpdated);
 				} catch (ParseException e) {
					 logger.info("Failed to parse date of table with id=" + d.getId()  + ", Date=" + d.getDate());
			    }
	       }
	    }
	    }
	    
	    
	    changed = false;	    
	    List<Message> messages = messageService.findAll();
	    if(messages != null) {
	    for (Message d : messages) {
	       if(d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) &&   d.getDate() != null){
	    	   Date dateUpdated = null;
	    	   try {	    		  
	    		  dateUpdated = parseDate(d.getDate());
 				  messageService.updateDate(d.getId(),dateUpdated);
 				} catch (ParseException e) {
					logger.info("Failed to parse date of message with id=" + d.getId() + ", Date=" + d.getDate());
	 			}
	       }
	    }
	    
	    }
	    
	    changed = false;	    
	    List<IGDocument> documents = documentService.findAll();
	    if(documents != null) {
	    for (IGDocument d : documents) {
	       if(d.getScope() != null && !d.getScope().equals(SCOPE.HL7STANDARD) &&   d.getMetaData().getDate() != null){
	    	   Date dateUpdated = null;
	    	   try {	    		  
	    		  dateUpdated = parseDate(d.getMetaData().getDate());
 				  documentService.updateDate(d.getId(),dateUpdated);
				} catch (ParseException e) {
					logger.info("Failed to parse date of table with id=" + d.getId() + ", Date=" + d.getMetaData().getDate());
	 			}
	       }
	    }
	    }
	    
  } 
  
  private Date parseDate(String dateString) throws ParseException{
	  Date dateUpdated = parseDate(dateString, DateUtils.FORMAT);
	  
	  if(dateUpdated == null){
		  dateUpdated = parseDate(dateString, "EEE MMM d HH:mm:ss zzz yyyy");
	  } 
	  
	  if(dateUpdated == null){
		  dateUpdated = parseDate(dateString, Constant.mdy.toPattern());
	  } 
	  
	  if(dateUpdated != null) 
		  return dateUpdated;
	  
	  throw new ParseException("", 1); 
  } 
  
  
  private Date parseDate(String dateString, String format){
	  Date dateUpdated = null;
	  try {
		  dateUpdated = new SimpleDateFormat(format).parse(dateString);
  		} catch (ParseException e) {
 			 dateUpdated = null;
	  }
	return dateUpdated;
  }
  
 	    

  
//
//  private void modifyCodeUsage() {
//    List<Table> allTables = tableService.findAll();
//
//    for (Table t : allTables) {
//      boolean isChanged = false;
//      for (Code c : t.getCodes()) {
//        if (c.getCodeUsage() == null) {
//          c.setCodeUsage("P");
//          isChanged = true;
//        } else if (!c.getCodeUsage().equals("R") && !c.getCodeUsage().equals("P")
//            && !c.getCodeUsage().equals("E")) {
//          c.setCodeUsage("P");
//          isChanged = true;
//        }
//      }
//      if (isChanged) {
//        tableService.save(t);
//        logger.info("Table " + t.getId() + " has been updated by the codeusage issue.");
//      }
//    }
//  }
//
//  private void modifyFieldUsage() {
//    List<Segment> allSegments = segmentService.findAll();
//
//    for (Segment s : allSegments) {
//      boolean isChanged = false;
//      for (Field f : s.getFields()) {
//        if (f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)) {
//          f.setUsage(Usage.X);
//          isChanged = true;
//        }
//      }
//      if (isChanged) {
//        segmentService.save(s);
//        logger.info("Segment " + s.getId() + " has been updated by the usage W/B issue.");
//      }
//    }
//  }
//  private void createNewSectionIds() throws IGDocumentException{
//	  List<IGDocument> igs=documentService.findAll();
//	  for(IGDocument ig: igs){
//		  if(ig.getChildSections()!=null&& !ig.getChildSections().isEmpty()){
//			  for(Section s : ig.getChildSections()){
//				  ChangeIdInside(s);
//			  }
//		  }
//		  documentService.save(ig);
//	  }
//  }
//  
//  private void ChangeIdInside(Section s) {
//	  s.setId(ObjectId.get().toString());
//	  if(s.getChildSections()!=null&& !s.getChildSections().isEmpty()){
//		  for(Section sub : s.getChildSections()){
//			 
//			  ChangeIdInside(sub);
//		  }
//	  }
//}
//
//private void modifyMSH2Constraint(){
//	  List<Segment> allSegments = segmentService.findAll();
//	  
//	  for (Segment s : allSegments) {
//		  if(s.getName().equals("MSH")){
//			  boolean isChanged = false;
//			  for(ConformanceStatement cs: s.getConformanceStatements()){
//				  if(cs.getConstraintTarget().equals("2[1]")){
//					  cs.setDescription("The value of MSH.2 (Encoding Characters) SHALL be '^~\\&'.");
//					  cs.setAssertion("<Assertion><PlainText IgnoreCase=\"false\" Path=\"2[1]\" Text=\"^~\\&amp;\"/></Assertion>");
//					  isChanged = true;
//				  }
//			  }
//			  
//			  if (isChanged) {
//				  segmentService.save(s);
//				  logger.info("Segment " + s.getId() + " has been updated by CS issue");
//			  }  
//		  }
//	  }
//	  
//  }
//
//private void modifyConstraint(){
//	  List<Segment> allSegments = segmentService.findAll();
//	  
//	  for (Segment s : allSegments) {
//		  if(!s.getName().equals("MSH")){
//			  boolean isChanged = false;
//			  ConformanceStatement wrongCS = null;
//			  for(ConformanceStatement cs: s.getConformanceStatements()){
//				  if(cs.getConstraintTarget().equals("2[1]")){
//					  if(cs.getDescription().startsWith("The value of MSH.2")){
//						  wrongCS = cs;
//						  isChanged = true;  
//					  }
//				  }
//			  }
//
//			  if (isChanged) {
//				  s.getConformanceStatements().remove(wrongCS);
//				  segmentService.save(s);
//				  logger.info("Segment " + s.getId() + " has been updated by CS issue");
//			  }  
//		  }
//	  }
//	  
//}
//
//  private void modifyComponentUsage() {
//    List<Datatype> allDatatypes = datatypeService.findAll();
//
//    for (Datatype d : allDatatypes) {
//      boolean isChanged = false;
//      for (Component c : d.getComponents()) {
//        if (c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)) {
//          c.setUsage(Usage.X);
//          isChanged = true;
//        }
//      }
//      if (isChanged) {
//        datatypeService.save(d);
//        logger.info("Datatype " + d.getId() + " has been updated by the usage W/B issue.");
//      }
//    }
//  }

  // private void changeTabletoTablesInNewHl7() {
  // List<String> hl7Versions = new ArrayList<String>();
  // hl7Versions.add("2.7.1");
  // hl7Versions.add("2.8");
  // hl7Versions.add("2.8.1");
  // hl7Versions.add("2.8.2");
  // List<IGDocument> igDocuments =
  // documentService.findByScopeAndVersionsInIg(IGDocumentScope.HL7STANDARD, hl7Versions);
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
    String v =
        IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_ValueSetLibrary.xml"));
    String c =
        IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Constraints.xml"));
    Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);

    profile.setScope(IGDocumentScope.PRELOADED);

    d.addProfile(profile);

    boolean existPreloadedDocument = false;

    String documentID = d.getMetaData().getIdentifier();
    String documentVersion = d.getMetaData().getVersion();

    List<IGDocument> igDocuments = documentService.findAll();

    for (IGDocument igd : igDocuments) {
      if (igd.getScope().equals(IGDocumentScope.PRELOADED)
          && documentID.equals(igd.getMetaData().getIdentifier())
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
    List<Datatype> dataInit = datatypeService.findByScopesAndVersion(scopes, "2.1");
    for (Datatype dt : dataInit) {
      ArrayList<List<String>> temp = new ArrayList<List<String>>();
      List<String> version1 = new ArrayList<String>();
      version1.add("2.1");
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
      // DatatypeMap.put(dt.getName(), temp);
      if (!DatatypeMap.containsKey(dt.getName())) {
        DatatypeMap.put(dt.getName(), temp);
      } else {
        for (int i = 0; i < DatatypeMap.get(dt.getName()).size(); i++) {
          Datatype d = datatypeService.findByNameAndVersionAndScope(dt.getName(),
              DatatypeMap.get(dt.getName()).get(i).get(0), "HL7STANDARD");
          if (d != null && !Visited.containsKey(dt.getName())) {
            if (d.isIdentique(dt)) {
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
    String[] versions = {"2.2", "2.3", "2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.7.1", "2.8",
        "2.8.1", "2.8.2"};
    // String[] versions = {"2.2","2.3"};
    for (int i = 0; i < versions.length; i++) {
      AddVersiontoMap(versions[i].toString());
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
      matrix.insert(dt);
    }
  }



  // NOTE:ADD version to preloaded segs,dts,vs
  private void addVersionAndScopetoHL7IG() {
    List<String> hl7Versions = new ArrayList<String>();
    hl7Versions.add("2.1");
    hl7Versions.add("2.2");
    hl7Versions.add("2.3");
    hl7Versions.add("2.3.1");
    hl7Versions.add("2.4");
    hl7Versions.add("2.5");
    hl7Versions.add("2.5.1");
    hl7Versions.add("2.6");
    hl7Versions.add("2.7");

    List<IGDocument> igDocuments =
        documentService.findByScopeAndVersions(IGDocumentScope.HL7STANDARD, hl7Versions);
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
  // documentService.findByScopeAndVersions(IGDocumentScope.PRELOADED, hl7Versions);
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
  // documentService.findByScopeAndVersionsInIg(IGDocumentScope.USER, hl7Versions);
  // for (IGDocument igd : igDocuments) {
  // Messages msgs = igd.getProfile().getMessages();
  // for (Message msg : msgs.getChildren()) {
  // if (SCOPE.USER.equals(msg.getScope()) || SCOPE.PRELOADED.equals(msg.getScope())) {
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
