package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.cdc.vocab.service.VocabService;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.caucho.hessian.client.HessianProxyFactory;

public class PhinvadsWSCallService {
  private VocabService service;

  public PhinvadsWSCallService() {

    String serviceUrl = "http://phinvads.cdc.gov/vocabService/v2";

    HessianProxyFactory factory = new HessianProxyFactory();
    try {
      setService((VocabService) factory.create(VocabService.class, serviceUrl));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public VocabService getService() {
    return service;
  }

  public void setService(VocabService service) {
    this.service = service;
  }


  public Set<Table> generateTableList(String searchText) throws MalformedURLException {
    Set<Table> tables = new HashSet<Table>();
    ValueSetSearchCriteriaDto vsSearchCrit = new ValueSetSearchCriteriaDto();
    vsSearchCrit.setFilterByViews(false);
    vsSearchCrit.setFilterByGroups(false);
    vsSearchCrit.setCodeSearch(true);
    vsSearchCrit.setNameSearch(true);
    vsSearchCrit.setOidSearch(true);
    vsSearchCrit.setDefinitionSearch(false);
    vsSearchCrit.setSearchType(1);
    vsSearchCrit.setSearchText(searchText);

    ValueSetResultDto vsSearchResult = null;

    vsSearchResult = this.getService().findValueSets(vsSearchCrit, 1, 5);
    List<ValueSet> valueSets = vsSearchResult.getValueSets();

    for (ValueSet vs : valueSets) {
      Table table = new Table();

      List<ValueSetVersion> vsvByVSOid =
          this.getService().getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions();
      ValueSetConceptResultDto vscByVSVid =
          this.getService().getValueSetConceptsByValueSetVersionId(vsvByVSOid.get(0).getId(), 1,
              100000);
      List<ValueSetConcept> valueSetConcepts = vscByVSVid.getValueSetConcepts();

      table.setBindingIdentifier(searchText);
      table.setDescription(vs.getDefinitionText());
      table.setName(vs.getName());
      table.setOid(vs.getOid());
      table.setVersion("" + vsvByVSOid.get(0).getVersionNumber());
      table.setContentDefinition(ContentDefinition.Extensional);
      table.setExtensibility(Extensibility.Closed);
      table.setId(null);
      table.setLibIds(new HashSet<String>());
      table.setScope(SCOPE.USER);
      table.setStability(Stability.Static);
      table.setStatus(STATUS.UNPUBLISHED);
      table.setType(Constant.TABLE);

      for (ValueSetConcept pcode : valueSetConcepts) {
        Code code = new Code();
        code.setCodeUsage("R");
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
        code.setCodeSystem(this.getService().findCodeSystems(csSearchCritDto, 1, 5)
            .getCodeSystems().get(0).getHl70396Identifier());
        table.addCode(code);
      }
      tables.add(table);
    }
    return tables;
  }

  public static void main(String[] args) throws Exception {
    PhinvadsWSCallService service = new PhinvadsWSCallService();
    Set<Table> tables = service.generateTableList("2.16.840.1.114222.4.11.3338");

    System.out.println(tables);
  }

}
