package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CoConstraintExportMode;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.DatatypeNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DynamicMappingItemSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DynamicMappingSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.FieldSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SegmentSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

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
 * Created by Maxence Lefort on 12/9/16.
 */
public class SerializableSegment extends SerializableSection {

  private Segment segment;
  private String defPreText, defPostText, name, label, description, comment;
  private List<SerializableConstraint> constraints;
  private Map<Field, Datatype> fieldDatatypeMap;
  private Map<Field, List<ValueSetOrSingleCodeBinding>> fieldValueSetBindingsMap;
  private List<Table> tables;
  private Map<String, Table> coConstraintValueTableMap;
  private Map<String, Datatype> coConstraintDatatypeMap;
  private Boolean showConfLength;
  private Map<String, Datatype> dynamicMappingDatatypeMap;
  private Boolean showInnerLinks;
  private Boolean greyOutOBX2FlavorColumn;
  private String host;
  private CoConstraintExportMode coConstraintExportMode;
  

  public SerializableSegment(String id, String prefix, String position, String headerLevel,
      String title, Segment segment, String name, String label, String description, String comment,
      String defPreText, String defPostText, List<SerializableConstraint> constraints,
      Map<Field, Datatype> fieldDatatypeMap,
      Map<Field, List<ValueSetOrSingleCodeBinding>> fieldValueSetBindingsMap, List<Table> tables,
      Map<String, Table> coConstraintValueTableMap, Map<String, Datatype> dynamicMappingDatatypeMap,
      Boolean showConfLength, Boolean showInnerLinks, Boolean greyOutOBX2FlavorColumn, String host,
      Map<String, Datatype> coConstraintDatatypeMap, CoConstraintExportMode coConstraintExportMode) {
    super(id, prefix, position, headerLevel, title);
    this.segment = segment;
    this.name = name;
    this.label = label;
    this.description = description;
    this.comment = comment;
    this.defPreText = defPreText;
    this.defPostText = defPostText;
    this.constraints = constraints;
    this.fieldDatatypeMap = fieldDatatypeMap;
    this.fieldValueSetBindingsMap = fieldValueSetBindingsMap;
    this.tables = tables;
    this.coConstraintValueTableMap = coConstraintValueTableMap;
    this.dynamicMappingDatatypeMap = dynamicMappingDatatypeMap;
    this.showConfLength = showConfLength;
    this.showInnerLinks = showInnerLinks;
    this.greyOutOBX2FlavorColumn = greyOutOBX2FlavorColumn;
    this.host = host;
    this.coConstraintDatatypeMap = coConstraintDatatypeMap;
    this.coConstraintExportMode= coConstraintExportMode;
  }



