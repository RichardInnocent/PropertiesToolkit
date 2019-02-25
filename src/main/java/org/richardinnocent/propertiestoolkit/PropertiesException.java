package org.richardinnocent.propertiestoolkit;

/**
 * The parent class for all exceptions that can be thrown while calling {@link Property#get()}.
 * @since 1.0.0
 * @author RichardInnocent
 */
public class PropertiesException extends RuntimeException {

  /**
   * Creates an empty {@code PropertiesException}.
   */
  public PropertiesException() {
    super();
  }

  /**
   * Creates a {@code PropertiesException} with the specified message.
   * @param message The exception message.
   */
  public PropertiesException(String message) {
    super(message);
  }

  /**
   * Creates a {@code PropertiesException} with the specified message and cause.
   * @param message The exception message.
   * @param cause The cause of the exception.
   */
  public PropertiesException(String message, Exception cause) {
    super(message, cause);
  }

  /**
   * Creates a {@code PropertiesException} with the specified cause.
   * @param e The cause of the exception.
   */
  public PropertiesException(Exception e) {
    super(e);
  }

}
