/*
  Copyright 2018 Adobe.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.adobe.rhino.sdk.data;

import com.adobe.rhino.sdk.users.User;

/**
 * User session is a stash to store objects and share them among scenarios per user session. A
 * user will be created before the simulation starts, and it will existing during the simulation
 * execution.
 *
 * @author <a href="mailto:bagdemir@adobe.com">Erhan Bagdemir</a>
 * @since 1.1.0
 */
public class UserSessionImpl extends ContextImpl implements UserSession {

  private User user;

  public UserSessionImpl(final User user) {
    this.user = user;
  }

  @Override
  public User getUser() {
    return user;
  }
}
