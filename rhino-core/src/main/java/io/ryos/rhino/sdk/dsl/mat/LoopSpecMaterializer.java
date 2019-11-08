/*
 * Copyright 2018 Ryos.io.
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
import io.ryos.rhino.sdk.dsl.specs.ForEachSpec;
import io.ryos.rhino.sdk.dsl.specs.HttpSpec;
import io.ryos.rhino.sdk.runners.EventDispatcher;
import java.util.Optional;
import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Erhan Bagdemir
 * @since 1.7.0
 */
public class LoopSpecMaterializer<E, R extends Iterable<E>> implements
    SpecMaterializer<ForEachSpec<E, R>, UserSession> {

  private static final Logger LOG = LoggerFactory.getLogger(LoopSpecMaterializer.class);

  private final EventDispatcher eventDispatcher;
  private final AsyncHttpClient asyncHttpClient;

  public LoopSpecMaterializer(EventDispatcher eventDispatcher, AsyncHttpClient asyncHttpClient) {
    this.eventDispatcher = eventDispatcher;
    this.asyncHttpClient = asyncHttpClient;
  }

  @Override
  public Mono<UserSession> materialize(final ForEachSpec<E, R> spec, final UserSession session) {
    var forEachBuilder = spec.getForEachBuilder();
    var iterable = Optional.ofNullable(forEachBuilder.getSessionExtractor().apply(session))
        .orElseThrow(() -> new IllegalArgumentException(String.format("forEach() failed. The "
            + "instance with key: %s", forEachBuilder.getKey())));
    var materializerFactory = new MaterializerFactory(asyncHttpClient, eventDispatcher);
    var loopFunction = forEachBuilder.getForEachFunction();

    return Flux.fromIterable(iterable)
        .flatMap(s -> materializerFactory.monoFrom(loopFunction.apply(s), session,
            new ChildrenResultHandler(session, (HttpSpec) loopFunction.apply(s), spec.getContextKey())))
        .reduce((s1, s2) -> s1)
        .doOnError(e -> LOG.error("Unexpected error: ", e));
  }
}
