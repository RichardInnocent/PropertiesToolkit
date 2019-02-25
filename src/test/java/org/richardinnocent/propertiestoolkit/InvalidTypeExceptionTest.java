package org.richardinnocent.propertiestoolkit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class InvalidTypeExceptionTest {

  @Test
  public void testForProperty() {
    String key = "key";
    String value = "value";
    String causeMessage = "Could not parse " + value + " to an int";
    Exception cause = new NumberFormatException(causeMessage);
    InvalidTypeException e = InvalidTypeException.forProperty(key, value, cause);
    assertEquals("Key, " + key + ", contains a value, " + value + ", that cannot be "
                     + "converted to the expected type", e.getMessage());
    assertEquals(cause, e.getCause());
  }

}
