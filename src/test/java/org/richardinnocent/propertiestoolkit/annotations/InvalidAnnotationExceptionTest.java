package org.richardinnocent.propertiestoolkit.annotations;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class InvalidAnnotationExceptionTest {

  @Test
  public void testConstructorWithMessage() {
    String message = "message";
    assertEquals(message, new InvalidAnnotationException(message).getMessage());
  }

  @Test
  public void testConstructorWithMessageAndCause() {
    String message = "message";
    Throwable cause = mock(Throwable.class);
    InvalidAnnotationException e = new InvalidAnnotationException(message, cause);
    assertEquals(message, e.getMessage());
    assertEquals(cause, e.getCause());
  }

}
