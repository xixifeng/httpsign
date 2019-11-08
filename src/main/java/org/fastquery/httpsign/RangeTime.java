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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public final class RangeTime {

	private static Map<Long, String> map = new HashMap<>();

	private RangeTime() {
	}

	/**
	 * 判断当前传递的随机码在20分钟内是否出现过
	 * 
	 * @param nonce 随机码
	 * @return 若出现了,返回ture,反之,false
	 */
	static boolean exists(String nonce) {
		return map.containsValue(nonce);
	}

	/**
	 * 删除距离存储时已经过去20分钟的随机码
	 * 
	 * @param current 当前时间
	 */
	private static void del(long current) {

		Iterator<Long> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Long k = iterator.next();
			if (current - k.longValue() > SignBuilder.TIME_LIMIT * 2) {
				map.remove(k);
			}
		}
		
		// 在遍历过程中,对容器进行删除操作,必须使用迭代器，否则,会抛出ConcurrentModificationException.
		
		/**
		 * <pre>
		// 这样写会导致 java.util.ConcurrentModificationException
		map.forEach((k, v) -> {
			if (Math.abs(k.longValue() - current) > SignBuilder.TIME_LIMIT * 2) {
				map.remove(k);
			}
		});
		</pre>
		 */
	}

	/**
	 * 请特别注意: 这个方法故意没有做同步. 因为每个请求都会执行这个方法,如果加同步会影响到每个请求. <br>
	 * 除非此方法出现不安全了,否则不会允许客户端进行重放攻击. 为了性能,放宽了容忍度,这样做合理吗?
	 * 
	 * @param current 当前时间
	 * @param nonce 随机码
	 */
	static void add(long current, String nonce) {
		del(current);
		map.put(current, nonce);
	}

}
