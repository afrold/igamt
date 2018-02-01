package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vividsolutions.jts.triangulate.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaElement;


@Service
public interface CompareService {
	
	public DeltaElement  compareDatatype(Datatype d1, Datatype d2 );

	public DeltaElement  compareSegment( Segment s1,  Segment s2 );

	public List<DeltaElement>  compareMessage( Message m1,  Message m2 );
	
	public List<DeltaElement>  compareField( Field f1,  Field f2 , int location);


	DeltaElement compareComponent(Component f1, Component f2, int location);


}
