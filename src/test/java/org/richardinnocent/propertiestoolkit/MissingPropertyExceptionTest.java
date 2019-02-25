package org.richardinnocent.propertiestoolkit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class MissingPropertyExceptionTest {

  @Test
  public void testForProperty() {
    String key = "key";
    MissingPropertyException e = MissingPropertyException.forProperty(key);
    assertEquals("Property, " + key + ", is missing", e.getMessage());
  }

}
