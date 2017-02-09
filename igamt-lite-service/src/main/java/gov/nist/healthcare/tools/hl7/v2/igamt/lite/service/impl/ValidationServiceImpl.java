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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationError;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationResult;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ValidationService;

@Service
public class ValidationServiceImpl implements ValidationService {


  private static final Logger logger = Logger.getLogger(ValidationServiceImpl.class);
  private static final String USAGE_RULES_ROOT = "usageMap";
  private static final String UNSUPPORTED_SCHEMA_VERSION = "others";
  protected JsonNode root;

  @Autowired
  DatatypeService datatypeService;


  public ValidationServiceImpl() {
    init();
  }

  public void init() {
    try {

      InputStream stream = getClass().getResourceAsStream("/validation/validationRules.txt");
      ObjectMapper mapper = new ObjectMapper();
      root = mapper.readTree(stream);
    } catch (IOException e) {
      logger.error("Failed to load the Rules file.");
      if (logger.isDebugEnabled()) {
        logger.debug(e, e);
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public ValidationResult validateSegment(Segment reference, Segment toBeValidated,
      boolean validateChildren) throws InvalidObjectException {
    ValidationResult result = new ValidationResult();
    List<ValidationError> fieldsResults = new ArrayList<ValidationError>();
    result.setTargetId(toBeValidated.getId());
    if (toBeValidated.getName().equals(reference.getName()) && toBeValidated.getFields() != null
        && reference.getFields() != null) {

      // Prerdicate Map
      Map<Integer, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          Integer target = Integer
              .parseInt(toBeValidated.getPredicates().get(j).getConstraintTarget().split("\\[")[0]);
          predicatesMap.put(target, toBeValidated.getPredicates().get(j));
        }
      }
      for (int i = 0; i < reference.getFields().size(); i++) {

        if (toBeValidated.getFields().get(i).getDatatype() == null) {
          List<ValidationError> compResults =
              validateField(reference.getFields().get(i), toBeValidated.getFields().get(i), false,
                  predicatesMap.get(toBeValidated.getFields().get(i).getPosition()),
                  toBeValidated.getHl7Version());

          fieldsResults.addAll(compResults);
        } else {
          List<ValidationError> compResults =
              validateField(reference.getFields().get(i), toBeValidated.getFields().get(i), true,
                  predicatesMap.get(toBeValidated.getFields().get(i).getPosition()),
                  toBeValidated.getHl7Version());
          fieldsResults.addAll(compResults);
        }



      }

    }
    result.setErrorCount(fieldsResults.size());
    result.setItems(fieldsResults);
    return result;
  }

  @Override
  public List<ValidationError> validateField(Field reference, Field toBeValidated,
      boolean validateChildren, Predicate predicate, String hl7Version)
      throws InvalidObjectException {

    List<ValidationError> result = new ArrayList<ValidationError>();
    if (reference.getUsage() != null) {
      ValidationError valError = new ValidationError();
      String usageValidation =
          validateUsage(reference.getUsage(), toBeValidated.getUsage(), predicate, hl7Version);
      if (usageValidation != null) {
        valError.setErrorMessage(usageValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setTargetName(toBeValidated.getName());
        valError.setType("Error");
        valError.setValidationType(ValidationType.USAGE);
        result.add(valError);
      }

    }
    String lengthValidation = validateLength(reference.getMinLength(), reference.getMaxLength(),
        toBeValidated.getMinLength(), toBeValidated.getMaxLength());
    if (lengthValidation != null) {
      ValidationError valErr = new ValidationError();
      valErr.setErrorMessage(lengthValidation);
      valErr.setPosition(toBeValidated.getPosition());
      valErr.setTargetId(toBeValidated.getId());
      valErr.setTargetName(toBeValidated.getName());
      valErr.setType("Error");
      valErr.setValidationType(ValidationType.LENGTH);
      result.add(valErr);
    }
    if (validateChildren) {
      Datatype childDatatype = datatypeService.findById(toBeValidated.getDatatype().getId());
      Datatype childReference = datatypeService.findByNameAndVersionAndScope(
          toBeValidated.getDatatype().getName(), childDatatype.getHl7Version(), "HL7STANDARD");
      List<ValidationError> errs =
          validateDatatype(childReference, childDatatype, false).getItems();
      for (ValidationError valErr : errs) {
        valErr.setParentId(toBeValidated.getId());
      }
      result.addAll(errs);
    }
    return result;
  }


  @Override
  public ValidationResult validateDatatype(Datatype reference, Datatype toBeValidated,
      boolean validateChildren) throws InvalidObjectException {
    ValidationResult result = new ValidationResult();
    List<ValidationError> componentsResults = new ArrayList<ValidationError>();
    result.setTargetId(toBeValidated.getId());

    if (toBeValidated.getName().equals(reference.getName()) && toBeValidated.getComponents() != null
        && reference.getComponents() != null) {
      // Build predicates Map
      Map<Integer, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          Integer target = Integer
              .parseInt(toBeValidated.getPredicates().get(j).getConstraintTarget().split("\\[")[0]);
          predicatesMap.put(target, toBeValidated.getPredicates().get(j));
        }
      }

      for (int i = 0; i < reference.getComponents().size(); i++) {

        if (toBeValidated.getComponents().get(i).getDatatype() == null) {
          List<ValidationError> compResults = validateComponent(reference.getComponents().get(i),
              toBeValidated.getComponents().get(i), false,
              predicatesMap.get(toBeValidated.getComponents().get(i).getPosition()),
              toBeValidated.getHl7Version());

          componentsResults.addAll(compResults);
        } else {
          List<ValidationError> compResults = validateComponent(reference.getComponents().get(i),
              toBeValidated.getComponents().get(i), true,
              predicatesMap.get(toBeValidated.getComponents().get(i).getPosition()),
              toBeValidated.getHl7Version());
          componentsResults.addAll(compResults);
        }


      }
    }
    result.setErrorCount(componentsResults.size());
    result.setItems(componentsResults);
    return result;
  }


