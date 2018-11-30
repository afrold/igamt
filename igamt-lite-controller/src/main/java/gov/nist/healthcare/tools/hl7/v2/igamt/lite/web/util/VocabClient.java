package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;


import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.caucho.hessian.client.HessianProxyFactory;
import com.mongodb.MongoClient;

import gov.cdc.vocab.service.VocabService;
import gov.cdc.vocab.service.bean.Authority;
import gov.cdc.vocab.service.bean.CodeSystem;
import gov.cdc.vocab.service.bean.CodeSystemConcept;
import gov.cdc.vocab.service.bean.CodeSystemConceptAltDesignation;
import gov.cdc.vocab.service.bean.CodeSystemConceptPropertyValue;
import gov.cdc.vocab.service.bean.CodeSystemPropertyDefinition;
import gov.cdc.vocab.service.bean.Group;
import gov.cdc.vocab.service.bean.Source;
import gov.cdc.vocab.service.bean.ValueSet;
import gov.cdc.vocab.service.bean.ValueSetConcept;
import gov.cdc.vocab.service.bean.ValueSetVersion;
import gov.cdc.vocab.service.bean.View;
import gov.cdc.vocab.service.bean.ViewVersion;
import gov.cdc.vocab.service.dto.input.CodeSystemConceptSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.CodeSystemSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.GroupSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.ValueSetConceptSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.ValueSetSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.ValueSetVersionSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.ViewVersionSearchCriteriaDto;
import gov.cdc.vocab.service.dto.output.CodeSystemConceptResultDto;
import gov.cdc.vocab.service.dto.output.CodeSystemPropertyDefinitionResultDto;
import gov.cdc.vocab.service.dto.output.CodeSystemResultDto;
import gov.cdc.vocab.service.dto.output.FileImageResultDto;
import gov.cdc.vocab.service.dto.output.GroupResultDto;
import gov.cdc.vocab.service.dto.output.IdResultDto;
import gov.cdc.vocab.service.dto.output.ServiceInfoResultDto;
import gov.cdc.vocab.service.dto.output.ValueSetConceptResultDto;
import gov.cdc.vocab.service.dto.output.ValueSetResultDto;
import gov.cdc.vocab.service.dto.output.ValueSetVersionResultDto;
import gov.cdc.vocab.service.dto.output.ViewVersionResultDto;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TargetType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;

/**
 * Test Client - Connects to the VADS Web Service and Unit Tests the Service methods
 * 
 * @author Eady
 */
public class VocabClient {

  /**
   * Client Instance of the Web Service
   */
  private static VocabService service;

