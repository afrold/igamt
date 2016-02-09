/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 * */
package gov.nist.healthcare.nht.acmgt.dto;

/**
 * Used to transport messages back to the client.
 */
public class ResponseMessage {
    public enum Type {
        success, warn, danger, info;
    }

    private final Type type;
    private final String text;
    private final String resourceId;
    private final String manualHandle;
    private   boolean skip;

    public ResponseMessage(Type type, String text, String resourceId, String manualHandle) {
        this.type = type;
        this.text = text;
        this.resourceId = resourceId;
        this.manualHandle = manualHandle;
    }
    

    public ResponseMessage(Type type, String text, String resourceId) {
        this.type = type;
        this.text = text;
        this.resourceId = resourceId;
        this.manualHandle = "false";
    }
  
    public ResponseMessage(Type type, String text, String resourceId,boolean skip) {
        this.type = type;
        this.text = text;
        this.resourceId = resourceId;
        this.manualHandle = "false";
        this.skip = skip;
    }

    public ResponseMessage(Type type, String text) {
        this.type = type;
        this.text = text;
        this.resourceId = null;
        this.manualHandle = "false";
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public String getResourceId() {
        return resourceId;
    }
  
    public String getManualHandle() {
        return manualHandle;
    }


	public boolean isSkip() {
		return skip;
	}


	public void setSkip(boolean skip) {
		this.skip = skip;
	}
    
    
    
}
