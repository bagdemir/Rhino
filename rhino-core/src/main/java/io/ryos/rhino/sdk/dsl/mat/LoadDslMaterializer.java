/*
 * Copyright 2020 Ryos.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ryos.rhino.sdk.dsl.mat;

import io.ryos.rhino.sdk.data.UserSession;
import io.ryos.rhino.sdk.dsl.LoadDsl;
import io.ryos.rhino.sdk.dsl.impl.DslMethodImpl;
import reactor.core.publisher.Mono;

public class LoadDslMaterializer implements DslMaterializer {

  private final LoadDsl dslItem;

  public LoadDslMaterializer(LoadDsl dslItem) {
    this.dslItem = dslItem;
  }

  @Override
  public Mono<UserSession> materialize(final UserSession userSession) {
    DslMethodImpl dslMethod = new DslMethodImpl("na", dslItem);
    dslMethod.setParent(dslItem.getParent());
    return new DslMethodMaterializer(dslMethod).materialize(userSession);
  }
}
