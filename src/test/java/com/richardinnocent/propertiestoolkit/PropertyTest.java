package com.richardinnocent.propertiestoolkit;

import java.util.function.Function;

import org.junit.Test;

import static com.richardinnocent.propertiestoolkit.DefaultCondition.*;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class PropertyTest {

  private static final String KEY = "key";
  private static final String STRING = "string";
  private static final String INT_TEXT = "43";
  private static final Integer INT = 43;
  private static final Function<String, Integer> INT_PARSER = Integer::parseInt;

  @Test
  public void testBasicParsing() {
    Property<Integer> property = new Property<>(KEY, INT_TEXT, INT_PARSER);
    assertEquals(INT, property.get());
  }

  @Test(expected = MissingPropertyException.class)
  public void testEmptyPropertyWithNoDefault() {
    new Property<>(KEY, null, INT_PARSER).get();
  }

  @Test
  public void testEmptyPropertyWithDefault() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(IS_EMPTY)
        .thenReturn(INT);
    assertEquals(INT,
                 new Property<>(KEY, null, INT_PARSER)
                     .withDefaultSettings(settings)
                     .get());
  }

  @Test(expected = InvalidTypeException.class)
  public void testInvalidTypeWithNoDefault() {
    new Property<>(KEY, STRING, INT_PARSER).get();
  }

  @Test
  public void testInvalidTypeWithDefault() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(PARSE_FAILS)
        .thenReturn(INT);
    assertEquals(INT,
                 new Property<>(KEY, STRING, INT_PARSER)
                     .withDefaultSettings(settings)
                     .get());
  }

  @Test(expected = ValidationException.class)
  public void testInvalidValueWithNoDefault() {
    new Property<>(KEY, INT_TEXT, INT_PARSER)
        .addValidation(value -> value < INT)
        .get();
  }

  @Test
  public void testInvalidValueWithDefault() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(IS_INVALID)
        .thenReturn(INT);
    assertEquals(INT,
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .withDefaultSettings(settings)
                     .get());
  }

  @Test
  public void testSingleValidation() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(IS_INVALID)
        .thenReturn(0);

    // Valid
    assertEquals(INT,
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .addValidation(value -> value > 0)
                     .get());

    // Invalid
    assertEquals(Integer.valueOf(0),
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .addValidation(value -> value < 40)
                     .withDefaultSettings(settings)
                     .get());
  }

  @Test
  public void testSingleNullValidation() {
    assertEquals(INT, new Property<>(KEY, INT_TEXT, INT_PARSER)
        .addValidation(null)
        .get());
  }

  @Test
  public void testMultipleValidations() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(IS_INVALID)
        .thenReturn(0);

    // Valid
    assertEquals(INT,
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .addValidation(null)
                     .addValidation(value -> value > 0)
                     .addValidation(value -> value < 50)
                     .get());

    assertEquals(Integer.valueOf(0),
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .withDefaultSettings(settings)
                     .addValidation(null)
                     .addValidation(value -> value > 0)
                     .addValidation(value -> value < 40)
                     .get());
  }

  @Test(expected = ValidationException.class)
  public void testExceptionThrownFromValidationNoDefaultThrowsException() {
    new Property<>(KEY, INT_TEXT, INT_PARSER)
        .addValidation(value -> {
          throw new RuntimeException();
        })
        .get();
  }

  @Test
  public void testExceptionThrownFromValidationCaughtByDefaultWhenSet() {
    DefaultSettings<Integer> settings = new DefaultSettings<Integer>()
        .when(IS_INVALID)
        .thenReturn(0);

    assertEquals(Integer.valueOf(0),
                 new Property<>(KEY, INT_TEXT, INT_PARSER)
                     .withDefaultSettings(settings)
                     .addValidation(value -> {
                       throw new RuntimeException();
                     })
                     .get());
  }

}
