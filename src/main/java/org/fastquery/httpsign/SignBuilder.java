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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.SortedMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 签名构建
 * 
 * @author mei.sir@aliyun.cn
 */
public class SignBuilder {

	public static final String UTF8 = "UTF-8";
	public static final int AUTH_LEN = 34;
	public static final String BASIC = "Basic ";
	public static final int TIME_LIMIT = 600000; // 时间容忍度600000毫秒(10分钟)
	public static final String HMACSHA = "HMACSHA1";

	private SignBuilder() {
	}

	private static String buildCustomHeader(SortedMap<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		headers.forEach((k, v) -> sb.append(k.toLowerCase()).append(':').append(v).append('\n'));
		return sb.toString();
	}

	/**
	 * 构建参与sign的字符串
	 * 
	 * @param factor 计算因子
	 * @return stringFactor
	 */
	static String buidStringFactor(Factor factor) {

		StringBuilder sb = new StringBuilder();
		sb.append(factor.getHttpMethod()).append('\n');
		String contentMD5 = factor.getContentMD5();
		if (contentMD5 != null) {
			sb.append(contentMD5).append('\n');
		}
		sb.append(factor.getAccept()).append('\n');
		sb.append(factor.getDate()).append('\n');
		sb.append(buildCustomHeader(factor.getHeaders()));
		sb.append(factor.getUri()).append('\n');
		sb.append(factor.getRequestParameters());

		return sb.toString();

	}

	/**
	 * 将传递的字符串用密钥进行签名
	 * 
	 * @param accessKeySecret 密钥
	 * @param stringToSign 参与签名的字符串
	 * @param algorithm 算法
	 * @return 签名
	 * @throws NoSuchAlgorithmException 没有匹配到算法
	 * @throws InvalidKeyException 无效KEY
	 * @throws UnsupportedEncodingException 不支持编码
	 */
	static String sign(String accessKeySecret, String stringToSign,String algorithm)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(accessKeySecret.getBytes(UTF8), algorithm)); // HmacSHA1,HmacSHA256
		byte[] signData = mac.doFinal(stringToSign.getBytes(UTF8));
		return Base64.getEncoder().encodeToString(signData);
	}

	/**
	 * 构建Authorization
	 * 
	 * @param sign 签名字符串
	 * @return Authorization
	 */
	static String buidAuthorization(String sign) {
		StringBuilder sb = new StringBuilder();
		sb.append(BASIC);
		sb.append(sign);
		return sb.toString();
	}
}
