/*
 * Copyright (c) 2016-2100, fastquery.org and/or its affiliates. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For more information, please see http://www.fastquery.org/.
 * 
 */

package org.fastquery.httpsign.sample;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Date;

import static org.hamcrest.Matchers.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fastquery.httpsign.Code;
import org.fastquery.httpsign.Constant;
import org.fastquery.httpsign.SignBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xixifeng.jersey.test.JerseyTest;
import com.xixifeng.jersey.test.TestContainerInfo;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class UserResorceTest extends JerseyTest {

	private static final Logger LOG = LoggerFactory.getLogger(UserResorceTest.class);

	private static String sign(String accessKeySecret, String stringToSign) throws Exception {
		Class<SignBuilder> clazz = SignBuilder.class;
		Method method = clazz.getDeclaredMethod("sign", String.class, String.class, String.class);
		method.setAccessible(true);
		return SignBuilder.BASIC + method.invoke(null, accessKeySecret, stringToSign, "HmacSHA1").toString();
	}

	@Override
	protected TestContainerInfo configure() {
		TestContainerInfo tci = new TestContainerInfo();
		tci.setApplication("org.fastquery.httpsign.sample.Application");
		tci.setContextPath("/httpsign");
		tci.setPathSpec("/*");
		return tci;
	}

	@Override
	protected Client builderClient() {
		Client client = super.builderClient();

		client.register(AuthorizationClientRequestFilter.class);

		return client;
	}

	@Test
	public void testHi() {
		WebTarget target = target("userResorce/hi").queryParam("n u m", "1 2 3 4 5 6 ~ * ").queryParam("info", "好好学习,天天想上").queryParam("action", "1")
				.queryParam("accessKeyId", "AP084671DF-5F8C-41D2");
		Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
		Response response = builder.get();
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);

		LOG.debug(json.toJSONString());
		assertThat(response.getStatus(), is(200));
		assertThat(json.containsKey("message"), is(false));
		assertThat(json.getIntValue("code"), is(0));
		assertThat(json.getJSONObject("data").getString("msg"), equalTo("Welcome to HttpSign!"));

	}

	@Test
	public void greet0() {
		Client client = builderClient();
		WebTarget target = client.target(getBaseUri()).path("userResorce/greet");
		// .queryParam("accessKeyId",Constant.accessKeyId)
		target = target.queryParam("accessKeyId", Constant.accessKeyId).queryParam("typeId", 7).queryParam("action", "myinfo");
		Builder builder = target.request(MediaType.APPLICATION_JSON);

		// 自定义请求头
		builder.header("X-Custom-Meta-Author", "FastQuery.HttpSign");
		builder.header("X-Custom-Meta-Description", "HTTP authentication techniques.");
		builder.header("X-Custom-Content-Range", "52363");

		String body = "蚓无爪牙之利，筋骨之强，上食埃土，下饮黄泉，用心一也";
		Entity<String> entity = Entity.text(body);
		Response response = builder.post(entity);
		System.out.println(response.getStatus());
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		String authorization = AuthorizationClientRequestFilter.getAuthorization();
		LOG.debug("json:{}", json);
		assertThat(json.getJSONObject("data").getString("authorization"), equalTo(authorization));
	}

	// 测试不传递 Authorization
	@Test
	public void greet1() {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww1").request();
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40000.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40000.getId()));
	}

	// 测试传递不符合规范的Authorization
	@Test
	public void greet2() {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww1").request();
		builder.header("Authorization", "xyz");
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40001.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40001.getId()));
	}

	// 测试已经正确传递Authorization
	@Test
	public void greet3() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww3").request();
		builder.header("Authorization", sign("abc", "123"));
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40001.getStatus()));
		assertThat(json.getIntValue("code"), not(is(Code.E40001.getId())));
	}

	// 测试是否传递accept
	@Test
	public void greet4() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww4").request();
		builder.header("Authorization", sign("abc", "123"));
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40002.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40002.getId()));
	}

	// 测试是否传递 date
	@Test
	public void greet5() throws Exception {

		Client client = super.builderClient();
		WebTarget target = client.target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww5");
		Builder builder = target.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40003.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40003.getId()));
	}

	// 测试 date是否是GMT时间
	@Test
	public void greet6() throws Exception {
		Client client = super.builderClient();
		WebTarget target = client.target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww6");
		Builder builder = target.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", "Fri Apr 13 11:32:34 CST 2018");
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40003.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40003.getId()));
	}

	// 测试 date是否慢10分钟/快10分钟
	@Test
	public void greet7() throws Exception {
		Client client = super.builderClient();
		WebTarget target = client.target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww7");
		Builder builder = target.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", "Fri, 13 Apr 2018 03:50:14 GMT");
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40004.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40004.getId()));
	}

	// 测试不传递 校验version字段
	@Test
	public void greet8() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww8").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40005.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40005.getId()));
	}

	// 测试传递version错误
	@Test
	public void greet9() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesww9").queryParam("version", "0")
				.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40006.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40006.getId()));
	}

	// 测试不传递action
	@Test
	public void greet10() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesw10").queryParam("version", "1")
				.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40007.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40007.getId()));
	}

	// 测试不传递nonce
	@Test
	public void greet11() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40008.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40008.getId()));
	}

	// 测试传递nonce非法
	@Test
	public void greet12() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "kgwx02").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40009.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40009.getId()));
	}

	// 测试传递nonce非法
	@Test
	public void greet13() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeeswwswwseege2").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40009.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40009.getId()));
	}

	// 测试没有传递accessKeyId
	@Test
	public void greet14() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeeswws").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40010.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40010.getId()));
	}

	// 测试找不到密钥
	@Test
	public void greet15() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesw15").queryParam("accessKeyId", "abc23546")
				.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		Response response = builder.post(null);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40011.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40011.getId()));
	}

	// 测试把算法传递错误
	@Test
	public void greet16() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesw16").queryParam("accessKeyId", "AP084671DF-5F8C-41D2")
				.queryParam("signatureMethod", "HMACSHA384").request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		String body = "  ";
		Entity<String> entity = Entity.text(body);
		Response response = builder.post(entity);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40012.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40012.getId()));
	}

	// 测试没有传递"Content-MD5"
	@Test
	public void greet17() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesw17").queryParam("accessKeyId", "AP084671DF-5F8C-41D2")
				.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Date", new Date());
		String body = "  ";
		Entity<String> entity = Entity.text(body);
		Response response = builder.post(entity);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40015.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40015.getId()));
	}

	// 测试
	@Test
	public void greet18() throws Exception {
		Builder builder = super.builderClient().target(getBaseUri()).path("userResorce/greet").queryParam("version", "1")
				.queryParam("action", "myinfo").queryParam("nonce", "swwsgeeswwswwseegeswwsgeesw18").queryParam("accessKeyId", "AP084671DF-5F8C-41D2")
				.request(MediaType.APPLICATION_JSON);
		builder.header("Authorization", sign("abc", "123"));
		builder.header("Content-MD5", "BheE8OSZqgEXBcg6TjcrfQ==");
		builder.header("Date", new Date());
		String body = "好好学习,天天向上";
		Entity<String> entity = Entity.text(body);
		Response response = builder.post(entity);
		String str = response.readEntity(String.class);
		JSONObject json = JSON.parseObject(str);
		LOG.debug("json:{}", json);
		assertThat(response.getStatus(), is(Code.E40018.getStatus()));
		assertThat(json.getIntValue("code"), is(Code.E40018.getId()));
	}

}
