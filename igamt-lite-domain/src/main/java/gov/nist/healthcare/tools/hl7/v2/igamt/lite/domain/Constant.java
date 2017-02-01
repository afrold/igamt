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
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.text.SimpleDateFormat;

/**
 * @author Harold Affo (harold.affo@nist.gov) Feb 13, 2015
 */
public class Constant {

  private Constant() {}

  public final static String ProfileComponent = "profilecomponent";
  public final static String ProfileComponentLibrary = "profilecomponentlibrary";
  public final static String SEGMENTS = "segments";
  public final static String SEGMENTLIBRARY = "segmentlibrary";
  public final static String SEGMENT = "segment";
  public final static String SEGMENTREF = "segmentRef";
  public final static String FIELD = "field";
  public final static String COMPONENT = "component";
  public final static String SUBCOMPONENT = "subcomponent";
  public final static String GROUP = "group";
  public final static String MESSAGE = "message";
  public final static String COMPOSITEMESSAGE = "compositemessage";
  public final static String DATATYPES = "datatypes";
  public final static String DATATYPELIBRARY = "datatypelibrary";
  public final static String DATATYPE = "datatype";
  public final static String SEGMENTORGROUP = "segmentorgroup";
  public final static String PROFILE = "profile";
  public final static String TABLELIBRARY = "tablelibrary";
  public final static String TABLES = "tables";
  public final static String TABLE = "table";
  public final static String CODE = "code";
  public final static String ID = "id";
  public final static String LABEL = "label";
  public final static String DefaultDatatypeLibrary = "DefaultDTLib";

  public final static String SECTION = "section";
  public final static String Document = "document";
  public final static String DECISION = "decision";

  public final static SimpleDateFormat mdy = new SimpleDateFormat("MMMM dd, yyyy");

  public final static int CODESIZELIMIT = 3000;

  public enum SCOPE {
    HL7STANDARD, MASTER, USER, PRELOADED, PHINVADS
  };
  public enum QUANTUM {
    BREVIS, SUMMA
  };

  public final static String ORG_NAME = "NIST";
  public static final String DATATYPE_LIBRARY_DOCUMENT = "datatypeLibraryDocument";

  public static enum STATUS {
    PUBLISHED, UNPUBLISHED
  };

  public static enum DataContext {
    IG, LIBRARY;
  }


}
