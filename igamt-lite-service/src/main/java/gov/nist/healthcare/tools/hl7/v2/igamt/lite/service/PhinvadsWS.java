package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

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

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * Test Client - Connects to the VADS Web Service and Unit Tests the Service methods
 * @author Eady
 */
public class PhinvadsWS {

  /**
   * Client Instance of the Web Service
   */
  private VocabService service;

  /**
   * Constructs the Client instance of the Web Service
   */
  public PhinvadsWS(){

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
  public VocabService getService(){
    return this.service;
  }

  /**
   * Unit tests the VADS Web Service.
   * @param args no arguments are needed.
   * @throws MalformedURLException
   */
  public static void main(String[] args) throws MalformedURLException {

	  PhinvadsWS client = new PhinvadsWS();

    // Call getReleaseNotes()
    System.out.println("Calling getReleaseNotes()");
    FileImageResultDto fileResultDto = client.getService().getReleaseNotes();
    System.out.println("File length: " + fileResultDto.getFileImage().length);

    // Call getCodeSystemRepresentation()
    System.out.println("Calling getCodeSystemRepresentation()");
    fileResultDto = client.getService().getCodeSystemRepresentation();
    System.out.println("File length: " + fileResultDto.getFileImage().length);

    /*
     * getServiceInfo()
     */
    System.out.println("Calling getAllGroups()");
    ServiceInfoResultDto siResult = client.getService().getServiceInfo();
    System.out.println("Content Version/Date:" + siResult.getContentVersion() + "/" + siResult.getContentVersionDate());

    /*
     * Search for value set concepts
     */
    ValueSetConceptSearchCriteriaDto searchCrit = new ValueSetConceptSearchCriteriaDto();
    searchCrit.setConceptCodeSearch(true);
    searchCrit.setPreferredNameSearch(true);
    searchCrit.setConceptNameSearch(true);
    searchCrit.setAlternateNameSearch(true);
    searchCrit.setFilterByCodeSystems(false);
    searchCrit.setFilterByGroups(false);
    searchCrit.setFilterByViews(false);
    searchCrit.setFilterByValueSets(false);
    searchCrit.setSearchText("New"); // Should return new york, new jersey, etc.
    searchCrit.setSearchType(1);

    ValueSetConceptResultDto searchResult = null;

    searchResult = client.getService().findValueSetConcepts(searchCrit, 1, 5);
    System.out.println("Count is " + searchResult.getTotalResults());

    List<ValueSetConcept> valueSetConcepts = searchResult.getValueSetConcepts();

    for (ValueSetConcept e : valueSetConcepts) {
      System.out.println("CodeSystemOid is " + e.getCodeSystemOid());
      System.out.println("Concept Code is " + e.getConceptCode());
      System.out.println("Statuscode is " + e.getStatus());
      System.out.println("Concept Id is " + e.getId());
    }

    /*
     * Get All Value Sets
     */
    ValueSetResultDto vsResultDto = client.getService().getAllValueSets();
    List<ValueSet> valueSets = vsResultDto.getValueSets();
    System.out.println("result set size is " + valueSets.size());
    for (ValueSet e : valueSets) {
      System.out.println("ValueSet Name is " + e.getName());
    }

    /*
     * Get All Value Set Versions
     */
    ValueSetVersionResultDto vsvResultDto = client.getService().getAllValueSetVersions();
    List<ValueSetVersion> valueSetVersions = vsvResultDto.getValueSetVersions();
    System.out.println("result set size is " + valueSetVersions.size());
    for (ValueSetVersion e : valueSetVersions) {
      System.out.println("ValueSetVersion id and oid and status are " + e.getId() + " : " + e.getValueSetOid() + ":"
          + e.getStatus());
    }

    /*
     * Get All Views
     */
    List<View> views = client.getService().getAllViews().getViews();

    System.out.println("result set size is " + views.size());
    for (View e : views) {

      System.out.println("VocabularyView Name: " + e.getName() + " id: " + e.getId());
    }

    /*
     * Get All View Versions
     */
    List<ViewVersion> viewVersions = client.getService().getAllViewVersions().getViewVersions();

    System.out.println("result set size is " + viewVersions.size());
    for (ViewVersion e : viewVersions) {
      System.out.println("ViewVersion [Id=" + e.getId() + "][ViewId=" + e.getViewId() + "][Version="
          + e.getVersionNumber() + "][Reason=" + e.getReason() + "]");
    }

    /*
     * Get All Groups
     */
    System.out.println("Calling getAllGroups()");
    List<Group> groups = client.getService().getAllGroups().getGroups();
    for (Group e : groups) {
      System.out.println("Group Id is " + e.getId());
      System.out.println("Group Name is " + e.getName());
    }

    /*
     * Get All Code Systems
     */
    List<CodeSystem> codeSystems = client.getService().getAllCodeSystems().getCodeSystems();

    for (CodeSystem e : codeSystems) {

      System.out.println("Code System name is " + e.getName());
    }

    // GetValueSetConcept
    ValueSetConcept vsc = client.getService().getValueSetConceptById("BE673BB3-D280-DD11-B38D-00188B398520").getValueSetConcept();
    System.out.println("Get ValueSetConcept");
    System.out.println(vsc.getId());

    // GetValueSetVersion
    ValueSetVersion vsv = client.getService().getValueSetVersionById("80D34BBC-617F-DD11-B38D-00188B398520").getValueSetVersion();
    System.out.println("Get ValueSetVersion");
    System.out.println(vsv.getId());

    // GetCodeSystemConcept
    CodeSystemConcept csc = client.getService().getCodeSystemConceptByOidAndCode("2.16.840.1.113883.6.92", "16").getCodeSystemConcept();
    System.out.println("Get CodeSystemConcept");
    System.out.println(csc.getName());

    // GetCodeSystemConcept
    csc = client.getService().getCodeSystemConceptById("D6B69C5D-4D7F-DD11-B38D-00188B398520").getCodeSystemConcept();
    System.out.println("Get CodeSystemConceptById");
    System.out.println(csc.getName());

    // Find ValueSetVersion
    ValueSetVersionSearchCriteriaDto vsvSearchCrit = new ValueSetVersionSearchCriteriaDto();
    vsvSearchCrit.setFilterByViews(false);
    vsvSearchCrit.setFilterByGroups(false);
    vsvSearchCrit.setCodeSearch(true);
    vsvSearchCrit.setNameSearch(true);
    vsvSearchCrit.setOidSearch(false);
    vsvSearchCrit.setDefinitionSearch(false);
    vsvSearchCrit.setSearchText("State");
    vsvSearchCrit.setSearchType(1);
    vsvSearchCrit.setVersionOption(1);

    ValueSetVersionResultDto vsvSearchResult = null;

    vsvSearchResult = client.getService().findValueSetVersions(vsvSearchCrit, 1, 5);
    System.out.println("VSV Search Count is " + vsvSearchResult.getTotalResults());

    valueSetVersions = vsvSearchResult.getValueSetVersions();

    for (ValueSetVersion e : valueSetVersions) {
      System.out.println("VSV OID is: " + e.getValueSetOid());
      System.out.println("VSV description is: " + e.getDescription());
      System.out.println("VSV vNumber is: " + e.getVersionNumber());
    }

    // Find ValueSet
    ValueSetSearchCriteriaDto vsSearchCrit = new ValueSetSearchCriteriaDto();
    vsSearchCrit.setFilterByViews(false);
    vsSearchCrit.setFilterByGroups(false);
    vsSearchCrit.setCodeSearch(true);
    vsSearchCrit.setNameSearch(true);
    vsSearchCrit.setOidSearch(false);
    vsSearchCrit.setDefinitionSearch(false);
    vsSearchCrit.setSearchText("State");
    vsSearchCrit.setSearchType(1);

    ValueSetResultDto vsSearchResult = null;

    IdResultDto vsIds = client.getService().findValueSetIds(vsSearchCrit, 1, 5);
    System.out.println("VSID:" + vsIds.getId());
    vsSearchResult = client.getService().findValueSets(vsSearchCrit, 1, 5);
    System.out.println("VS Search Count is " + vsvSearchResult.getTotalResults());

    valueSets = vsSearchResult.getValueSets();

    for (ValueSet e : valueSets) {
      System.out.println("VS OID is: " + e.getOid());
      System.out.println("VS description is: " + e.getDefinitionText());
      System.out.println("VS name is: " + e.getName());
    }


    System.out.println("");
    System.out.println("Related Object Retrieval:");
    System.out.println("");

    // Call getGroupIdsByValueSetOid()
    List<String> groupIdsByVSOid = client.getService().getGroupIdsByValueSetOid("2.16.840.1.114222.4.11.830").getIds();
    System.out.println("Calling getGroupIdsByValueSetOid() returned " + groupIdsByVSOid.size() + " row(s):");
    for (String e : groupIdsByVSOid) {
      Group group = client.getService().getGroupById(e).getGroup();
      System.out.println("Group [Id=" + group.getId() + "][Name=" + group.getName() + "][Description="
          + group.getDescriptionText() + "]");
      // System.out.println("Group [Id=" + e + "]");
    }
    System.out.println("");

    // Call getViewVersionIdsByValueSetVersionId()
    List<String> viewVersionIdsByVSVId = client.getService().getViewVersionIdsByValueSetVersionId(
        "80D34BBC-617F-DD11-B38D-00188B398520").getIds();
    System.out.println("Calling getViewVersionIdsByValueSetVersionId() returned " + viewVersionIdsByVSVId.size()
        + " row(s):");
    for (String e : viewVersionIdsByVSVId) {
      ViewVersion vv = client.getService().getViewVersionById(e).getViewVersion();
      View view = client.getService().getViewById(vv.getViewId()).getView();
      System.out.println("ViewVersion [Id=" + vv.getId() + "][ViewId=" + vv.getViewId() + "][Name=" + view.getName()
          + "][Description=" + view.getDescriptionText() + "][Version=" + vv.getVersionNumber() + "][Reason="
          + vv.getReason() + "]");
      // System.out.println("ViewVersion [Id=" + e + "]");
    }
    System.out.println("");

    // Call getValueSetVersionsByValueSetOid
    List<ValueSetVersion> vsvByVSOid = client.getService().getValueSetVersionsByValueSetOid("2.16.840.1.114222.4.11.830")
        .getValueSetVersions();
    System.out.println("Calling getValueSetVersionsByValueSetOid() returned " + vsvByVSOid.size() + " row(s):");
    for (ValueSetVersion e : vsvByVSOid) {
      System.out.println("VSVByVSOID [Id=" + e.getId() + "][VersionNumber=" + e.getVersionNumber() + "]");
    }
    System.out.println("");



    // Call getValueSetConceptsByValueSetVersionId

    Calendar cal1 = Calendar.getInstance();

    @SuppressWarnings("unused")
    ValueSetConceptResultDto vscByVSVid = client.getService().getValueSetConceptsByValueSetVersionId(
        "B8D34BBC-617F-DD11-B38D-00188B398520", 1, 100);

    Calendar cal2 = Calendar.getInstance();

    System.out.println("Call to method took " + getElapsedMiliSeconds(cal1, cal2) + " miliseconds.");



    // Call getCodeSystemConceptAltDesignationByCodeSystemOidAndConceptCode()
    List<CodeSystemConceptAltDesignation> cscAltDes = client.getService().getCodeSystemConceptAltDesignationByOidAndCode(
        "2.16.840.1.113883.6.93", "17045").getCodeSystemConceptAltDesignations();
    System.out.println("Calling getCodeSystemConceptAltDesignationByCodeSystemOidAndConceptCode() returned "
        + cscAltDes.size() + " row(s):");
    for (CodeSystemConceptAltDesignation e : cscAltDes) {
      System.out.println("CSC Alternate Designation [Id=" + e.getId() + "][CSOid=" + e.getCodeSystemOid()
          + "][ConceptCode=" + e.getConceptCode() + "][SDODesignationId=" + e.getSdoDesignationId()
          + "][PhinPreferredTerm=" + e.isPhinPreferredTerm() + "][Code=" + e.isCode() + "][ConceptDesignationText="
          + e.getConceptDesignationText() + "]");
    }
    System.out.println("");

    // Call findCodeSystems
    CodeSystemSearchCriteriaDto csSearchCritDto = new CodeSystemSearchCriteriaDto();
    csSearchCritDto.setCodeSearch(true);
    csSearchCritDto.setNameSearch(true);
    csSearchCritDto.setOidSearch(false);
    csSearchCritDto.setDefinitionSearch(false);
    csSearchCritDto.setAssigningAuthoritySearch(false);
    csSearchCritDto.setTable396Search(false);
    csSearchCritDto.setSearchType(1);
    csSearchCritDto.setSearchText("\"Country\"");

    CodeSystemResultDto csSearchResultDto = client.getService().findCodeSystems(csSearchCritDto, 1, 5);
    System.out.println("Calling findCodeSystems() returned " + csSearchResultDto.getTotalResults() + " matches");
    for (CodeSystem e : csSearchResultDto.getCodeSystems()) {
      System.out.println("code system id:" + e.getId());
      System.out.println("code system name:" + e.getName());
    }
    System.out.println("");

    // Call findCodeSystemConcepts

    CodeSystemConceptSearchCriteriaDto cscSearchCritDto = new CodeSystemConceptSearchCriteriaDto();
    cscSearchCritDto.setCodeSearch(true);
    cscSearchCritDto.setNameSearch(true);
    cscSearchCritDto.setPreferredNameSearch(false);
    cscSearchCritDto.setAlternateNameSearch(false);
    cscSearchCritDto.setDefinitionSearch(false);
    cscSearchCritDto.setSearchType(1);
    cscSearchCritDto.setSearchText("Texas");
    CodeSystemConceptResultDto cscSearchResultDto = client.getService().findCodeSystemConcepts(cscSearchCritDto, 1, 5);
    System.out
        .println("Calling findCodeSystemConcepts() returned " + cscSearchResultDto.getTotalResults() + " matches");
    for (CodeSystemConcept e : cscSearchResultDto.getCodeSystemConcepts()) {
      System.out.println("code system concept id:" + e.getConceptCode());
      System.out.println("code system concept name:" + e.getName());
      System.out.println("code system oid:" + e.getCodeSystemOid());
    }
    System.out.println("");

    // GetCodeSystem
    CodeSystem cs = client.getService().getCodeSystemByOid("2.16.840.1.113883.6.92").getCodeSystem();
    System.out.println("Call Get CodeSystem");
    System.out.println(cs.getOid());
    System.out.println(cs.getName());
    System.out.println("");

    // GetCodeSystemPropertyDefinitionsByCodeSystemOid
    System.out.println("Call Get GetCodeSystemPropertyDefinitionsByCodeSystemOid");
    List<CodeSystemPropertyDefinition> cspdList = client.getService().getCodeSystemPropertyDefinitionsByCodeSystemOid(
        "2.16.840.1.113883.6.93").getCodeSystemPropertyDefinitions();
    System.out.println("Number of results: " + cspdList.size());
    for (CodeSystemPropertyDefinition e : cspdList) {
      System.out.println("name: " + e.getName());
      System.out.println("dataTypeCode: " + e.getDataType());
    }
    System.out.println("");

    // GetAllSources
    System.out.println("Call getAllSources()");
    List<Source> sources = client.getService().getAllSources().getSources();
    System.out.println("allSources length: " + sources.size());
    System.out.println("name: " + sources.get(1).getName());
    System.out.println("description: " + sources.get(1).getDescription());
    System.out.println("");

    // GetAllSources
    System.out.println("Call getAllAuthorities()");
    List<Authority> authorities = client.getService().getAllAuthorities().getAuthoritys();
    System.out.println("allAuthorities length: " + authorities.size());
    System.out.println("name: " + authorities.get(0).getName());
    System.out.println("description: " + authorities.get(0).getDescription());
    System.out.println("");

    // GetParentCodeSystemConcepts
    System.out.println("Call GetParentCodeSystemConcepts()");
    List<CodeSystemConcept> parentConcepts = client.getService().getParentCodeSystemConceptsByOidAndCode("2.16.840.1.113883.6.238",
        "1000-9").getCodeSystemConcepts();
    System.out.println("parentConcepts length: " + parentConcepts.size());
    for (CodeSystemConcept e : parentConcepts) {
      System.out.println("Concept Name: " + e.getName());
      System.out.println("Concept Code: " + e.getConceptCode());
    }
    System.out.println("");

    // GetChildCodeSystemConcepts
    System.out.println("Call GetChildCodeSystemConcepts()");
    List<CodeSystemConcept> childConcepts = client.getService().getChildCodeSystemConceptsByOidAndCode("2.16.840.1.113883.6.238",
        "1000-9").getCodeSystemConcepts();
    System.out.println("childConcepts length: " + childConcepts.size());
    for (CodeSystemConcept e : childConcepts) {
      System.out.println("Concept Name: " + e.getName());
      System.out.println("Concept Code: " + e.getConceptCode());
    }
    System.out.println("");

    // Call getValueSetVersionIdsByCodeSystemConceptOidAndCode()
    List<String> vsvIds = client.getService().getValueSetVersionIdsByCodeSystemConceptOidAndCode("2.16.840.1.113883.6.93", "17045")
        .getIds();
    System.out.println("Calling getValueSetVersionIdsByCodeSystemConceptOidAndCode() returned " + vsvIds.size()
        + " row(s):");
    for (String e : vsvIds) {
      System.out.println("Id: " + e);
    }
    System.out.println("");

    // Call getValueSetsByCodeSystemConceptOidAndCode()
    List<ValueSet> vsObjs = client.getService().getValueSetsByCodeSystemConceptOidAndCode("2.16.840.1.113883.6.93", "17045").getValueSets();
    System.out.println("Calling getValueSetsByCodeSystemConceptOidAndCode() returned " + vsObjs.size()
        + " row(s):");
    for (ValueSet e : vsObjs) {
      System.out.println("Id: " + e.getOid() + " - " + e.getName());
    }
    System.out.println("");

    // Call getCodeSystemConceptPropertyValuesByOidAndCode()
    List<CodeSystemConceptPropertyValue> cscPropVals = client.getService().getCodeSystemConceptPropertyValuesByOidAndCode(
        "2.16.840.1.113883.6.93", "13257").getCodeSystemConceptPropertyValues();
    System.out.println("Calling getCodeSystemConceptPropertyValuesByOidAndCode() returned " + cscPropVals.size()
        + " row(s):");
    for (CodeSystemConceptPropertyValue e : cscPropVals) {
      System.out.println("PropName: " + e.getPropertyName());
      System.out.println("PropValue: " + e.getStringValue() + e.getDateValue() + e.getNumericValue()
          + e.isBooleanValue());
      System.out.println("ValueType: " + e.getValueType());
    }
    System.out.println("");

    // Call getCodeSystemConceptsByCodeSystemOid
    // cscSearchResultDto = client.getService().getCodeSystemConceptsByCodeSystemOid("2.16.840.1.113883.6.92", 1, 5);
    cscSearchResultDto = client.getService().getCodeSystemConceptsByCodeSystemOid("2.16.2", 1, 500);
    System.out.println("Calling getCodeSystemConceptsByCodeSystemOid() returned "
        + cscSearchResultDto.getTotalResults() + " matches");
    for (CodeSystemConcept e : cscSearchResultDto.getCodeSystemConcepts()) {
      System.out.println("code system concept id:" + e.getConceptCode());
      System.out.println("code system concept name:" + e.getName());
      System.out.println("code system oid:" + e.getCodeSystemOid());
    }
    System.out.println("");

    // Call findGroups
    GroupSearchCriteriaDto gSearchCritDto = new GroupSearchCriteriaDto();
    gSearchCritDto.setNameSearch(true);
    gSearchCritDto.setDefinitionSearch(false);
    gSearchCritDto.setSearchType(1);
    gSearchCritDto.setSearchText("Organism");

    GroupResultDto gSearchResultDto = client.getService().findGroups(gSearchCritDto, 1, 5);
    System.out.println("Calling findGroups() returned " + gSearchResultDto.getTotalResults() + " matches");
    for (Group e : gSearchResultDto.getGroups()) {
      System.out.println("group name:" + e.getName());
    }
    System.out.println("");

    // Call getValueSetOidsByGroupId

    for (Group group : groups) {
      List<String> vsOids = client.getService().getValueSetOidsByGroupId(group.getId()).getIds();
      System.out.println("Calling getValueSetOidsByGroupId() for " + group.getName() + " returned " + vsOids.size()
          + " row(s):");
      for (String e : vsOids) {
        System.out.println("Id: " + e);
      }
    }
    System.out.println("");

    // Call getValueSetVersionIdsByViewVersionId

    vsvIds = client.getService().getValueSetVersionIdsByViewVersionId("42ECC799-1284-DD11-B2C6-00188B398520").getIds();
    System.out
        .println("Calling getValueSetVersionIdsByViewVersionId() for 42ECC799-1284-DD11-B2C6-00188B398520 returned "
            + vsvIds.size() + " row(s):");
    System.out.println("");

    // Call getViewVersionsByViewId

    viewVersions = client.getService().getViewVersionsByViewId("26ECC799-1284-DD11-B2C6-00188B398520").getViewVersions();
    System.out.println("Calling getViewVersionsByViewId() for 26ECC799-1284-DD11-B2C6-00188B398520 returned "
        + viewVersions.size() + " row(s):");
    System.out.println("");

    // Call findViewVersions
    System.out.println("Calling findViewVersions()");
    ViewVersionSearchCriteriaDto vvSearchCrit = new ViewVersionSearchCriteriaDto();
    vvSearchCrit.setSearchText("CRA");
    vvSearchCrit.setVersionOption(3);
    vvSearchCrit.setSearchType(1);
    ViewVersionResultDto vvResultDto = client.getService().findViewVersions(vvSearchCrit, 1, 5);
    for (ViewVersion vv : vvResultDto.getViewVersions()) {
      System.out.println("View Version.  ID: " + vv.getId());
    }

    // Call getAllCodeSystemPropertyDefinitions()
    System.out.println("Calling getAllCodeSystemPropertyDefinitions()");
    CodeSystemPropertyDefinitionResultDto cspdResultDto = client.getService().getAllCodeSystemPropertyDefinitions();
    System.out.println("Total Results:" + cspdResultDto.getTotalResults());
    System.out.println("");


    //Validate Stuff
    System.out.println("Calling validateMethods()");
    System.out.println("True:" + client.getService().validateCodeSystem("2.16.840.1.113883.12.78").isValid());
    System.out.println("False:" + client.getService().validateCodeSystem("2.16.840.1.113883.12.78.007.007").isValid());
    System.out.println("True:" + client.getService().validateConceptCodeSystemMembership("2.16.840.1.113883.12.78", "HH").isValid());
    System.out.println("False:" + client.getService().validateConceptCodeSystemMembership("2.16.840.1.113883.12.78.007.007", "HH").isValid());
    System.out.println("False:" + client.getService().validateConceptCodeSystemMembership("2.16.840.1.113883.12.78", "XXXXXX1337").isValid());
    System.out.println("True:" + client.getService().validateConceptValueSetMembership("2.16.840.1.113883.12.78", "HH", "2.16.840.1.114222.4.11.800", 1).isValid());
    System.out.println("True:" + client.getService().validateConceptValueSetMembership("2.16.840.1.113883.12.78", "HH", "2.16.840.1.114222.4.11.800", null).isValid());
    System.out.println("False:" + client.getService().validateConceptValueSetMembership("2.16.840.1.113883.12.78", "HHXL3TT", "2.16.840.1.114222.4.11.800", 1).isValid());
    System.out.println("False:" + client.getService().validateConceptValueSetMembership("2.16.840.1.113883.12.78.007.007", "HH", "2.16.840.1.114222.4.11.800", 1).isValid());
    System.out.println("False:" + client.getService().validateConceptValueSetMembership("2.16.840.1.113883.12.78", "HH", "2.16.840.1.114222.4.11.800.007.007", 1).isValid());
    System.out.println("True:" + client.getService().validateValueSet("2.16.840.1.114222.4.11.800").isValid());
    System.out.println("False:" + client.getService().validateValueSet("2.16.840.1.114222.4.11.800.007.007").isValid());
    System.out.println("");

    System.out.println("Calling getValueSetVersionByOidAndNumber");
    System.out.println("1:" + client.getService().getValueSetVersionByValueSetOidAndVersionNumber("2.16.840.1.114222.4.11.834", 1).getValueSetVersion().getVersionNumber());
    System.out.println("!1:" + client.getService().getValueSetVersionByValueSetOidAndVersionNumber("2.16.840.1.114222.4.11.834", null).getValueSetVersion().getVersionNumber());
    System.out.println("");

    System.out.println("Calling getViewVersionByViewNameAndVersionNumber");
    System.out.println("1:" + client.getService().getViewVersionByViewNameAndVersionNumber("CRA Referral Request", 1).getViewVersion().getVersionNumber());
    System.out.println("!1:" + client.getService().getViewVersionByViewNameAndVersionNumber("CRA Referral Request", null).getViewVersion().getVersionNumber());
    System.out.println("");

    System.out.println("Calling getViewByName");
    System.out.println("CRA Referral Request:" + client.getService().getViewByName("CRA Referral Request").getView().getName());
    System.out.println("");

    System.out.println("Calling getViewVersionsByViewName");
    vvResultDto = client.getService().getViewVersionsByViewName("CRA Referral Request");
    System.out.println("size > 0:" + vvResultDto.getViewVersions().size());
    System.out.println("");


    //Test get group by name

    System.out.println("*******END*********");

  }

  private static String getElapsedMiliSeconds(Calendar cal1, Calendar cal2){
    long cal1Time = cal1.getTimeInMillis();
    long cal2Time = cal2.getTimeInMillis();
    long diffTime = cal2Time - cal1Time;
    return "" + diffTime;
  }


}
