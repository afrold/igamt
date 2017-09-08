package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoConstraintsTable implements java.io.Serializable, Cloneable {

  /**
   * 
   */
  private static final long serialVersionUID = 5941308834247104133L;
  private CoConstraintColumnDefinition ifColumnDefinition;
  private List<CoConstraintColumnDefinition> thenColumnDefinitionList =
      new ArrayList<CoConstraintColumnDefinition>();
  private List<CoConstraintUserColumnDefinition> userColumnDefinitionList =
      new ArrayList<CoConstraintUserColumnDefinition>();

  private int rowSize = 0;
  private List<CoConstraintIFColumnData> ifColumnData = new ArrayList<CoConstraintIFColumnData>();
  private Map<String, List<CoConstraintTHENColumnData>> thenMapData;
  private Map<String, List<CoConstraintUSERColumnData>> userMapData;

  public CoConstraintsTable() {
    super();
  }

  public int getRowSize() {
    return rowSize;
  }

  public void setRowSize(int rowSize) {
    this.rowSize = rowSize;
  }

  public CoConstraintColumnDefinition getIfColumnDefinition() {
    return ifColumnDefinition;
  }

  public void setIfColumnDefinition(CoConstraintColumnDefinition ifColumnDefinition) {
    this.ifColumnDefinition = ifColumnDefinition;
  }

  public List<CoConstraintColumnDefinition> getThenColumnDefinitionList() {
    return thenColumnDefinitionList;
  }

  public void setThenColumnDefinitionList(
      List<CoConstraintColumnDefinition> thenColumnDefinitionList) {
    this.thenColumnDefinitionList = thenColumnDefinitionList;
  }

  public List<CoConstraintUserColumnDefinition> getUserColumnDefinitionList() {
    return userColumnDefinitionList;
  }

  public void setUserColumnDefinitionList(
      List<CoConstraintUserColumnDefinition> userColumnDefinitionList) {
    this.userColumnDefinitionList = userColumnDefinitionList;
  }

  public List<CoConstraintIFColumnData> getIfColumnData() {
    return ifColumnData;
  }

  public void setIfColumnData(List<CoConstraintIFColumnData> ifColumnData) {
    this.ifColumnData = ifColumnData;
  }

  public Map<String, List<CoConstraintTHENColumnData>> getThenMapData() {
    return thenMapData;
  }

  public void setThenMapData(Map<String, List<CoConstraintTHENColumnData>> thenMapData) {
    this.thenMapData = thenMapData;
  }

  public Map<String, List<CoConstraintUSERColumnData>> getUserMapData() {
    return userMapData;
  }

  public void setUserMapData(Map<String, List<CoConstraintUSERColumnData>> userMapData) {
    this.userMapData = userMapData;
  }

  @Override
  public CoConstraintsTable clone() throws CloneNotSupportedException {
    CoConstraintsTable cloned = new CoConstraintsTable();
    if(ifColumnDefinition != null) cloned.setIfColumnDefinition(ifColumnDefinition.clone());
    cloned.setThenColumnDefinitionList(new ArrayList<CoConstraintColumnDefinition>());
    for(CoConstraintColumnDefinition def : this.thenColumnDefinitionList){
      if(def != null) cloned.getThenColumnDefinitionList().add(def.clone());
    }
    cloned.setUserColumnDefinitionList(new ArrayList<CoConstraintUserColumnDefinition>());
    for(CoConstraintUserColumnDefinition def : this.userColumnDefinitionList){
      if(def != null)  cloned.getUserColumnDefinitionList().add(def.clone());
    }
    cloned.setRowSize(rowSize);
    cloned.setIfColumnData(new ArrayList<CoConstraintIFColumnData>());
    for(CoConstraintIFColumnData data : this.ifColumnData){
      if(data != null) cloned.getIfColumnData().add(data.clone());
    }
    
    if(thenMapData == null) cloned.setThenMapData(null);
    else {
      cloned.setThenMapData(new HashMap<String, List<CoConstraintTHENColumnData>>());
      
      for(String key:this.thenMapData.keySet()){
        List<CoConstraintTHENColumnData> dataList = thenMapData.get(key);
        List<CoConstraintTHENColumnData> clonedList = new ArrayList<CoConstraintTHENColumnData>();
        
        for(CoConstraintTHENColumnData data : dataList){
          if(data != null) clonedList.add(data.clone());
        }
        cloned.getThenMapData().put(key, clonedList);
      }
    }
    
    if(userMapData == null) cloned.setUserMapData(null);
    else {
      cloned.setUserMapData(new HashMap<String, List<CoConstraintUSERColumnData>>());
      
      for(String key:this.userMapData.keySet()){
        List<CoConstraintUSERColumnData> dataList = userMapData.get(key);
        List<CoConstraintUSERColumnData> clonedList = new ArrayList<CoConstraintUSERColumnData>();
        
        for(CoConstraintUSERColumnData data : dataList){
          if(data != null) clonedList.add(data.clone());
        }
        cloned.getUserMapData().put(key, clonedList);
      }
    }
    return cloned;
  }

}
