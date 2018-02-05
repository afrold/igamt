package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
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
	public DeltaElement  compareDatatype(Datatype d1, Datatype d2, String path) {
		DeltaElement ret = new DeltaElement();
		ret.setName(new Delta(d1.getLabel(), d2.getLabel()));
		int baseCount = d1.getComponents().size();
		int toCompareCount = d2.getComponents().size();
		int max=Integer.max(baseCount, toCompareCount);
		
		if(baseCount==toCompareCount){
			for(int i=0; i< baseCount; i++){
			DeltaElement cmp = compareComponent(d1,d2,i,  path);
			

			if(cmp.getState().equals(State.CHANGED)){
				ret.setState(State.CHANGED);
			}
			cmp.setPath(ret.getPath()+"."+i);
			ret.getChildren().add(cmp);
		
			}
		}else if(baseCount<max){
			for(int j=baseCount; j<max; j++){
				DeltaElement added = new DeltaElement();
				added.setState(State.ADDED);
				added.setName(new Delta("",d2.getComponents().get(j).getName()));
				added.setPath(ret.getPath()+"."+j);

				ret.getChildren().add(added);
			}
			ret.setState(State.CHANGED);

		}else{
			for(int k=baseCount; k<max; k++){
				DeltaElement removed = new DeltaElement();
				removed.setState(State.ADDED);
				removed.setName(new Delta(d1.getComponents().get(k).getName(), ""));
				removed.setPath(ret.getPath()+"."+k);
				ret.getChildren().add(removed);

			}
			ret.setState(State.CHANGED);

		}
		
		distributePredicate(d1,d2,ret);
		
		
		
		return ret;
		
    }

	

	private void distributePredicate(Datatype d1, Datatype d2, DeltaElement ret) {
		
		if(d1.getPredicates() != null &&!d1.getPredicates().isEmpty()){
			for(Predicate p : d1.getPredicates()){
				String inD2= findPredicateByTarget(d2,p.getConstraintTarget());
				
				if(inD2!=null&& !inD2.equals(p.getDescription())){
					Delta predicate = new Delta(p.getDescription(),inD2);
					putInTree(ret, predicate,getPathList(p.getConstraintTarget()));
					
				}else{
					Delta predicate = new Delta(p.getDescription(),null);
					putInTree(ret, predicate,getPathList(p.getConstraintTarget()));
				}
				}
			
			for(Predicate p : d2.getPredicates()){
				String inD1= findPredicateByTarget(d1,p.getConstraintTarget());
				
				if(inD1!=null&& !inD1.equals(p.getDescription())){
					Delta predicate = new Delta(p.getDescription(),inD1);
					putInTree(ret, predicate,getPathList(p.getConstraintTarget()));
					
				}
				}	
				
			}
		}
	
	private void putInTree(DeltaElement ret, Delta predicate,  String pathList) {
		if(pathList.length()==1){
			int index= Integer.valueOf(pathList);
			if(index<=ret.getChildren().size()&&index>=1){
				ret.getChildren().get(index-1).getData().setPredicate(predicate);
			}
		}else{
		int firstindex = pathList.indexOf(".");
		if(firstindex >-1){
			Integer first = Integer.valueOf(pathList.substring(0,firstindex ));
			
			if(first<=ret.getChildren().size()&&first>=1){
				DeltaElement child = ret.getChildren().get(first -1);
				putInTree(child, predicate,pathList.substring(first));
			}
		 }
		}
		// TODO Auto-generated method stub
		
	}



	private String findPredicateByTarget(Datatype d2, String constraintTarget) {
		String ret = null;
		for(Predicate p : d2.getPredicates()){
		
			if(p.getConstraintTarget().equals(constraintTarget)){
				return p.getDescription();
			}
			}	
		return ret ;
	}



		// TODO Auto-generated method stub
		



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
	public DeltaElement compareComponent(Datatype d1, Datatype d2,int i, String path) {
		DeltaElement row= new DeltaElement();
		
		
		Component f1= d1.getComponents().get(i);
		Component f2= d2.getComponents().get(i);
		
		row.setName(new Delta(f1.getName(),f1.getName()));

		
		DeltaNode node = new DeltaNode();
		//name 
			Delta name = new Delta(f1.getName(),f2.getName());
			node.setName(name);
		//usage 
		if(!(f1.getUsage()).equals(f2.getUsage())){
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
		if(f2_dt !=null||f1_dt !=null){
			String newPath=  path+"."+i+1;
			DeltaElement deltaResult = compareDatatype(f1_dt,f2_dt, newPath);
			
				row= deltaResult;
				row.setName(name);
				
		}
		
		
		if(dt2_label==dt1_label){ // see the delta of dataypes if both are not found to notify the user
			Delta dt =new Delta(dt1_label, dt2_label);
			node.setDatatypeLabel(dt);
		}
		
		}
		
		
		
		
		row.setData(node);
		
		

		
		return row;
		
		
		// TODO Auto-generated method stub
	}
	public String getPathList(String path){
		  String reg = "\\[([a-z]||[0-9]||[A-Z])*\\]";
		  String refined = path.replaceAll(reg, "");
//		  String[] pathTable= refined.split("\\.");
//		  List<String> listPath=Arrays.asList(pathTable);
		  
		  return refined;
	}
	
//	public void distributePredicate(DeltaElement delta,Datatype d1, Datatype d2, List<String> path){
//		if(delta.getChildren()!=null&&delta.getChildren().isEmpty()){
//			for(int i=0; i<delta.getChildren().size(); i++){
//				path.add(i+1+"");
//				String p1= findPredicateInPath(d1.getPredicates(),path);
//				String p2= findPredicateInPath(d2.getPredicates(),path);
//				if(p1 !=null ||p2 !=null){
//					Delta predicate = new Delta(p1, p2); 
//					delta.getChildren().get(i).getData().setPredicate(predicate);					
//				}
//			}
//		}
//		
//	}







	
}
