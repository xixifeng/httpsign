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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * 日期工具
 * @author mei.sir@aliyun.cn
 */
public class DateUtil {

	// RFC 822 Date Format
	private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
	
	private DateUtil() {
	}
	
	/**
	 * Formats Date to GMT string.
	 */
	/**
	 * 将传递的Date格式化成GMT时间的字符串形式
	 * @param date 时间
	 * @return GMT字面字符串
	 */
	public static String formatRfc822Date(Date date) {
		return getRfc822DateFormat().format(date);
	}

	/**
	 * 将当前Date格式化成GMT时间的字符串形式
	 * @return GMT字面字符串
	 */
	public static String formatRfc822Date() {
		return formatRfc822Date(new Date());
	}

	/**
	 * 将 GMT 字符串格式化成 Date
	 * @param gmt 字符串表示的GMT
	 * @return 时间
	 * @throws ParseException 格式化异常
	 */
	public static Date parseRfc822Date(String gmt) throws ParseException {
		return getRfc822DateFormat().parse(gmt);
	}

	private static DateFormat getRfc822DateFormat() {
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
		rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

		return rfc822DateFormat;
	}
}
