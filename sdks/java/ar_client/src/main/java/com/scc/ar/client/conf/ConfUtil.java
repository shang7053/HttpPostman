package com.scc.ar.client.conf;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SchedulerUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2018年1月12日 下午5:39:24
 * 
 */
public class ConfUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfUtil.class);
	private static final Properties P = new Properties();
	private static ConfUtil confUtil;
	static {
		try {
			P.load(ConfUtil.class.getClassLoader().getResourceAsStream("arconf.properties"));
			confUtil = new ConfUtil();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author shangchengcai@voole.com
	 * @date 2018年1月12日 下午6:07:52
	 */
	private ConfUtil() {
	}

	public static ConfUtil instance() {
		return confUtil;
	}

	public String getConf(String pname) {
		return P.getProperty(pname);
	}

	public static void main(String[] args) {
		System.out.println(ConfUtil.instance().getConf("sdk.kafka.address"));
	}
}
