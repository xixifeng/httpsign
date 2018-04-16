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

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class AuthorizationTest {

	// 详细计算authorization的过程
	@org.junit.Test
	public void authorization() throws Exception {

		// 密钥
		String accessKeySecret = "KYA8A4-74E17B58B093";

		String uriPath = "/httpsign/userResorce/greet";
		String httpMethod = "POST";
		String accept = "application/json";
		String date = "Wed, 11 Apr 2018 06:03:43 GMT";

		// 构建请求头
		java.util.TreeMap<String, String> headerTreeMap = new java.util.TreeMap<>();
		headerTreeMap.put("X-Custom-Content-Range", "52363");
		headerTreeMap.put("X-Custom-Meta-Author", "FastQuery.HttpSign");
		headerTreeMap.put("X-Custom-Meta-Description", "HTTP authentication techniques.");
		StringBuilder headersBuilder = new StringBuilder();
		headerTreeMap.forEach((k, v) -> headersBuilder.append(k.toLowerCase()).append(':').append(v).append('\n'));
		String headersStr = headersBuilder.toString();

		// 构建请求参数
		java.util.TreeMap<String, String> queryStringTreeMap = new java.util.TreeMap<>();
		queryStringTreeMap.put("accessKeyId", "AP084671DF-5F8C-41D2");
		queryStringTreeMap.put("typeId", "7");
		queryStringTreeMap.put("nonce", "e6e03b6f-7de2-4d02-8e04-3ccbad143389");
		StringBuilder requestParametersBuilder = new StringBuilder();
		queryStringTreeMap.forEach((k, v) -> {
			try {
				requestParametersBuilder.append('&').append(k).append('=')
						.append(java.net.URLEncoder.encode(v, "utf-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~"));
			} catch (java.io.UnsupportedEncodingException e) {
				throw new RuntimeException("URL编码出错", e);
			}
		});
		String requestParameters = requestParametersBuilder.substring(1);

		// 计算Content-MD5的值
		String requestBody = "蚓无爪牙之利，筋骨之强，上食埃土，下饮黄泉，用心一也";
		byte[] input = requestBody.getBytes(java.nio.charset.Charset.forName("utf-8"));
		java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
		messageDigest.update(input);
		byte[] md5Bytes = messageDigest.digest();
		String contentMD5 = java.util.Base64.getEncoder().encodeToString(md5Bytes);

		// 构建 stringToSign
		StringBuilder sb = new StringBuilder();
		sb.append(httpMethod).append('\n');
		sb.append(contentMD5).append('\n');
		sb.append(accept).append('\n');
		sb.append(date).append('\n');
		sb.append(headersStr);
		sb.append(uriPath).append('\n');
		sb.append(requestParameters);
		String stringToSign = sb.toString();

		// 计算出signature
		javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
		mac.init(new javax.crypto.spec.SecretKeySpec(accessKeySecret.getBytes(java.nio.charset.Charset.forName("utf-8")), "HmacSHA1"));
		byte[] signData = mac.doFinal(stringToSign.getBytes(java.nio.charset.Charset.forName("utf-8")));
		String signature = java.util.Base64.getEncoder().encodeToString(signData);

		// 得出authorization
		String authorization = "Basic " + signature;
		// 断言:authorization等于"Basic 3qo3tKAYM16Pr88Lpr5WPj2VJco="
		org.junit.Assert.assertThat(authorization, org.hamcrest.Matchers.equalTo("Basic 3qo3tKAYM16Pr88Lpr5WPj2VJco="));

		// 截至这里, 解答完毕.

	}

}
