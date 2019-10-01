package org.richardinnocent.propertiestoolkit.annotations;

import java.util.function.Function;

import org.richardinnocent.propertiestoolkit.DefaultSettings;

/**
 * Specifies how an object will be mapped from a text value, and what will occur in the event of errors. This is
 * designed for integration with the {@link FromProperty} annotation.
 * @param <T> The desired object type.
 * @since 3.0.0
 */
@FunctionalInterface
public interface PropertyExtractor<T> {

  /**
   * The function that will map text to an object.
   * @return The {@code Function} that will map text to an object.
   * @throws RuntimeException Thrown if there is a problem when mapping, such as if an {@code
   * IllegalArgumentException} is thrown.
   *
   */
  Function<String, T> getExtractionMethod() throws RuntimeException;

  /**
   * Defines the behaviour that should occur when {@link
   * org.richardinnocent.propertiestoolkit.PropertiesException}s occur.
   * @return The behaviour that should occur when exceptions occur. If this is {@code null}, any
   * exception will be thrown at runtime.
   * @see DefaultSettings
   */
  default DefaultSettings<T> getDefaultSettings() {
    return null;
  }

}
