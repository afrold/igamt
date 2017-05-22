package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.io.InputStream;
import java.util.HashSet;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;


public interface VerificationService {


  public ElementVerification verifyMessages(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifyMessage(Profile p, Profile baseP, String id, String type);



  public ElementVerification verifySegmentOrGroup(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifySegments(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifySegment(Profile p, Profile baseP, String id, String type);

  public InputStream verifySegment2(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifyField(Profile p, Profile baseP, String id, String type);



  public ElementVerification verifyDatatypes(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifyDatatype(Profile p, Profile baseP, String id, String type);

  public InputStream verifyDatatype2(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifyComponent(Profile p, Profile baseP, String id, String type);

  public ElementVerification verifyValueSetLibrary(Profile p, Profile baseP, String id,
      String type);


  public ElementVerification verifyValueSet(Profile p, Profile baseP, String id, String type);


  public InputStream verifyValueSet2(Profile p, Profile baseP, String id, String type);


  public ElementVerification verifyUsage(Profile p, Profile baseP, String id, String type,
      String eltName, String eltValue);

  public InputStream verifyUsage2(Profile p, Profile baseP, String id, String type, String eltName,
      String eltValue);

  public ElementVerification verifyCardinality(Profile p, String id, String type, String eltName,
      String eltValue);

  public InputStream verifyCardinality2(Profile p, String id, String type, String eltName,
      String eltValue);

  public ElementVerification verifyLength(Profile p, String id, String type, String eltName,
      String eltValue);



  public HashSet<String> duplicates(String[] values);



}
