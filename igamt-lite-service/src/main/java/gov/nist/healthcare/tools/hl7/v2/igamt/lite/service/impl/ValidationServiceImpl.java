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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ComponentComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationError;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationResult;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator.FieldComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator.SegmentRefOrGroupComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ValidationService;

@Service
public class ValidationServiceImpl implements ValidationService {


  private static final Logger logger = Logger.getLogger(ValidationServiceImpl.class);
  private static final String USAGE_RULES_ROOT = "usageMap";
  private static final String UNSUPPORTED_SCHEMA_VERSION = "others";
  protected JsonNode root;
  @Autowired
  MessageService messageService;
  @Autowired
  DatatypeService datatypeService;
  @Autowired
  SegmentService segmentService;
  @Autowired
  SegmentLibraryService segmentLibraryService;
  @Autowired
  DatatypeLibraryService datatypeLibraryService;


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

  public Set<String> getMessageIds(Set<Message> messages) {
    Set<String> msgIds = new HashSet<String>();
    for (Message message : messages) {
      msgIds.add(message.getId());

    }
    return msgIds;

  }

  @Override
  public ValidationResult validateIg(IGDocument ig) throws InvalidObjectException {
    ValidationResult result = new ValidationResult();
    HashMap<String, ValidationResult> blocks = new HashMap<String, ValidationResult>();
    Set<String> userMsgIds = getMessageIds(ig.getProfile().getMessages().getChildren());
    List<Message> userMsgs = messageService.findByIds(userMsgIds);
    for (Message msg : userMsgs) {
      msg.setHl7Version(ig.getProfile().getMetaData().getHl7Version());
      Message referenceMessage = messageService.findByStructIdAndScopeAndVersion(msg.getStructID(),
          "HL7STANDARD", ig.getProfile().getMetaData().getHl7Version());
      System.out.println("HEEEEEEREEEE" + msg.getHl7Version());
      ValidationResult valRes = validateMessage(referenceMessage, msg, true);
      blocks.put(msg.getId(), valRes);


    }
    Set<String> userSegIds = new HashSet<String>();
    SegmentLibrary userSegLib =
        segmentLibraryService.findById(ig.getProfile().getSegmentLibrary().getId());
    Set<SegmentLink> segLinks = userSegLib.getChildren();
    for (SegmentLink segLink : segLinks) {
      userSegIds.add(segLink.getId());
    }
    List<Segment> userSegs = segmentService.findByIds(userSegIds);
    for (Segment seg : userSegs) {
      if (!seg.getScope().equals(SCOPE.HL7STANDARD)) {
        Segment referenceSegment = segmentService.findByNameAndVersionAndScope(seg.getName(),
            seg.getHl7Version(), "HL7STANDARD");
        ValidationResult valSegRes = validateSegment(referenceSegment, seg, true,
            ig.getProfile().getMetaData().getHl7Version(), null);
        blocks.put(seg.getId(), valSegRes);
      } else {
        ValidationResult valSeg = new ValidationResult();
        valSeg.setErrorCount(0);
        valSeg.setTargetId(seg.getId());
        blocks.put(seg.getId(), valSeg);
      }

    }
    Set<String> userdtIds = new HashSet<String>();
    DatatypeLibrary userdtLib =
        datatypeLibraryService.findById(ig.getProfile().getDatatypeLibrary().getId());
    Set<DatatypeLink> dtLinks = userdtLib.getChildren();
    for (DatatypeLink dtLink : dtLinks) {
      userdtIds.add(dtLink.getId());
    }
    List<Datatype> userdts = datatypeService.findByIds(userdtIds);
    for (Datatype dt : userdts) {
      if (!dt.getScope().equals(SCOPE.HL7STANDARD)) {
        List<Datatype> referenceDatatypes = datatypeService.findByNameAndVersionAndScope(
            dt.getName(), dt.getHl7Version(), SCOPE.HL7STANDARD.toString());
        Datatype referenceDatatype = referenceDatatypes != null && !referenceDatatypes.isEmpty()
            ? referenceDatatypes.get(0) : null;
        ValidationResult valdtRes = validateDatatype(referenceDatatype, dt, null,
            ig.getProfile().getMetaData().getHl7Version(), null);
        blocks.put(dt.getId(), valdtRes);
      } else {
        ValidationResult valdt = new ValidationResult();
        valdt.setErrorCount(0);
        valdt.setTargetId(dt.getId());
        blocks.put(dt.getId(), valdt);
      }

    }


    Integer blockCount = 0;
    for (Map.Entry<String, ValidationResult> entry : blocks.entrySet()) {
      ValidationResult value = entry.getValue();
      blockCount = blockCount + value.getErrorCount();

    }
    result.setErrorCount(blockCount);
    result.setBlocks(blocks);
    result.setTargetId(ig.getId());



    return result;
  }



