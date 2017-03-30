package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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

    public SerializableProfileComponent(String id, String prefix, String position,
        String headerLevel, String title, ProfileComponent profileComponent) {
        super(id, prefix, position, headerLevel, title);
        this.profileComponent = profileComponent;
    }

    @Override public Element serializeElement() {
        Element profileComponentElement = new Element("ProfileComponent");
        profileComponentElement.addAttribute(new Attribute("ID", this.profileComponent.getId() + ""));
        profileComponentElement.addAttribute(new Attribute("Name", this.profileComponent.getName() + ""));
        if (this.profileComponent.getDescription() != null && !this.profileComponent.getDescription().equals(""))
            profileComponentElement.addAttribute(new Attribute("Description", this.profileComponent.getDescription()));
        if (this.profileComponent.getComment() != null && !this.profileComponent.getComment().isEmpty()) {
            profileComponentElement.addAttribute(new Attribute("Comment", this.profileComponent.getComment()));
        }
        for(SubProfileComponent subProfileComponent : profileComponent.getChildren()){
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
                        subProfileComponentElement.addAttribute(new Attribute("Datatype",subProfileComponentAttributes.getDatatype().getExt()));
                    }
                    if(subProfileComponentAttributes.getTables()!=null && !subProfileComponentAttributes.getTables().isEmpty()){
                        ArrayList<String> valueSets = new ArrayList<>();
                        for(TableLink tableLink : subProfileComponentAttributes.getTables()){
                            valueSets.add(tableLink.getBindingIdentifier());
                        }
                        subProfileComponentElement.addAttribute(new Attribute("ValueSet",
                            StringUtils.join(valueSets,",")));
                    }
                    if(subProfileComponentAttributes.getComment()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("Comment",subProfileComponentAttributes.getComment()));
                    }
                    if(subProfileComponentAttributes.getText()!=null){
                        subProfileComponentElement.addAttribute(new Attribute("DefinitionText",subProfileComponentAttributes.getText()));
                    }
                    profileComponentElement.appendChild(subProfileComponentElement);
                }
            }
        }
        return profileComponentElement;
    }

}
