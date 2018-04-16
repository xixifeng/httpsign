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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 应答构建
 * @author mei.sir@aliyun.cn
 */
public class ReplyBuilder {
	private JSON data;

	private ReplyBuilder() {
	}
	
	/**
	 * 构建成功 Response
	 * @param data JSON对象数据
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder success(JSONObject data) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("data",data);
		return Response.ok(json.toJSONString());
	}

	/**
	 * 构建成功 Response
	 * @param data JSON数组数据
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder success(JSONArray data) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("data",data);
		return Response.ok(json.toJSONString());
	}

	/**
	 * 构建错误 Response
	 * @param code 自定义的错误编码
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder error(Code code) {
		JSONObject json = new JSONObject();
		json.put("code", code.getId());
		json.put("message", code.getMessage());
		return  Response.ok(json.toJSONString()).status(code.getStatus());
	}
	
	/**
	 * 构建错误 Response, 该方法不对外
	 * @param status HTTP Status
	 * @param message 错误信息
	 * @return ResponseBuilder
	 */
	static ResponseBuilder error(int status,String message) {
		JSONObject json = new JSONObject();
		json.put("code", status);
		json.put("message", message==null?"":message);
		return  Response.ok(json.toJSONString()).status(status);
	}
	
	/**
	 * 获取业务数据
	 * @return JSON数据
	 */
	public JSON getData() {
		return data;
	}
}
