/*
  Copyright 2018 Ryos.io.

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

package io.ryos.rhino.sdk.specs;

/**
 * Retriable spec is the DSL spec which is to be retried if predicate turns true.
 * <p>
 *
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public interface MeasurableSpec extends Spec {

  /**
   * Disables the measurement recording.
   * <p>
   *
   * @return {@link HttpConfigSpec} instance.
   */
  Spec noMeasurement();

  /**
   * Cumulative measurement.
   * <p>
   *
   * @return {@link HttpConfigSpec} instance.
   */
  Spec cumulativeMeasurement();
}
