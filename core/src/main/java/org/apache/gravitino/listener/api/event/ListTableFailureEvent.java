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

package org.apache.gravitino.listener.api.event;

import org.apache.gravitino.NameIdentifier;
import org.apache.gravitino.Namespace;
import org.apache.gravitino.annotation.DeveloperApi;

/**
 * Represents an event that is triggered when an attempt to list tables within a namespace fails due
 * to an exception.
 */
@DeveloperApi
public final class ListTableFailureEvent extends TableFailureEvent {
  private final Namespace namespace;

  /**
   * Constructs a {@code ListTableFailureEvent} instance.
   *
   * @param user The username of the individual who initiated the operation to list tables.
   * @param namespace The namespace for which the table listing was attempted.
   * @param exception The exception encountered during the attempt to list tables.
   */
  public ListTableFailureEvent(String user, Namespace namespace, Exception exception) {
    super(user, NameIdentifier.of(namespace.levels()), exception);
    this.namespace = namespace;
  }

  /**
   * Retrieves the namespace associated with this failure event.
   *
   * @return A {@link Namespace} instance for which the table listing was attempted
   */
  public Namespace namespace() {
    return namespace;
  }

  /**
   * Returns the type of operation.
   *
   * @return the operation type.
   */
  @Override
  public OperationType operationType() {
    return OperationType.LIST_TABLE;
  }
}
