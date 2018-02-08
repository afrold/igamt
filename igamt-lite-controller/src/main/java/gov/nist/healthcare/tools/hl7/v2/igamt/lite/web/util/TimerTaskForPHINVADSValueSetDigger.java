package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.caucho.hessian.client.HessianProxyFactory;
import com.mongodb.MongoClient;

import gov.cdc.vocab.service.VocabService;
import gov.cdc.vocab.service.bean.CodeSystem;
import gov.cdc.vocab.service.bean.ValueSet;
import gov.cdc.vocab.service.bean.ValueSetConcept;
import gov.cdc.vocab.service.bean.ValueSetVersion;
import gov.cdc.vocab.service.dto.input.CodeSystemSearchCriteriaDto;
import gov.cdc.vocab.service.dto.input.ValueSetSearchCriteriaDto;
import gov.cdc.vocab.service.dto.output.ValueSetConceptResultDto;
import gov.cdc.vocab.service.dto.output.ValueSetResultDto;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TargetType;

public class TimerTaskForPHINVADSValueSetDigger extends TimerTask {

  Logger log = LoggerFactory.getLogger(TimerTaskForPHINVADSValueSetDigger.class);
  private VocabService service;
  private MongoOperations mongoOps;
  private List<IGDocument> igDocs = null;

  public static void main(String[] args) throws IOException {
    TimerTaskForPHINVADSValueSetDigger tool = new TimerTaskForPHINVADSValueSetDigger();
    tool.run();

//     MongoOperations mongoOps =
//     new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
//     String tableId = "57ee310484ae2aadc10efcca";
//     List<IGDocument> igDocs = mongoOps
//     .find(Query.query(Criteria.where("scope").is(Constant.SCOPE.USER)), IGDocument.class);
//     for (IGDocument ig : igDocs) {
//     if (ig.getProfile().getTableLibrary().findOneTableById(tableId) != null) {
//     Notification item = new Notification();
//     item.setByWhom("CDC");
//     item.setChangedDate(new Date());
//     item.setTargetType(TargetType.Valueset);
//     item.setTargetId(tableId);
//    
//     Notifications notifications = new Notifications();
//     notifications.setIgDocumentId(ig.getId());
//     notifications.addItem(item);
//     mongoOps.save(notifications);
//    
//     notificationEmail(notifications.getId());
//     }
//     }
  }

  public TimerTaskForPHINVADSValueSetDigger() {

    String serviceUrl = "https://phinvads.cdc.gov/vocabService/v2";

    HessianProxyFactory factory = new HessianProxyFactory();
    try {
      setService((VocabService) factory.create(VocabService.class, serviceUrl));
      mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void run() {
    igDocs = mongoOps.find(Query.query(Criteria.where("scope").is(Constant.SCOPE.USER)),
        IGDocument.class);
    log.info("You have " + igDocs.size() + " igDocuemnts. ");

    log.info("PHINVADSValueSetDigger started at " + new Date());

    List<ValueSet> vss = this.service.getAllValueSets().getValueSets();
    log.info(vss.size() + " value sets' info has been found!");
    int count = 0;
    for (ValueSet vs : vss) {
      count++;
      log.info("########" + count + "/" + vss.size() + "########");
      this.tableSaveOrUpdate(vs.getOid());
    }
    log.info("PHINVADSValueSetDigger ended at " + new Date());
  }

  public Table tableSaveOrUpdate(String oid) {
    // 1. Get metadata from PHINVADS web service
    log.info("Get metadata from PHINVADS web service for " + oid);

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
      log.info("Successfully got the metadata from PHINVADS web service for " + oid);
      log.info(oid + " last updated date is " + vs.getStatusDate().toString());
      log.info(oid + " the Version number is " + vsv.getVersionNumber());

    } else {
      log.info("Failed to get the metadata from PHINVADS web service for " + oid);
    }

    // 2. Get Table from DB
    log.info("Get metadata from DB for " + oid);

    Table table = null;
    table = mongoOps.findOne(
        Query.query(Criteria.where("oid").is(oid).and("scope").is(Constant.SCOPE.PHINVADS)),
        Table.class);

    if (table != null) {
      log.info("Successfully got the metadata from DBe for " + oid);
      log.info(oid + " last updated date is " + table.getDate());
      log.info(oid + " the Version number is " + table.getVersion());
    } else {
      log.info("Failed to get the metadata from DB for " + oid);
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
              log.info(oid + " Table has no change! however local PHINVADS codes may be missing");
            }
          }
        } else {
          needUpdate = true;
          log.info(oid + " Table has a change! because different version number and date.");
        }
      } else {
        needUpdate = true;
        log.info(oid + " table is new one.");
      }
    } else {
      needUpdate = false;
      log.info(oid + " Table has no change! because PHINVADS does not have it.");
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
      table.setDefPreText(vs.getDefinitionText() != null ? vs.getDefinitionText().replaceAll("\u0019s", " "): null);
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
        mongoOps.save(table);
        for (IGDocument ig : this.igDocs) {
          if (ig.getProfile().getTableLibrary().findOneTableById(table.getId()) != null) {
            Notification item = new Notification();
            item.setByWhom("CDC");
            item.setChangedDate(new Date());
            item.setTargetType(TargetType.Valueset);
            item.setTargetId(table.getId());
            Criteria where = Criteria.where("igDocumentId").is(ig.getId());
            Query qry = Query.query(where);
            Notifications notifications = mongoOps.findOne(qry, Notifications.class);
            if (notifications == null) {
              notifications = new Notifications();
              notifications.setIgDocumentId(ig.getId());
              notifications.addItem(item);
            }
            mongoOps.save(notifications);
            notificationEmail(notifications.getId());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      return table;
    }
    return null;
  }

  private static void notificationEmail(String notificationsId) throws IOException {
    if (notificationsId != null) {
      String endpoint = System.getProperty("IGAMT_URL");
      if (endpoint != null) {
        String encodedNotificationsId = Base64.getEncoder().encodeToString(notificationsId.getBytes());
        endpoint = endpoint + "/api/notifications/" + encodedNotificationsId + "/sendEmail";
        
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) {
          throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        conn.disconnect();
      }
    }
  }

  public VocabService getService() {
    return service;
  }

  public void setService(VocabService service) {
    this.service = service;
  }

  public List<Table> findAllpreloadedPHINVADSTables() {
    List<Table> tables = new ArrayList<Table>();
    tables = mongoOps.find(Query.query(Criteria.where("scope").is(Constant.SCOPE.PHINVADS)),
        Table.class);

    for (Table t : tables) {
      t.setCodes(null);
    }
    return tables;
  }

  public List<Table> findAllpreloadedPHINVADSTablesBySearch(String searchValue) {
    List<Table> tables = new ArrayList<Table>();
    tables =
        mongoOps
            .find(
                Query.query(Criteria.where("scope")
                    .is(Constant.SCOPE.PHINVADS).and("bindingIdentifier").regex(Pattern
                        .compile(searchValue, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))),
                Table.class);

    for (Table t : tables) {
      t.setCodes(new ArrayList<Code>());
    }
    return tables;
  }
}
