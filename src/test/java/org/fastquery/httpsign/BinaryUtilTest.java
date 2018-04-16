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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class BinaryUtilTest {

	@Test
	public void testToBase64String() {
	}

	@Test
	public void testFromBase64String() {
	}

	@Test
	public void testCalculateMd5() {
	}

	@Test
	public void testEncodeMD5() {
	}

	@Test
	public void contextMD5() throws NoSuchAlgorithmException {
		String content = "0123456789";
		// 1. 先计算MD5加密的二进制数组（128位）
		byte[] bytes = BinaryUtil.calculateMd5(content.getBytes(Charset.forName(SignBuilder.UTF8)));

		// 2. 再对这个二进制进行base64编码（而不是对32位字符串编码）。
		String str = BinaryUtil.toBase64String(bytes);

		assertThat(str, equalTo("eB5eJF1ptWaXm4bijSPyxw=="));
	}

	@Test
	public void encodeMD5() throws NoSuchAlgorithmException {
		String content = "0123456789";
		String str = BinaryUtil.encodeMD5(content.getBytes());
		assertThat(str, equalTo("781E5E245D69B566979B86E28D23F2C7"));
	}

	@Test
	public void calueMD5() throws NoSuchAlgorithmException {
		// 待计算的内容
		String content = "好好学习,天天向上";
		byte[] input = content.getBytes(java.nio.charset.Charset.forName("utf-8"));

		// 1. 先计算出MD5加密的字节数组(16个字节)
		java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
		messageDigest.update(input);
		byte[] md5Bytes = messageDigest.digest();

		// 2. 再对这个字节数组进行Base64编码（而不是对长度为32的MD5字符串进行编码）。
		// Java 8+ 中自带的Base64工具(java.util.Base64)
		String str = java.util.Base64.getEncoder().encodeToString(md5Bytes);

		// 正确的值应该是 "BheE8OSZqgEXBcg6TjcrfQ=="

		// 断言
		assertThat(str, equalTo("BheE8OSZqgEXBcg6TjcrfQ=="));

	}
}
