package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vividsolutions.jts.triangulate.Segment;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompareService;
@Service
public class CompareServiceImpl implements CompareService {

	@Override
	public List<DeltaElement> compareDatatype(Datatype d1, Datatype d2) {
		List<DeltaElement> ret = new ArrayList<DeltaElement>();
		
		
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DeltaElement> compareSegment(Segment s1, Segment s2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeltaElement> compareMessage(Message m1, Message m2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeltaElement> compareField(Field f1, Field f2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeltaElement> compareComponent(Component f1, Component f2) {
		// TODO Auto-generated method stub
		return null;
	}

}
