/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.gravitino.integration.test.container;

/**
 * Common lifecycle interface for integration test containers. Implementations may use a single
 * {@link org.testcontainers.containers.GenericContainer} (see {@link BaseContainer}) or a
 * multi-container orchestration such as {@link org.testcontainers.containers.ComposeContainer}.
 */
public interface ContainerLifecycle extends AutoCloseable {

  /** Starts the container(s). */
  void start();

  /** Stops and cleans up the container(s). */
  @Override
  void close();

  /** Returns the IP address (or host) that tests should use to connect to this container. */
  String getContainerIpAddress();

  /**
   * Checks whether the container is ready to accept connections.
   *
   * @param retryLimit the maximum number of retries
   * @return {@code true} if the container is ready
   */
  boolean checkContainerStatus(int retryLimit);
}
