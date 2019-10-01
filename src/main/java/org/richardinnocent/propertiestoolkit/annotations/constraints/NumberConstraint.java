package org.richardinnocent.propertiestoolkit.annotations.constraints;

/**
 * Abstract class for constraints regarding {@code Number}s.
 * @since 3.0.0
 */
public abstract class NumberConstraint extends PropertyConstraint<Number> {

  /**
   * Creates a new {@code Number} constraint.
   */
  public NumberConstraint() {
    super(Number.class);
  }

}
