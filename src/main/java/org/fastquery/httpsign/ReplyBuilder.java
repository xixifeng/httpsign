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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 应答构建
 * 
 * @author mei.sir@aliyun.cn
 */
public class ReplyBuilder {
	private ReplyBuilder() {
	}

	/**
	 * 构建成功 Response
	 * 
	 * @param data JSON对象数据
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder success(JSONObject data) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("data", data);
		return Response.ok(json.toJSONString());
	}

	/**
	 * 构建成功 Response
	 * 
	 * @param data JSON数组数据
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder success(JSONArray data) {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("data", data);
		return Response.ok(json.toJSONString());
	}

	public static ResponseBuilder error(Err err) {
		return error(err.getId(), err.getStatus(), err.getMessage());
	}
	
	/**
	 * 
	 * @param err 实现于Err的实例
	 * @param append 不能传递null
	 * @return ResponseBuilder
	 */
	public static ResponseBuilder error(Err err,String append) {
		Objects.requireNonNull(append);
		return error(err.getId(), err.getStatus(), err.getMessage() + append);
	}

	/**
	 * 仅供内部使用
	 * 
	 * @param code 错误码
	 * @param status http 状态码
	 * @param message 信息 不能传递null
	 * @return ResponseBuilder
	 */
	static ResponseBuilder error(int code, int status, String message) {
		Objects.requireNonNull(message);
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("message", message);
		return Response.ok(json.toJSONString()).type(MediaType.APPLICATION_JSON).status(status);
	}
}
