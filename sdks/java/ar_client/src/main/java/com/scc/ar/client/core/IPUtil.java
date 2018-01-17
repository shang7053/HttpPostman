package com.scc.ar.client.core;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: IPUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2017年4月12日 上午10:06:18
 * 
 */
final class IPUtil {
	private static final Logger logger = LoggerFactory.getLogger(IPUtil.class);

	/**
	 * @Title: getIp
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author shangchengcai@voole.com
	 * @date 2017年4月12日 上午9:51:28
	 * @return
	 */
	public static String getIp() {
		String os = System.getProperty("os.name");
		logger.info("os:" + os);
		String ip = "";
		if (os.toLowerCase().startsWith("win")) {
			ip = getWinIp();
		} else {
			ip = getLinuxIp();
		}
		logger.info("Ip:" + ip);
		return ip;
	}

	/**
	 * @Title: getLinuxIp
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author shangchengcai@voole.com
	 * @date 2017年4月12日 上午9:56:02
	 */
	private static String getLinuxIp() {
		Enumeration allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @Title: getWinIp
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author shangchengcai@voole.com
	 * @date 2017年4月12日 上午9:55:59
	 */
	private static String getWinIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
}
