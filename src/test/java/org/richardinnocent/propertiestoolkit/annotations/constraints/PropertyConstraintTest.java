package org.richardinnocent.propertiestoolkit.annotations.constraints;

import java.util.function.Predicate;

import org.junit.Test;
import org.richardinnocent.propertiestoolkit.annotations.constraints.PropertyConstraint;

import static org.junit.Assert.*;

public class PropertyConstraintTest {

  @Test
  public void testTypeIsSetCorrectlyFromConstructor() {
    class Constraint<T> extends PropertyConstraint<T> {
      Constraint(Class<T> type) {
        super(type);
      }

      @Override
      public Predicate<T> getConstraint() {
        return null;
      }
    }
    assertEquals(String.class, new Constraint<>(String.class).getType());
  }

}
