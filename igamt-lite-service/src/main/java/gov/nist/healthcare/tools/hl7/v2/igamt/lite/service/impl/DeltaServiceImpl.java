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
    if (d1.getName().equals("AD") && d1.getHl7Version().equals("2.5.1") && d2.getName().equals("AD")
        && d2.getHl7Version().equals("2.6")) {
      System.out.println("Comparing UI");
    }
    if (!d1.getName().equals(d2.getName())) {

      return false;

    } else if (d1.getComponents().size() != d2.getComponents().size()) {
      return false;
    } else {
      for (int i = 0; i < d1.getComponents().size(); i++) {

        if (!CompareComponent(d1.getComponents().get(i), d2.getComponents().get(i))) {
          System.out.println(d1.getComponents().get(i));
          System.out.println(d2.getComponents().get(i));
          return false;
        }

      }
      return true;
    }
  }


  public boolean CompareComponent(Component c1, Component c2) {
    if (!c1.getUsage().equals(c2.getUsage())) {
      System.out.println("Comparing UI");

      return false;
    } else if (!c1.getMaxLength().equals(c2.getMaxLength())) {
      return false;
    } else if (!c1.getMinLength().equals(c2.getMinLength())) {
      return false;
    }
    if (c1.getDatatype() != null && c2.getDatatype() != null) {
      Datatype d1 = datatypeService.findById(c1.getDatatype().getId());
      Datatype d2 = datatypeService.findById(c2.getDatatype().getId());
      return CompareDatatypes(d1, d2);
      // return (c1.getDatatype().getName().toLowerCase().trim() == c2.getDatatype().getName()
      // .toLowerCase().trim());
    }
    return true;

  }

}
