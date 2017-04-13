package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;

@Document(collection = "segment")
public class Segment extends DataModelWithConstraints implements java.io.Serializable, Cloneable, Comparable<Segment> {

	private static final long serialVersionUID = 1L;

	public Segment() {
		super();
		type = Constant.SEGMENT;
	}

	@Id
	private String id;

	private String label;

	private String ext;

	private List<Field> fields = new ArrayList<Field>();

	@Deprecated
	private DynamicMapping dynamicMapping = new DynamicMapping();

	private DynamicMappingDefinition dynamicMappingDefinition;

	private CoConstraintsTable coConstraintsTable = new CoConstraintsTable();

	private List<ValueSetOrSingleCodeBinding> valueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();

	private List<SingleElementValue> singleElementValues = new ArrayList<SingleElementValue>();

	private List<Comment> comments = new ArrayList<Comment>();

	private String name;

	private String description;

	protected String comment = "";

	private String text1 = "";

	private String text2 = "";

	private CoConstraints coConstraints = new CoConstraints();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		fields.add(field);
	}

	public void addValueSetBinding(ValueSetOrSingleCodeBinding vsb) {
		valueSetBindings.add(vsb);
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void addSingleElementValue(SingleElementValue sev) {
		singleElementValues.add(sev);
	}

	public Field findOneField(String id) {
		if (this.fields != null)
			for (Field m : this.fields) {
				if (id.equals(m.getId())) {
					return m;
				}
			}
		return null;
	}

	public Field findOneFieldByName(String name) {
		if (this.fields != null)
			for (Field m : this.fields) {
				if (name.equals(m.getName())) {
					return m;
				}
			}
		return null;
	}

	@Deprecated
	public Mapping findOneMappingByPositionAndByReference(int position, int reference) {
		if (this.dynamicMapping != null) {
			for (Mapping m : this.dynamicMapping.getMappings()) {
				if (m.getPosition() == position && m.getReference() == reference) {
					return m;
				}
			}
		}
		return null;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Segment [id=" + getId() + ", label=" + label + ", name=" + name + ", description=" + description
				+ ", comment=" + comment + "]";
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	@Deprecated
	public DynamicMapping getDynamicMapping() {
		return dynamicMapping;
	}

	@Deprecated
	public void setDynamicMapping(DynamicMapping dynamicMapping) {
		this.dynamicMapping = dynamicMapping;
	}

	public Segment clone() throws CloneNotSupportedException {
		Segment clonedSegment = new Segment();
		clonedSegment.setComment(comment);

		clonedSegment.setDescription(description);

		clonedSegment.setFields(new ArrayList<Field>());
		for (Field f : this.fields) {
			clonedSegment.addField(f.clone());
		}

		clonedSegment.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
		for (ValueSetOrSingleCodeBinding vsb : this.valueSetBindings) {
			clonedSegment.addValueSetBinding(vsb);
		}

		clonedSegment.setComments(new ArrayList<Comment>());
		for (Comment c : this.comments) {
			clonedSegment.addComment(c);
		}

		clonedSegment.setSingleElementValues(new ArrayList<SingleElementValue>());
		for (SingleElementValue sev : this.singleElementValues) {
			clonedSegment.addSingleElementValue(sev);
		}

		clonedSegment.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedSegment.addPredicate(cp.clone());
		}

		clonedSegment.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedSegment.addConformanceStatement(cs.clone());
		}

		clonedSegment.setLabel(label);
		clonedSegment.setName(name);
		clonedSegment.setText1(text1);
		clonedSegment.setText2(text2);
		clonedSegment.setHl7Version(this.getHl7Version());
		clonedSegment.setExt(ext);

		return clonedSegment;
	}

	@Override
	public int compareTo(Segment o) {
		int x = String.CASE_INSENSITIVE_ORDER.compare(
				this.getName() != null && this.label != null ? this.getName() + this.getLabel() : "",
				o.getName() != null && this.getLabel() != null ? o.getName() + this.getLabel() : "");
		if (x == 0) {
			x = (this.getName() != null && this.getLabel() != null ? this.getName() + this.getLabel() : "")
					.compareTo(o.getName() != null && this.getLabel() != null ? o.getName() + o.getLabel() : "");
		}
		return x;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(id).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Segment))
			return false;
		if (obj == this)
			return true;

		Segment rhs = (Segment) obj;
		return new EqualsBuilder().append(id, rhs.id).isEquals();
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getLabel() {
		if (this.ext == null) {
			return this.name;
		} else if (this.ext.equals("")) {
			return this.name;
		} else {
			return this.name + "_" + this.ext;
		}
	}

	public CoConstraints getCoConstraints() {
		return coConstraints;
	}

	public void setCoConstraints(CoConstraints coConstraints) {
		this.coConstraints = coConstraints;
	}

	public List<ValueSetOrSingleCodeBinding> getValueSetBindings() {
		return valueSetBindings;
	}

	public void setValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings) {
		this.valueSetBindings = valueSetBindings;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<SingleElementValue> getSingleElementValues() {
		return singleElementValues;
	}

	public void setSingleElementValues(List<SingleElementValue> singleElementValues) {
		this.singleElementValues = singleElementValues;
	}

	public DynamicMappingDefinition getDynamicMappingDefinition() {
		return dynamicMappingDefinition;
	}

	public void setDynamicMappingDefinition(DynamicMappingDefinition dynamicMappingDefinition) {
		this.dynamicMappingDefinition = dynamicMappingDefinition;
	}

	public CoConstraintsTable getCoConstraintsTable() {
		return coConstraintsTable;
	}

	public void setCoConstraintsTable(CoConstraintsTable coConstraintsTable) {
		this.coConstraintsTable = coConstraintsTable;
	}

	public List<ConformanceStatement> retrieveConformanceStatementsForConstant() {

		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

		for (SingleElementValue constant : this.singleElementValues) {
			String[] paths = constant.getLocation().split("\\.");
			String path = "";
			for (String p : paths) {
				path = path + "." + p + "[1]";
			}
			path = path.substring(1);

			String constraintId = this.getLabel() + "-" + constant.location;
			String description = this.getName() + "-" + constant.getLocation() + "(" + constant.getName()
					+ ") SHALL contain the constant value '" + constant.getValue() + "'.";
			String assertion = "<Assertion><PlainText Path=\"" + path + "\" Text=\"" + constant.getValue()
					+ "\" IgnoreCase=\"false\"/></Assertion>";
			ConformanceStatement cs = new ConformanceStatement();
			cs.setId(ObjectId.get().toString());
			cs.setConstraintId(constraintId);
			cs.setDescription(description);
			cs.setAssertion(assertion);

			results.add(cs);

		}
		return results;
	}

	public List<ConformanceStatement> retrieveConformanceStatementsForSingleCode() {
		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

		for (ValueSetOrSingleCodeBinding vsoscb : this.valueSetBindings) {
			if (vsoscb instanceof SingleCodeBinding) {
				SingleCodeBinding scb = (SingleCodeBinding) vsoscb;

				String[] paths = scb.getLocation().split("\\.");
				String path = "";
				for (String p : paths) {
					path = path + "." + p + "[1]";
				}
				path = path.substring(1);

				String constraintId = this.getLabel() + "-" + scb.getLocation();
				String description = this.getName() + "-" + scb.getLocation() + " SHALL contain the constant value '"
						+ scb.getCode().getValue() + "' drawn from the code system '" + scb.getCode().getCodeSystem()
						+ "'.";
				String assertion = "<Assertion><PlainText Path=\"" + path + "\" Text=\"" + scb.getCode().getValue()
						+ "\" IgnoreCase=\"false\"/></Assertion>";
				ConformanceStatement cs = new ConformanceStatement();
				cs.setId(ObjectId.get().toString());
				cs.setConstraintId(constraintId);
				cs.setDescription(description);
				cs.setAssertion(assertion);

				results.add(cs);
			}
		}
		return results;
	}

	public List<ConformanceStatement> retrieveAllConformanceStatements() {
		List<ConformanceStatement> results = this.conformanceStatements;
		results.addAll(this.retrieveConformanceStatementsForSingleCode());
		results.addAll(this.retrieveConformanceStatementsForConstant());
		return results;
	}
	
	public List<ConformanceStatement> retrieveAllConformanceStatementsForXML(Map<String, Table> tablesMap) {
		List<ConformanceStatement> results = this.conformanceStatements;
		results.addAll(this.retrieveConformanceStatementsForSingleCode());
		results.addAll(this.retrieveConformanceStatementsForConstant());
		results.addAll(this.retrieveConformanceStatementsForCoConstraints(tablesMap));
		return results;
	}

	private List<ConformanceStatement> retrieveConformanceStatementsForCoConstraints(Map<String, Table> tablesMap) {
		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();
		
		if(this.coConstraintsTable != null){
			if(this.coConstraintsTable.getIfColumnDefinition() != null){
				CoConstraintColumnDefinition definitionIF = this.coConstraintsTable.getIfColumnDefinition();
				
				for(int i=0; i<this.coConstraintsTable.getRowSize(); i++){
					CoConstraintIFColumnData ifData = this.coConstraintsTable.getIfColumnData().get(i);
					
					if(ifData != null && ifData.getValueData() != null && ifData.getValueData().getValue() != null){
						int index = 0;
						for(CoConstraintColumnDefinition definitionThen:this.coConstraintsTable.getThenColumnDefinitionList()){
							index = index + 1;
							if(definitionThen != null){
								String constraintId = this.getLabel() + "-CoConstraint-" + (i + 1) + "-" + index;
								
								String ifDescription = null;
								String ifAssertion = null;
								String thenDescription = null;
								String thenAssertion = null;
								
								ifDescription =  "If the value of " + this.getName() + "-" + definitionIF.getConstraintPath() + " (" + definitionIF.getName() + ") should be '" + ifData.getValueData().getValue() + "',";
								
								if(definitionIF.isPrimitive()){
									ifAssertion = "<PlainText Path=\"" + definitionIF.getConstraintPath() + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
								}else {
									if(ifData.getValueData().getBindingLocation() == null){
										ifAssertion = "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".1[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
									}else {
										//"1 or 4", "1 or 4 or 10"
										if(ifData.getValueData().getBindingLocation().equals("1 or 4")){
											ifAssertion = "<OR>" 
														+ "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".1[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
														+ "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".4[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
														+ "</OR>";
										}else if(ifData.getValueData().getBindingLocation().equals("1 or 4 or 10")){
											ifAssertion = "<OR><OR>" 
													+ "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".1[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
													+ "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".4[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
													+ "</OR>"
													+ "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".10[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
													+ "</OR>";
										}else {
											ifAssertion = "<PlainText Path=\"" + definitionIF.getConstraintPath() + "."+ ifData.getValueData().getBindingLocation()  +"[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
										}
										ifAssertion = "<PlainText Path=\"" + definitionIF.getConstraintPath() + ".1[1]" + "\" Text=\"" + ifData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
									}
								}
								
								CoConstraintTHENColumnData thenData = this.coConstraintsTable.getThenMapData().get(definitionThen.getId()).get(i);
								if(definitionThen.getConstraintType().equals("value") || definitionThen.getConstraintType().equals("dmr")){
									thenDescription = "then the value of " + this.getName() + "-" + definitionThen.getConstraintPath() + " (" + definitionThen.getName() + ") should be '"
														+ thenData.getValueData().getValue() + "'.";
									
									if(definitionThen.isPrimitive()){
										thenAssertion = "<PlainText Path=\"" + definitionThen.getConstraintPath() + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
									}else{
										if(thenData.getValueData().getBindingLocation() == null){
											thenAssertion = "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".1[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
										}else {
											//"1 or 4", "1 or 4 or 10"
											if(thenData.getValueData().getBindingLocation().equals("1 or 4")){
												thenAssertion = "<OR>" 
															+ "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".1[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
															+ "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".4[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
															+ "</OR>";
											}else if(thenData.getValueData().getBindingLocation().equals("1 or 4 or 10")){
												thenAssertion = "<OR><OR>" 
														+ "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".1[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
														+ "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".4[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
														+ "</OR>"
														+ "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".10[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>"
														+ "</OR>";
											}else {
												thenAssertion = "<PlainText Path=\"" + definitionThen.getConstraintPath() + "."+ thenData.getValueData().getBindingLocation()  +"[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
											}
											thenAssertion = "<PlainText Path=\"" + definitionThen.getConstraintPath() + ".1[1]" + "\" Text=\"" + thenData.getValueData().getValue() + "\" IgnoreCase=\"false\"/>";
										}
									}
								}else if(definitionThen.getConstraintType().equals("valueset")){
									thenDescription = "then the value of " + this.getName() + "-" +definitionThen.getConstraintPath() + " (" + definitionThen.getName() + ") should be one of codes in ";
									for(ValueSetData vs:thenData.getValueSets()){
										Table t = tablesMap.get(vs.getTableId());
										thenDescription = "'" + thenDescription + t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-") + "' ";
									}
									thenDescription = thenDescription + ".";
								
									if(thenData.getValueSets().size() == 1){
										Table t = tablesMap.get(thenData.getValueSets().get(0).getTableId());
										if(thenData.getValueSets().get(0).getBindingLocation() == null){
											thenAssertion = "<ValueSet Path=\"" + definitionThen.getConstraintPath() + "\" ValueSetID=\"" + t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-") + "\" BindingLocation=\"1\" BindingStrength=\"" + thenData.getValueSets().get(0).getBindingStrength() + "\"/>";	
										}else {
											String bindingLocation = thenData.getValueSets().get(0).getBindingLocation().replaceAll(" or ", ":");
											thenAssertion = "<ValueSet Path=\"" + definitionThen.getConstraintPath() + "\" ValueSetID=\"" + t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-") + "\" BindingLocation=\""+ bindingLocation +"\" BindingStrength=\"" + thenData.getValueSets().get(0).getBindingStrength() + "\"/>";	
										}
									}else {
										thenAssertion = "<EXIST>";
										for(ValueSetData vs:thenData.getValueSets()){
											Table t = tablesMap.get(vs.getTableId());
											String bindingLocation = null;
											if(vs.getBindingLocation() == null ) bindingLocation = "1";
											bindingLocation = vs.getBindingLocation().replaceAll(" or ", ":");
											thenAssertion = thenAssertion + "<ValueSet Path=\"" + definitionThen.getConstraintPath() + "\" ValueSetID=\"" + t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-") + "\" BindingLocation=\""+ bindingLocation + "\" BindingStrength=\"" + vs.getBindingStrength() + "\"/>";	
										}
										thenAssertion = thenAssertion + "</EXIST>";
									}
								}
								
								String description = "[CoConstraint-" + (i + 1) + "-" + index + "]" + ifDescription + thenDescription;
//										+ "If the value of " + definitionIF.getConstraintPath() + "(" + definitionIF.getName() + ") is '" 
//										+ ifData.getValueData().getValue() + "', then "
										
								String assertion = "<Assertion><IMPLY>" + ifAssertion + thenAssertion + "</IMPLY></Assertion>";
								ConformanceStatement cs = new ConformanceStatement();
								cs.setId(ObjectId.get().toString());
								cs.setConstraintId(constraintId);
								cs.setDescription(description);
								cs.setAssertion(assertion);

								results.add(cs);
							}
						}
					}
				}
				
			}
		}
		
		return results;
	}

	public Field findFieldByPosition(Integer position) {
		for (Field child : this.fields) {
			if (child.getPosition().equals(position))
				return child;
		}
		return null;
	}

}