  @Override
  public Element serializeElement() throws SegmentSerializationException {
    Element segmentElement = new Element("Segment");
    Element segmentMetadata = super.createMetadataElement(segment);
	if(segmentMetadata!=null){
		segmentElement.appendChild(segmentMetadata);
	}
    try {
      if (segment != null) {
        segmentElement.addAttribute(new Attribute("id", segment.getId()));
        segmentElement.addAttribute(new Attribute("Name", this.name));
        segmentElement.addAttribute(new Attribute("Label", this.label));
        segmentElement.addAttribute(new Attribute("Position", ""));
        segmentElement.addAttribute(new Attribute("Description", this.description));
        segmentElement.addAttribute(new Attribute("ShowConfLength", String.valueOf(showConfLength)));
        if (this.comment != null && !this.comment.isEmpty()) {
          segmentElement.addAttribute(new Attribute("Comment", this.comment));
        }

        if ((segment.getText1() != null && !segment.getText1().isEmpty()) || (segment.getText2() != null && !segment.getText2().isEmpty())) {
          if (this.defPreText != null && !this.defPreText.isEmpty()) {
            segmentElement.appendChild(this.createTextElement("DefPreText", this.defPreText));
          }
          if (this.defPostText != null && !this.defPostText.isEmpty()) {
            segmentElement.appendChild(this.createTextElement("DefPostText", this.defPostText));
          }
        }

        if (segment.getValueSetBindings() != null && !segment.getValueSetBindings().isEmpty()) {
          Element valueSetBindingListElement = super
              .createValueSetBindingListElement(segment.getValueSetBindings(), this.tables,
                  segment.getLabel());
          if (valueSetBindingListElement != null) {
            segmentElement.appendChild(valueSetBindingListElement);
          }
        }
        if (segment.getComments() != null && !segment.getComments().isEmpty()) {
          Element commentListElement = super.createCommentListElement(segment.getComments(), segment.getLabel());
          if (commentListElement != null) {
            segmentElement.appendChild(commentListElement);
          }
        }
        for (int i = 0; i < segment.getFields().size(); i++) {
          try {
            Field field = segment.getFields().get(i);
            Element fieldElement = new Element("Field");
            boolean isComplex = false;
            fieldElement.addAttribute(new Attribute("Name", field.getName()));
            fieldElement.addAttribute(new Attribute("Usage", getFullUsage(segment, i).toString()));
            if(field.getDatatype() != null && !fieldDatatypeMap.containsKey(field)){
              throw new DatatypeNotFoundException(field.getDatatype().getId());
            }
            Datatype datatype = fieldDatatypeMap.get(field);
            if (field.getDatatype() != null && datatype != null) {
              fieldElement.addAttribute(new Attribute("Datatype", datatype.getLabel()));
              if (this.showInnerLinks) {
                String link = this.generateInnerLink(datatype, host);
                if (!"".equals(link)) {
                  fieldElement.addAttribute(new Attribute("InnerLink", link));
                }
              }
              if (datatype.getComponents().size() > 0) {
                isComplex = true;
                fieldElement.addAttribute(new Attribute("ConfLength", ""));
                fieldElement.addAttribute(new Attribute("MinLength", ""));
                fieldElement.addAttribute(new Attribute("MaxLength", ""));
              } else {
                if (field.getConfLength() != null && !"".equals(field.getConfLength())) {
                  fieldElement.addAttribute(new Attribute("ConfLength", field.getConfLength()));
                }
                fieldElement
                    .addAttribute(new Attribute("MinLength", String.valueOf(field.getMinLength())));
                if (field.getMaxLength() != null && !field.getMaxLength().equals(""))
                  fieldElement.addAttribute(new Attribute("MaxLength", field.getMaxLength()));
              }
            }
            if (this.fieldValueSetBindingsMap.containsKey(field)) {
              List<ValueSetOrSingleCodeBinding> valueSetBindings = this.fieldValueSetBindingsMap.get(field);
              if (valueSetBindings != null && !valueSetBindings.isEmpty()) {
                List<String> bindingIdentifierList = new ArrayList<>();
                for (ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : valueSetBindings) {
                  try {
                    if (valueSetOrSingleCodeBinding != null
                        && valueSetOrSingleCodeBinding.getTableId() != null && !valueSetOrSingleCodeBinding.getTableId().isEmpty()) {
                      Table table = super.findTable(tables, valueSetOrSingleCodeBinding.getTableId());
                      if (table != null) {
                        String link = this.generateInnerLink(table, host);
                        if (this.showInnerLinks && !"".equals(link)) {
                          String wrappedLink = this.wrapLink(link, table.getBindingIdentifier());
                          bindingIdentifierList.add(wrappedLink);
                        } else {
                          bindingIdentifierList.add(table.getBindingIdentifier());
                        }
                      } else {
                        throw new TableNotFoundException(valueSetOrSingleCodeBinding.getTableId());
                      }
                    }
                  } catch (Exception e) {
                    throw new TableSerializationException(e,valueSetOrSingleCodeBinding.getLocation());
                  }
                }
                String bindingIdentifier = StringUtils.join(bindingIdentifierList, ",");
                if (bindingIdentifier != null && !bindingIdentifier.isEmpty()) {
                  fieldElement.addAttribute(new Attribute("BindingIdentifier", bindingIdentifier));
                }
              }
            }
            fieldElement.addAttribute(new Attribute("complex", String.valueOf(isComplex)));
            fieldElement.addAttribute(new Attribute("Min", String.valueOf(field.getMin())));
            fieldElement.addAttribute(new Attribute("Max", field.getMax()));
            if (field.getItemNo() != null && !field.getItemNo().equals(""))
              fieldElement.addAttribute(new Attribute("ItemNo", field.getItemNo()));
            String comments = super.findComments(field.getPosition(), segment.getComments());
            if (comments != null && !comments.isEmpty())
              fieldElement.addAttribute(new Attribute("Comment", comments));
            fieldElement
                .addAttribute(new Attribute("Position", String.valueOf(field.getPosition())));

            if (field.getText() != null && !field.getText().isEmpty()) {
              fieldElement.appendChild(this.createTextElement("Text", field.getText()));
            }
            segmentElement.appendChild(fieldElement);
          } catch (Exception e) {
            throw new FieldSerializationException(e, "Field[" + i + "]");
          }
        }

        if (!constraints.isEmpty()) {
          for (SerializableConstraint constraint : constraints) {
            segmentElement.appendChild(constraint.serializeElement());
          }
        }
        CoConstraintsTable coConstraintsTable = segment.getCoConstraintsTable();
        if (coConstraintsTable.getIfColumnData() != null && !coConstraintsTable.getIfColumnData()
            .isEmpty()) {
        	  SerializableCoConstraints serializableCoConstraints = new SerializableCoConstraints(coConstraintsTable, this.segment.getName(), coConstraintValueTableMap, coConstraintDatatypeMap, coConstraintExportMode, greyOutOBX2FlavorColumn);
          Element coConstraintsElement = serializableCoConstraints.serializeElement();
          if (coConstraintsElement != null) {
            segmentElement.appendChild(coConstraintsElement);
          }

        }
        DynamicMappingDefinition dynamicMappingDefinition = segment.getDynamicMappingDefinition();
        if (dynamicMappingDefinition != null && !dynamicMappingDefinition.getDynamicMappingItems()
            .isEmpty()) {
          Element dynamicMappingElement = generateDynamicMappingElement(dynamicMappingDefinition, this.dynamicMappingDatatypeMap);
          if (dynamicMappingElement != null) {
            segmentElement.appendChild(dynamicMappingElement);
          }
        }
      }
    } catch (Exception e){
      throw new SegmentSerializationException(e,segment.getLabel());
    }

    return segmentElement;
  }
  
