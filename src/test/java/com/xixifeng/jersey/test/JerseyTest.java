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

package com.xixifeng.jersey.test;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.glassfish.jersey.servlet.ServletContainer;

import org.junit.AfterClass;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mei.sir@aliyun.cn
 */
public abstract class JerseyTest {

	private static final Logger LOG = LoggerFactory.getLogger(JerseyTest.class);

	private static Server server;

	protected TestContainerInfo configure() {
		throw new UnsupportedOperationException("The configure method must be implemented by the extending class");
	}

	protected Client builderClient() {
		return ClientBuilder.newClient();
	}

	public final WebTarget target(final String path) {
		WebTarget target = builderClient().target(getBaseUri()).path(path);
		return target;
	}

	@Before
	public void setUp() throws Exception {
		if (server == null) {
			synchronized (JerseyTest.class) {
				if (server == null) {
					TestContainerInfo tci = configure();
					server = new Server(tci.getPort());
					ServletHolder servlet = new ServletHolder(ServletContainer.class);
					servlet.setInitParameter("javax.ws.rs.Application", tci.getApplication());
					ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
					handler.setContextPath(tci.getContextPath());
					handler.addServlet(servlet, tci.getPathSpec());
					server.setHandler(handler);
					server.start();
				}
			}
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		server.clearAttributes();
		server.stop();
	}

	/**
	 * Returns the base URI of the tested application.
	 *
	 * @return the base URI of the tested application.
	 */
	// TODO make final
	protected URI getBaseUri() {
		TestContainerInfo tci = configure();
		String pathSpec = tci.getPathSpec();
		pathSpec = pathSpec.replaceAll("\\/\\*", "/");
		return UriBuilder.fromUri("http://localhost").port(tci.getPort()).path(tci.getContextPath()).path(pathSpec).build();
	}

	/**
	 * Utility method that safely closes a response without throwing an exception.
	 *
	 * @param responses responses to close. Each response may be {@code null}.
	 * @since 2.5
	 */
	public final void close(final Response... responses) {
		if (responses == null || responses.length == 0) {
			return;
		}

		for (final Response response : responses) {
			if (response == null) {
				continue;
			}
			try {
				response.close();
			} catch (final Throwable t) {
				LOG.warn("Error closing a response.", t);
			}
		}
	}

	/**
	 * Utility method that safely closes a client instance without throwing an exception.
	 *
	 * @param clients client instances to close. Each instance may be {@code null}.
	 * @since 2.5
	 */
	public static void closeIfNotNull(final Client... clients) {
		if (clients == null || clients.length == 0) {
			return;
		}

		for (final Client c : clients) {
			if (c == null) {
				continue;
			}
			try {
				c.close();
			} catch (final Throwable t) {
				LOG.warn("Error closing a client instance.", t);
			}

		}
	}
}
