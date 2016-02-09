package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AppInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.AppInfoRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.AppInfoService;

@Service(value="appInfoService")
public class AppInfoServiceImpl implements AppInfoService{
	
	@Autowired
	private AppInfoRepository appInfoRepository;
	
	@Override
	public AppInfo get() {
		List<AppInfo> infos = appInfoRepository.findAll();
		if (infos != null && !infos.isEmpty()) {
			AppInfo appInfo = infos.get(0);
			return appInfo;
		}
		return null;
	}

}
