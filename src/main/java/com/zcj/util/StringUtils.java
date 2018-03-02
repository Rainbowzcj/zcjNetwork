package com.zcj.util;

import java.util.List;

/**
 * 字符串相关工具类
 *
 * @author HuangChendi
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {
	
	/**
	 * 处理逗号分隔的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String dealString(String str) {
		String res = "";
		if (!isEmpty(str)) {
			String[] ids = str.split(",");
			for (String id : ids) {
				id=id.replaceAll(" ", "");
				if (!StringUtils.isEmpty(id))
					res = res.length() == 0 ? id : res + "," + id;
			}
		}
		return res;
	}
	
	/**
	 * 处理逗号分隔的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String dealString(List<?> ids) {
		String res = "";
		for (Object id : ids) {
			if (!StringUtils.isEmpty(id))
				res = res.length() == 0 ? id+"" : res + "," + id;
		}
		return res;
	}
} 

