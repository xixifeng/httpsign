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

import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.TreeMap;
import java.util.UUID;

import org.fastquery.bytes.HexUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class EncodeTest {

	private static final Logger LOG = LoggerFactory.getLogger(EncodeTest.class);

	@Test
	public void encode1() throws UnsupportedEncodingException {
		String str = "αβγ";
		LOG.debug(HexUtil.hexStr(str.getBytes("utf-8")));
		LOG.debug(URLEncoder.encode(str, "utf-8"));

		String ab = "%CE%B1%CE%B2%CE%B3";
		LOG.debug(URLDecoder.decode(ab, "utf-8"));

		ab = "%5E";
		LOG.debug(URLDecoder.decode(ab, "utf-8"));

		char a = 'α';
		LOG.debug(String.valueOf(a + 0));
		LOG.debug(String.valueOf(0XFFFF));
	}

	@Test
	public void encode2() throws UnsupportedEncodingException {

		// 对于字符 A~Z、a~z、0~9 以及字符“-”、“_”、“.”、“~”不编码
		// -_.~ 火狐/google/ie6 都遵守这个规则
		LOG.debug("\"-\" 编码结果: " + URLEncoder.encode("-", "utf-8")); // -
		LOG.debug("\"_\" 编码结果: " + URLEncoder.encode("_", "utf-8")); // _
		LOG.debug("\".\" 编码结果: " + URLEncoder.encode(".", "utf-8")); // .
		LOG.debug("\"~\" 编码结果: " + URLEncoder.encode("~", "utf-8")); // %7E

		LOG.debug("\" \" 编码结果: " + URLEncoder.encode(" ", "utf-8")); // +
		LOG.debug("\"*\" 编码结果: " + URLEncoder.encode("*", "utf-8")); // *
		LOG.debug("\"+\" 编码结果: " + URLEncoder.encode("+", "utf-8")); // %2B

	}

	@Test
	public void encode3() throws UnsupportedEncodingException {

		String s = "a b c";

		LOG.debug(URLEncoder.encode(s, "utf-8")); // a+b+c

		LOG.debug(URLDecoder.decode("a+b+c", "utf-8")); // a b c

		TreeMap<String, String> t = new TreeMap<>();
		t.put("T", ((byte) 'T') + "");
		t.put("i", ((byte) 'i') + "");
		t.put("l", ((byte) 'l') + "");
		t.put("o", ((byte) 'o') + "");
		t.put("A", ((byte) 'A') + "");
		t.put("N", ((byte) 'N') + "");
		t.put("R", ((byte) 'R') + "");
		t.put("S", ((byte) 'S') + "");

		t.forEach((k, v) -> {
			LOG.debug("|" + k + "|" + v + "|");
		});

		LOG.debug(String.valueOf(t));

	}

	@Test
	public void uuid() {
		assertThat(UUID.randomUUID().toString().length(), is(36));
	}
}
