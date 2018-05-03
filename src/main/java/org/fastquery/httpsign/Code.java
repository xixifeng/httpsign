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
 * 错误码
 * 
 * @author mei.sir@aliyun.cn
 */
public enum Code {

	E40000("没有传递请求头Authorization."), 
	E40001("传递的请求头Authorization不符合规范."), 
	E40002("传递的请求头Accept不符合要求,要么是\"application/json\" 要么是 \"application/xml\"."), 
	E40003("请求头Date必须传递,并且必须是HTTP 1.1协议中规定的GMT时间."), 
	E40004("请求端的时间不能比服务器时间快10分钟或慢10分钟."), 
	E40005("没有传递请求参数version."), 
	E40006("传递的version参数,不符合要求."), 
	E40007("名称为action的请求参数没有传递."), 
	E40008("名称为nonce的请求参数没有传递."), 
	E40009("nonce的长度不能超过36且不能小与8."), 
	E40010("名称为accessKeyId的请求参数没有传递."), 
	E40011("根据accessKeyId没有找到对应的accessKeySecret."), 
	E40012("签名算法要么传递HMACSHA1或HMACSHA256,要不传递(默认:HMACSHA1)."), 
	E40013("传递的token错误."), 
	E40014("token认证失败."), 
	E40015("有请求body,而没有传递请求头Content-MD5."),
	E40016("计算请求body的MD5出错."), 
	E40017("生成请求头Authorization出错."), 
	E40018("传过来的Authorization是错的."),
	E40300("在10分钟内不能传递相同的随机码."),
	
	E50300("服务不可用.");
	
	private String message;
	private String msg = "";

	private Code(String message) {
		this.message = message;
	}

	/**
	 * 获取自定义编码
	 * 
	 * @return 编码
	 */
	public int getId() {
		String name = this.name();
		return Integer.parseInt(name.substring(1));
	}

	/**
	 * HTTP状态码
	 * 
	 * @return 状态码
	 */
	public int getStatus() {
		return getId() / 100;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return 信息
	 */
	public String getMessage() {
		return message + msg;
	}

	/**
	 * 附加描述
	 * 
	 * @param msg 详细错误
	 * @return 当前实例
	 */
	public Code appendMsg(String msg) {
		this.msg = msg == null ? this.msg : msg;
		return this;
	}
}
