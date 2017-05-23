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
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.MongoException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementChange;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

@Service
public class ProfileServiceImpl implements ProfileService {

	Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

	@Autowired
	private ProfileRepository profileRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Profile save(Profile p) throws ProfileException {
		try {
			return save(p, DateUtils.getCurrentDate());
		} catch (MongoException e) {
			throw new ProfileException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Profile save(Profile p, Date date) throws ProfileException {
		try {
			p.setDateUpdated(date);
			return profileRepository.save(p);
		} catch (MongoException e) {
			throw new ProfileException(e);
		}
	}

	// public Set<Datatype> findPrimitiveDatatypes(Datatypes datatypes) {
	// Set<Datatype> primitives = new HashSet<Datatype>();
	// for (Datatype datatype : datatypes.getChildren()) {
	// findPrimitiveDatatypes(datatype, primitives);
	// }
	// return primitives;
	// }
	//
	// public Set<Datatype> findPrimitiveDatatypes(Datatype datatype,
	// Set<Datatype> result) {
	// if (datatype.getComponents() == null
	// || datatype.getComponents().isEmpty()) {
	// result.add(datatype);
	// } else {
	// for (Component component : datatype.getComponents()) {
	// findPrimitiveDatatypes(component.getDatatype(), result);
	// }
	// }
	// return result;
	// }

	@Override
	@Transactional
	public void delete(String id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(String id) {
		Profile profile = profileRepository.findOne(id);
		return profile;
	}

	// public Profile setDatatypeReferences(Profile profile) {
	// for (Segment s : profile.getSegments().getChildren()) {
	// setDatatypeReferences(s, profile.getDatatypes());
	// }
	// for (Datatype d : profile.getDatatypes().getChildren()) {
	// setDatatypeReferences(d, profile.getDatatypes());
	// }
	// return profile;
	// }
	//
	// private void setDatatypeReferences(Segment segment, Datatypes datatypes)
	// {
	// for (Field f : segment.getFields()) {
	// f.setDatatype(datatypes.find(f.getDatatypeLabel()));
	// }
	// }
	//
	// private void setDatatypeReferences(Datatype datatype, Datatypes
	// datatypes) {
	// if (datatype != null && datatype.getComponents() != null) {
	// for (Component c : datatype.getComponents()) {
	// c.setDatatype(datatypes.find(c.getDatatypeLabel()));
	// }
	// }
	// }

	@Override
	public List<Profile> findAllPreloaded() {
		List<Profile> profiles = profileRepository.findPreloaded();
		return profiles;
	}

	@Override
	public List<Profile> findAllProfiles() {
		List<Profile> profiles = profileRepository.findAll();
		return profiles;
	}

	// private void processChildren(Profile profile) {
	// List<Message> messages = messageService.findByMessagesId(profile
	// .getMessages().getId());
	// profile.getMessages().getChildren().addAll(messages);
	// }

	@Override
	public List<Profile> findByAccountId(Long accountId) {
		List<Profile> profiles = profileRepository.findByAccountId(accountId);
		// if (profiles != null && !profiles.isEmpty()) {
		// for (Profile profile : profiles) {
		// processChildren(profile);
		// }
		// }
		log.debug("User profiles found=" + profiles.size());
		return profiles;
	}

	@Override
	public Profile clone(Profile p) throws CloneNotSupportedException {
		return new ProfileClone().clone(p);
	}

	@Override
	public InputStream diffToPdf(Profile p) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new ProfileDiffImpl()).diffToPdf(base, p);
		} else {
			return new NullInputStream(1L);
		}
	}

	@Override
	public InputStream diffToJson(Profile p) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new ProfileDiffImpl()).diffToJson(base, p);
		} else {
			return new NullInputStream(1L);
		}
	}

	@Override
	public Map<String, List<ElementChange>> delta(Profile p) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new ProfileDiffImpl()).diff(base, p);
		} else {
			throw new IllegalArgumentException("Unknown base profile with id=" + p.getBaseId());
		}
	}

	@Override
	public ElementVerification verifyMessages(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyMessages(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyMessage(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyMessage(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifySegmentRefOrGroup(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifySegmentOrGroup(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifySegments(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifySegments(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifySegment(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifySegment(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyField(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyField(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyDatatypes(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyDatatypes(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyDatatype(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyDatatype(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyComponent(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyComponent(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyValueSetLibrary(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyValueSetLibrary(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyValueSet(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyValueSet(p, base, id, type));
		}
		return null;
	}

	@Override
	public ElementVerification verifyUsage(Profile p, String id, String type, String eltName, String eltValue) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationServiceImpl().verifyUsage(p, base, id, type, eltName, eltValue));
		}
		return null;
	}

	@Override
	public ElementVerification verifyCardinality(Profile p, String id, String type, String eltName, String eltValue) {
		return (new VerificationServiceImpl().verifyCardinality(p, id, type, eltName, eltValue));
	}

	@Override
	public ElementVerification verifyLength(Profile p, String id, String type, String eltName, String eltValue) {
		return (new VerificationServiceImpl().verifyLength(p, id, type, eltName, eltValue));
	}

	public ProfileRepository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public Profile apply(Profile p) throws ProfileException {
		return save(p);
	}

}
