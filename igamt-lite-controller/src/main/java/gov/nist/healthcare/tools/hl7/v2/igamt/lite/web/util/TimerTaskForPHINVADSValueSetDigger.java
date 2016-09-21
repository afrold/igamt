package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianProxyFactory;

import gov.cdc.vocab.service.VocabService;
import gov.cdc.vocab.service.bean.ValueSet;
import gov.cdc.vocab.service.dto.input.ValueSetSearchCriteriaDto;
import gov.cdc.vocab.service.dto.output.ValueSetResultDto;

public class TimerTaskForPHINVADSValueSetDigger extends TimerTask {

	Logger log = LoggerFactory.getLogger(TimerTaskForPHINVADSValueSetDigger.class);
	private VocabService service;
	
	
	public TimerTaskForPHINVADSValueSetDigger() {

		String serviceUrl = "https://phinvads.cdc.gov/vocabService/v2";
		// String serviceUrl = http://phinvads.cdc.gov/vocabService/v2 

		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			setService((VocabService) factory.create(VocabService.class, serviceUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public void run() {
    	log.info("PHINVADSValueSetDigger started at " + new Date());
    	List<String> oids = new ArrayList<String>();
    	oids.add("2.16.840.1.114222.4.11.7267");

    	
    	
    	for(String oid:oids){
        	//1. Get metadata from PHINVADS web service
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
    		
    		
    		
    		
        	//2. Get Table from DB
        	
        	//3. compare metadata
        	
        	//4. if updated, get full codes from PHINVADs web service
        	
        	//5. update Table on DB    		
    	}

    	

    	
    	
    }

	public VocabService getService() {
		return service;
	}

	public void setService(VocabService service) {
		this.service = service;
	}
}
