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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fastquery.httpsign.RangeTime.*;

/**
 * 实现Authorization,作用与服务端
 * @author mei.sir@aliyun.cn
 */
public abstract class AuthAbstractContainerRequestFilter implements ContainerRequestFilter, AccessAccount {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthAbstractContainerRequestFilter.class);
	
	@Context
	private HttpServletRequest request;
	
	/**
	 * 构建参与sign的Query String
	 * 
	 * @param parameters 请求参数
	 * @return 经过处理的QueryString
	 * @throws UnsupportedEncodingException 不支持编码异常
	 */
	private String buildQueryString(SortedMap<String, String> parameters) throws UnsupportedEncodingException {
		// 构造待签名的QueryString
		Iterator<String> it = parameters.keySet().iterator();
		StringBuilder queryStringTmp = new StringBuilder();
		while (it.hasNext()) {
			String name = it.next();
			queryStringTmp.append("&").append(specialUrlEncode(name)).append('=').append(specialUrlEncode(parameters.get(name)));
		}
		return queryStringTmp.substring(1);// 去除第一个多余的&符号
	}

	private String specialUrlEncode(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, SignBuilder.UTF8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
				
		String nonce = request.getParameter("nonce");
		if(nonce == null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40008).build());
			return;
		}
		
		// 10分钟内不能传递相同的随机码
		if(exists(nonce)) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40300).build());
			return;
		}
		// 10分钟内不能传递相同的随机码 End
		
		
		String clientAuth = requestContext.getHeaderString("Authorization");
		// 检测Authorization是否已传递
		if(clientAuth==null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40000).build());
			return;
		}
		
		// 校验Authorization长度是否够
		if(clientAuth.length() != SignBuilder.AUTH_LEN) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40001).build());
			return;
		}
		
		// 校验accept
		String accept = requestContext.getHeaderString("Accept");
		if(accept!=null && !MediaType.APPLICATION_JSON.equals(accept) && !MediaType.APPLICATION_XML.equals(accept) ) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40002).build());
			return;
		}
		
		// 校验date
		String date = requestContext.getHeaderString("Date");
		if(date==null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40003).build());
			return;
		}
		// 检测是否符合GMT格式
		Date clientDate = null;
		try {
			clientDate = DateUtil.parseRfc822Date(date);
		} catch (ParseException e) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40003).build());
			return;
		}
				
		// 请求端的时间不能比服务器时间快10分钟或慢10分钟
		long current = System.currentTimeMillis();
		if(Math.abs(current - clientDate.getTime()) > SignBuilder.TIME_LIMIT) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40004).build());
			return;
		} else {
			add(current,nonce);
		}
		
		// 校验version字段
		/*
		String version = request.getParameter("version");
		if(version == null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40005).build());
			return;
		}
		if(!"1".equals(version)) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40006).build());
			return;
		}
		*/
		
		// 校验action
		String action = request.getParameter("action");
		if(action == null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40007).build());
			return;
		}
		
		// 校验nonce
		int nonceLen = nonce.length();
		if(nonceLen<8 || nonceLen>36) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40009).build());
			return;
		}
		
		sign(requestContext, clientAuth, accept, date);
	}

	private void sign(ContainerRequestContext requestContext, String clientAuth, String accept, String date) throws UnsupportedEncodingException {
		// 获取accessKeyId
		String accessKeyId = request.getParameter("accessKeyId");
		if(accessKeyId==null || accessKeyId.length() < 8 || accessKeyId.length() > 36) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40010).build());
			return;
		}
		
		// 根据 accessKeyId 获取 accessKeySecret
		String accessKeySecret = getAccessKeySecret(accessKeyId);
		if(accessKeySecret == null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40011).build());
			return;
		}
		
		// 签名算法
		String signatureMethod = request.getParameter("signatureMethod");
		if(signatureMethod!=null && !Algorithm.HMACSHA1.name().equals(signatureMethod) && !Algorithm.HMACSHA256.name().equals(signatureMethod) ) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40012).build());
			return;
		}
		
		if(signatureMethod==null) {
			signatureMethod = SignBuilder.HMACSHA;
		}
		
		String httpMethod = requestContext.getMethod();
		String contentMD5 = requestContext.getHeaderString("Content-MD5");
		if(requestContext.getLength() > 0 && contentMD5 == null) {
			requestContext.abortWith(ReplyBuilder.error(Code.E40015).build());
			return;
		}
		String uriPath = requestContext.getUriInfo().getRequestUri().getPath();		

		TreeMap<String, String> parametersMap = new TreeMap<>();
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			parametersMap.put(name, request.getParameter(name));
		}

		String requestParameters = buildQueryString(parametersMap);

		Enumeration<String> enumHeaderNames = request.getHeaderNames();
		TreeMap<String, String> headerTreeMap = new TreeMap<>();
		while (enumHeaderNames.hasMoreElements()) {
			String name = enumHeaderNames.nextElement();
			String value = request.getHeader(name);
			// 筛选出自定义头
			if (name.toLowerCase().startsWith("x-custom-")) {
				headerTreeMap.put(name, value);
			}

		}
		Factor factor = new Factor(httpMethod, contentMD5, accept, date, uriPath, requestParameters, headerTreeMap);
		
		String stringFactor = SignBuilder.buidStringFactor(factor);
		String sign;
		try {
			sign = SignBuilder.sign(accessKeySecret, stringFactor,signatureMethod);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(),e);
			requestContext.abortWith(ReplyBuilder.error(Code.E40017).build());
			return;
		}

		String authorization = SignBuilder.buidAuthorization(sign);
		
		if (!authorization.equals(clientAuth)) {

			requestContext
					.abortWith(ReplyBuilder.error(Code.E40018).build());

		}
	}

}
