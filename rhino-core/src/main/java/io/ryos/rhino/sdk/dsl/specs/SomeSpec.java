package io.ryos.rhino.sdk.dsl.specs;

import io.ryos.rhino.sdk.data.UserSession;
import java.util.function.Function;

/**
 * Some spec is a custom spec to enable developers to add arbitrary code snippets into the DSL.
 * <p>
 *
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public interface SomeSpec extends Spec {

  /**
   * Function contains the code snippet to be applied.
   * <p>
   *
   * @return Spec function.
   */
  Function<UserSession, String> getFunction();

  /**
   * Method to add a spec function into the DSL.
   * <p>
   */
  Spec as(Function<UserSession, String> function);
}
