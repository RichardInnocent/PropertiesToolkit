package com.richardinnocent.propertiestoolkit;

/**
 * This exception is thrown when calling {@link Property#get()}, when the raw {@code String} value
 * is {@code null} or empty ({@code ""}). This exception can be suppressed by providing the {@code
 * Property} with some {@link DefaultSettings} that specify what value should be returned when a
 * {@link DefaultCondition#IS_EMPTY} condition is encountered.
 * @since 1.0.0
 * @author RichardInnocent
 */
@SuppressWarnings("WeakerAccess")
public class MissingPropertyException extends PropertiesException {

  private MissingPropertyException(String message) {
    super(message);
  }

  /**
   * Creates a {@code MissingPropertyException} with an appropriate message, given the {@code key}.
   * @param key The property key.
   * @return A new {@code MissingPropertyException}, with an appropriate message.
   */
  public static MissingPropertyException forProperty(String key) {
    return new MissingPropertyException(String.format("Property, %s, is missing", key));
  }

}