  /**
   * Constructs the Client instance of the Web Service
   */
  public VocabClient() {

    String serviceUrl = "https://phinvads.cdc.gov/vocabService/v2";

    HessianProxyFactory factory = new HessianProxyFactory();
    try {
      service = (VocabService) factory.create(VocabService.class, serviceUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the web service client
   */
  public VocabService getService() {
    return this.service;
  }

  /**
   * Unit tests the VADS Web Service.
   * 
   * @param args no arguments are needed.
   * @throws MalformedURLException
   * @throws UnknownHostException 
   */
  public static void main(String[] args) throws MalformedURLException, UnknownHostException {
    
    VocabService service;
    MongoOperations mongoOps;
    
    String serviceUrl = "https://phinvads.cdc.gov/vocabService/v2";

    HessianProxyFactory factory = new HessianProxyFactory();
    try {
      service = (VocabService) factory.create(VocabService.class, serviceUrl);
      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
      List<ValueSet> vss = service.getAllValueSets().getValueSets();
      
      for (ValueSet vs : vss) {
       System.out.println(vs.getCode() + "," + vs.getName() + "," + vs.getOid());
      }
      
      new VocabClient().tableSaveOrUpdate("2.16.840.1.114222.4.11.7830", mongoOps);
      
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    
    


//    VocabClient client = new VocabClient();
//
//    ValueSetSearchCriteriaDto vsSearchCrit = new ValueSetSearchCriteriaDto();
//    vsSearchCrit.setFilterByViews(false);
//    vsSearchCrit.setFilterByGroups(false);
//    vsSearchCrit.setCodeSearch(false);
//    vsSearchCrit.setNameSearch(false);
//    vsSearchCrit.setOidSearch(true);
//    vsSearchCrit.setDefinitionSearch(false);
//    vsSearchCrit.setSearchType(1);
//    vsSearchCrit.setSearchText("2.16.840.1.114222.4.11.7574");
//
//    ValueSetResultDto vsSearchResult = null;
//
//    vsSearchResult = service.findValueSets(vsSearchCrit, 1, 5);
//    List<ValueSet> valueSets = vsSearchResult.getValueSets();
//    
//    for(ValueSet vs :valueSets ){
//      System.out.println(vs);
//      vs = valueSets.get(0);
//      ValueSetVersion vsv = service.getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions()
//          .get(0);
//      
//      ValueSetConceptResultDto vscByVSVid = service.getValueSetConceptsByValueSetVersionId(vsv.getId(), 1, 100000);
//      List<ValueSetConcept> valueSetConcepts = vscByVSVid.getValueSetConcepts();
//      
//      for (ValueSetConcept pcode : valueSetConcepts) {
//        System.out.println(pcode.getCodeSystemConceptName());
//        System.out.println(pcode.getConceptCode());
//      }
//    }

  }

  private static String getElapsedMiliSeconds(Calendar cal1, Calendar cal2) {
    long cal1Time = cal1.getTimeInMillis();
    long cal2Time = cal2.getTimeInMillis();
    long diffTime = cal2Time - cal1Time;
    return "" + diffTime;
  }

  
  public Table tableSaveOrUpdate(String oid, MongoOperations mongoOps) {
    // 1. Get metadata from PHINVADS web service
    System.out.println("Get metadata from PHINVADS web service for " + oid);

    ValueSetSearchCriteriaDto vsSearchCrit = new ValueSetSearchCriteriaDto();
    vsSearchCrit.setFilterByViews(false);
    vsSearchCrit.setFilterByGroups(false);
    vsSearchCrit.setCodeSearch(false);
    vsSearchCrit.setNameSearch(false);
    vsSearchCrit.setOidSearch(true);
    vsSearchCrit.setDefinitionSearch(false);
    vsSearchCrit.setSearchType(1);
    vsSearchCrit.setSearchText(oid);

    ValueSetResultDto vsSearchResult = null;

    vsSearchResult = this.getService().findValueSets(vsSearchCrit, 1, 5);
    List<ValueSet> valueSets = vsSearchResult.getValueSets();

    ValueSet vs = null;
    ValueSetVersion vsv = null;
    if (valueSets != null && valueSets.size() > 0) {
      vs = valueSets.get(0);
      vsv = this.getService().getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions()
          .get(0);
      System.out.println("Successfully got the metadata from PHINVADS web service for " + oid);
      System.out.println(oid + " last updated date is " + vs.getStatusDate().toString());
      System.out.println(oid + " the Version number is " + vsv.getVersionNumber());

    } else {
      System.out.println("Failed to get the metadata from PHINVADS web service for " + oid);
    }

    // 2. Get Table from DB
    System.out.println("Get metadata from DB for " + oid);

    Table table = null;
    table = mongoOps.findOne(
        Query.query(Criteria.where("oid").is(oid).and("scope").is(Constant.SCOPE.PHINVADS)),
        Table.class);

    if (table != null) {
      System.out.println("Successfully got the metadata from DBe for " + oid);
      System.out.println(oid + " last updated date is " + table.getDate());
      System.out.println(oid + " the Version number is " + table.getVersion());
    } else {
      System.out.println("Failed to get the metadata from DB for " + oid);
    }

    ValueSetConceptResultDto vscByVSVid = null;
    List<ValueSetConcept> valueSetConcepts = null;

    // 3. compare metadata
    boolean needUpdate = false;
    if (vs != null && vsv != null) {
      if (table != null) {
        if (table.getDate().toString().equals(vs.getStatusDate().toString())
            && table.getVersion().equals(vsv.getVersionNumber() + "")) {
          if (table.getCodes().size() == 0 && table.getNumberOfCodes() == 0) {
            vscByVSVid =
                this.getService().getValueSetConceptsByValueSetVersionId(vsv.getId(), 1, 100000);
            valueSetConcepts = vscByVSVid.getValueSetConcepts();
            if (valueSetConcepts.size() != 0) {
              needUpdate = true;
              System.out.println(oid + " Table has no change! however local PHINVADS codes may be missing");
            }
          }
        } else {
          needUpdate = true;
          System.out.println(oid + " Table has a change! because different version number and date.");
        }
      } else {
        needUpdate = true;
        System.out.println(oid + " table is new one.");
      }
    } else {
      needUpdate = false;
      System.out.println(oid + " Table has no change! because PHINVADS does not have it.");
    }

    // 4. if updated, get full codes from PHINVADs web service
    if (needUpdate) {
      if (vscByVSVid == null)
        vscByVSVid =
            this.getService().getValueSetConceptsByValueSetVersionId(vsv.getId(), 1, 100000);
      if (valueSetConcepts == null)
        valueSetConcepts = vscByVSVid.getValueSetConcepts();
      if (table == null)
        table = new Table();
      List<ValueSetVersion> vsvByVSOid =
          this.getService().getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions();
      table.setBindingIdentifier(vs.getCode());
      // table.setDescription(vs.getDefinitionText());
      table.setDefPreText(vs.getDefinitionText().replaceAll("\u0019s", " "));
      table.setName(vs.getName());
      table.setOid(vs.getOid());
      table.setVersion("" + vsvByVSOid.get(0).getVersionNumber());
      table.setContentDefinition(ContentDefinition.Extensional);
      table.setExtensibility(Extensibility.Closed);
      table.setLibIds(new HashSet<String>());
      table.setScope(SCOPE.PHINVADS);
      table.setStability(Stability.Static);
      table.setStatus(STATUS.PUBLISHED);
      table.setType(Constant.TABLE);
      table.setComment(vsvByVSOid.get(0).getDescription());
      table.setDate(vs.getStatusDate().toString());
      table.setCodes(new ArrayList<Code>());
      table.setNumberOfCodes(valueSetConcepts.size());
      table.setManagedBy(Constant.External);
      table.setSourceType(SourceType.EXTERNAL);

      if (valueSetConcepts.size() > 500) {

      } else {
        for (ValueSetConcept pcode : valueSetConcepts) {
          Code code = new Code();
          code.setCodeUsage("P");
          code.setLabel(pcode.getCodeSystemConceptName());
          code.setValue(pcode.getConceptCode());
          CodeSystemSearchCriteriaDto csSearchCritDto = new CodeSystemSearchCriteriaDto();
          csSearchCritDto.setCodeSearch(false);
          csSearchCritDto.setNameSearch(false);
          csSearchCritDto.setOidSearch(true);
          csSearchCritDto.setDefinitionSearch(false);
          csSearchCritDto.setAssigningAuthoritySearch(false);
          csSearchCritDto.setTable396Search(false);
          csSearchCritDto.setSearchType(1);
          csSearchCritDto.setSearchText(pcode.getCodeSystemOid());
          CodeSystem cs =
              this.getService().findCodeSystems(csSearchCritDto, 1, 5).getCodeSystems().get(0);
          code.setCodeSystem(cs.getHl70396Identifier());
          code.setCodeSystemVersion(cs.getVersion());
          code.setComments(pcode.getDefinitionText());
          code.setType(Constant.CODE);
          table.addCode(code);
        }
      }

      // 5. update Table on DB
      try {
        table = this.fixValueSetDescription(table);
        System.out.println("This will be updated!!");
        System.out.println(table);
        mongoOps.save(table);
//        for (IGDocument ig : this.igDocs) {
//          if (ig.getProfile().getTableLibrary().findOneTableById(table.getId()) != null) {
//            Notification item = new Notification();
//            item.setByWhom("CDC");
//            item.setChangedDate(new Date());
//            item.setTargetType(TargetType.Valueset);
//            item.setTargetId(table.getId());
//            Criteria where = Criteria.where("igDocumentId").is(ig.getId());
//            Query qry = Query.query(where);
//            Notifications notifications = mongoOps.findOne(qry, Notifications.class);
//            if (notifications == null) {
//              notifications = new Notifications();
//              notifications.setIgDocumentId(ig.getId());
//              notifications.addItem(item);
//            }
//            mongoOps.save(notifications);
//            notificationEmail(notifications.getId());
//          }
//        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      return table;
    }
    return null;
  }

  private Table fixValueSetDescription(Table t) {
    String description = t.getDescription();
    if (description == null)
      description = "";
    else {
      description = description.replaceAll("\u0019s", " ");
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
    
    t.setDescription(description);
    t.setDefPostText(defPostText);
    t.setDefPreText(defPreText);
   
    return t;
  }

}
