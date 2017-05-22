package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class DTMConstraints {

  private List<DTMComponentDefinition> dtmComponentDefinitions = new ArrayList<DTMComponentDefinition>();

  public List<DTMComponentDefinition> getDtmComponentDefinitions() {
    return dtmComponentDefinitions;
  }

  public void setDtmComponentDefinitions(List<DTMComponentDefinition> dtmComponentDefinitions) {
    this.dtmComponentDefinitions = dtmComponentDefinitions;
  }
  
  public DTMConstraints generateDefaultDTMConstraints(){
    DTMConstraints dtmConstraints = new DTMConstraints();
    DTMComponentDefinition year = new DTMComponentDefinition();
    year.setName("YYYY");
    year.setDescription("Year");
    year.setPosition(1);
    year.setUsage(Usage.R);
    dtmConstraints.getDtmComponentDefinitions().add(year);

    DTMComponentDefinition month = new DTMComponentDefinition();
    month.setName("MM");
    month.setDescription("Month");
    month.setPosition(2);
    month.setUsage(Usage.O);
    dtmConstraints.getDtmComponentDefinitions().add(month);

    DTMComponentDefinition day = new DTMComponentDefinition();
    day.setName("DD");
    day.setDescription("Day");
    day.setPosition(3);
    day.setUsage(Usage.C);
    DTMPredicate dayPredicate = new DTMPredicate();
    dayPredicate.setTrueUsage(Usage.O);
    dayPredicate.setFalseUsage(Usage.X);
    dayPredicate.setTarget(month);
    dayPredicate.setVerb("is valued");
    day.setDtmPredicate(dayPredicate);
    dtmConstraints.getDtmComponentDefinitions().add(day);

    DTMComponentDefinition hour = new DTMComponentDefinition();
    hour.setName("HH");
    hour.setDescription("Hour");
    hour.setPosition(4);
    hour.setUsage(Usage.C);
    DTMPredicate hourPredicate = new DTMPredicate();
    hourPredicate.setTrueUsage(Usage.O);
    hourPredicate.setFalseUsage(Usage.X);
    hourPredicate.setTarget(day);
    hourPredicate.setVerb("is valued");
    hour.setDtmPredicate(hourPredicate);
    dtmConstraints.getDtmComponentDefinitions().add(hour);

    DTMComponentDefinition minute = new DTMComponentDefinition();
    minute.setName("MM");
    minute.setDescription("Minute");
    minute.setPosition(5);
    minute.setUsage(Usage.C);
    DTMPredicate minutePredicate = new DTMPredicate();
    minutePredicate.setTrueUsage(Usage.O);
    minutePredicate.setFalseUsage(Usage.X);
    minutePredicate.setTarget(hour);
    minutePredicate.setVerb("is valued");
    minute.setDtmPredicate(minutePredicate);
    dtmConstraints.getDtmComponentDefinitions().add(minute);

    DTMComponentDefinition second = new DTMComponentDefinition();
    second.setName("SS");
    second.setDescription("Second");
    second.setPosition(6);
    second.setUsage(Usage.C);
    DTMPredicate secondPredicate = new DTMPredicate();
    secondPredicate.setTrueUsage(Usage.O);
    secondPredicate.setFalseUsage(Usage.X);
    secondPredicate.setTarget(minute);
    secondPredicate.setVerb("is valued");
    second.setDtmPredicate(secondPredicate);
    dtmConstraints.getDtmComponentDefinitions().add(second);

    DTMComponentDefinition miliSecond1 = new DTMComponentDefinition();
    miliSecond1.setName("s");
    miliSecond1.setDescription("1/10 second");
    miliSecond1.setPosition(7);
    miliSecond1.setUsage(Usage.C);
    DTMPredicate miliSecondPredicate1 = new DTMPredicate();
    miliSecondPredicate1.setTrueUsage(Usage.O);
    miliSecondPredicate1.setFalseUsage(Usage.X);
    miliSecondPredicate1.setTarget(second);
    miliSecondPredicate1.setVerb("is valued");
    miliSecond1.setDtmPredicate(miliSecondPredicate1);
    dtmConstraints.getDtmComponentDefinitions().add(miliSecond1);
    
    DTMComponentDefinition miliSecond2 = new DTMComponentDefinition();
    miliSecond2.setName("s");
    miliSecond2.setDescription("1/100 second");
    miliSecond2.setPosition(8);
    miliSecond2.setUsage(Usage.C);
    DTMPredicate miliSecondPredicate2 = new DTMPredicate();
    miliSecondPredicate2.setTrueUsage(Usage.O);
    miliSecondPredicate2.setFalseUsage(Usage.X);
    miliSecondPredicate2.setTarget(miliSecond1);
    miliSecondPredicate2.setVerb("is valued");
    miliSecond2.setDtmPredicate(miliSecondPredicate2);
    dtmConstraints.getDtmComponentDefinitions().add(miliSecond2);
    
    DTMComponentDefinition miliSecond3 = new DTMComponentDefinition();
    miliSecond3.setName("s");
    miliSecond3.setDescription("1/1000 second");
    miliSecond3.setPosition(9);
    miliSecond3.setUsage(Usage.C);
    DTMPredicate miliSecondPredicate3 = new DTMPredicate();
    miliSecondPredicate3.setTrueUsage(Usage.O);
    miliSecondPredicate3.setFalseUsage(Usage.X);
    miliSecondPredicate3.setTarget(miliSecond2);
    miliSecondPredicate3.setVerb("is valued");
    miliSecond3.setDtmPredicate(miliSecondPredicate3);
    dtmConstraints.getDtmComponentDefinitions().add(miliSecond3);
    
    DTMComponentDefinition miliSecond4 = new DTMComponentDefinition();
    miliSecond4.setName("s");
    miliSecond4.setPosition(10);
    miliSecond4.setDescription("1/10000 second");
    miliSecond4.setUsage(Usage.C);
    DTMPredicate miliSecondPredicate4 = new DTMPredicate();
    miliSecondPredicate4.setTrueUsage(Usage.O);
    miliSecondPredicate4.setFalseUsage(Usage.X);
    miliSecondPredicate4.setTarget(miliSecond3);
    miliSecondPredicate4.setVerb("is valued");
    miliSecond4.setDtmPredicate(miliSecondPredicate4);
    dtmConstraints.getDtmComponentDefinitions().add(miliSecond4);

    DTMComponentDefinition timeZone = new DTMComponentDefinition();
    timeZone.setName("ZZZZ");
    timeZone.setDescription("Time Zone");
    timeZone.setPosition(11);
    timeZone.setUsage(Usage.O);
    dtmConstraints.getDtmComponentDefinitions().add(timeZone);
    return dtmConstraints;
  }
  
}
