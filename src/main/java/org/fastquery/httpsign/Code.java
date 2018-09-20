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

import java.util.Objects;

/**
 * 错误码
 * 
 * @author mei.sir@aliyun.cn
 */
public enum Code implements Err {

	E40000("没有传递请求头Authorization."), 
	E40001("传递的请求头Authorization不符合规范."), 
	E40002("传递的请求头Accept不符合要求,要么是\"application/json\" 要么是 \"application/xml\"."), 
	E40003("请求头Date必须传递,并且必须是HTTP 1.1协议中规定的GMT时间."), 
	E40004("请求端的时间不能比服务器时间快10分钟或慢10分钟."), 
	// API 若是多版本满天飞,简直是噩梦,如其解决问题,不如消灭问题
	//E40005("没有传递请求参数version."), 
	//E40006("传递的version参数,不符合要求."), 
	//E40007("名称为action的请求参数没有传递."), 
	E40008("名称为nonce的请求参数没有传递."), 
	E40009("nonce的长度不能超过36且不能小与8."), 
	E40010("名称为accessKeyId的请求参数没有传递或是范围越界,允许长度范围[8,36]."), 
	E40011("根据accessKeyId没有找到对应的accessKeySecret."), 
	E40012("签名算法要么传递HMACSHA1或HMACSHA256,要不传递(默认:HMACSHA1)."), 
	E40013("传递的token错误."), 
	E40014("token认证失败."), 
	E40015("有请求body,而没有传递请求头Content-MD5."),
	E40016("计算请求body的MD5出错."), 
	E40017("计算Authorization出错."), 
	E40018("传过来的Authorization是错的."),
	E40019("参数校验不通过."),
	E40300("在10分钟内不能传递相同的随机码."),
	E40301("该接口只允许登录前访问,不允许登录后访问."),
	E40302("该接口只允许登录后访问."),
	E50300("服务不可用.");
	
	private String message;
	private int status;
	private int id;

	private Code(String message) {
		Objects.requireNonNull(message);
		this.message = message;

		String name = this.name();
		this.id = Integer.parseInt(name.substring(1));
		this.status = Integer.parseInt(name.substring(1).substring(0, 3));
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
