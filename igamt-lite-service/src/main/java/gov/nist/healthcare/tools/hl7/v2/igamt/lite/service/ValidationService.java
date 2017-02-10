/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Jan 31, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationError;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationResult;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public interface ValidationService {
  /**
   * <p>
   * Validates the {@code toBeValidated} {@code Segment} against the supplied {@code reference}
   * {@code Message}.
   * </p>
   * 
   * @param reference - base Reference Message to Validate against
   * @param toBeValidated - Message object to be validated
   * @return ValidationResult
   */
  public ValidationResult validateMessage(Message reference, Message toBeValidated,
      boolean validateChildren) throws InvalidObjectException;

  public HashMap<String, List<ValidationError>> validateSegmentRefOrGroup(
      SegmentRefOrGroup reference, SegmentRefOrGroup toBeValidated, Predicate predicate,
      String hl7Version) throws InvalidObjectException;

  public ValidationResult validateGroup(Group reference, Group toBeValidated)
      throws InvalidObjectException;

  /**
   * <p>
   * Validates the {@code toBeValidated} {@code Segment} against the supplied {@code reference}
   * {@code Segment}.
   * </p>
   * 
   * @param reference - base Reference Segment to Validate against
   * @param toBeValidated - Segment object to be validated
   * @return List<ValidationResult>
   */
  public ValidationResult validateSegment(Segment reference, Segment toBeValidated,
      boolean validateChildren) throws InvalidObjectException;


  /**
   * <p>
   * Validates the {@code toBeValidate} {@code Field} against the supplied {@code reference}
   * {@code Field}.
   * </p>
   * <p>
   * {@code context} is the scope of this {@code Field} i.e. the name of the parent object that this
   * field is part of. E.g if this {@code Field} is part of the MSH segment, then value for
   * {@code context} will be MSH.
   * 
   * @param reference
   * @param toBeValidated
   * @return
   */

  public HashMap<String, List<ValidationError>> validateField(Field reference, Field toBeValidated,
      Predicate predicate, String hl7Version, String parentId) throws InvalidObjectException;

  /**
   * Validates the {@code tobeValidated} {@code Element} against the provided {@code reference}
   * {@code Datatype}.
   * 
   * @param reference
   * @param toBeValidated
   * @return
   * @throws InvalidObjectException
   */


  public ValidationResult validateDatatype(Datatype reference, Datatype toBeValidated,
      String parentId) throws InvalidObjectException;

  /**
   * Validates the {@code tobeValidated} {@code Element} against the provided {@code reference}
   * {@code Component}.
   * 
   * @param reference
   * @param toBeValidated
   * @return
   * @throws InvalidObjectException
   */


  public HashMap<String, List<ValidationError>> validateComponent(Component reference,
      Component toBeValidated, Predicate predicate, String hl7Version, String parentId)
      throws InvalidObjectException;

  /**
   * <p>
   * Validates the {@code toBeValidate} {@code String} against the supplied {@code reference}
   * {@code Usage}.
   * </p>
   * 
   * @param reference - existing Usage object
   * @param toBeValidated - String object to validate
   * @return String - validation Message
   */
  public String validateUsage(Usage reference, Usage newValueForUsage, Predicate predicate,
      String hl7Version);

  public String validateLength(int referenceMinLen, String referenceMaxLen, int toBeMinLen,
      String toBeMaxLen);

}
