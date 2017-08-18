package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;


import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;

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
   */
  public static void main(String[] args) throws MalformedURLException {

    VocabClient client = new VocabClient();

    ValueSetSearchCriteriaDto vsSearchCrit = new ValueSetSearchCriteriaDto();
    vsSearchCrit.setFilterByViews(false);
    vsSearchCrit.setFilterByGroups(false);
    vsSearchCrit.setCodeSearch(false);
    vsSearchCrit.setNameSearch(false);
    vsSearchCrit.setOidSearch(true);
    vsSearchCrit.setDefinitionSearch(false);
    vsSearchCrit.setSearchType(1);
    vsSearchCrit.setSearchText("2.16.840.1.114222.4.11.7574");

    ValueSetResultDto vsSearchResult = null;

    vsSearchResult = service.findValueSets(vsSearchCrit, 1, 5);
    List<ValueSet> valueSets = vsSearchResult.getValueSets();
    
    for(ValueSet vs :valueSets ){
      System.out.println(vs);
      vs = valueSets.get(0);
      ValueSetVersion vsv = service.getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions()
          .get(0);
      
      ValueSetConceptResultDto vscByVSVid = service.getValueSetConceptsByValueSetVersionId(vsv.getId(), 1, 100000);
      List<ValueSetConcept> valueSetConcepts = vscByVSVid.getValueSetConcepts();
      
      for (ValueSetConcept pcode : valueSetConcepts) {
        System.out.println(pcode.getCodeSystemConceptName());
        System.out.println(pcode.getConceptCode());
      }
    }

  }

  private static String getElapsedMiliSeconds(Calendar cal1, Calendar cal2) {
    long cal1Time = cal1.getTimeInMillis();
    long cal2Time = cal2.getTimeInMillis();
    long diffTime = cal2Time - cal1Time;
    return "" + diffTime;
  }


}
