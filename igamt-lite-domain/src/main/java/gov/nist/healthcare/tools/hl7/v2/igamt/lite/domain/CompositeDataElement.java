package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class CompositeDataElement extends DataModel implements java.io.Serializable, Cloneable,
Comparable<CompositeDataElement>{
	
	 private static final long serialVersionUID = 1L;
	  protected String name;
	  protected Usage usage;
	  protected Integer minLength;
	  protected String maxLength;
	  protected String confLength;
	  protected List<CompositeTable> tables = new ArrayList<CompositeTable>();
	  protected CompositeDatatype datatype;
	  protected Integer position = 0;
	  protected String comment = "";
	  protected String text = "";
	  protected boolean hide;
	  
	  

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public Usage getUsage() {
		return usage;
	}



	public void setUsage(Usage usage) {
		this.usage = usage;
	}



	public Integer getMinLength() {
		return minLength;
	}



	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}



	public String getMaxLength() {
		return maxLength;
	}



	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}



	public String getConfLength() {
		return confLength;
	}



	public void setConfLength(String confLength) {
		this.confLength = confLength;
	}



	public List<CompositeTable> getTables() {
		return tables;
	}



	public void setTables(List<CompositeTable> tables) {
		this.tables = tables;
	}



	public CompositeDatatype getDatatype() {
		return datatype;
	}



	public void setDatatype(CompositeDatatype datatype) {
		this.datatype = datatype;
	}



	public Integer getPosition() {
		return position;
	}



	public void setPosition(Integer position) {
		this.position = position;
	}



	public String getComment() {
		return comment;
	}



	public void setComment(String comment) {
		this.comment = comment;
	}



	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}



	public boolean isHide() {
		return hide;
	}



	public void setHide(boolean hide) {
		this.hide = hide;
	}



	@Override
	public int compareTo(CompositeDataElement o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
