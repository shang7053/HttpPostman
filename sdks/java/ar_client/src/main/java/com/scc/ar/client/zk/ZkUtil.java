package com.scc.ar.client.zk;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ZkUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2018年1月16日 下午3:39:34
 * 
 */
public class ZkUtil implements Watcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkUtil.class);
	private static ZkUtil zkUtil;
	private static ZooKeeper zk;
	static {
		zkUtil = new ZkUtil();
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
	 * @date 2018年1月16日 下午3:39:34
	 */
	private ZkUtil() {
		// TODO Auto-generated constructor stub
	}

	public static ZkUtil instance() {
		return zkUtil;
	}

	public ZkUtil connect(String address) {
		try {
			zk = new ZooKeeper(address, 3000, zkUtil);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		return this;
	}

	public ZkUtil createNode(String basePath, String domain, String host, String weight) {
		StringBuilder path = new StringBuilder(basePath);
		path.append(domain);
		path.append("/");
		String dataNodeName = host + "$" + weight;
		path.append(dataNodeName);
		this.createPersistentNode(path.toString(), dataNodeName);
		return this;
	}

	/**
	 * @Title: createNode
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author shangchengcai@voole.com
	 * @param host
	 * @date 2018年1月16日 下午5:23:29
	 * @param string
	 */
	private void createPersistentNode(String path, String host) {
		try {
			String[] nodes = path.split("/");
			StringBuilder pathtmp = new StringBuilder("/");
			for (String node : nodes) {
				if (StringUtils.isBlank(node)) {
					continue;
				}
				pathtmp.append(node);
				if (null == zk.exists(pathtmp.toString(), false)) {
					if (node.equals(host)) {
						zk.create(pathtmp.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
					} else {
						zk.create(pathtmp.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					}
				}
				pathtmp.append("/");
			}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}

	public ZkUtil close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		return this;
	}

	/*
	 * (非 Javadoc) <p>Title: process</p> <p>Description: </p>
	 * 
	 * @param event
	 * 
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	@Override
	public void process(WatchedEvent event) {
		LOGGER.debug("Receive watched event:{},but do nothing for it", event);
	}
}