  public Set<String> getSegmentIds(List<SegmentRefOrGroup> segRefsOrGrps) {

    Set<String> segIds = new HashSet<String>();
    for (SegmentRefOrGroup segRefOrGrp : segRefsOrGrps) {
      if (segRefOrGrp instanceof SegmentRef) {
        SegmentRef segRef = (SegmentRef) segRefOrGrp;
        segIds.add(segRef.getRef().getId());
      } else if (segRefOrGrp instanceof Group) {
        Group grp = (Group) segRefOrGrp;

        segIds.addAll(getSegmentIds(grp.getChildren()));
      }
    }
    return segIds;

  }

  public boolean verifyMsgStruct(List<SegmentRefOrGroup> ref, List<SegmentRefOrGroup> filteredToBeVal) {
    if (ref.size() != filteredToBeVal.size()) {
      return false;
    }
    for (int i = 0; i < filteredToBeVal.size(); i++) {
      if (!filteredToBeVal.get(i).getType().equals(ref.get(i).getType())) {
        return false;
      } else {
        if (filteredToBeVal.get(i) instanceof SegmentRef && ref.get(i) instanceof SegmentRef) {
          SegmentRef segRef1 = (SegmentRef) filteredToBeVal.get(i);
          SegmentRef segRef2 = (SegmentRef) ref.get(i);
          if (!segRef1.getRef().getName().equals(segRef2.getRef().getName()))
            return false;
        }
        if (filteredToBeVal.get(i) instanceof Group && ref.get(i) instanceof Group) {
          Group grp1 = (Group) filteredToBeVal.get(i);
          Group grp2 = (Group) ref.get(i);
          return verifyMsgStruct(grp2.getChildren(), grp1.getChildren());
        }
      }
    }
    return true;
  }


  private static Predicate findPredicate(String path, Map<String, Predicate> predicates) {
    return predicates.get(path);
  }


  private static HashMap<String, Predicate> findChildrenPredicates(String path,
      Map<String, Predicate> predicates) {
    HashMap<String, Predicate> map = new HashMap<String, Predicate>();
    for (String target : predicates.keySet()) {
      if (target.startsWith(path) && !target.equals(path)) {
        // remvoe the parent path;
        if (target.indexOf(".") > -1) {
          String newTarget = target.substring(target.indexOf(".") + 1, target.length());
          if (newTarget != null && !"".equals(newTarget))
            map.put(newTarget, predicates.get(target));
        }
      }
    }
    return map;
  }


  @Override
  public ValidationResult validateMessage(Message reference, Message toBeValidated,
      boolean validateChildren) throws InvalidObjectException {
    ValidationResult result = new ValidationResult();

    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    HashMap<String, ValidationResult> blocks = new HashMap<String, ValidationResult>();
    
    List<SegmentRefOrGroup> filteredToBeVal = new ArrayList<>();
	  for(SegmentRefOrGroup segmentRefOrGroup : toBeValidated.getChildren()){
		  if(segmentRefOrGroup.getAdded().equals(Constant.NO)){
			  filteredToBeVal.add(segmentRefOrGroup);
		  }
	  }

    if (reference == null
        || !verifyMsgStruct(reference.getChildren(), filteredToBeVal)) {
      result.setTargetId(toBeValidated.getId());
      result.setErrorCount(0);
      result.setItems(items);
      result.setBlocks(blocks);
      return result;
    }
    Set<String> userSegIds = getSegmentIds(filteredToBeVal);
    List<Segment> userSegs = segmentService.findByIds(userSegIds);
    HashMap<String, Segment> userSegMap = new HashMap<String, Segment>();
    for (Segment seg : userSegs) {
      userSegMap.put(seg.getId(), seg);
    }
    result.setTargetId(toBeValidated.getId());
    if (toBeValidated.getStructID().equals(reference.getStructID())
        && filteredToBeVal != null && reference.getChildren() != null) {
      // Prerdicate Map
      Map<String, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          Predicate predicate = toBeValidated.getPredicates().get(j);
          String constraintTarget = predicate.getConstraintTarget();
          // Integer target = Integer.parseInt(constraintTarget.split("\\[")[0]);
          predicatesMap.put(constraintTarget, toBeValidated.getPredicates().get(j));
        }
      }


      SegmentRefOrGroupComparator comp = new SegmentRefOrGroupComparator();
      Collections.sort(reference.getChildren(), comp);
      Collections.sort(filteredToBeVal, comp);


