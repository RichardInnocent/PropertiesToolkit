package org.richardinnocent.propertiestoolkit.annotations.constraints;

import java.util.function.Predicate;

/**
 * Indicates that a value, {@code x}, is only value if {@code x >= 0}.
 * @since 3.0.0
 */
public class NumberMustNotBeNegative extends NumberConstraint {

  private static final Predicate<Number> CONSTRAINT =
      number -> number != null && number.doubleValue() >= 0d;

  @Override
  public Predicate<Number> getConstraint() {
    return CONSTRAINT;
  }

}
