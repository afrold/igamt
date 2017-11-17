package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ProfileComponentSerializationException;
import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * Created by Maxence Lefort on 3/30/17.
 */
public class SerializableProfileComponent extends SerializableSection {

    private ProfileComponent profileComponent;
    private Map<SubProfileComponentAttributes,String> definitionTexts;
    private String defPreText, defPostText;
    private Map<String,Table> tableidTableMap;
    private Boolean showInnerLinks;
    private String host;
    private List<SubProfileComponent> subComponentsToBeExported;

    public SerializableProfileComponent(String id, String prefix, String position,
        String headerLevel, String title, ProfileComponent profileComponent, Map<SubProfileComponentAttributes,String> definitionTexts, String defPreText, String defPostText, Map<String,Table> tableidTableMap, Boolean showInnerLinks, String host, List<SubProfileComponent> subComponentsToBeExported) {
        super(id, prefix, position, headerLevel, title);
        this.profileComponent = profileComponent;
        this.definitionTexts = definitionTexts;
        this.defPreText = defPreText;
        this.defPostText = defPostText;
        this.tableidTableMap = tableidTableMap;
        this.showInnerLinks = showInnerLinks;
        this.subComponentsToBeExported = subComponentsToBeExported;
        this.host = host;
    }

    @Override public Element serializeElement() throws ProfileComponentSerializationException {
        Element profileComponentElement = new Element("ProfileComponent");
        profileComponentElement.addAttribute(new Attribute("ID", this.profileComponent.getId() + ""));
        profileComponentElement.addAttribute(new Attribute("Name", this.profileComponent.getName() + ""));
        if (this.profileComponent.getDescription() != null && !this.profileComponent.getDescription().equals(
            "")) {
            profileComponentElement
                .addAttribute(new Attribute("Description", this.profileComponent.getDescription()));
        } else {
            profileComponentElement
                .addAttribute(new Attribute("Description", new String()));
        }
        if ((this.defPreText != null && !this.defPreText.isEmpty()) || (this.defPostText != null && !this.defPostText.isEmpty())) {
            if (this.defPreText != null && !this.defPreText.isEmpty()) {
            	profileComponentElement.appendChild(super.createTextElement("DefPreText",
                    this.defPreText));
            }
            if (this.defPostText != null && !this.defPostText.isEmpty()) {
            	profileComponentElement.appendChild(super.createTextElement("DefPostText",
                    this.defPostText));
            }
        }
        if (this.profileComponent.getComment() != null && !this.profileComponent.getComment().isEmpty()) {
            profileComponentElement.addAttribute(new Attribute("Comment", this.profileComponent.getComment()));
        }
        for(SubProfileComponent subProfileComponent : subComponentsToBeExported){
            if(subProfileComponent!=null && subProfileComponent.getAttributes() != null) {
                SubProfileComponentAttributes subProfileComponentAttributes =
                    subProfileComponent.getAttributes();
                if(subProfileComponentAttributes!=null) {
                    Element subProfileComponentElement = new Element("SubProfileComponent");
                    if (subProfileComponent.getName() != null && !subProfileComponent.getName()
                        .isEmpty()) {
                        subProfileComponentElement
                            .addAttribute(new Attribute("Name", subProfileComponent.getName()));
                    } else if(subProfileComponentAttributes.getRef()!=null && subProfileComponentAttributes.getRef().getLabel() != null){
                        subProfileComponentElement
                            .addAttribute(new Attribute("Name", subProfileComponentAttributes.getRef().getLabel()));
                    }
                    if (subProfileComponent.getFrom() != null && !subProfileComponent.getFrom()
                        .isEmpty()) {
                        subProfileComponentElement
                            .addAttribute(new Attribute("From", subProfileComponent.getFrom()));
                    }
                    if (subProfileComponent.getPath() != null && !subProfileComponent.getPath()
                        .isEmpty()) {
                        subProfileComponentElement
                            .addAttribute(new Attribute("Path", subProfileComponent.getPath()));
                    }
                    if(subProfileComponent.getComments()!=null && !subProfileComponent.getComments().isEmpty()){
                        ArrayList<String> comments = new ArrayList<>();
                        for(Comment comment : subProfileComponent.getComments()){
                            comments.add(comment.getDescription());
                        }
                        if(!comments.isEmpty()){
                            subProfileComponentElement.addAttribute(new Attribute("Comment", StringUtils.join(comments, ", ")));
                        }
                    }
                    if(subProfileComponent.getValueSetBindings()!=null && !subProfileComponent.getValueSetBindings().isEmpty()){
                        ArrayList<String> valueSets = new ArrayList<>();
                        for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : subProfileComponent.getValueSetBindings()){
                            Table table = tableidTableMap.get(valueSetOrSingleCodeBinding.getTableId());  
                            if(valueSetOrSingleCodeBinding instanceof ValueSetBinding){
                                if(table!=null){
                                  String link = this.generateInnerLink(table,host);
                                  if(this.showInnerLinks && !"".equals(link)){
                                    String wrappedLink = this.wrapLink(link,table.getBindingIdentifier());
                                    valueSets.add(wrappedLink);
                                  } else {
                                    valueSets.add(table.getBindingIdentifier());
                                  }
                                }
                            } else if(valueSetOrSingleCodeBinding instanceof SingleCodeBinding){
                                valueSets.add(
                                    ((SingleCodeBinding) valueSetOrSingleCodeBinding).getCode().getCodeSystem());
                            }
                        }
                        if(!valueSets.isEmpty()){
                            subProfileComponentElement.addAttribute(new Attribute("ValueSet", StringUtils.join(valueSets, ", ")));
                        }
                    }
                    if(subProfileComponent.getSingleElementValues()!=null && subProfileComponent.getSingleElementValues().getValue() != null){
                    	subProfileComponentElement.addAttribute(new Attribute("SingleElement",
                            subProfileComponent.getSingleElementValues().getValue()));
                    }

                    if(subProfileComponentAttributes.getUsage()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("Usage",subProfileComponentAttributes.getUsage().value()));
                    }
                    if(subProfileComponentAttributes.getMin()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("Min",String.valueOf(subProfileComponentAttributes.getMin())));
                    }
                    if(subProfileComponentAttributes.getMax()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("Max",String.valueOf(subProfileComponentAttributes.getMax())));
                    }
                    if(subProfileComponentAttributes.getMinLength()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("MinLength",String.valueOf(subProfileComponentAttributes.getMinLength())));
                    }
                    if(subProfileComponentAttributes.getMaxLength()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("MaxLength",String.valueOf(
                            subProfileComponentAttributes.getMaxLength())));
                    }
                    if(subProfileComponentAttributes.getConfLength()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("ConfLength",subProfileComponentAttributes.getConfLength()));
                    }
                    if(subProfileComponentAttributes.getDatatype()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("Datatype",subProfileComponentAttributes.getDatatype().getLabel()));
                        if(this.showInnerLinks){
                          String link = this.generateInnerLink(subProfileComponentAttributes.getDatatype(),host);
                          if(!"".equals(link)){
                            subProfileComponentElement.addAttribute(new Attribute("InnerLink",link));
                          }
                        }
                    }
                    if(definitionTexts!=null && definitionTexts.containsKey(subProfileComponentAttributes)){
                        subProfileComponentElement
                            .addAttribute(new Attribute("DefinitionText", definitionTexts.get(subProfileComponentAttributes)));
                    }
                    profileComponentElement.appendChild(subProfileComponentElement);
                    //TODO Need to revise
//                    if(subProfileComponent.getPredicates()!=null && !subProfileComponent.getPredicates().isEmpty()){
//                    	List<SerializableConstraint> serializableConstraints = new ArrayList<>();
//                    	for(Predicate predicate : subProfileComponent.getPredicates()){
//                    		SerializableConstraint serializableConstraint = new SerializableConstraint(predicate, subProfileComponent.getPath());
//                    		serializableConstraints.add(serializableConstraint);
//                    	}
//                    	if(!serializableConstraints.isEmpty()){
//                    		profileComponentElement.appendChild(new SerializableConstraints(serializableConstraints, this.profileComponent.getId(), "", "Conformance Statements", "").serializeElement());
//                    	}
//                    }
                    	
                }
            }
        }
        return profileComponentElement;
    }

}
