package org.richardinnocent.propertiestoolkit.annotations.constraints;

import java.util.function.Predicate;

import org.junit.Test;

import static org.junit.Assert.*;

public class NumberMustBeNegativeTest {

  private static final Predicate<Number> constraint = new NumberMustBeNegative().getConstraint();

  @Test
  public void testConstraintOnByte() {
    assertFalse(constraint.test((byte) 1));
    assertFalse(constraint.test((byte) 0));
    assertTrue(constraint.test((byte) -1));
  }

  @Test
  public void testConstraintOnShort() {
    assertFalse(constraint.test((short) 1));
    assertFalse(constraint.test((short) 0));
    assertTrue(constraint.test((short) -1));
  }

  @Test
  public void testConstraintOnInt() {
    assertFalse(constraint.test(1));
    assertFalse(constraint.test(0));
    assertTrue(constraint.test(-1));
  }

  @Test
  public void testConstraintOnLong() {
    assertFalse(constraint.test(1L));
    assertFalse(constraint.test(0L));
    assertTrue(constraint.test(-1L));
  }

  @Test
  public void testConstraintOnFloat() {
    assertFalse(constraint.test(0.1f));
    assertFalse(constraint.test(0f));
    assertTrue(constraint.test(-0.1f));
  }

  @Test
  public void testConstraintOnDouble() {
    assertFalse(constraint.test(0.1d));
    assertFalse(constraint.test(0d));
    assertTrue(constraint.test(-0.1d));
  }

}
