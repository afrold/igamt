package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.caucho.hessian.client.HessianProxyFactory;

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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public class PhinvadsWSCallService {
  private VocabService service;

  public PhinvadsWSCallService() {

    String serviceUrl = "https://phinvads.cdc.gov/vocabService/v2";
    /*
     * http://phinvads.cdc.gov/vocabService/v2
     */
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

    if (valueSets != null) {
      for (ValueSet vs : valueSets) {
        Table table = new Table();

        List<ValueSetVersion> vsvByVSOid =
            this.getService().getValueSetVersionsByValueSetOid(vs.getOid()).getValueSetVersions();

        table.setBindingIdentifier(vs.getCode());
        table.setDescription(vs.getDefinitionText());
        table.setDefPreText(vs.getDefinitionText());
        table.setName(vs.getName());
        table.setOid(vs.getOid());
        table.setVersion("" + vsvByVSOid.get(0).getVersionNumber());
        table.setContentDefinition(ContentDefinition.Extensional);
        table.setExtensibility(Extensibility.Closed);
        table.setId(null);
        table.setLibIds(new HashSet<String>());
        table.setScope(SCOPE.PHINVADS);
        table.setStability(Stability.Static);
        table.setStatus(STATUS.UNPUBLISHED);
        table.setType(Constant.TABLE);
        table.setComment(vsvByVSOid.get(0).getDescription());
        table.setDate(vs.getStatusDate().toString());

        ValueSetConceptResultDto vscByVSVid = this.getService()
            .getValueSetConceptsByValueSetVersionId(vsvByVSOid.get(0).getId(), 1, 100000);
        List<ValueSetConcept> valueSetConcepts = vscByVSVid.getValueSetConcepts();

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
        tables.add(table);

        /*
         * System.out.println("---VS---"); System.out.println(vs.getAssigningAuthorityId());
         * System.out.println(vs.getCode()); System.out.println(vs.getDefinitionText());
         * System.out.println(vs.getId()); System.out.println(vs.getName());
         * System.out.println(vs.getOid()); System.out.println(vs.getScopeNoteText());
         * System.out.println(vs.getStatus()); System.out.println(vs.getStatusDate());
         * System.out.println(vs.getValueSetCreatedDate());
         * System.out.println(vs.getValueSetLastRevisionDate());
         * System.out.println("---vsvByVSOids---"); for(ValueSetVersion vsv : vsvByVSOid){
         * System.out.println(vsv.getAssigningAuthorityText());
         * System.out.println(vsv.getAssigningauthoritytext());
         * System.out.println(vsv.getDescription()); System.out.println(vsv.getId());
         * System.out.println(vsv.getNoteText()); System.out.println(vsv.getStatus());
         * System.out.println(vsv.getValueSetOid()); System.out.println(vsv.getVersionNumber());
         * System.out.println(vsv.getAssigningAuthorityReleaseDate());
         * System.out.println(vsv.getEffectiveDate()); System.out.println(vsv.getExpiryDate());
         * System.out.println(vsv.getStatusDate()); System.out.println("------------"); }
         * 
         * System.out.println("---ValueSetConceptResultDto---");
         * System.out.println(vscByVSVid.getErrorText());
         * System.out.println(vscByVSVid.getTotalResults());
         * System.out.println(vscByVSVid.getValueSetConcept());
         * 
         * System.out.println("---ValueSetConcepts---"); int index = 0; for(ValueSetConcept vsc :
         * valueSetConcepts){ index = index + 1; System.out.println("---------" + index);
         * System.out.println(vsc.getCdcPreferredDesignation());
         * System.out.println(vsc.getCodeSystemConceptName());
         * System.out.println(vsc.getCodeSystemOid()); System.out.println(vsc.getConceptCode());
         * System.out.println(vsc.getDefinitionText()); System.out.println(vsc.getId());
         * System.out.println(vsc.getPreferredAlternateCode());
         * System.out.println(vsc.getScopeNoteText()); System.out.println(vsc.getStatus());
         * System.out.println(vsc.getValueSetVersionId()); System.out.println(vsc.getSequence());
         * System.out.println(vsc.getStatusDate());
         * 
         * CodeSystemSearchCriteriaDto csSearchCritDto = new CodeSystemSearchCriteriaDto();
         * csSearchCritDto.setCodeSearch(false); csSearchCritDto.setNameSearch(false);
         * csSearchCritDto.setOidSearch(true); csSearchCritDto.setDefinitionSearch(false);
         * csSearchCritDto.setAssigningAuthoritySearch(false);
         * csSearchCritDto.setTable396Search(false); csSearchCritDto.setSearchType(1);
         * csSearchCritDto.setSearchText(vsc.getCodeSystemOid());
         * 
         * System.out.println(this.getService().findCodeSystems( csSearchCritDto, 1, 5)
         * .getCodeSystems().get(0).getHl70396Identifier());
         * 
         * }
         */
      }
    }

    return tables;
  }

  public static void main(String[] args) throws Exception {
    PhinvadsWSCallService service = new PhinvadsWSCallService();
    Set<Table> tables = service.generateTableList("Coding System");
    System.out.println(tables.size());
    System.out.println(tables);
  }

}
