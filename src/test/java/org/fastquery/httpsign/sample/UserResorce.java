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

package org.fastquery.httpsign.sample;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.fastquery.httpsign.Authorization;
import org.fastquery.httpsign.ReplyBuilder;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
@Authorization
@Path("userResorce")
public class UserResorce {

	@Context
	private HttpServletRequest request;
	
	@Path("hi")
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response hi() {
		JSONObject json = new JSONObject();
		json.put("msg", "Welcome to HttpSign!");

		return ReplyBuilder.success(json).build();
	}
	
	@Path("greet")
	@POST
	@Produces("application/json;charset=UTF-8")
	public Response greet(String body) {
		JSONObject json = new JSONObject();
		json.put("authorization", request.getHeader("Authorization"));
		return ReplyBuilder.success(json).build();
	}

}
