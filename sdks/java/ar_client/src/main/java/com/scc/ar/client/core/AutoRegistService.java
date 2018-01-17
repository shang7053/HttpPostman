package com.scc.ar.client.core;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scc.ar.client.conf.ConfUtil;
import com.scc.ar.client.zk.ZkUtil;

/**
 * @ClassName: AutoRegistService
 * @DescrserviceIption: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2018年1月12日 下午6:19:00
 * 
 */
public class AutoRegistService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoRegistService.class);
	private String zkAddress;
	private String zkBasePath;
	private String serviceDomain;
	private String serviceRegisterInterval;
	private String serviceIp;
	private String servicePort;
	private String serviceWeight;

	public void init() {
		LOGGER.info("-------------------------init AutoRegistService start-------------------------");
		try {
			LOGGER.info(
					"befor get from conf file,zkAddress={},zkBasePath={},serviceDomain={},serviceRegisterInterval={},serviceIp={},servicePort={},serviceWeight={}",
					new Object[] { this.zkAddress, this.zkBasePath, this.serviceDomain, this.serviceRegisterInterval,
							this.serviceIp, this.servicePort, this.serviceWeight });
			if (StringUtils.isBlank(this.zkAddress)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("zk.address"))) {
				this.serviceDomain = ConfUtil.instance().getConf("zk.address");
			} else if (StringUtils.isBlank(this.zkAddress)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("zk.address"))) {
				this.zkAddress = "127.0.0.1:2181";
			}
			if (StringUtils.isBlank(this.zkBasePath)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("zk.basePath"))) {
				this.serviceDomain = ConfUtil.instance().getConf("zk.basePath");
			} else if (StringUtils.isBlank(this.zkBasePath)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("zk.basePath"))) {
				this.zkBasePath = "/ar/domains";
			}
			if (StringUtils.isBlank(this.serviceDomain)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("service.domain"))) {
				this.serviceDomain = ConfUtil.instance().getConf("service.domain");
			} else if (StringUtils.isBlank(this.serviceDomain)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("service.domain"))) {
				this.serviceDomain = "localhost";
			}
			if (StringUtils.isBlank(this.serviceRegisterInterval)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("service.register.interval"))) {
				this.serviceRegisterInterval = ConfUtil.instance().getConf("service.register.interval");
			} else if (StringUtils.isBlank(this.serviceRegisterInterval)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("service.register.interval"))) {
				this.serviceRegisterInterval = "1800";
			}
			if (StringUtils.isBlank(this.serviceIp)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("service.ip"))) {
				this.serviceIp = ConfUtil.instance().getConf("service.ip");
			} else if (StringUtils.isBlank(this.serviceIp)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("service.ip"))) {
				this.serviceIp = IPUtil.getIp();
			}
			if (StringUtils.isBlank(this.servicePort)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("service.port"))) {
				this.servicePort = ConfUtil.instance().getConf("service.port");
			} else if (StringUtils.isBlank(this.servicePort)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("service.port"))) {
				this.servicePort = "8080";
			}
			if (StringUtils.isBlank(this.serviceWeight)
					&& StringUtils.isNotBlank(ConfUtil.instance().getConf("service.weight"))) {
				this.serviceWeight = ConfUtil.instance().getConf("service.weight");
			} else if (StringUtils.isBlank(this.serviceWeight)
					&& StringUtils.isBlank(ConfUtil.instance().getConf("service.weight"))) {
				this.serviceWeight = "1";
			}
			if (this.zkBasePath.lastIndexOf("/") != this.zkBasePath.length()) {
				this.zkBasePath = this.zkBasePath + "/";
			}
			LOGGER.info(
					"after get from conf file,zkAddress={},zkBasePath={},serviceDomain={},serviceRegisterInterval={},serviceIp={},servicePort={},serviceWeight={}",
					new Object[] { this.zkAddress, this.zkBasePath, this.serviceDomain, this.serviceRegisterInterval,
							this.serviceIp, this.servicePort, this.serviceWeight });
			ZkUtil.instance().connect(this.zkAddress).createNode(this.zkBasePath, this.serviceDomain,
					this.serviceIp + ":" + this.servicePort, this.serviceWeight);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}
		LOGGER.info("-------------------------init AutoRegistService end-------------------------");
	}

	public void close() {
		// RedisUtil.instance().close();
		// SchedulerUtil.instance().close();
		ZkUtil.instance().close();
	}

	/**
	 * @return the zkAddress
	 * @author shangchengcai@voole.com
	 * @date 2018年1月16日 下午3:55:29
	 */
	public String getZkAddress() {
		return this.zkAddress;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月16日 下午3:55:29
	 * @param zkAddress the zkAddress to set
	 */
	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	/**
	 * @return the serviceDomain
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 */
	public String getServiceDomain() {
		return this.serviceDomain;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 * @param serviceDomain the serviceDomain to set
	 */
	public void setServiceDomain(String serviceDomain) {
		this.serviceDomain = serviceDomain;
	}

	/**
	 * @return the serviceRegisterInterval
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 */
	public String getServiceRegisterInterval() {
		return this.serviceRegisterInterval;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 * @param serviceRegisterInterval the serviceRegisterInterval to set
	 */
	public void setServiceRegisterInterval(String serviceRegisterInterval) {
		this.serviceRegisterInterval = serviceRegisterInterval;
	}

	/**
	 * @return the serviceIp
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 */
	public String getServiceIp() {
		return this.serviceIp;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 * @param serviceIp the serviceIp to set
	 */
	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}

	/**
	 * @return the servicePort
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 */
	public String getServicePort() {
		return this.servicePort;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午3:58:35
	 * @param servicePort the servicePort to set
	 */
	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	/**
	 * @return the serviceWeight
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午4:09:00
	 */
	public String getServiceWeight() {
		return this.serviceWeight;
	}

	/**
	 * @author shangchengcai@voole.com
	 * @date 2018年1月15日 下午4:09:00
	 * @param serviceWeight the serviceWeight to set
	 */
	public void setServiceWeight(String serviceWeight) {
		this.serviceWeight = serviceWeight;
	}

}
