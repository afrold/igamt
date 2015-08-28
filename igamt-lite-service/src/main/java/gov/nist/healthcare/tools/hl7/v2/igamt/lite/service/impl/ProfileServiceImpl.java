/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementChange;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import nu.xom.Builder;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mongodb.MongoException;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Profile save(Profile p) throws ProfileException {
		try {
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
			throw new IllegalArgumentException("Unknown base profile with id="
					+ p.getBaseId());
		}
	}

	public ElementVerification verifyMessages(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyMessages(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyMessage(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyMessage(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifySegmentRefOrGroup(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifySegmentOrGroup(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifySegments(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifySegments(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifySegment(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifySegment(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyField(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyField(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyDatatypes(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyDatatypes(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyDatatype(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyDatatype(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyComponent(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyComponent(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyValueSetLibrary(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyValueSetLibrary(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyValueSet(Profile p, String id, String type) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyValueSet(p, base, id, type));		
		}
		return null;
	}

	public ElementVerification verifyUsage(Profile p, String id, String type, String eltName, String eltValue) {
		Profile base = this.findOne(p.getBaseId());
		if (base != null) {
			return (new VerificationService().verifyUsage(p, base, id, type, eltName, eltValue));		
		}
		return null;
	}

	public ElementVerification verifyCardinality(Profile p, String id, String type, String eltName, String eltValue) {
		return (new VerificationService().verifyCardinality(p, id, type, eltName, eltValue));		
	}

	public ElementVerification verifyLength(Profile p, String id, String type, String eltName, String eltValue) {
		return (new VerificationService().verifyLength(p, id, type, eltName, eltValue));		
	}


	public ProfileRepository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public Profile apply(Profile p) throws ProfileSaveException {
		// List<ProfilePropertySaveError> errors = new ProfileChangeService()
		// .apply(newProfile, oldProfile, newValues);
		// if (errors != null && !errors.isEmpty()) {
		// throw new ProfileSaveException(errors);
		// } else {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		p.getMetaData().setDate(
				dateFormat.format(Calendar.getInstance().getTime()));
		profileRepository.save(p);
		// }
		return p;
	}

}
