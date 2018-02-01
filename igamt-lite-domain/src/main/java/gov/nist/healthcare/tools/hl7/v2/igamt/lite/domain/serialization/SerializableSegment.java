package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.DatatypeNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.*;
import org.apache.commons.lang3.StringUtils;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CoConstraintExportMode;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUSERColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUserColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;
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
  private Boolean duplicateOBXDataTypeWhenFlavorNull;
  private String host;
  CoConstraintExportMode coConstraintExportMode;

  public SerializableSegment(String id, String prefix, String position, String headerLevel,
      String title, Segment segment, String name, String label, String description, String comment,
      String defPreText, String defPostText, List<SerializableConstraint> constraints,
      Map<Field, Datatype> fieldDatatypeMap,
      Map<Field, List<ValueSetOrSingleCodeBinding>> fieldValueSetBindingsMap, List<Table> tables,
      Map<String, Table> coConstraintValueTableMap, Map<String, Datatype> dynamicMappingDatatypeMap,
      Boolean showConfLength, Boolean showInnerLinks, Boolean duplicateOBXDataTypeWhenFlavorNull, String host,
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
    this.duplicateOBXDataTypeWhenFlavorNull = duplicateOBXDataTypeWhenFlavorNull;
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
        	
        	
          Element coConstraintsElement = generateCoConstraintsTable();
          if (coConstraintsElement != null) {
            segmentElement.appendChild(coConstraintsElement);
          }

        }
        DynamicMappingDefinition dynamicMappingDefinition = segment.getDynamicMappingDefinition();
        if (dynamicMappingDefinition != null && !dynamicMappingDefinition.getDynamicMappingItems()
            .isEmpty()) {
          Element dynamicMappingElement = generateDynamicMappingElement();
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
  
  
  Element generateCoConstraintsTable() throws CoConstraintSerializationException{
	  if(this.coConstraintExportMode ==null||this.coConstraintExportMode.equals(coConstraintExportMode.COMPACT)){
		  return this.generateCoConstraintsTableCompact();
		  
	  }else{
		  return this.generateCoConstraintsTableVerbose();
	  }
	  
  }
  private Element generateDynamicMappingElement() throws DynamicMappingSerializationException {
    try {
      DynamicMappingDefinition dynamicMappingDefinition = segment.getDynamicMappingDefinition();
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
	            Datatype datatype = this.dynamicMappingDatatypeMap.get(dynamicMappingItem.getDatatypeId());
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



  private Element generateCoConstraintsTableCompact() throws CoConstraintSerializationException {
    CoConstraintsTable coConstraintsTable = segment.getCoConstraintsTable();
    if(coConstraintsTable != null) {
      try {
        Element coConstraintsElement = new Element("coconstraints");
        Element tableElement = new Element("table");
        tableElement.addAttribute(new Attribute("class", "contentTable"));
        Element thead = new Element("thead");
        thead.addAttribute(new Attribute("class", "contentThead"));
        Element tr = new Element("tr");
        Element th = new Element("th");
        th.addAttribute(new Attribute("class", "ifContentThead"));
        th.appendChild("IF");
        tr.appendChild(th);
        th = new Element("th");
        th.addAttribute(new Attribute("colspan", String.valueOf(calSize(coConstraintsTable.getThenColumnDefinitionList()))));
        th.appendChild("THEN");
        tr.appendChild(th);
        th = new Element("th");
        th.addAttribute(new Attribute("colspan", String.valueOf(coConstraintsTable.getUserColumnDefinitionList().size())));
        th.appendChild("USER");
        tr.appendChild(th);
        thead.appendChild(tr);
        tr = new Element("tr");
        th = new Element("th");
        th.addAttribute(new Attribute("class", "ifContentThead"));
        th.appendChild(this.segment.getName() + "-" + coConstraintsTable.getIfColumnDefinition().getPath());
        tr.appendChild(th);
        for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
            .getThenColumnDefinitionList()) {
          if (this.segment.getName().equals("OBX") && coConstraintColumnDefinition.getPath()
              .equals("2")) {
            Element thThen1 = new Element("th");
            thThen1.appendChild(
                this.segment.getName() + "-" + coConstraintColumnDefinition.getPath() + "(Value)");
            tr.appendChild(thThen1);
            Element thThen2 = new Element("th");
            thThen2.appendChild(
                this.segment.getName() + "-" + coConstraintColumnDefinition.getPath() + "(Flavor)");
            tr.appendChild(thThen2);
          } else {
            Element thThen = new Element("th");
            thThen
                .appendChild(this.segment.getName() + "-" + coConstraintColumnDefinition.getPath());
            tr.appendChild(thThen);
          }
        }
        for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : coConstraintsTable
            .getUserColumnDefinitionList()) {
          Element thUser = new Element("th");
          thUser.appendChild(coConstraintColumnDefinition.getTitle());
          tr.appendChild(thUser);
        }
        thead.appendChild(tr);
        tableElement.appendChild(thead);
        Element tbody = new Element("tbody");
        for (int i = 0; i < coConstraintsTable.getRowSize(); i++) {
          if (coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() != null && !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue().isEmpty()) {
            boolean thenEmpty = true;
            for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
                .getThenColumnDefinitionList()) {
              if (!coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
                  .get(i).getValueSets().isEmpty() || coConstraintsTable.getThenMapData() != null && coConstraintsTable
                  .getThenMapData().containsKey(coConstraintColumnDefinition.getId()) &&
                  (coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
                      .get(i).getValueData().getValue()) != null && !(coConstraintsTable
                  .getThenMapData().get(coConstraintColumnDefinition.getId()).get(i).getValueData()
                  .getValue()).isEmpty()) {
                thenEmpty = false;
                break;
              }
            }
            if (!thenEmpty) {
              tr = new Element("tr");
              Element td = new Element("td");
              td.appendChild(coConstraintsTable.getIfColumnData().get(i).getValueData().getValue());
              tr.appendChild(td);
              for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
                  .getThenColumnDefinitionList()) {
                if (this.segment.getName().equals("OBX") && coConstraintColumnDefinition.getPath()
                    .equals("2")) {
                  CoConstraintTHENColumnData coConstraintTHENColumnData =
                      coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
                          .get(i);
                  td = new Element("td");
                  Element td2 = new Element("td");
                  if (coConstraintTHENColumnData.getValueData() == null
                      || coConstraintTHENColumnData.getValueData().getValue() == null || coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
                    td.addAttribute(new Attribute("class", "greyCell"));
                  } else {
                    td.appendChild(coConstraintTHENColumnData.getValueData().getValue());
                  }
                  if (coConstraintTHENColumnData.getDatatypeId() == null) {
                    if (this.duplicateOBXDataTypeWhenFlavorNull
                        && coConstraintTHENColumnData.getValueData() != null
                        && coConstraintTHENColumnData.getValueData().getValue() != null && !coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
                      td2.appendChild(coConstraintTHENColumnData.getValueData().getValue());
                    } else {
                      td2.addAttribute(new Attribute("class", "greyCell"));
                    }
                  } else {
                    td2.appendChild(coConstraintDatatypeMap.get(coConstraintTHENColumnData.getDatatypeId())
                            .getLabel());
                  }
                  tr.appendChild(td);
                  tr.appendChild(td2);
                } else {
                  CoConstraintTHENColumnData coConstraintTHENColumnData =
                      coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
                          .get(i);
                  td = new Element("td");
                  if (coConstraintTHENColumnData.getValueSets().isEmpty()) {
                    if (coConstraintTHENColumnData.getValueData() == null
                        || coConstraintTHENColumnData.getValueData().getValue() == null || coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
                      td.addAttribute(new Attribute("class", "greyCell"));
                    } else {
                      td.appendChild(coConstraintTHENColumnData.getValueData().getValue());
                    }
                  } else {
                    ArrayList<String> valueSetsList = new ArrayList<>();
                    for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
                      Table table = coConstraintValueTableMap.get(valueSetData.getTableId());
                      if (table != null) {
                        valueSetsList.add(table.getBindingIdentifier());
                      }
                    }
                    td.appendChild(StringUtils.join(valueSetsList, ","));
                  }
                  tr.appendChild(td);
                }
              }
              for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : coConstraintsTable
                  .getUserColumnDefinitionList()) {
                CoConstraintUSERColumnData coConstraintUSERColumnData =
                    coConstraintsTable.getUserMapData().get(coConstraintColumnDefinition.getId())
                        .get(i);
                td = new Element("td");
                if (coConstraintUSERColumnData != null && coConstraintUSERColumnData.getText() != null
                    && !coConstraintUSERColumnData.getText().isEmpty()) {
                  td.appendChild(coConstraintUSERColumnData.getText());
                } else {
                  td.addAttribute(new Attribute("class", "greyCell"));
                }
                tr.appendChild(td);
              }
              tbody.appendChild(tr);
            } else {
              throw new CoConstraintDataSerializationException("THEN",i+1,"Empty THEN column data");
            }
          } else {
            throw new CoConstraintDataSerializationException("IF",i+1,"Missing IF column data");
          }
        }
        tableElement.appendChild(tbody);
        coConstraintsElement.appendChild(tableElement);
        return coConstraintsElement;
      } catch (Exception e){
        throw new CoConstraintSerializationException(e,"Co-Constraints table");
      }
    }
    return null;
  }

  private int calSize(List<CoConstraintColumnDefinition> thenColumnDefinitionList) {
    int count = 0;
    for (CoConstraintColumnDefinition coConstraintColumnDefinition : thenColumnDefinitionList) {
      if (this.segment.getName().equals("OBX")
          && coConstraintColumnDefinition.getPath().equals("2")) {
        count = count + 2;
      } else {
        count = count + 1;
      }
    }
    return count;
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
  private Element generateCoConstraintsTableVerbose() throws CoConstraintSerializationException {
	    CoConstraintsTable coConstraintsTable = segment.getCoConstraintsTable();
	    if(coConstraintsTable != null) {
	      try {
	        Element coConstraintsElement = new Element("coconstraints");
	        Element tableElement = new Element("table");
	        tableElement.addAttribute(new Attribute("class", "contentTable"));
	        Element thead = new Element("thead");
	        thead.addAttribute(new Attribute("class", "contentThead"));
	        Element tr = new Element("tr");
	        Element th = new Element("th");
	        th.addAttribute(new Attribute("class", "ifContentThead"));
	        th.appendChild("IF ("+(this.segment.getName() + "-" + coConstraintsTable.getIfColumnDefinition().getPath())+")");

	        tr.appendChild(th);
	        th = new Element("th");
	        th.appendChild("Column");
	        tr.appendChild(th);
	        th = new Element("th");
	        th.appendChild("Value");
	        tr.appendChild(th);
	        thead.appendChild(tr);
	        
	        tableElement.appendChild(thead);
	        Element tbody = new Element("tbody");
	        for (int i = 0; i < coConstraintsTable.getRowSize(); i++) {
	          if (coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() != null && !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue().isEmpty()) {
	            boolean thenEmpty = true;
	            for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
	                .getThenColumnDefinitionList()) {
	              if (!coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
	                  .get(i).getValueSets().isEmpty() || coConstraintsTable.getThenMapData() != null && coConstraintsTable
	                  .getThenMapData().containsKey(coConstraintColumnDefinition.getId()) &&
	                  (coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId())
	                      .get(i).getValueData().getValue()) != null && !(coConstraintsTable
	                  .getThenMapData().get(coConstraintColumnDefinition.getId()).get(i).getValueData()
	                  .getValue()).isEmpty()) {
	                thenEmpty = false;
	                break;
	              }
	            }
	            if (!thenEmpty) {
	            	CoConstaintVerboseRow line= getRow(coConstraintsTable,i);
	            	if(line.children.size()>0){
	            		Element tr1= new Element("tr");
	            		Element td1 = new Element("td");
	            		Element td2=new Element("td");
	            		Element td3=new Element("td");
	            		if(coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() !=null&& !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue().isEmpty()){
	            		td1.appendChild(coConstraintsTable.getIfColumnData().get(i).getValueData().getValue());	
	  	  
	            		}else{
	            			break;
	            		}
	                	td1.addAttribute(new Attribute("rowspan", String.valueOf(line.children.size())));
	                	td1.addAttribute(new Attribute("class","ifContent"));
	                	if(line.children.get(0).getType().equals("user")){
//	                		td2.addAttribute(new Attribute("class","greenHeader"));
//	                		td3.addAttribute(new Attribute("class","greenContent"));
	                	}
	  	              	td2.appendChild(line.children.get(0).getKey());
                 		td2.addAttribute(new Attribute("colspan",String.valueOf(line.children.get(0).getColspan())));
                 		td2.addAttribute(new Attribute("class","alignCenter"));
                		if(line.children.get(0).getColspan()==1){
    	  	              	td3.appendChild(line.children.get(0).getValue());
    	  	              	tr1.appendChild(td3);

        				}
	  	              	td3.appendChild(line.children.get(0).getValue());
	  	              	tr1.appendChild(td1);
	  	              	tr1.appendChild(td2);
	            		tbody.appendChild(tr1);
	            		
	            		if(line.children.size()>1){
	            			for(int j=1; j<line.children.size(); j++){
	            				Element trtemp= new Element("tr");
	            				Element tdKey= new Element("td");
	            				Element tdValue=new Element("td");
    	                 		tdKey.addAttribute(new Attribute("colspan",String.valueOf(line.children.get(j).getColspan())));
	            				tdKey.appendChild(line.children.get(j).getKey());

	            				trtemp.appendChild(tdKey);

	    	                 	if(line.children.get(j).getType().equals("user")){
	    	                 		if(line.children.get(j).getColspan()<2){
	    	                 			trtemp.addAttribute(new Attribute("class","greenContent"));
	    	                 		}else{
	    	                 			trtemp.addAttribute(new Attribute("class","greenHeader alignCenter"));


	    	                 		}

	    	                 			//"greenHeader"
	    	                	}
	            				if(line.children.get(j).getColspan()==1){
		            				tdValue.appendChild(line.children.get(j).getValue());
		            				trtemp.appendChild(tdValue);

	            				}
	            				tbody.appendChild(trtemp);
	            				
	            				
	            			}
	            		}
	            	}
	 
	             //tbody.appendChild(tr);
	            } else {
	              throw new CoConstraintDataSerializationException("THEN",i+1,"Empty THEN column data");
	            }
	          } else {
	            throw new CoConstraintDataSerializationException("IF",i+1,"Missing IF column data");
	          }
	        }
	        tableElement.appendChild(tbody);
	        coConstraintsElement.appendChild(tableElement);
	        return coConstraintsElement;
	      } catch (Exception e){
	        throw new CoConstraintSerializationException(e,"Co-Constraints table");
	      }
	    }
	    return null;
	  }
  
  public  CoConstaintVerboseRow getRow(CoConstraintsTable table , int i){
	  int rowsPan=0;
	  CoConstaintVerboseRow  line=new CoConstaintVerboseRow();
	  if(table.getIfColumnData().get(i)!=null){
	  line.setId( table.getIfColumnData().get(i).getValueData().getValue());
	  }
	  
	 if(!table.getThenColumnDefinitionList().isEmpty()){
		 CoConstraintRow thenheader = new CoConstraintRow();
		 thenheader.setType("THEN");
		 thenheader.setKey("THEN");
		 thenheader.setValue("THEN");
		 thenheader.setColspan(2);
     	 line.children.add(thenheader);
		 for(CoConstraintColumnDefinition then :table.getThenColumnDefinitionList()){
	           if (this.segment.getName().equals("OBX") && then.getPath()
     	              .equals("2")) {
	        	   	CoConstraintRow row1 = new CoConstraintRow();
	        	   	row1.setKey(this.segment.getName() + "-" + then.getPath() + "(Value)");
	        	   	row1.setType("then");
	        	   	CoConstraintRow row2 = new CoConstraintRow();
	        		row2.setKey(this.segment.getName() + "-" + then.getPath() + "(Flavor)");
	        	   	row2.setType("then");
	        	   	
	        	    CoConstraintTHENColumnData coConstraintTHENColumnData =
		                   table.getThenMapData().get(then.getId()).get(i);
	        	    
	                  if (coConstraintTHENColumnData.getValueData() == null
		                      || coConstraintTHENColumnData.getValueData().getValue() == null || coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
	                	  		// 
		                  } else {
		                	  row1.setValue(coConstraintTHENColumnData.getValueData().getValue());
		                	  line.children.add(row1);
		                	  rowsPan++;
		                  }
		                  if (coConstraintTHENColumnData.getDatatypeId() == null) {
		                    if (this.duplicateOBXDataTypeWhenFlavorNull
		                        && coConstraintTHENColumnData.getValueData() != null
		                        && coConstraintTHENColumnData.getValueData().getValue() != null && !coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
		                      row2.setValue(coConstraintTHENColumnData.getValueData().getValue());
		                      rowsPan++;
		                      line.children.add(row2);
		                    } else {
		                      //td2.addAttribute(new Attribute("class", "greyCell"));
		                    }
		                  } else {
		                	  row2.setValue(coConstraintDatatypeMap.get(coConstraintTHENColumnData.getDatatypeId())
		                            .getLabel());
		                	  rowsPan++;
		                      line.children.add(row2);
		                      }
	        	   	}else{
	        	   		CoConstraintRow row = new CoConstraintRow();
	        	   		row.setType("then");
    	                row.setKey(this.segment.getName() + "-" + then.getPath());
    	                
    	                CoConstraintTHENColumnData coConstraintTHENColumnData =
    		                      table.getThenMapData().get(then.getId())
    		                          .get(i);
    		                  if (coConstraintTHENColumnData.getValueSets().isEmpty()) {
    		                    if (coConstraintTHENColumnData.getValueData() == null
    		                        || coConstraintTHENColumnData.getValueData().getValue() == null || coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
    		                    } else {
    		                      row.setValue(coConstraintTHENColumnData.getValueData().getValue());
    		                      row.setKey(row.getKey()+"(Value)");
    		                      line.children.add(row);
    		                      rowsPan++;
    		                    }
    		                  } else {
    		                    ArrayList<String> valueSetsList = new ArrayList<>();
    		                    for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
    		                      Table vs = coConstraintValueTableMap.get(valueSetData.getTableId());
    		                      if (vs != null) {
    		                        valueSetsList.add(vs.getBindingIdentifier());
    		                      }
    		                    }
    		                    row.setValue(StringUtils.join(valueSetsList, ","));
    		                    row.setKey(row.getKey()+"(Value Set)");
    		                    line.children.add(row);
  		                      	rowsPan++;
    		                  }   		
	        	   	}
		 }
	 }
	 if(table.getUserColumnDefinitionList().size()>0){
	  		CoConstraintRow row = new CoConstraintRow();

			row.setType("user");
	   		row.setKey("USER");
	   		row.setValue("USER");
	   		row.setColspan(2);
     	   line.children.add(row);

	 }
     for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : table
             .getUserColumnDefinitionList()) {
 	   		
  		CoConstraintRow row = new CoConstraintRow();
   		row.setType("user");
   		row.setKey(coConstraintColumnDefinition.getTitle());
     	      
           CoConstraintUSERColumnData coConstraintUSERColumnData =
              table.getUserMapData().get(coConstraintColumnDefinition.getId())
                   .get(i);
           if (coConstraintUSERColumnData != null && coConstraintUSERColumnData.getText() != null
               && !coConstraintUSERColumnData.getText().isEmpty()) {
        	   row.setValue(coConstraintUSERColumnData.getText());
        	   line.children.add(row);
        	   rowsPan++;
           } else {
           }
         }
	  return line;
	  
  }
  
  
  
  
  
  
}
