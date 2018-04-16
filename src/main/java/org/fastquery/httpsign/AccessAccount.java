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
 * 密钥查找
 * 
 * @author mei.sir@aliyun.cn
 */
@FunctionalInterface
public interface AccessAccount {
	/**
	 * 根据accessKeyId找出accessKeySecret
	 * 
	 * @param accessKeyId 在云API密钥上申请的标识身份的 accessKeyId,一个 accessKeyId 对应唯一的 accessKeySecret
	 * @return accessKeySecret
	 */
	String getAccessKeySecret(String accessKeyId);
}
