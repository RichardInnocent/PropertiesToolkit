package org.richardinnocent.propertiestoolkit;

/**
 * This exception is thrown when calling {@link Property#get()}, and the provided {@link
 * java.util.function.Function} cannot parse the text from the {@code Properties} file to the
 * correct type. This exception can be suppressed by providing the {@code Property} with some
 * {@link DefaultSettings} that specify what value should be returned when a {@link
 * DefaultCondition#PARSE_FAILS} condition is encountered.
 * @since 1.0.0
 * @author RichardInnocent
 */
@SuppressWarnings("WeakerAccess")
public class InvalidTypeException extends PropertiesException {

  private InvalidTypeException(String message, Exception e) {
    super(message, e);
  }

  /**
   * Creates an {@code InvalidTypeException} with an appropriate message, given the {@code key} and
   * {@code value}. The provided {@code cause} is added as the new {@code InvalidTypeException}'s
   * cause.
   * @param key The property key.
   * @param value The raw {@code String} value from the properties file.
   * @param cause The exception thrown when trying to convert the {@code value} to the appropriate
   *   type.
   * @return A new, appropriately initialised {@code InvalidTypeException}.
   */
  public static InvalidTypeException forProperty(String key, String value, Exception cause) {
    return new InvalidTypeException(
        String.format("Key, %s, contains a value, %s, that cannot be "
                          + "converted to the expected type", key, value), cause);
  }

}
