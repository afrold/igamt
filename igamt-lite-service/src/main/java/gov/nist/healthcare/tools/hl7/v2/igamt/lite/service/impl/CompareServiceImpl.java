package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.triangulate.Segment;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaElement.State;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaNode;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompareService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;

@Service
public class CompareServiceImpl implements CompareService {

	@Autowired
	DatatypeService datatypeService;
	@Override
	public DeltaElement  compareDatatype(Datatype d1, Datatype d2) {
		DeltaElement ret = new DeltaElement();
		int baseCount = d1.getComponents().size();
		int toCompareCount = d2.getComponents().size();
		int max=Integer.max(baseCount, toCompareCount);
		
		if(baseCount==toCompareCount){
			for(int i=0; i< baseCount; i++){
			DeltaElement cmp = compareComponent(d1.getComponents().get(i),d2.getComponents().get(i), i);
			if(cmp.getState().equals(State.CHANGED)){
				ret.setState(State.CHANGED);
			}
			ret.getChildren().add(cmp);
			}
		}else if(baseCount<max){
			for(int j=baseCount; j<max; j++){
				DeltaElement added = new DeltaElement();
				added.setState(State.ADDED);
				added.setName(d2.getComponents().get(j).getName());
				ret.getChildren().add(added);
			}
			ret.setState(State.CHANGED);

		}else{
			for(int k=baseCount; k<max; k++){
				DeltaElement removed = new DeltaElement();
				removed.setState(State.ADDED);
				removed.setName(d1.getComponents().get(k).getName());
				ret.getChildren().add(removed);
			}
			ret.setState(State.CHANGED);

		}
		return ret;
		
		}

	

	@Override
	public DeltaElement compareSegment(Segment s1, Segment s2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeltaElement> compareMessage(Message m1, Message m2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeltaElement> compareField(Field f1, Field f2 ,int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeltaElement compareComponent(Component f1, Component f2, int location) {
		DeltaElement row= new DeltaElement();
		
		
		DeltaNode node = new DeltaNode();
		//name 
		if(!f1.getName().toLowerCase().equals(f2.getName().toLowerCase())){
			Delta name = new Delta(f1.getName(),f2.getName());
			node.setName(name);
		}
		//usage 
		if(!(f1.getUsage().toString().toLowerCase()).equals(f2.getUsage().toString().toLowerCase())){
			Delta usage = new Delta(f1.getUsage().toString(),f2.getUsage().toString());
			node.setUsage(usage);
		}
		// min Length
		if(!f1.getMinLength().toLowerCase().equals(f2.getMinLength().toLowerCase())){
			Delta minLength=new Delta(f1.getMinLength(),f2.getMinLength() );
			node.setMinLength(minLength);
		}
		//max Length
		if(!f1.getMaxLength().toLowerCase().equals(f2.getMaxLength().toLowerCase())){
			Delta maxLength=new Delta(f1.getMaxLength(),f2.getMaxLength());
			node.setMaxLength(maxLength);
		}
		// Conf Length
		if(!f1.getConfLength().toLowerCase().equals(f2.getConfLength().toLowerCase())){
			Delta confLength=new Delta(f1.getConfLength(),f2.getConfLength());
			node.setConfLength(confLength);
		}
		
		if(f1.getDatatype() !=null && f2.getDatatype()!=null){
		Datatype f1_dt= datatypeService.findById(f1.getDatatype().getId());
		Datatype f2_dt= datatypeService.findById(f2.getDatatype().getId());
		String dt1_label;
		String dt2_label;
		if(f1_dt ==null){
			dt1_label= "Not Found"; // Enum to be defined
		}else{
			dt1_label=f1_dt.getLabel();
		}
		if(f2_dt==null){
			dt2_label ="Not Found";
		}else{
			dt2_label=f2_dt.getLabel();

		}
		if(f2_dt !=null &&f1_dt !=null){
			DeltaElement deltaResult = compareDatatype(f1_dt,f2_dt);
			if(deltaResult.getState().equals(State.CHANGED)){
				row= deltaResult;
				
			}
		}
		if(dt2_label!=dt1_label && dt2_label.equals("Not Found")){ // see the delta of dataypes if both are not found to notify the user
			Delta dt =new Delta(dt1_label, dt2_label);
			node.setDatatypeLabel(dt);
		}
		
		}
		
		
		row.setData(node);
		
		

		
		return row;
		
		
		// TODO Auto-generated method stub
	}

}
