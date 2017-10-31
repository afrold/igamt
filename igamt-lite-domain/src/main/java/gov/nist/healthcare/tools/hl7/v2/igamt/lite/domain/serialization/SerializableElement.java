package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p>
 * Created by Maxence Lefort on 12/7/16.
 */
public abstract class SerializableElement {

    private String prefix;

    private static final String FORMAT = "yyyy/MM/dd HH:mm:ss";


    public abstract Element serializeElement() throws SerializationException;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    protected static String format(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(FORMAT);
            return dateFormat.format(date);
        }
        return null;
    }

    protected Element createTextElement(String type, String content){
        Element textElement = new Element("Text");
        textElement.addAttribute(new Attribute("Type", type));
        textElement.appendChild(content);
        return textElement;
    }
    
    protected String generateInnerLink(Object dataModel, String host){
      StringBuilder url = new StringBuilder();
      url.append(host);
      url.append("/api/export/");
      if(dataModel instanceof Datatype){
        url.append("datatype/");
        url.append(((Datatype) dataModel).getId());        
      } else if(dataModel instanceof DatatypeLink){
        url.append("datatype/");
        url.append(((DatatypeLink) dataModel).getId());
      } else if(dataModel instanceof Table){
        url.append("valueSet/");
        url.append(((Table) dataModel).getId());
      } else if(dataModel instanceof Segment){
        url.append("segment/");
        url.append(((Segment) dataModel).getId());
      }
      url.append("/html");
      return url.toString();
    }
    
    protected String wrapLink(String link, String bindingIdentifier) {
      return "<a href=\""+link+"\" target=\"_blank\">"+bindingIdentifier+"</a>";
    }
}
