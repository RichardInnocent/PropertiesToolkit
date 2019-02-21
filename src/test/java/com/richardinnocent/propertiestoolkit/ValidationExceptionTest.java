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
    String expectedMessage = "Key, " + key + ", contains an invalid value, " + value;
    ValidationException ve1 = ValidationException.forProperty(key, value);
    assertEquals(expectedMessage, ve1.getMessage());

    RuntimeException e = new RuntimeException("test exception");
    ValidationException ve2 = ValidationException.forProperty(key, value, e);
    assertEquals(expectedMessage, ve2.getMessage());
    assertEquals(e, ve2.getCause());
  }

}
