/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo;

import java.util.Iterator;

import org.hibernate.mapping.Set;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypesRepository;

@Service
public class DatatypesService {

	@Autowired
	private DatatypesRepository datatypesRepository;

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private ComponentService componentService;

	/**
	 * 
	 * @param p
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Datatypes save(Datatypes ds) {
		datatypesRepository.saveAndFlush(ds);
		java.util.Set<Datatype> datatypes = ds.getDatatypes();
		if (datatypes != null) {
			System.out.println(datatypes.size());
			Iterator<Datatype> it = datatypes.iterator(); 
//			while (it.hasNext()) {
//				Datatype d = it.next();
// 				datatypeService.save(d);
//				//System.out.println("Datatype."+ d.getId() + "-" + d.getName());
//			}
			
			it = datatypes.iterator();
			while (it.hasNext()) {
				Datatype d = it.next();
				java.util.Set<Component> components = d.getComponents();
				if (components != null && !components.isEmpty()) {
					Iterator<Component> cIt = components.iterator();
					while (cIt.hasNext()) {
						Component c = cIt.next();
 						componentService.save(c);
						System.out.println("Comp." + c.getId() + "-" +   c.getName());
					}
				}
			}

		}
		return ds;
	}

	/**
	 * 
	 * @param id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Long id) {
		datatypesRepository.delete(id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Datatypes findOne(Long id) {
		return datatypesRepository.findOne(id);
	}

}