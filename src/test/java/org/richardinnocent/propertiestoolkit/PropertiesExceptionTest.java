package org.richardinnocent.propertiestoolkit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class PropertiesExceptionTest {

  private static final String MESSAGE = "message";
  private static final Exception CAUSE = new Exception("cause");

  @Test
  public void testMessage() {
    assertEquals(MESSAGE, new PropertiesException(MESSAGE).getMessage());
  }

  @Test
  public void testMessageAndCause() {
    PropertiesException e = new PropertiesException(MESSAGE, CAUSE);
    assertEquals(MESSAGE, e.getMessage());
    assertEquals(CAUSE, e.getCause());
  }

  @Test
  public void testCause() {
    assertEquals(CAUSE, new PropertiesException(CAUSE).getCause());
  }

}
