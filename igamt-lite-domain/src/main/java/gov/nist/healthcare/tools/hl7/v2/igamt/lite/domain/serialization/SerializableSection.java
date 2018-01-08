package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception.TableNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class SerializableSection extends SerializableElement {

    private List<SerializableSection> serializableSectionList;

    protected Element sectionElement;

    protected String id,prefix,position,headerLevel,title;

    private Attribute prefixAttribute,headerAttribute,titleAttribute;

    public void addSection(SerializableSection serializableSection){
        this.serializableSectionList.add(serializableSection);
    }

    public SerializableSection(String id,String prefix,String position, String headerLevel, String title) {
        this.id = id;
        this.sectionElement = new Element("Section");
        this.sectionElement.addAttribute(new Attribute("id", id == null ? "" : id));
        this.serializableSectionList = new ArrayList<>();
        this.prefix = prefix;
        this.position = position;
        this.headerLevel = headerLevel;
        this.title = title;
        this.prefixAttribute = new Attribute("prefix", prefix  == null ? "" : prefix);
        this.sectionElement.addAttribute(this.prefixAttribute);
        this.sectionElement.addAttribute(new Attribute("position", position == null ? "" : position));
        this.headerAttribute = new Attribute("h", headerLevel == null ? "" : headerLevel);
        this.sectionElement.addAttribute(headerAttribute);
        this.titleAttribute = new Attribute("title", title == null ? "" : title);
        this.sectionElement.addAttribute(titleAttribute);
    }

    @Override
    public Element serializeElement() throws SerializationException {
        for(SerializableSection serializableSection : serializableSectionList){
            if(serializableSection!=null) {
                sectionElement.appendChild(serializableSection.serializeElement());
            }
        }
        return sectionElement;
    }

    public Element getSectionElement() {
        return sectionElement;
    }

    protected List<Predicate> findPredicate(Integer target, List<Predicate> predicates) {
        List<Predicate> constraints = new ArrayList<>();
        for (Predicate pre : predicates) {
            if (pre.getConstraintTarget().indexOf('[') != -1) {
                if (target == Integer
                    .parseInt(pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))) {
                    constraints.add(pre);
                }
            }
        }
        return constraints;
    }
    
    public void addSectionContent(String content){
        Element sectionContentElement = new Element("SectionContent");
        sectionContentElement.appendChild(content);
        sectionElement.appendChild(sectionContentElement);
    }

    public List<SerializableSection> getSerializableSectionList() {
        return serializableSectionList;
    }

    @Override public String getPrefix() {
        return prefix;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleAttribute = new Attribute("title", title);
        this.sectionElement.addAttribute(titleAttribute);
    }
    
    protected Element createCommentListElement(List<Comment> commentList, String locationPrefix){
    	return createCommentListElement(commentList, locationPrefix, new HashMap<String,String>());
    }

    protected Element createCommentListElement(List<Comment> commentList, String locationPrefix, HashMap<String,String> locationPathMap){
        Element commentListElement = new Element("CommentList");
        if(commentList!=null && !commentList.isEmpty()){
            for(Comment comment : commentList){
                if(comment!=null){
                    Element commentElement = new Element("Comment");
                    if (comment.getLocation() != null) {
                        String location = "";
                        if(locationPathMap.containsKey(comment.getLocation())){
                        	location+=locationPathMap.get(comment.getLocation());
                        } else {
                        	location+=comment.getLocation();
                        }
                        commentElement
                            .addAttribute(new Attribute("Location", location));
                    }
                    if(comment.getAuthorId()!=null){
                        commentElement.addAttribute(new Attribute("AuthorId",String.valueOf(
                            comment.getAuthorId())));
                    }
                    if(comment.getLastUpdatedDate()!=null){
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy HH:mm");
                        commentElement.addAttribute(new Attribute("Date",simpleDateFormat.format(comment.getLastUpdatedDate())));
                    }
                    if(comment.getDescription()!=null){
                    	String escapedComment = StringEscapeUtils.escapeHtml4(comment.getDescription());
                        commentElement.addAttribute(new Attribute("Description",escapedComment));
                    }
                    commentListElement.appendChild(commentElement);
                }
            }
        }
        return commentListElement;
    }

    protected Element createValueSetBindingListElement(List<ValueSetOrSingleCodeBinding> valueSetOrSingleCodeBindings, List<Table> tables,String locationPrefix) throws TableNotFoundException {
        return createValueSetBindingListElement(valueSetOrSingleCodeBindings, tables, locationPrefix,new HashMap<String,String>());
    }
    
    protected Element createValueSetBindingListElement(List<ValueSetOrSingleCodeBinding> valueSetOrSingleCodeBindings, List<Table> tables,String locationPrefix, HashMap<String,String> locationPathMap) throws
        TableNotFoundException {
        Element valueSetBindingListElement = new Element("ValueSetBindingList");
        for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : valueSetOrSingleCodeBindings){
            if(valueSetOrSingleCodeBinding!=null) {
                Element valueSetBindingElement = new Element("ValueSetBinding");
                if(valueSetOrSingleCodeBinding instanceof SingleCodeBinding){
                    valueSetBindingElement.addAttribute(new Attribute("Type","SC"));
                    if(((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode()!=null) {
                        if(((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode()
                            .getValue()!=null) {
                            valueSetBindingElement.addAttribute(new Attribute("CodeValue", ((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode()
                                .getValue()));
                        }
                        if(((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode()
                            .getCodeSystem()!=null) {
                            valueSetBindingElement.addAttribute(new Attribute("CodeSystem", ((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode()
                                .getCodeSystem()));
                        }
                    }
                } else {
	                Table table = findTable(tables,valueSetOrSingleCodeBinding.getTableId());
	                if(table!=null) {
	                    if(table.getBindingIdentifier()!=null){
	                        valueSetBindingElement.addAttribute(new Attribute("BindingIdentifier",table.getBindingIdentifier()));
	                    }
	                    if(table.getName()!=null){
	                        valueSetBindingElement.addAttribute(new Attribute("Name", table.getName()));
	                    }
	                    if (valueSetOrSingleCodeBinding.getLocation() != null) {
	                        String location = "";
	                        if(locationPathMap.containsKey(valueSetOrSingleCodeBinding.getLocation())){
	                        	location = locationPathMap.get(valueSetOrSingleCodeBinding.getLocation());
	                        } else {
		                        if (locationPrefix != null && !locationPrefix.isEmpty()) {
		                            location += locationPrefix + "-";
		                        }
		                        location += valueSetOrSingleCodeBinding.getLocation();
	                        }
	                        valueSetBindingElement.addAttribute(new Attribute("Location", location));
	                        valueSetBindingElement.addAttribute(new Attribute("SortLocation", valueSetOrSingleCodeBinding.getLocation()));
	                    }
	                    if(valueSetOrSingleCodeBinding instanceof ValueSetBinding) {
	                        valueSetBindingElement.addAttribute(new Attribute("Type","VS"));
	                        if (((ValueSetBinding) valueSetOrSingleCodeBinding).getBindingLocation() != null) {
	                            valueSetBindingElement.addAttribute(new Attribute("BindingLocation",
	                                ((ValueSetBinding) valueSetOrSingleCodeBinding).getBindingLocation()));
	                        }
	                        if (((ValueSetBinding) valueSetOrSingleCodeBinding).getBindingStrength() != null) {
	                            valueSetBindingElement.addAttribute(new Attribute("BindingStrength",
	                                ((ValueSetBinding) valueSetOrSingleCodeBinding).getBindingStrength().value()));
	                        }
	                    }
	                    valueSetBindingListElement.appendChild(valueSetBindingElement);
	                } else {
	                    throw new TableNotFoundException(valueSetOrSingleCodeBinding.getTableId());
	                }
                }
            }
        }
        return valueSetBindingListElement;
    }

    protected Table findTable(List<Table> tables, String tableId) throws TableNotFoundException{
        if(tableId!=null && !tableId.isEmpty()) {
            for (Table table : tables) {
                if (table != null && table.getId() != null && table.getId().equals(tableId)) {
                    return table;
                }
            }
        }
        throw new TableNotFoundException(tableId);
    }

    public String findComments(Integer position, List<Comment> comments) {
        List<String> elementComments = new ArrayList<>();
        for(Comment comment : comments){
            if(comment.getLocation().equals(String.valueOf(position))){
                elementComments.add(comment.getDescription());
            }
        }
        return StringUtils.join(elementComments, ", ");
    }
    
}
