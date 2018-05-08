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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字节编码工具
 * @author mei.sir@aliyun.cn
 */
public class BinaryUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BinaryUtil.class);	
	
    private static final char[] DIGITS_LOWER =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    private static final char[] DIGITS_UPPER =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private BinaryUtil() {
	}
	
	/**
	 * 对字节数组进行Base64编码
	 * @param bytes 待编码的字节数组
	 * @return Base64字符串
	 */
	public static String toBase64String(byte ... bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 将Base64字符串解码成字节数组
	 * @param base64String 待解码的Base64字符串
	 * @return 字节数组
	 */
	public static byte[] fromBase64String(String base64String) {
		return Base64.getDecoder().decode(base64String);
	}

	/**
	 * 算出字节数组的md5
	 * @param bytes 待计算的字节数组
	 * @return md5结果(字节形式)
	 * @throws NoSuchAlgorithmException 没有找到匹配的算法
	 */
	public static byte[] md5(byte ... bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		return messageDigest.digest();
	}
	
	/**
	 * 
	 * @param str 待计算的字符串
	 * @param toLowerCase 是否转换成小写
	 * @return md5 hex
	 */
	public static String md5Hex(String str,final boolean toLowerCase) {
		char[] digits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
		byte[] bs;
		try {
			bs = md5(str.getBytes(Charset.forName("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		final int l = bs.length;
		final char[] out = new char[l << 1];
		int j = 0;
		for (int i = 0; i < l; i++) {
			out[j++] = digits[(0xF0 & bs[i]) >>> 4];
			out[j++] = digits[0x0F & bs[i]];
		}

		return new String(out);
	}

	/**
	 * 计算文件的md5值
	 * @param file 文件
	 * @param toLowerCase 输出的字符串是否转化成小写
	 * @return md5 hex
	 */
	public static String md5Hex(File file,final boolean toLowerCase) {
		char[] digits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;

		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}

		try (FileInputStream data = new FileInputStream(file)) {
			final byte[] buffer = new byte[1024];
			int read = data.read(buffer, 0, 1024);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = data.read(buffer, 0, 1024);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}

		byte[] bs = messageDigest.digest();
		final int l = bs.length;
		final char[] out = new char[l << 1];
		int j = 0;
		for (int i = 0; i < l; i++) {
			out[j++] = digits[(0xF0 & bs[i]) >>> 4];
			out[j++] = digits[0x0F & bs[i]];
		}

		return new String(out);
	}
}
