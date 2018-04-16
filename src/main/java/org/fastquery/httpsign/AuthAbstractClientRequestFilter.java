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

package org.fastquery.httpsign;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现Authorization,作用与客户端
 * @author mei.sir@aliyun.cn
 */
public abstract class AuthAbstractClientRequestFilter implements ClientRequestFilter, AccessAccount {

	private static final Logger LOG = LoggerFactory.getLogger(AuthAbstractClientRequestFilter.class);

	@Context
	private HttpServletRequest request;

	/**
	 * 设置请求头
	 * 
	 * @param name 头名称
	 * @param value 值
	 * @param requestContext 上下文
	 */
	private void header(final String name, final Object value, ClientRequestContext requestContext) {
		final MultivaluedMap<String, Object> headers = requestContext.getHeaders();

		if (value == null) {
			headers.remove(name);
		} else {
			headers.remove(name);
			headers.add(name, value);
		}
	}

	/**
	 * 给予默认值: nonce,version,Authorization,Accept,Content-MD5,Date,User-Agent,signatureMethod
	 */
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {

		MultivaluedMap<String, String> map = requestContext.getStringHeaders();

		URI uri = requestContext.getUri();
		String nonce = UUID.randomUUID().toString();
		uri = UriBuilder.fromUri(uri).queryParam("nonce", nonce).queryParam("version", 1).build();
		requestContext.setUri(uri);

		// 获取accessKeyId 直接去读配置文件
		String accessKeyId;
		if (request != null) {
			accessKeyId = request.getParameter("accessKeyId");
		} else {
			accessKeyId = new QueryStringParser(uri.getQuery()).get("accessKeyId");
		}
		// 根据 accessKeyId 获取 accessKeySecret
		String accessKeySecret = getAccessKeySecret(accessKeyId);

		// 签名算法
		String signatureMethod = Algorithm.HMACSHA1.name();
		
		String rawQueryString = uri.getRawQuery(); // 参数名和参数值是 URLEncoder.encode的结果
		rawQueryString = rawQueryString.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		LOG.debug("QueryString按照规范编码后:{}", rawQueryString);

		String[] ps = rawQueryString.split("\\&");
		Arrays.sort(ps);
		StringBuilder parms = new StringBuilder();
		for (String p : ps) {
			parms.append('&').append(p);
		}
		String requestParameters = parms.substring(1); // 去除第一个多余的&符号

		TreeMap<String, String> headerTreeMap = new TreeMap<>();
		map.forEach((name, v) -> {
			String value = v.get(0);
			// 筛选出自定义头
			if (name.toLowerCase().startsWith("x-custom-")) {
				headerTreeMap.put(name.trim(), value.trim());
			}
		});

		String httpMethod = requestContext.getMethod();
		String contentMD5 = null;
		if(requestContext.hasEntity()) {
			
			if(requestContext.getEntityType() == String.class) {
				String body = requestContext.getEntity().toString();
				byte[] bytes;
				try {
					bytes = BinaryUtil.calculateMd5(body.getBytes(Charset.forName(SignBuilder.UTF8)));
				} catch (NoSuchAlgorithmException e) {
					requestContext.abortWith(ReplyBuilder.error(Code.E40016).build());
					return;
				}
				contentMD5 = BinaryUtil.toBase64String(bytes);
			}
		}
		String contentType = requestContext.getHeaderString("Accept");
		String date = DateUtil.formatRfc822Date();
		String uriPath = uri.getPath();

		Factor factor = new Factor(httpMethod, contentMD5, contentType, date, uriPath, requestParameters, headerTreeMap);		
		String stringFactor = SignBuilder.buidStringFactor(factor);
		String sign;
		try {
			sign = SignBuilder.sign(accessKeySecret, stringFactor,signatureMethod);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40017).build());
			return;
		}
		String authorization = SignBuilder.buidAuthorization(sign);
		header("Authorization", authorization, requestContext);
		header("Content-MD5", contentMD5, requestContext);
		header("Date", date, requestContext);
		header("User-Agent", "httpsign/1.0", requestContext);

	}

}
