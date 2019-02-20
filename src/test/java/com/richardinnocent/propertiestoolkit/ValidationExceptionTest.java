package com.richardinnocent.propertiestoolkit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class ValidationExceptionTest {

  @Test
  public void testForProperty() {
    String key = "key";
    String value = "value";
    ValidationException e = ValidationException.forProperty(key, value);
    assertEquals("Key, " + key + ", contains an invalid value, " + value, e.getMessage());
  }

}
