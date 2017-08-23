package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableCompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeCompositeProfileService;

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
 * Created by Maxence Lefort on 3/9/17.
 */
@Service
public class SerializeCompositeProfileServiceImpl extends SerializeMessageOrCompositeProfile implements SerializeCompositeProfileService {

    @Override
    public SerializableCompositeProfile serializeCompositeProfile(CompositeProfile compositeProfile,
        String prefix, SerializationLayout serializationLayout, String hl7Version,
        ExportConfig exportConfig) {
        List<SerializableSegmentRefOrGroup> serializableSegmentRefOrGroups = new ArrayList<>();
        String type = "ConformanceStatement";
        SerializableConstraints serializableConformanceStatements = super.serializeConstraints(
        		compositeProfile.retrieveAllConformanceStatements(), compositeProfile.getName(),
            compositeProfile.getPosition(), type);
        type = "ConditionPredicate";
        SerializableConstraints serializablePredicates = super.serializeConstraints(
            compositeProfile.getPredicates(), compositeProfile.getName(),
            compositeProfile.getPosition(), type);
        int segmentSectionPosition = 1;
        String usageNote, defPreText, defPostText;
        usageNote = defPreText = defPostText = "";
        if(compositeProfile.getUsageNote()!=null&&!compositeProfile.getUsageNote().isEmpty()){
            usageNote = serializationUtil.cleanRichtext(compositeProfile.getUsageNote());
            segmentSectionPosition++;
        }
        if(compositeProfile.getDefPreText()!=null&&!compositeProfile.getDefPreText().isEmpty()){
            defPreText = serializationUtil.cleanRichtext(compositeProfile.getDefPreText());
            segmentSectionPosition++;
        }
        if(compositeProfile.getDefPostText()!=null&&!compositeProfile.getDefPostText().isEmpty()){
            defPostText = serializationUtil.cleanRichtext(compositeProfile.getDefPostText());
        }
        Boolean showConfLength = serializationUtil.isShowConfLength(hl7Version);
        List<Table> tables = new ArrayList<>();
        for(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding : compositeProfile.getValueSetBindings()){
            if(valueSetOrSingleCodeBinding.getTableId()!=null && !valueSetOrSingleCodeBinding.getTableId().isEmpty()){
                Table table = findTableInProfile(valueSetOrSingleCodeBinding,compositeProfile);
                if(table!=null){
                    tables.add(table);
                }
            }
        }
        String title = generateTitle(compositeProfile);

        SerializableCompositeProfile serializableCompositeProfile = new SerializableCompositeProfile(compositeProfile,prefix,title,serializableSegmentRefOrGroups,serializableConformanceStatements,serializablePredicates,usageNote,defPreText,defPostText,tables,showConfLength);
        SerializableSection compositeProfileSegments = new SerializableSection(compositeProfile.getIdentifier()+"_segments",prefix+"."+String.valueOf(compositeProfile.getPosition())+"."+segmentSectionPosition,"1","4","Segment definitions");
        this.messageSegmentsNameList = new ArrayList<>();
        this.segmentPosition = 1;
        UsageConfig fieldsUsageConfig = exportConfig.getFieldsExport();
        UsageConfig segmentUsageConfig = exportConfig.getSegmentsExport();
        UsageConfig segmentOrGroupUsageConfig = exportConfig.getSegmentORGroupsCompositeProfileExport();
        for(SegmentRefOrGroup segmentRefOrGroup : compositeProfile.getChildren()){
            SerializableSegmentRefOrGroup serializableSegmentRefOrGroup = serializeSegmentRefOrGroup(segmentRefOrGroup,segmentOrGroupUsageConfig,fieldsUsageConfig, compositeProfile.getSegmentsMap());
            serializableSegmentRefOrGroups.add(serializableSegmentRefOrGroup);
            if(serializationLayout.equals(SerializationLayout.PROFILE)){
                serializeSegment(segmentRefOrGroup,
                    compositeProfileSegments.getPrefix() + ".", compositeProfileSegments, segmentUsageConfig, fieldsUsageConfig, exportConfig.isDuplicateOBXDataTypeWhenFlavorNull());
            }
        }
        if(!compositeProfileSegments.getSerializableSectionList().isEmpty()){
            serializableCompositeProfile.addSection(compositeProfileSegments);
        }
        return serializableCompositeProfile;
    }

    private String generateTitle(CompositeProfile compositeProfile) {
        String title = "";
        if(compositeProfile.getName() != null){
            title = compositeProfile.getName();
        } else {
            title = compositeProfile.getMessageType() + "^" + compositeProfile.getEvent() + "^" + compositeProfile.getStructID();
        }
        if(compositeProfile.getIdentifier() != null && !compositeProfile.getIdentifier().isEmpty()){
            title += " - " + compositeProfile.getIdentifier();
        }
        if(compositeProfile.getDescription() != null && !compositeProfile.getDescription().isEmpty()){
            title += " - " + compositeProfile.getDescription();
        }
        return title;
    }

    private Table findTableInProfile(ValueSetOrSingleCodeBinding valueSetOrSingleCodeBinding,
        CompositeProfile compositeProfile) {
    	if(compositeProfile!=null && compositeProfile.getTablesMap() != null && !compositeProfile.getTablesMap().isEmpty()){
	        for(String currentId : compositeProfile.getTablesMap().keySet()){
	            if(currentId.equals(valueSetOrSingleCodeBinding.getId())){
	                return compositeProfile.getTablesMap().get(currentId);
	            }
	        }
    	}
        return null;
    }
}