  public static Element generateDynamicMappingElement(DynamicMappingDefinition dynamicMappingDefinition, Map<String, Datatype> dynamicMappingDatatypeMap) throws DynamicMappingSerializationException {
    try {
      Element dynamicMappingElement = new Element("DynamicMapping");
      if (dynamicMappingDefinition.getMappingStructure() != null) {
        String basePath = "";
        if (dynamicMappingDefinition.getMappingStructure().getSegmentName() != null
            && !dynamicMappingDefinition.getMappingStructure().getSegmentName().isEmpty()) {
          basePath = dynamicMappingDefinition.getMappingStructure().getSegmentName() + "-";
        }
        if (dynamicMappingDefinition.getMappingStructure().getTargetLocation() != null
            && !dynamicMappingDefinition.getMappingStructure().getTargetLocation().isEmpty()) {
          dynamicMappingElement.addAttribute(new Attribute("DynamicDatatypeField", basePath + dynamicMappingDefinition.getMappingStructure().getTargetLocation()));
        }
        if (dynamicMappingDefinition.getMappingStructure().getReferenceLocation() != null
            && !dynamicMappingDefinition.getMappingStructure().getReferenceLocation().isEmpty()) {
          dynamicMappingElement.addAttribute(new Attribute("Reference", basePath + dynamicMappingDefinition.getMappingStructure().getReferenceLocation()));
        }
      }
      for (DynamicMappingItem dynamicMappingItem : dynamicMappingDefinition.getDynamicMappingItems()) {
        if (dynamicMappingItem != null) {
          Element dynamicMappingItemElement = new Element("DynamicMappingItem");
          try {
            if (dynamicMappingItem.getFirstReferenceValue() != null && !dynamicMappingItem
                .getFirstReferenceValue().isEmpty()) {
              dynamicMappingItemElement.addAttribute(new Attribute("FirstReferenceValue",
                  dynamicMappingItem.getFirstReferenceValue()));
            } else {
              throw new DynamicMappingItemSerializationException(new Exception(),dynamicMappingDefinition.getDynamicMappingItems()
                  .indexOf(dynamicMappingItem)+1,"Missing first reference value");
            }
            if (dynamicMappingItem.getSecondReferenceValue() != null && !dynamicMappingItem
                .getSecondReferenceValue().isEmpty()) {
              dynamicMappingItemElement.addAttribute(new Attribute("SecondReferenceValue",
                  dynamicMappingItem.getSecondReferenceValue()));
            }
            /*Second reference value isn't required
             * else {
              throw new DynamicMappingItemSerializationException(new Exception(), "DynamicMappingItem[" + dynamicMappingDefinition.getDynamicMappingItems()
                  .indexOf(dynamicMappingItem) + "]","Missing second reference value");
            }*/
            if(dynamicMappingItem.getDatatypeId() != null && !dynamicMappingItem.getDatatypeId().isEmpty()){
	            Datatype datatype = dynamicMappingDatatypeMap.get(dynamicMappingItem.getDatatypeId());
	            if (datatype != null) {
	              dynamicMappingItemElement.addAttribute(new Attribute("Datatype", datatype.getLabel()));
	            } else {
	              throw new DatatypeNotFoundException(dynamicMappingItem.getDatatypeId());
	            }
            } else {
            	throw new DynamicMappingItemSerializationException(new Exception(),dynamicMappingDefinition.getDynamicMappingItems()
                .indexOf(dynamicMappingItem)+1,"Missing datatype");
            }
          } catch (Exception e) {
        	  if(e instanceof DynamicMappingItemSerializationException){
        		  throw e;
        	  } else {
        		  throw new DynamicMappingItemSerializationException(e, dynamicMappingDefinition.getDynamicMappingItems()
        		  	.indexOf(dynamicMappingItem) + 1);
        	  }
          }
          dynamicMappingElement.appendChild(dynamicMappingItemElement);
        }
      }
      return dynamicMappingElement;
    } catch (Exception e){
      throw new DynamicMappingSerializationException(e,"Dynamic Mapping table");
    }
  }
  
  private String getFullUsage(Segment segment, int i) {
    List<Predicate> predicates = super.findPredicate(i + 1, segment.getPredicates());
    if (predicates == null || predicates.isEmpty()) {
      return segment.getFields().get(i).getUsage().toString();
    } else {
      Predicate p = predicates.get(0);
      return segment.getFields().get(i).getUsage().toString() + "(" + p.getTrueUsage() + "/"
          + p.getFalseUsage() + ")";
    }
  }

  public List<SerializableConstraint> getConstraints() {
    return constraints;
  }

  public Segment getSegment() {
    return segment;
  }

  public Map<String, Datatype> getCoConstraintDatatypeMap() {
    return coConstraintDatatypeMap;
  }

  public void setCoConstraintDatatypeMap(Map<String, Datatype> coConstraintDatatypeMap) {
    this.coConstraintDatatypeMap = coConstraintDatatypeMap;
  }  
  
}
