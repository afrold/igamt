/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Aug 29, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Service
public class VauleSetCorrection {
  @Autowired
  TableService tableService;

  private HashMap<String, List<Table>> tables = new HashMap<String, List<Table>>();
  private HashMap<String, HashMap<String, List<Table>>> versionDupLicates =
      new HashMap<String, HashMap<String, List<Table>>>();
  String[] versions =
      {"2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.7.1", "2.8", "2.8.1", "2.8.2"};


  public HashMap<String, List<Table>> organizeByBindingIdentifier(String version) {
    List<SCOPE> scopes = new ArrayList<SCOPE>();
    scopes.add(SCOPE.HL7STANDARD);
    tables = new HashMap<String, List<Table>>();
    List<Table> tablesForVersion = tableService.findByScopesAndVersion(scopes, version);
    for (Table t : tablesForVersion) {
      if (!tables.containsKey(t.getBindingIdentifier())) {
        List<Table> temp = new ArrayList<Table>();
        temp.add(t);
        tables.put(t.getBindingIdentifier(), temp);
      } else {
        tables.get(t.getBindingIdentifier()).add(t);
      }
    }

    Iterator<HashMap.Entry<String, List<Table>>> iter = tables.entrySet().iterator();
    while (iter.hasNext()) {
      HashMap.Entry<String, List<Table>> entry = iter.next();
      if (entry.getValue().size() < 2) {
        iter.remove();
      }
    }
    System.out.println(tables.entrySet().size());
    return tables;
  }

  public HashMap<String, HashMap<String, List<Table>>> groupAllDuplicated() {
    for (String v : versions) {
      if (!organizeByBindingIdentifier(v).entrySet().isEmpty()) {
        HashMap<String, List<Table>> temp = organizeByBindingIdentifier(v);
        System.out.println(temp);


        versionDupLicates.put(v, organizeByBindingIdentifier(v));
      }
    }

    System.out.println(versionDupLicates);
    return versionDupLicates;

  }

}
