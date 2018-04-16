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

import java.util.Date;
import java.util.SortedMap;

/**
 * 参与计算签名(Signature)的因子
 * @author mei.sir@aliyun.cn
 */
class Factor {

	// http method
	private String httpMethod;

	private String contentMD5 = "";

	private String accept = "";

	private String date = DateUtil.formatRfc822Date(new Date());

	private String uri;

	private String requestParameters;

	private SortedMap<String, String> headers;

	Factor() {
	}

	Factor(String httpMethod, String contentMD5, String accept, String date, String uri, String requestParameters,
			SortedMap<String, String> headers) {
		this.httpMethod = httpMethod;
		this.contentMD5 = contentMD5;
		this.accept = accept;
		this.date = date;
		this.uri = uri;
		this.requestParameters = requestParameters;
		this.headers = headers;
	}

	String getHttpMethod() {
		return httpMethod;
	}

	void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	String getContentMD5() {
		return contentMD5;
	}

	void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	String getAccept() {
		return accept;
	}

	void setAccept(String accept) {
		this.accept = accept;
	}

	String getDate() {
		return date;
	}

	void setDate(String date) {
		this.date = date;
	}

	String getUri() {
		return uri;
	}

	void setUri(String uri) {
		this.uri = uri;
	}

	String getRequestParameters() {
		return requestParameters;
	}

	void setRequestParameters(String requestParameters) {
		this.requestParameters = requestParameters;
	}

	SortedMap<String, String> getHeaders() {
		return headers;
	}

	void setHeaders(SortedMap<String, String> headers) {
		this.headers = headers;
	}
	
	@Override
	public String toString() {
		return "Factor [httpMethod=" + httpMethod + ", contentMD5=" + contentMD5 + ", accept=" + accept + ", date=" + date + ", uri=" + uri
				+ ", requestParameters=" + requestParameters + ", headers=" + headers + "]";
	}
}
