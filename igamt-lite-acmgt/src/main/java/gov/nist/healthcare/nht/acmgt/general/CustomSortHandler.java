/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.general;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

/**
 * @author fdevaulx
 * 
 */
public class CustomSortHandler {

	static final Logger logger = LoggerFactory
			.getLogger(CustomSortHandler.class);

	String ARRAY_DELIMITOR = ",";
	String SORT_INFO_DELIMITOR = "::";
	List<String> sorts = null;

	public CustomSortHandler(List<String> sorts) {
		this.sorts = sorts;
	}

	public CustomSortHandler(String sorts) {
		try {
			this.sorts = sorts == null ? null : Arrays.asList(sorts
					.split(ARRAY_DELIMITOR));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Sort getSort() {
		if (sorts != null && !sorts.isEmpty()) {
			List<Sort.Order> ords = new LinkedList<Sort.Order>();
			for (String individualSort : sorts) {
				String sortField = "";
				String sortDirection = "";
				String[] sortInfo = individualSort.split(SORT_INFO_DELIMITOR);
				if (sortInfo.length == 2) {
					sortField = sortInfo[0];
					sortDirection = sortInfo[1];
					ords.add(new Sort.Order(Sort.Direction
							.fromString(sortDirection), sortField));
				} else {
					// TODO error
				}
			}
			return new Sort(ords);
		} else {
			return null;
		}
	}

}
