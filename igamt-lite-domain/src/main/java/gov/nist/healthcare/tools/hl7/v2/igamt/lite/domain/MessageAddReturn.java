package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageAddReturn {
	
	Set<Segment> segments;
	Set<Datatype> datatypes;
	Set<Table> tables;
    List<Message> msgsToadd = new ArrayList<Message>();

	public List<Message> getMsgsToadd() {
		return msgsToadd;
	}

	public void setMsgsToadd(List<Message> msgsToadd) {
		this.msgsToadd = msgsToadd;
	}

	public MessageAddReturn() {
		 segments =new HashSet<Segment>();
		 datatypes = new HashSet<Datatype>();
		 tables = new HashSet<Table>();
		// TODO Auto-generated constructor stub
	}
	
	public Set<Segment> getSegments() {
		return segments;
	}

	public void setSegments(Set<Segment> segments) {
		this.segments = segments;
	}

	public Set<Datatype> getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Set<Datatype> datatypes) {
		this.datatypes = datatypes;
	}

	public Set<Table> getTables() {
		return tables;
	}

	public void setTables(Set<Table> tables) {
		this.tables = tables;
	}

	public void addSegment(Segment s){
		this.segments.add(s);
	
	}
	public void addDatatype(Datatype d){
		
		this.datatypes.add(d);
	}
	
	public void addTable(Table t){
		
		this.tables.add(t);
	}

	

}
