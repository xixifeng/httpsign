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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 字节编码工具
 * @author mei.sir@aliyun.cn
 */
public class BinaryUtil {

	private static final char[] HEXDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
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
	public static byte[] calculateMd5(byte ... bytes) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		return messageDigest.digest();
	}

	/**
	 *  算出字节数组的md5
	 * @param bytes 待计算的字节数组
	 * @return md5结果(16进制字符串形式)
	 * @throws NoSuchAlgorithmException 没有找到匹配的算法
	 */
	public static String encodeMD5(byte ... bytes) throws NoSuchAlgorithmException {
		byte[] md5Bytes = calculateMd5(bytes);
		int len = md5Bytes.length;
		char[] buf = new char[len * 2];
		for (int i = 0; i < len; i++) {
			buf[i * 2] = HEXDIGITS[(md5Bytes[i] >>> 4) & 0x0f];
			buf[i * 2 + 1] = HEXDIGITS[md5Bytes[i] & 0x0f];
		}
		return new String(buf);
	}
}
