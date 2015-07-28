package cf.study.web.service;

import java.util.logging.Logger;

import cf.study.utils.MiscUtils;

public class PathParamEntity {
	static Logger log = Logger.getLogger(PathParamEntity.class.getName());
	public PathParamEntity(String strParam) {
		log.info(MiscUtils.invocationInfo() + ":\t" + strParam);
	}
}