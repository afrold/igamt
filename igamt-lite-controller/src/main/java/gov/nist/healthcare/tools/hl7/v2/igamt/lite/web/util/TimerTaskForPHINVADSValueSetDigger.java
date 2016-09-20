package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTaskForPHINVADSValueSetDigger extends TimerTask {

	Logger log = LoggerFactory.getLogger(TimerTaskForPHINVADSValueSetDigger.class);
	
    @Override
    public void run() {
    	log.info("PHINVADSValueSetDigger started at " + new Date());
    }
}
