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

package io.ryos.rhino.sdk.runners;

import static io.ryos.rhino.sdk.utils.ReflectionUtils.getFieldByAnnotation;
import static io.ryos.rhino.sdk.utils.ReflectionUtils.instanceOf;

import io.ryos.rhino.sdk.SimulationMetadata;
import io.ryos.rhino.sdk.annotations.Feeder;
import io.ryos.rhino.sdk.annotations.SessionFeeder;
import io.ryos.rhino.sdk.annotations.UserFeeder;
import io.ryos.rhino.sdk.data.InjectionPoint;
import io.ryos.rhino.sdk.data.UserSession;
import io.ryos.rhino.sdk.exceptions.BadInjectionException;
import io.ryos.rhino.sdk.users.data.User;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Injector for reactive runner. The difference from {@link DefaultRunnerSimulationInjector} is
 * the reactive variant does inject the factory classes which return values to the DSL
 * instances, whereas the default injector implementation does inject the value itself into the
 * injection points, that are marked with {@link Feeder} annotation.
 * <p>
 *
 * @author Erhan Bagdemir
 * @see ReactiveHttpSimulationRunner
 * @see DefaultRunnerSimulationInjector
 * @since 1.1.0
 */
public class ReactiveRunnerSimulationInjector extends AbstractSimulationInjector {

  private static final Logger LOG = LogManager.getLogger(ReactiveRunnerSimulationInjector.class);

  /**
   * Simulation metadata.
   * <p>
   */
  private final SimulationMetadata simulationMetadata;

  /**
   * Current user session, that is actively running.
   * <p>
   */
  private final UserSession userSession;

  /**
   * Instantiates a new {@link ReactiveRunnerSimulationInjector} instance.
   * <p>
   *
   * @param simulationMetadata Simulation metadata.
   */
  public ReactiveRunnerSimulationInjector(final SimulationMetadata simulationMetadata,
      final UserSession userSession) {
    this.simulationMetadata = Objects.requireNonNull(simulationMetadata);
    this.userSession = userSession;
  }

  @Override
  public void injectOn(Object injectable) {
    if (userSession != null) {
      injectUser();// Each thread will run as the same user.
      injectSession();
    }

    injectCustomFeeders(injectable);
  }

  private void injectSession() {
    var fieldAnnotation = getFieldByAnnotation(simulationMetadata.getSimulationClass(),
        SessionFeeder.class);
    fieldAnnotation
        .ifPresent(f -> { throw new BadInjectionException("The session injection is only "
            + "supported in default mode. Every spec definition in load DSLs has already access "
            + "to the user session in closures."); });
  }

  private void injectUser() {
    var fieldAnnotation = getFieldByAnnotation(simulationMetadata.getSimulationClass(),
        UserFeeder.class);
    fieldAnnotation
        .ifPresent(f -> { throw new BadInjectionException("The user injection is only "
            + "supported in default mode. Every spec definition in load DSLs has already access "
            + "to the user session in closures."); });
  }

  /* Find the first annotation type, clazzAnnotation, on field declarations of the clazz.  */
  private void injectCustomFeeders(final Object simulationInstance) {

    Arrays.stream(simulationMetadata.getSimulationClass().getDeclaredFields())
        .filter(hasFeeder)
        .map(injectionPointFunction)
        .forEach(ip -> feed(simulationInstance, ip));
  }

  // Feedable the feeder value into the field.
  protected void feed(final Object instance, final InjectionPoint<Feeder> injectionPoint) {
    Objects.requireNonNull(instance, "Object instance is null.");
    var factoryInstance = instanceOf(injectionPoint.getAnnotation().factory()).orElseThrow();
    try {
      var field = injectionPoint.getField();
      field.setAccessible(true);
      //TODO pre-check before assignment.
      field.set(instance, factoryInstance);
    } catch (IllegalAccessException e) {
      LOG.error("Access to field failed.", e);
    } catch (IllegalArgumentException e) {
      LOG.error("Feedable's return type and field's type is not compatible: " + e.getMessage());
    }
  }
}
