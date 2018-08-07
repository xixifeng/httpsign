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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一处理 WebApplicationException 异常
 * 
 * @author mei.sir@aliyun.cn
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	private static final Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);
	
	public WebApplicationExceptionMapper() {
		LOG.info("WebApplicationExceptionMapper 已经示例化");
	}

	@Context
    private UriInfo ui;
	 
	@Override
	public Response toResponse(WebApplicationException we) {
		LOG.info("RequestUri: {}, path: {}",ui.getRequestUri(),ui.getPath());
		LOG.warn(we.getMessage(), we);
		int status = we.getResponse().getStatus();
		String msg = we.getMessage();
		return ReplyBuilder.error(status,status,msg == null ? "" : msg).build();
	}
}
