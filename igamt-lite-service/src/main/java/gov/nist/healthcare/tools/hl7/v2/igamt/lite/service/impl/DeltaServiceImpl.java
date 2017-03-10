package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DeltaRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DeltaService;

@Service

public class DeltaServiceImpl implements DeltaService {
  Logger log = LoggerFactory.getLogger(DeltaServiceImpl.class);



  @Autowired
  private DeltaRepository deltaRepository;
  @Autowired
  private DatatypeService datatypeService;


  @Override
  public Delta findById(String id) {
    // TODO Auto-generated method stub
    return deltaRepository.findOne(id);
  }

  @Override
  public List<Delta> findAll() {
    // TODO Auto-generated method stub
    return deltaRepository.findAll();
  }

  @Override
  public Delta save(Delta delta) {
    // TODO Auto-generated method stub
    return deltaRepository.save(delta);
  }

  @Override
  public Delta create(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(Delta delta) {
    // TODO Auto-generated method stub
    deltaRepository.delete(delta);
  }

  @Override
  public boolean CompareDatatypes(Datatype d1, Datatype d2) {

    if (!d1.getName().toLowerCase().equals(d2.getName().toLowerCase())) {

      return false;
    } else if (d1.getComponents().size() != d2.getComponents().size()) {

      return false;
    } else if (d1.getComponents().size() == 0) {
      return true;
    } else {
      for (int i = 0; i < d1.getComponents().size(); i++) {

        if (!CompareComponent(d1.getComponents().get(i), d2.getComponents().get(i))) {
          return false;
        }

      }
      return true;
    }
  }


  public boolean CompareComponent(Component c1, Component c2) {
    if (c1.isIdentique(c2)) {


      String name = c1.getDatatype().getName().toLowerCase();
      if (name.equals("st") || name.equals("si") || name.equals("varies") || name.equals("-")
          || name.equals("dt") || name.equals("dtm") || name.equals("id") || name.equals("is")
          || name.equals("tx") || name.equals("fn")) {
        return true;
      } else {


        Datatype d1 = datatypeService.findById(c1.getDatatype().getId());
        Datatype d2 = datatypeService.findById(c2.getDatatype().getId());
        if (d1 != null && d2 != null) {
          return CompareDatatypesOnLevel(d1, d2);
        } else {
          return true;
        }

      }

    } else {
      return false;

    }
  }

  /**
   * @param d1
   * @param d2
   * @return
   */
  private boolean CompareDatatypesOnLevel(Datatype d1, Datatype d2) {
    // TODO Auto-generated method stub
    if (!d1.getName().toLowerCase().equals(d2.getName().toLowerCase())) {

      return false;
    } else if (d1.getComponents().size() != d2.getComponents().size()) {

      return false;
    } else if (d1.getComponents().size() == 0) {
      return true;
    } else {
      for (int i = 0; i < d1.getComponents().size(); i++) {

        if (!d1.getComponents().get(i).isIdentique(d2.getComponents().get(i))) {
          return false;
        }
      }
      return true;
    }
  }
}
