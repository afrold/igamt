package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentAttributes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;

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
@Service
public class SerializeProfileComponentServiceImpl implements SerializeProfileComponentService {

    @Autowired ProfileComponentService profileComponentService;
    @Autowired SerializationUtil serializationUtil;
    @Autowired TableService tableService;

    @Override public SerializableSection serializeProfileComponent(
        ProfileComponentLink profileComponentLink, Integer position) {
        if(profileComponentLink!=null){
            ProfileComponent profileComponent = profileComponentService.findById(profileComponentLink.getId());
            return serializeProfileComponent(profileComponent,position,String.valueOf(3), false, null);
        }
        return null;
    }

	private SerializableSection serializeProfileComponent(ProfileComponent profileComponent, Integer position, String sectionHeaderLevel, Boolean showInnerLinks, String host) {
		if(profileComponent!=null){
            String id = profileComponent.getId();
            String segmentPosition = String.valueOf(position);
            String title = profileComponent.getName();
            SerializableSection serializableSection = new SerializableSection(id,profileComponent.getName(),segmentPosition,sectionHeaderLevel,title);
            Map<SubProfileComponentAttributes,String> definitionTexts = new HashMap<>();
            Map<String,Table> tableidTableMap = new HashMap<>();
            for(SubProfileComponent subProfileComponent : profileComponent.getChildren()){
              if(subProfileComponent.getAttributes()!=null && subProfileComponent.getAttributes().getText()!=null && !subProfileComponent.getAttributes().getText().isEmpty()){
                  String definitionText = serializationUtil.cleanRichtext(subProfileComponent.getAttributes().getText());
                  if(definitionText != null && !definitionText.isEmpty()){
                      definitionTexts.put(subProfileComponent.getAttributes(),definitionText);
                  }
              }
              if(!subProfileComponent.getValueSetBindings().isEmpty()){
                for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : subProfileComponent.getValueSetBindings()){
                  Table table = tableService.findById(valueSetOrSingleCodeBinding.getTableId());
                  if(table!=null){
                    tableidTableMap.put(valueSetOrSingleCodeBinding.getTableId(), table);
                  }
                }
              }
            }
            String defPreText, defPostText;
            defPreText = defPostText = null;
            if(profileComponent.getDefPreText()!=null&&!profileComponent.getDefPreText().isEmpty()){
                defPreText = serializationUtil.cleanRichtext(profileComponent.getDefPreText());
            }
            if(profileComponent.getDefPostText()!=null&&!profileComponent.getDefPostText().isEmpty()){
                defPostText = serializationUtil.cleanRichtext(profileComponent.getDefPostText());
            }
            SerializableProfileComponent serializableProfileComponent = new SerializableProfileComponent(id, profileComponent.getName(),segmentPosition,sectionHeaderLevel,title,profileComponent,definitionTexts, defPreText,defPostText,tableidTableMap, showInnerLinks, host);
            if(serializableProfileComponent != null) {
                serializableSection.addSection(serializableProfileComponent);
                return serializableSection;
            }
        }
		return null;
	}

	@Override
	public SerializableElement serializeProfileComponent(ProfileComponent profileComponent, String host) {
		return serializeProfileComponent(profileComponent,1,String.valueOf(1), true, host);
	}
}
