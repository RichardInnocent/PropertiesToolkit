package com.richardinnocent.propertiestoolkit;

/**
 * This exception is thrown when calling {@link Property#get()}, and any of the constraint checks
 * fail. This exception can be suppressed by providing the {@code Property} with some {@link
 * DefaultSettings} that specify what value should be returned when a {@link
 * DefaultCondition#IS_INVALID} condition is encountered.
 * @since 1.0.0
 * @author RichardInnocent
 */
@SuppressWarnings("WeakerAccess")
public class ValidationException extends PropertiesException {

  private ValidationException(String message) {
    super(message);
  }

  private ValidationException(String message, RuntimeException e) {
    super(message, e);
  }

  /**
   * Creates a new {@code ValidationException} with an appropriate message, given the {@code key}
   * and {@code value}.
   * @param key The property key.
   * @param value The raw {@code String} value from the {@code Properties} file.
   * @return A new {@code ValidationException} with an appropriate message.
   */
  public static ValidationException forProperty(String key, String value) {
    return new ValidationException(getExceptionMessage(key, value));
  }

  /**
   * Creates a new {@code ValidationException} with an appropriate message. The provided {@code
   * cause} is added as the new {@code ValidationException}'s cause.
   * @param key The property key.
   * @param value The raw {@code String} value from the {@code Properties} file.
   * @param cause The exception thrown when trying to run the validation.
   * @return A new, appropriately initialised {@code ValidationException}.
   */
  public static ValidationException forProperty(String key, String value, RuntimeException cause) {
    return new ValidationException(getExceptionMessage(key, value), cause);
  }

  private static String getExceptionMessage(String key, String value) {
    return String.format("Key, %s, contains an invalid value, %s", key, value);
  }

}
