package org.richardinnocent.propertiestoolkit.annotations.constraints;

import java.util.function.Predicate;

/**
 * Represents a constraint that should be applied to the object after mapping it to the appropriate
 * type. If a value passes all constraints, the field will be set to that value. If the value fails
 * any of the constraints, a {@link org.richardinnocent.propertiestoolkit.ValidationException} will
 * be thrown. This will be handled as appropriate by the {@link
 * org.richardinnocent.propertiestoolkit.DefaultSettings} on the specified {@link
 * org.richardinnocent.propertiestoolkit.annotations.PropertyExtractor}.
 * @param <T> The constraint type. This should match (or be a parent of) the field type.
 * @since 3.0.0
 */
public abstract class PropertyConstraint<T> {

  private final Class<T> type;

  /**
   * Creates a new constraint.
   * @param type The constraint type. This should match (or be a parent of) the field type.
   */
  public PropertyConstraint(Class<T> type) {
    this.type = type;
  }

  /**
   * Gets the property constraint. For a property to be valid, the result of testing this predicate
   * must be {@code true}.
   * @return The property constraint.
   */
  public abstract Predicate<T> getConstraint();

  /**
   * Gets the type.
   * @return The type.
   */
  public final Class<T> getType() {
    return type;
  }

}
