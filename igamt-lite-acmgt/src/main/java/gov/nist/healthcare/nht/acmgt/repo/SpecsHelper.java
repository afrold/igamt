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
package gov.nist.healthcare.nht.acmgt.repo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author fdevaulx
 *
 */
public abstract class SpecsHelper<T> {

	static final Logger logger = LoggerFactory.getLogger(SpecsHelper.class);
	
	public String ARRAY_DELIMITOR = ",";
	public String FILTER_INFO_DELIMITOR = "::";
	
	public abstract Specification<T> getSpecificationFromString(String filterKey, String filterValue);
	
	public Specification<T> getSpecification(String filters){
		List<String> filterList = null;
		try {
			filterList = Arrays.asList(filters.split(ARRAY_DELIMITOR));
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		return this.getSpecification(filterList);
	}
	
	public Specification<T> getSpecification(List<String> filterList){
		if (filterList != null && !filterList.isEmpty()){
			Specification<T> spec = null;

			for (String individualFilter : filterList){
				String filterKey = "";
				String filterValue = "";
				String[] filterInfo = individualFilter.split(FILTER_INFO_DELIMITOR);
				if (filterInfo.length == 2){
					Specification<T> internalSpec = null;
					filterKey = filterInfo[0];
					filterValue = filterInfo[1];

					internalSpec = (Specification<T>) this.getSpecificationFromString(filterKey, filterValue);
					
					if (spec != null) {
						spec = where(spec).and(internalSpec) ;	
					} else {
						spec = internalSpec;
					}

				} else {
					// TODO error
				}
			}
			return spec;
		} else {
			return null;
		}
	}
	
}
