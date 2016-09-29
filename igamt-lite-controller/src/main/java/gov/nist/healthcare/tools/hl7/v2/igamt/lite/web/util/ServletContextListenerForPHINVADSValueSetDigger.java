package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextListenerForPHINVADSValueSetDigger implements ServletContextListener {

    private ThreadForPHINVADSValueSetDigger myThread = null;
    
    public void contextInitialized(ServletContextEvent sce) {
        if ((myThread == null) || (!myThread.isAlive())) {
            myThread = new ThreadForPHINVADSValueSetDigger(new TimerTaskForPHINVADSValueSetDigger());
            System.out.println("!!!!!!!!!" + "THREAD Created!");
            
            myThread.start();
        }
    }

    public void contextDestroyed(ServletContextEvent sce){
        if (myThread != null && myThread.isAlive()) {
            myThread.quit();
        }
    }
}


