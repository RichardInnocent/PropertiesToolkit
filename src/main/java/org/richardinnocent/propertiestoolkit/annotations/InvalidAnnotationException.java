package org.richardinnocent.propertiestoolkit.annotations;

import org.richardinnocent.propertiestoolkit.PropertiesException;

/**
 * An exception of this type implies that the annotations that were applied to a field are not
 * valid, and will need to be modified.
 */
public class InvalidAnnotationException extends PropertiesException {

  /**
   * Creates an exception with the given message.
   * @param message The message.
   */
  public InvalidAnnotationException(String message) {
    super(message);
  }

  /**
   * Creates an exception with the given message and cause.
   * @param message The message.
   * @param cause The cause.
   */
  public InvalidAnnotationException(String message, Throwable cause) {
    super(message, cause);
  }

}
