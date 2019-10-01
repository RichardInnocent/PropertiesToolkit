package org.richardinnocent.propertiestoolkit.annotations.constraints;

import java.util.function.Predicate;

import org.junit.Test;

import static org.junit.Assert.*;

public class NumberMustNotBeNegativeTest {

  private static final Predicate<Number> constraint = new NumberMustNotBeNegative().getConstraint();

  @Test
  public void testConstraintOnByte() {
    assertTrue(constraint.test((byte) 1));
    assertTrue(constraint.test((byte) 0));
    assertFalse(constraint.test((byte) -1));
  }

  @Test
  public void testConstraintOnShort() {
    assertTrue(constraint.test((short) 1));
    assertTrue(constraint.test((short) 0));
    assertFalse(constraint.test((short) -1));
  }

  @Test
  public void testConstraintOnInt() {
    assertTrue(constraint.test(1));
    assertTrue(constraint.test(0));
    assertFalse(constraint.test(-1));
  }

  @Test
  public void testConstraintOnLong() {
    assertTrue(constraint.test(1L));
    assertTrue(constraint.test(0L));
    assertFalse(constraint.test(-1L));
  }

  @Test
  public void testConstraintOnFloat() {
    assertTrue(constraint.test(0.1f));
    assertTrue(constraint.test(0f));
    assertFalse(constraint.test(-0.1f));
  }

  @Test
  public void testConstraintOnDouble() {
    assertTrue(constraint.test(0.1d));
    assertTrue(constraint.test(0d));
    assertFalse(constraint.test(-0.1d));
  }

}
