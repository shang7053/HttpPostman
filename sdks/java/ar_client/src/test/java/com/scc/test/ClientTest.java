package com.scc.test;

import java.io.IOException;

import com.scc.ar.client.core.AutoRegistService;

/**
 * @ClassName: ClientTest
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2018年1月15日 下午5:05:08
 * 
 */
public class ClientTest {

	public static void main(String[] args) throws IOException {
		AutoRegistService service = new AutoRegistService();
		service.setZkAddress("172.16.40.4:2181");
		service.setServiceDomain("www.baidu.net");
		service.setServicePort("80");
		service.setServiceWeight("2");
		service.setServiceRegisterInterval("100");
		service.init();
		System.in.read();
	}

}