      for (int i = 0; i < reference.getChildren().size(); i++) {

        if (i < filteredToBeVal.size()) {
          SegmentRefOrGroup refSegOrGr = reference.getChildren().get(i);
          SegmentRefOrGroup toBeValSegOrGr = filteredToBeVal.get(i);
          String path = toBeValSegOrGr.getPosition() + "[1]";
          HashMap<String, List<ValidationError>> valE = validateSegmentRefOrGroup(refSegOrGr,
              toBeValSegOrGr, findPredicate(path, predicatesMap), toBeValidated.getHl7Version());
          if (valE != null) {
            items.putAll(valE);
          }
          if (filteredToBeVal.get(i) instanceof SegmentRef) {
            SegmentRef segRef = (SegmentRef) filteredToBeVal.get(i);
            Segment childSegment = userSegMap.get(segRef.getRef().getId());
            if (childSegment != null) {
              if (!childSegment.getScope().equals(SCOPE.HL7STANDARD)) {
                Segment childReference = segmentService.findByNameAndVersionAndScope(
                    childSegment.getName(), childSegment.getHl7Version(), "HL7STANDARD");
                ValidationResult block = validateSegment(childReference, childSegment, false,
                    toBeValidated.getHl7Version(), findChildrenPredicates(path, predicatesMap));
                if (result.getErrorCount() != null && block.getErrorCount() != null) {
                  result.setErrorCount(result.getErrorCount() + block.getErrorCount());
                }
                blocks.put(filteredToBeVal.get(i).getId(), block);
              } else {
                ValidationResult block = new ValidationResult();
                block.setErrorCount(0);
                block.setTargetId(childSegment.getId());
                blocks.put(filteredToBeVal.get(i).getId(), block);
              }
            }
          } else if (filteredToBeVal.get(i) instanceof Group
              && reference.getChildren().get(i) instanceof Group) {
            Group userGrp = (Group) filteredToBeVal.get(i);
            Group referenceGrp = (Group) reference.getChildren().get(i);
            ValidationResult block = validateGroup(referenceGrp, userGrp,
                toBeValidated.getHl7Version(), findChildrenPredicates(path, predicatesMap));
            if (result.getErrorCount() != null && block.getErrorCount() != null) {
              result.setErrorCount(result.getErrorCount() + block.getErrorCount());

            }
            blocks.put(filteredToBeVal.get(i).getId(), block);
          }
        }
      }
    }


    result.setItems(items);
    Integer itemCount = 0;
    Integer blockCount = 0;
    for (Map.Entry<String, List<ValidationError>> entry : items.entrySet()) {
      List<ValidationError> value = entry.getValue();
      itemCount = itemCount + value.size();

    }
    for (Map.Entry<String, ValidationResult> entry : blocks.entrySet()) {
      ValidationResult value = entry.getValue();
      blockCount = blockCount + value.getErrorCount();

    }
    result.setErrorCount(itemCount + blockCount);
    result.setBlocks(blocks);

    return result;
  }


  @Override
  public ValidationResult validateGroup(Group reference, Group toBeValidated, String igHl7Version,
      Map<String, Predicate> childrenPredicates) throws InvalidObjectException {
    ValidationResult result = new ValidationResult();
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    HashMap<String, ValidationResult> blocks = new HashMap<String, ValidationResult>();
    result.setTargetId(toBeValidated.getId());

    if (toBeValidated.getName().equals(reference.getName()) && toBeValidated.getChildren() != null
        && reference.getChildren() != null) {

      // Prerdicate Map
      Map<String, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          String target = toBeValidated.getPredicates().get(j).getConstraintTarget();
          predicatesMap.put(target, toBeValidated.getPredicates().get(j));
        }
      }

      if (childrenPredicates != null && !childrenPredicates.isEmpty()) {
        predicatesMap.putAll(childrenPredicates);
      }

      SegmentRefOrGroupComparator comp = new SegmentRefOrGroupComparator();
      Collections.sort(reference.getChildren(), comp);
      Collections.sort(toBeValidated.getChildren(), comp);


      for (int i = 0; i < reference.getChildren().size(); i++) {

        if (i < toBeValidated.getChildren().size()) {

          String path = toBeValidated.getChildren().get(i).getPosition() + "[1]";
          HashMap<String, List<ValidationError>> valE = validateSegmentRefOrGroup(
              reference.getChildren().get(i), toBeValidated.getChildren().get(i),
              findPredicate(path, predicatesMap), toBeValidated.getHl7Version());
          if (valE != null) {
            items.putAll(valE);

          }
          if (toBeValidated.getChildren().get(i) instanceof SegmentRef) {
            SegmentRef toBeValSegRef = (SegmentRef) toBeValidated.getChildren().get(i);

            Segment childSegment = segmentService.findById(toBeValSegRef.getRef().getId());
            if (!childSegment.getScope().equals(SCOPE.HL7STANDARD)) {
              Segment childReference = segmentService.findByNameAndVersionAndScope(
                  childSegment.getName(), childSegment.getHl7Version(), "HL7STANDARD");
              ValidationResult block = validateSegment(childReference, childSegment, false,
                  igHl7Version, findChildrenPredicates(path, predicatesMap));
              if (result.getErrorCount() != null && block.getErrorCount() != null) {
                result.setErrorCount(result.getErrorCount() + block.getErrorCount());

              }

              blocks.put(toBeValidated.getChildren().get(i).getId(), block);


            } else {
              ValidationResult block = new ValidationResult();
              block.setErrorCount(0);
              block.setTargetId(childSegment.getId());
              blocks.put(toBeValidated.getChildren().get(i).getId(), block);

            }



          } else if (toBeValidated.getChildren().get(i) instanceof Group) {
            Group userGrp = (Group) toBeValidated.getChildren().get(i);
            ValidationResult block = null;
            if (reference.getChildren().get(i) instanceof Group) {
              Group referenceGrp = (Group) reference.getChildren().get(i);
              block = validateGroup(referenceGrp, userGrp, igHl7Version,
                  findChildrenPredicates(path, predicatesMap));
              if (result.getErrorCount() != null && block.getErrorCount() != null) {
                result.setErrorCount(result.getErrorCount() + block.getErrorCount());
              }
              blocks.put(toBeValidated.getChildren().get(i).getId(), block);
            }
          }
        }
      }
    }
    result.setItems(items);
    Integer itemCount = 0;
    Integer blockCount = 0;
    for (Map.Entry<String, List<ValidationError>> entry : items.entrySet()) {
      List<ValidationError> value = entry.getValue();
      itemCount = itemCount + value.size();

    }
    for (Map.Entry<String, ValidationResult> entry : blocks.entrySet()) {
      ValidationResult value = entry.getValue();
      blockCount = blockCount + value.getErrorCount();

    }
    result.setErrorCount(itemCount + blockCount);
    result.setBlocks(blocks);

    return result;
  }

  @Override
  public HashMap<String, List<ValidationError>> validateSegmentRefOrGroup(
      SegmentRefOrGroup reference, SegmentRefOrGroup toBeValidated, Predicate predicate,
      String hl7Version) throws InvalidObjectException {
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    if (reference.getUsage() != null) {

      String usageValidation =
          validateUsage(reference.getUsage(), toBeValidated.getUsage(), predicate, hl7Version);
      if (usageValidation != null) {
        ValidationError valError = new ValidationError();
        valError.setErrorMessage(usageValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setType("Error");
        valError.setTargetType(toBeValidated.getType());
        valError.setValidationType(ValidationType.USAGE);
        validationErrors.add(valError);
      }

    }



    String cardinalityValidation =
        validateCardinality(reference.getMin(), reference.getMax(), reference.getUsage().toString(),
            toBeValidated.getMin(), toBeValidated.getMax(), toBeValidated.getUsage().toString());

    if (cardinalityValidation != null) {
      ValidationError valError = new ValidationError();
      valError.setErrorMessage(cardinalityValidation);
      valError.setPosition(toBeValidated.getPosition());
      valError.setTargetId(toBeValidated.getId());
      valError.setType("Error");
      valError.setTargetType(toBeValidated.getType());
      valError.setValidationType(ValidationType.CARDINALITY);
      validationErrors.add(valError);
    }
    if (!validationErrors.isEmpty()) {
      items.put(toBeValidated.getId(), validationErrors);

    } else {
      return null;
    }


    return items;
  }

  public Set<String> getDatatypeIdsFromSegment(Segment seg) {

    Set<String> dtIds = new HashSet<String>();
    for (Field field : seg.getFields()) {
    	if(field.getAdded().equals(Constant.NO)){
    		dtIds.add(field.getDatatype().getId());
    	}

    }
    return dtIds;

  }

  @Override
  public ValidationResult validateSegment(Segment reference, Segment toBeValidated,
      boolean validateChildren, String igHl7Version, Map<String, Predicate> childrenPredicates)
      throws InvalidObjectException {

    ValidationResult result = new ValidationResult();
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    HashMap<String, ValidationResult> blocks = new HashMap<String, ValidationResult>();
    result.setTargetId(toBeValidated.getId());

    Set<String> referenceDtIds = getDatatypeIdsFromSegment(reference);
    Set<String> toBeValidatedDtIds = getDatatypeIdsFromSegment(toBeValidated);
    List<Datatype> referenceDts = datatypeService.findByIds(referenceDtIds);
    List<Datatype> toBeValidatedDts = datatypeService.findByIds(toBeValidatedDtIds);
    HashMap<String, Datatype> userDtMap = new HashMap<String, Datatype>();
    for (Datatype dt : toBeValidatedDts) {
      userDtMap.put(dt.getId(), dt);
    }
    HashMap<String, Datatype> referenceDtMap = new HashMap<String, Datatype>();
    for (Datatype dt : referenceDts) {
      referenceDtMap.put(dt.getId(), dt);
    }
    
    

    if (toBeValidated.getName().equals(reference.getName()) && toBeValidated.getFields() != null
        && reference.getFields() != null) {
    	
    	List<Field> filteredToBeValidatedFields = new ArrayList<>();
    	for(Field field : toBeValidated.getFields()){
    		if(field.getAdded().equals(Constant.NO)){
    			filteredToBeValidatedFields.add(field);
    		}
    	}
      // Prerdicate Map
      Map<String, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          String target = toBeValidated.getPredicates().get(j).getConstraintTarget();
          predicatesMap.put(target, toBeValidated.getPredicates().get(j));
        }
      }

      if (childrenPredicates != null && !childrenPredicates.isEmpty()) {
        predicatesMap.putAll(childrenPredicates);
      }

      FieldComparator Comp = new FieldComparator();
      Collections.sort(reference.getFields(), Comp);
      Collections.sort(filteredToBeValidatedFields, Comp);
      for (int i = 0; i < reference.getFields().size(); i++) {

        if (i < filteredToBeValidatedFields.size()) {
          boolean validateConf = true;

          if (!userDtMap.get(filteredToBeValidatedFields.get(i).getDatatype().getId()).getComponents()
              .isEmpty()) {
            validateConf = false;
          }
          String path = filteredToBeValidatedFields.get(i).getPosition() + "[1]";

          
          HashMap<String, List<ValidationError>> valE = validateField(reference.getFields().get(i),
        		  filteredToBeValidatedFields.get(i), findPredicate(path, predicatesMap),
              toBeValidated.getHl7Version(), toBeValidated.getId(), igHl7Version, validateConf);
          if (valE != null) {
            items.putAll(valE);

          }
          if (!userDtMap.get(filteredToBeValidatedFields.get(i).getDatatype().getId()).getComponents()
              .isEmpty()) {



            Datatype childDatatype =
                userDtMap.get(filteredToBeValidatedFields.get(i).getDatatype().getId());
            if (!childDatatype.getScope().equals(SCOPE.HL7STANDARD)) {

              Datatype childReference =
                  referenceDtMap.get(reference.getFields().get(i).getDatatype().getId());
              ValidationResult block = validateDatatype(childReference, childDatatype,
            		  filteredToBeValidatedFields.get(i).getId(), igHl7Version,
                  findChildrenPredicates(path, predicatesMap));

              if (result.getErrorCount() != null && block.getErrorCount() != null) {
                result.setErrorCount(result.getErrorCount() + block.getErrorCount());

              }
              blocks.put(filteredToBeValidatedFields.get(i).getId(), block);


            } else {
              ValidationResult block = new ValidationResult();
              block.setErrorCount(0);
              block.setTargetId(childDatatype.getId());
              blocks.put(filteredToBeValidatedFields.get(i).getId(), block);

            }



          } else {
            Datatype childDatatype =
                userDtMap.get(filteredToBeValidatedFields.get(i).getDatatype().getId());
            ValidationResult block = new ValidationResult();
            block.setErrorCount(0);
            block.setTargetId(childDatatype.getId());
            blocks.put(filteredToBeValidatedFields.get(i).getId(), block);
          }

        }



      }

    }


    result.setItems(items);
    Integer itemCount = 0;
    Integer blockCount = 0;
    for (Map.Entry<String, List<ValidationError>> entry : items.entrySet()) {
      List<ValidationError> value = entry.getValue();
      itemCount = itemCount + value.size();

    }
    for (Map.Entry<String, ValidationResult> entry : blocks.entrySet()) {
      ValidationResult value = entry.getValue();
      blockCount = blockCount + value.getErrorCount();

    }
    result.setErrorCount(itemCount + blockCount);
    result.setBlocks(blocks);
    result.setTargetId(toBeValidated.getId());

    return result;
  }



  public Set<String> getDatatypeIdsFromDatatype(Datatype dt) {

    Set<String> dtIds = new HashSet<String>();
    for (Component comp : dt.getComponents()) {
      dtIds.add(comp.getDatatype().getId());

    }
    return dtIds;

  }

  @Override
  public ValidationResult validateDatatype(Datatype reference, Datatype toBeValidated,
      String parentId, String igHl7Version, Map<String, Predicate> childrenPredicates)
      throws InvalidObjectException {

    ValidationResult result = new ValidationResult();
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    HashMap<String, ValidationResult> blocks = new HashMap<String, ValidationResult>();
    Set<String> referenceDtIds = getDatatypeIdsFromDatatype(reference);
    Set<String> toBeValidatedDtIds = getDatatypeIdsFromDatatype(toBeValidated);
    List<Datatype> referenceDts = datatypeService.findByIds(referenceDtIds);
    List<Datatype> toBeValidatedDts = datatypeService.findByIds(toBeValidatedDtIds);
    HashMap<String, Datatype> userDtMap = new HashMap<String, Datatype>();
    for (Datatype dt : toBeValidatedDts) {
      userDtMap.put(dt.getId(), dt);
    }
    HashMap<String, Datatype> referenceDtMap = new HashMap<String, Datatype>();
    for (Datatype dt : referenceDts) {
      referenceDtMap.put(dt.getId(), dt);
    }


    if (toBeValidated.getName().equals(reference.getName()) && toBeValidated.getComponents() != null
        && reference.getComponents() != null) {
      // Build predicates Map
      Map<String, Predicate> predicatesMap = new HashMap<>();
      if (toBeValidated.getPredicates().size() > 0) {
        for (int j = 0; j < toBeValidated.getPredicates().size(); j++) {
          String target = toBeValidated.getPredicates().get(j).getConstraintTarget();
          predicatesMap.put(target, toBeValidated.getPredicates().get(j));
        }
      }

      if (childrenPredicates != null && !childrenPredicates.isEmpty()) {
        predicatesMap.putAll(childrenPredicates);
      }

      ComponentComparator comp = new ComponentComparator();
      Collections.sort(reference.getComponents(), comp);
      Collections.sort(toBeValidated.getComponents(), comp);

      for (int i = 0; i < reference.getComponents().size(); i++) {
        boolean validateConf = true;
        if (!userDtMap.get(toBeValidated.getComponents().get(i).getDatatype().getId())
            .getComponents().isEmpty()) {
          validateConf = false;
        }

        String path = toBeValidated.getComponents().get(i).getPosition() + "[1]";

        HashMap<String, List<ValidationError>> valE =
            validateComponent(reference.getComponents().get(i),
                toBeValidated.getComponents().get(i), findPredicate(path, predicatesMap),
                toBeValidated.getHl7Version(), toBeValidated.getId(), igHl7Version, validateConf);
        items.putAll(valE);

        if (!userDtMap.get(toBeValidated.getComponents().get(i).getDatatype().getId())
            .getComponents().isEmpty()) {

          Datatype childDatatype =
              userDtMap.get(toBeValidated.getComponents().get(i).getDatatype().getId());
          if (!childDatatype.getScope().equals(SCOPE.HL7STANDARD)) {

            Datatype childReference =
                referenceDtMap.get(reference.getComponents().get(i).getDatatype().getId());
            ValidationResult block = validateDatatype(childReference, childDatatype,
                toBeValidated.getComponents().get(i).getId(), igHl7Version,
                findChildrenPredicates(path, predicatesMap));
            block.setParentId(toBeValidated.getId());
            if (result.getErrorCount() != null && block.getErrorCount() != null) {
              result.setErrorCount(result.getErrorCount() + block.getErrorCount());

            }
            blocks.put(toBeValidated.getComponents().get(i).getId(), block);


          } else {
            ValidationResult block = new ValidationResult();
            block.setErrorCount(0);
            block.setTargetId(childDatatype.getId());
            blocks.put(toBeValidated.getComponents().get(i).getId(), block);

          }



        } else {
          Datatype childDatatype =
              userDtMap.get(toBeValidated.getComponents().get(i).getDatatype().getId());
          ValidationResult block = new ValidationResult();
          block.setErrorCount(0);
          block.setTargetId(childDatatype.getId());
          blocks.put(toBeValidated.getComponents().get(i).getId(), block);
        }



      }
    }
    result.setTargetId(toBeValidated.getId());
    result.setItems(items);
    Integer itemCount = 0;
    Integer blockCount = 0;
    for (Map.Entry<String, List<ValidationError>> entry : items.entrySet()) {
      List<ValidationError> value = entry.getValue();
      itemCount = itemCount + value.size();

    }
    for (Map.Entry<String, ValidationResult> entry : blocks.entrySet()) {
      ValidationResult value = entry.getValue();
      blockCount = blockCount + value.getErrorCount();

    }
    result.setErrorCount(itemCount + blockCount);

    result.setBlocks(blocks);

    return result;
  }

  @Override
  public HashMap<String, List<ValidationError>> validateComponent(Component reference,
      Component toBeValidated, Predicate predicate, String hl7Version, String parentId,
      String igHl7Version, boolean validateConf) throws InvalidObjectException {
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    if (reference.getUsage() != null) {
      ValidationError valError = new ValidationError();

      String usageValidation =
          validateUsage(reference.getUsage(), toBeValidated.getUsage(), predicate, hl7Version);
      if (usageValidation != null) {
        valError.setErrorMessage(usageValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setTargetName(toBeValidated.getName());
        valError.setParentId(parentId);
        valError.setTargetType(toBeValidated.getType());
        valError.setType("Error");
        valError.setValidationType(ValidationType.USAGE);
        validationErrors.add(valError);
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
      valErr.setParentId(parentId);
      valErr.setTargetType(toBeValidated.getType());
      valErr.setType("Error");
      valErr.setValidationType(ValidationType.LENGTH);
      validationErrors.add(valErr);

    }
    if (validateConf) {
      String confLengthValidation = validateConfLength(toBeValidated.getConfLength(), igHl7Version);
      if (confLengthValidation != null) {
        ValidationError valError = new ValidationError();
        valError.setErrorMessage(confLengthValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setType("Error");
        valError.setTargetType(toBeValidated.getType());
        valError.setValidationType(ValidationType.CONFLENGTH);
        validationErrors.add(valError);
      }
    }



    if (!validationErrors.isEmpty()) {
      items.put(toBeValidated.getId(), validationErrors);

    }


    return items;
  }



  @Override
  public HashMap<String, List<ValidationError>> validateField(Field reference, Field toBeValidated,
      Predicate predicate, String hl7Version, String parentId, String igHl7Version,
      boolean validateConf) throws InvalidObjectException {
    HashMap<String, List<ValidationError>> items = new HashMap<String, List<ValidationError>>();
    List<ValidationError> validationErrors = new ArrayList<ValidationError>();


    if (reference.getUsage() != null) {
      String usageValidation =
          validateUsage(reference.getUsage(), toBeValidated.getUsage(), predicate, hl7Version);
      if (usageValidation != null) {
        ValidationError valError = new ValidationError();
        valError.setErrorMessage(usageValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setParentId(parentId);
        valError.setTargetName(toBeValidated.getName());
        valError.setType("Error");
        valError.setTargetType(toBeValidated.getType());
        valError.setValidationType(ValidationType.USAGE);
        validationErrors.add(valError);
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
      valErr.setTargetType(toBeValidated.getType());
      valErr.setParentId(parentId);
      valErr.setType("Error");
      valErr.setValidationType(ValidationType.LENGTH);
      validationErrors.add(valErr);
    }
    String cardinalityValidation =
        validateCardinality(reference.getMin(), reference.getMax(), reference.getUsage().toString(),
            toBeValidated.getMin(), toBeValidated.getMax(), toBeValidated.getUsage().toString());

    if (cardinalityValidation != null) {
      ValidationError valError = new ValidationError();
      valError.setErrorMessage(cardinalityValidation);
      valError.setPosition(toBeValidated.getPosition());
      valError.setTargetId(toBeValidated.getId());
      valError.setType("Error");
      valError.setTargetType(toBeValidated.getType());
      valError.setValidationType(ValidationType.CARDINALITY);
      validationErrors.add(valError);
    }
    if (validateConf) {
      String confLengthValidation = validateConfLength(toBeValidated.getConfLength(), igHl7Version);
      if (confLengthValidation != null) {
        ValidationError valError = new ValidationError();
        valError.setErrorMessage(confLengthValidation);
        valError.setPosition(toBeValidated.getPosition());
        valError.setTargetId(toBeValidated.getId());
        valError.setType("Error");
        valError.setTargetType(toBeValidated.getType());
        valError.setValidationType(ValidationType.CONFLENGTH);
        validationErrors.add(valError);
      }
    }


    if (!validationErrors.isEmpty()) {
      items.put(toBeValidated.getId(), validationErrors);

    } else {
      return null;
    }


    return items;
  }



  @Override
  public String validateUsage(Usage reference, Usage newValueForUsage, Predicate predicate,
      String hl7Version) {

    String validationResult = null;
    //
    // if (predicate != null && newValueForUsage != Usage.C) {
    // return "Usage must be conditional when a predicate is defined";
    // } else

    if (predicate == null && newValueForUsage == Usage.C) {
      return "Predicate is missing for conditional usage " + newValueForUsage.value();

    }

    JsonNode node = getRulesNodeBySchemaVersion(hl7Version).path("constrainable")
        .get(USAGE_RULES_ROOT).get(reference.value());
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



    return null;
  }

  @Override
  public String validateLength(String referenceMinLen, String referenceMaxLen, String toBeMinLen,
      String toBeMaxLen) {
    if (toBeMinLen == null|| toBeMinLen.isEmpty()) {
      return "Min Length cannot be empty";
    }
    if (toBeMaxLen == null|| toBeMaxLen.isEmpty()) {
      return "Max Length cannot be empty";
    }

    if ((DataElement.LENGTH_NA.equals(toBeMinLen) && !DataElement.LENGTH_NA.equals(toBeMaxLen))) {
      return "Max Length must be NA because Min Length is NA ";
    }
    if ((DataElement.LENGTH_NA.equals(toBeMaxLen) && !DataElement.LENGTH_NA.equals(toBeMaxLen))) {
        return "Min Length must be NA because Max Length is NA ";
    }

    String result = null;
    if (!NumberUtils.isNumber(toBeMaxLen)) {
      if (!"*".equalsIgnoreCase(toBeMaxLen) && !DataElement.LENGTH_NA.equals(toBeMaxLen)) {
        result = "Max Length has to be * or a numerical value.";
      }
      return result;
    }

    if (!NumberUtils.isNumber(toBeMinLen)) {
      if (!DataElement.LENGTH_NA.equals(toBeMinLen)) {
        result = "Min Length has to be NA or a numerical value.";
      }
      return result;
    }


    int toBeMaxLenT = Integer.valueOf(toBeMaxLen.trim());
    int toBeMinLenT = Integer.valueOf(toBeMinLen.trim());

    if (toBeMaxLenT < toBeMinLenT) {
      result = "Max Length cannot be less than Min Length.";
    }

    return result;
  }

  @Override
  public String validateCardinality(Integer referenceMin, String referenceMax,
      String referenceUsage, Integer toBeMin, String pToBeMax, String toBeUsage) {
    String result = null;
    if (toBeMin == null) {
      return "Cardinality Min cannot be empty";
    }
    if (!NumberUtils.isNumber(pToBeMax) && !StringUtils.equalsIgnoreCase(pToBeMax, "*")) {

      return "Cardinality Max has to be * or a numerical value.";

    }

    int toBeMax = 0;
    if ("*".equalsIgnoreCase(pToBeMax)) {
      toBeMax = Integer.MAX_VALUE;
    } else {
      toBeMax = Integer.valueOf(pToBeMax);
    }
    String maxCannotBeLessThanMin = "Cardinality Max cannot be less than Cardinality Min.";
    // if X usage, then cardinality min and max cannot be greater than 0
    if (toBeUsage.equalsIgnoreCase(Usage.X.toString())) {
      if (toBeMin != 0 || toBeMax != 0) {
        result = "Cardinality Min and Max must be 0 when Usage is: " + toBeUsage;
      }
      return "".equals(result) ? null : result;
    }
    // if R usage, then cardinality min has to be greater than zero and
    // cardinality max
    // cannot be less than cardinality min
    if (toBeUsage.equalsIgnoreCase(Usage.R.toString())) {
      String message = "";
      if (toBeMin < 1) {
        message =
            message + "Cardinality Min cannot be less than 1 when Usage is: " + toBeUsage + ". ";

      }
      if (toBeMax < toBeMin) {
        message = message + maxCannotBeLessThanMin;
      }
      result = message;
      return "".equals(result) ? null : result;
    }

    // if RE usage, then cardinality min cannot be less than zero and
    // cardinality max
    // cannot be less than cardinality min
    if (toBeUsage.equalsIgnoreCase(Usage.RE.toString())) {

      String message = "";

      if (toBeMin < 0) {
        message = "Cardinality Min must be 0 when Usage is: " + toBeUsage + ". ";
      }

      if (toBeMin >= 0 && toBeMin > toBeMax) {
        message = message + (maxCannotBeLessThanMin);
      }
      result = message;
      return "".equals(result) ? null : result;
    }
    // if C,CE,O, or C(a/b) usage, min cannot be less than or greater than
    // zero and cardinality
    // max can not be less than 1
    if (toBeUsage.equalsIgnoreCase(Usage.O.value())
        || toBeUsage.equalsIgnoreCase(Usage.C.value())) {

      if (toBeMin != 0 && !(toBeMax >= 1 && toBeMax != toBeMin)) {
        String message = "Cardinality Min must be 0 when Usage is: " + toBeUsage + ". ";
        message = message + (maxCannotBeLessThanMin);
        result = message;
      }
      if (!(toBeMin == 0) && (toBeMax >= 1 && toBeMax != toBeMin)) {
        String message = "Cardinality Min must be 0 when Usage is: " + toBeUsage + ". ";
        result = message;
      }
      return "".equals(result) ? null : result;
    }

    return "".equals(result) ? null : result;


  }

  @Override
  public String validateConfLength(String confLength, String igHl7Version) {
    if (igHl7Version.compareTo("2.5.1") > 0) {
      if (confLength == null) {
        return "Conf. Length cannot be empty";
      } else if (!DataElement.LENGTH_NA.equals(confLength)) {
        Pattern pattern = Pattern.compile("\\d*[#=]{0,1}");
        Matcher m = pattern.matcher(confLength);
        if (!m.matches()) {
          return "Conf. Length is invalid. Expected format is " + pattern;
        }
      }

    }

    return null;
  }



  /***************************************************************************************************
   * HELPER METHODS
   ***************************************************************************************************/

  private JsonNode getRulesNodeBySchemaVersion(String schemaVersion) {

    JsonNode node = root.path(schemaVersion);
    return node.isMissingNode() ? root.path(UNSUPPORTED_SCHEMA_VERSION) : node;

  }



}
