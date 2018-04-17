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

import java.text.ParseException;

import org.junit.Test;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class Calculate {

	@Test
	public void gmt() throws ParseException {
		
		// RFC 822 日期格式
		String f = "EEE, dd MMM yyyy HH:mm:ss z";
		java.text.SimpleDateFormat rfc822DateFormat = new java.text.SimpleDateFormat(f, java.util.Locale.US);
		rfc822DateFormat.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
		
		// 将date格式化成GMT时间格式的字符串
		java.util.Date date = new java.util.Date();
		String gmtStr = rfc822DateFormat.format(date);
		
		// 将GMT时间格式的字符串解析成Date对象
		java.util.Date d = rfc822DateFormat.parse(gmtStr);
		
	}
}
