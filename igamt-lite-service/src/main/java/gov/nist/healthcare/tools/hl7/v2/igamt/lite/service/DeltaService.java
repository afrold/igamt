package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;

public interface DeltaService {


  List<Delta> findAll();

  Delta create(String name);

  void delete(Delta delta);

  Delta findById(String id);

  Delta save(Delta delta);

  boolean CompareDatatypes(Datatype d1, Datatype d2);
}
