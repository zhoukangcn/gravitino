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

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.gravitino.integration.test.util.CommandExecutor;
import org.apache.gravitino.integration.test.util.ITUtils;
import org.apache.gravitino.integration.test.util.ProcessData;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;

public class DorisITContainers implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(DorisITContainers.class);

  public static String dockerComposeDir;

  private static ImmutableSet<String> servicesName =
      ImmutableSet.of("fe", "be", "broker");

  Map<String, String> servicesUri = new HashMap<>();

  DorisITContainers() {
    String dir = System.getenv("GRAVITINO_ROOT_DIR");
    if (Strings.isEmpty(dir)) {
      throw new RuntimeException("GRAVITINO_ROOT_DIR is not set");
    }

    dockerComposeDir = ITUtils.joinPath(dir, "integration-test-common", "docker-script");
  }

  public void launch() throws Exception {
    shutdown();

    String command = ITUtils.joinPath(dockerComposeDir, "launch-doris.sh");
    Object output =
        CommandExecutor.executeCommandLocalHost(
            command, false, ProcessData.TypesOfData.STREAMS_MERGED);
    LOG.info("Command {} output:\n{}", command, output);

    String outputString = output.toString();
    if (Strings.isNotEmpty(outputString)
        && !outputString.contains("All Docker Compose services are now available")) {
      throw new ContainerLaunchException("Failed to start containers:\n " + outputString);
    }

    resolveServerAddress();
  }

  private void resolveServerAddress() throws Exception {
    String command = ITUtils.joinPath(dockerComposeDir, "inspect-doris.sh");
    Object output =
        CommandExecutor.executeCommandLocalHost(
            command, false, ProcessData.TypesOfData.STREAMS_MERGED);
    LOG.info("Command {} output:\n{}", command, output);

    // expect the output to be like:
    // fe:172.20.0.10
    // be:172.20.0.11
    // broker:172.20.0.12

    String containerIpMapping = output.toString();
    if (containerIpMapping.isEmpty()) {
      throw new ContainerLaunchException(
          "Failed to get the container status, the containers have not started");
    }

    try {
      String[] containerInfos = containerIpMapping.split("\n");
      for (String container : containerInfos) {
        String[] info = container.split(":");

        String containerName = info[0];
        String address = info[1];

        if (containerName.equals("fe")) {
          servicesUri.put("fe", String.format("jdbc:mysql://%s:9030", address));
          servicesUri.put("fe_http", String.format("http://%s:8030", address));
        } else if (containerName.equals("be")) {
          servicesUri.put("be", String.format("http://%s:8040", address));
        } else if (containerName.equals("broker")) {
          servicesUri.put("broker", String.format("http://%s:8000", address));
        }
      }
    } catch (Exception e) {
      throw new ContainerLaunchException("Unexpected container status :\n" + containerIpMapping, e);
    }

    for (String serviceName : servicesName) {
      if (!servicesUri.containsKey(serviceName)) {
        throw new ContainerLaunchException(
            String.format("The container for the %s service is not started: ", serviceName));
      }
    }
  }

  public void shutdown() {
    String command = ITUtils.joinPath(dockerComposeDir, "shutdown-doris.sh");
    Object output =
        CommandExecutor.executeCommandLocalHost(
            command, false, ProcessData.TypesOfData.STREAMS_MERGED);
    LOG.info("Command {} output:\n{}", command, output);
  }

  public String getFeJdbcUri() {
    return servicesUri.get("fe");
  }

  public String getFeHttpUri() {
    return servicesUri.get("fe_http");
  }

  public String getBeHttpUri() {
    return servicesUri.get("be");
  }

  public String getBrokerHttpUri() {
    return servicesUri.get("broker");
  }

  @Override
  public void close() throws Exception {
    shutdown();
  }
}
