package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializeProfileComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override public SerializableProfileComponent serializeProfileComponent(
        ProfileComponentLink profileComponentLink, Integer position) {
        if(profileComponentLink!=null){
            ProfileComponent profileComponent = profileComponentService.findById(profileComponentLink.getId());
            if(profileComponent!=null){
                String id = profileComponent.getId();
                String segmentPosition = String.valueOf(position);
                String sectionHeaderLevel = String.valueOf(3);
                String title = profileComponentLink.getName() + " - " + profileComponent.getDescription();
                return new SerializableProfileComponent(id, profileComponentLink.getName(),segmentPosition,sectionHeaderLevel,title,profileComponent);
            }
        }
        return null;
    }
}
