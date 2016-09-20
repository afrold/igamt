package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadForPHINVADSValueSetDigger extends Thread {
    private boolean done = false;
    private Timer timer;
    private TimerTask task;
    
    public ThreadForPHINVADSValueSetDigger (TimerTask task) {
        this.task = task;
    }
    
    public void quit() {
        this.done = true;
        this.interrupt();
    }
    
    public boolean finishing() {
        return (done || Thread.interrupted());
    }

    @Override
    public void run() {
        super.run();
        
        timer = new Timer();
        Calendar date = Calendar.getInstance();
        date.set(Calendar.AM_PM, Calendar.PM);
        date.set(Calendar.HOUR, 5);
        date.set(Calendar.MINUTE, 26);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        
        
        timer.schedule(task, date.getTime());
        
        while (!finishing()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        timer.cancel();
    }
}