  @Override
  public List<ValidationError> validateComponent(Component reference, Component toBeValidated,
      boolean validateChildren, Predicate predicate, String hl7Version)
      throws InvalidObjectException {

    List<ValidationError> result = new ArrayList<ValidationError>();
    if (reference.getUsage() != null) {
      ValidationError valError = new ValidationError();
      String usageValidation =
          validateUsage(reference.getUsage(), toBeValidated.getUsage(), predicate, hl7Version);
      if (usageValidation != null) {
        valError.setErrorMessage(usageValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setTargetName(toBeValidated.getName());
        valError.setType("Error");
        valError.setValidationType(ValidationType.USAGE);
        result.add(valError);
      }

    }
    String lengthValidation = validateLength(reference.getMinLength(), reference.getMaxLength(),
        toBeValidated.getMinLength(), toBeValidated.getMaxLength());
    if (lengthValidation != null) {
      ValidationError valErr = new ValidationError();
      valErr.setErrorMessage(lengthValidation);
      valErr.setPosition(toBeValidated.getPosition());
      valErr.setTargetId(toBeValidated.getId());
      valErr.setTargetName(toBeValidated.getName());
      valErr.setType("Error");
      valErr.setValidationType(ValidationType.LENGTH);
      result.add(valErr);
    }
    if (validateChildren) {
      Datatype childDatatype = datatypeService.findById(toBeValidated.getDatatype().getId());
      Datatype childReference = datatypeService.findByNameAndVersionAndScope(
          toBeValidated.getDatatype().getName(), childDatatype.getHl7Version(), "HL7STANDARD");
      List<ValidationError> errs =
          validateDatatype(childReference, childDatatype, false).getItems();
      for (ValidationError valErr : errs) {
        valErr.setParentId(toBeValidated.getId());
      }
      result.addAll(errs);
    }
    return result;
  }



  @Override
  public String validateUsage(Usage reference, Usage newValueForUsage, Predicate predicate,
      String hl7Version) {
    if (!Usage.X.toString().equalsIgnoreCase(newValueForUsage.name())) {
      String validationResult = null;

      if (predicate != null && newValueForUsage != Usage.C) {
        return "Usage must be conditional when a predicate is defined";
      } else if (predicate == null && newValueForUsage == Usage.C) {
        return "Predicate is missing for conditional usage " + newValueForUsage.value();

      }
      System.out.println(reference.value());

      JsonNode node = getRulesNodeBySchemaVersion(hl7Version).path("constrainable")
          .get(USAGE_RULES_ROOT).get(reference.value());
      System.out.println(node);
      List<String> usages = new ArrayList<String>();
      if (node != null && !node.isMissingNode()) {
        ArrayNode array = (ArrayNode) node;
        Iterator<JsonNode> it = array.iterator();
        while (it.hasNext()) {
          usages.add(it.next().textValue());
        }
      }
      if (predicate != null) {
        // C({{p.trueUsage}}/{{p.falseUsage}})
        String predicateUsage =
            "C(" + predicate.getTrueUsage() + "/" + predicate.getFalseUsage() + ")";
        System.out.println(predicateUsage);
        if (!usages.contains(predicateUsage)) {
          validationResult = "Selected usage of " + predicateUsage
              + " is non-compatible with base usage " + reference.value();
          return validationResult;
        } else {
          return null;
        }
      }
      if (!newValueForUsage.name().equals(Usage.C)) {
        if (!usages.contains(newValueForUsage.value())) {
          validationResult = "Selected usage of " + newValueForUsage.value()
              + " is non-compatible with base usage " + reference.value();
          return validationResult;
        } else {
          return null;
        }
      }
      System.out.println(usages);



    }
    return null;
  }

  @Override
  public String validateLength(int referenceMinLen, String referenceMaxLen, int toBeMinLen,
      String toBeMaxLen) {
    String result = null;
    if (!NumberUtils.isNumber(toBeMaxLen)) {
      if (!"*".equalsIgnoreCase(toBeMaxLen)) {
        result = "Max Length has to be * or a numerical value.";
      }
      return result;
    }
    int toBeMaxLenT = Integer.valueOf(toBeMaxLen.trim());

    if (toBeMaxLenT < toBeMinLen) {
      result = "Max Length cannot be less than Min Length.";
    }

    return result;
  }


  /***************************************************************************************************
   * HELPER METHODS
   ***************************************************************************************************/

  private JsonNode getRulesNodeBySchemaVersion(String schemaVersion) {

    JsonNode node = root.path(schemaVersion);
    return node.isMissingNode() ? root.path(UNSUPPORTED_SCHEMA_VERSION) : node;

  }



}
