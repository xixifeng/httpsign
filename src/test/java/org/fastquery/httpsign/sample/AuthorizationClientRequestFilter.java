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

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;

import org.fastquery.httpsign.AuthAbstractClientRequestFilter;
import org.fastquery.httpsign.Constant;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class AuthorizationClientRequestFilter extends AuthAbstractClientRequestFilter {

	private static ThreadLocal<String> auth = new ThreadLocal<>();

	@Override
	public String getAccessKeySecret(String accessKeyId) {
		if(accessKeyId!=null && accessKeyId.equals(Constant.accessKeyId)) {
			return Constant.accessKeySecret;
		} else {
			return Constant.accessKeySecret2;
		}
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		super.filter(requestContext);
		auth.set(requestContext.getHeaderString("Authorization"));
	}

	public static String getAuthorization() {
		return auth.get();
	}
}
