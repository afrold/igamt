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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;

/**
 * @author gcr1 12.Feb.16
 */
@ReadingConverter
public class SegmentLibraryReadConverter extends AbstractReadConverter<DBObject, SegmentLibrary> {
	
	private static final Logger log = LoggerFactory.getLogger(SegmentLibraryReadConverter.class);

	public SegmentLibraryReadConverter() {
		log.info("SegmentLibraryReadConverter Read Converter Created");
	}

	@Override
	public SegmentLibrary convert(DBObject source) {
		SegmentLibrary segLib = new SegmentLibrary();
		return segLib(source, segLib);
	} 
	
	private SegmentLibrary segLib(DBObject source, SegmentLibrary segLib) {
		segLib.setId(readMongoId(source));
		segLib.setType(Constant.SEGMENTS);
		segLib.setAccountId((Long) source.get(ACCOUNT_ID));
		
		segLib.setSectionContents((String) source.get(SECTION_COMMENTS));
		segLib.setSectionDescription((String) source.get(SECTION_DESCRIPTION));
		segLib.setSectionPosition((Integer) source.get(SECTION_POSITION));
		segLib.setSectionTitle((String) source.get(SECTION_TITLE));
		BasicDBList segLibDBObjects = (BasicDBList) source.get(CHILDREN);
		segLib.setChildren(new HashSet<Segment>());
		
		SegmentReadConverter segCnv = new SegmentReadConverter();
		if (segLibDBObjects != null) {
			for (Object childObj : segLibDBObjects) {
				DBObject child = (DBObject) childObj;
				if (segLib.findOneSegmentById(readMongoId(child)) == null) {
					segLib.addSegment(segCnv.convert(child));
				}
			}
		}

		return segLib;
	}
}
