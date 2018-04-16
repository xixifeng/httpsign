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

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.junit.Test;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public class CreateMDTable {

	@Test
	public void status() {
		System.out.println("|错误码|描述|");
		System.out.println("|:-----:|:-----|");
		Class<Status> clazz = Status.class;
		Status[] status = clazz.getEnumConstants();
		for (Status s : status) {
			Family f = s.getFamily();
			if (f == Family.CLIENT_ERROR || f == Family.SERVER_ERROR) {
				System.out.println("|" + s.getStatusCode() + "|" + s.getReasonPhrase() + "|");
			}
		}
	}

	@Test
	public void code() {
		System.out.println("|错误码|描述|");
		System.out.println("|:-----:|:-----|");
		Class<Code> clazz = Code.class;
		Code[] codes = clazz.getEnumConstants();
		for (Code c : codes) {
			System.out.println("|" + c.getId() + "|" + c.getMessage() + "|");
		}
	}

}
