package org.richardinnocent.propertiestoolkit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class DefaultSettingsTest {

  private static final Integer RETURN_VALUE = 40;
  private static final String KEY = "key";
  private static final String VALUE_INVALID_TYPE = "value";
  private static final String VALUE = "38";
  private static final String VALUE_NULL = null;

  private final Map<String, String> taskMap = new HashMap<>();
  private final DefaultSettings<Integer> settings = new DefaultSettings<>();

  @Test(expected = IllegalArgumentException.class)
  public void testConditionsCantBeEmpty() {
    DefaultCondition[] conditions = new DefaultCondition[0];
    settings.when(conditions);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConditionsCantBeNull() {
    settings.when((DefaultCondition[]) null);
  }

  @Test
  public void testParseFailsWhenSpecified() {
    testSpecifiedCondition(DefaultCondition.PARSE_FAILS, KEY, VALUE_INVALID_TYPE);
  }

  @Test
  public void testIsEmptyWhenSpecified() {
    testSpecifiedCondition(DefaultCondition.IS_EMPTY, KEY, VALUE_NULL);
  }

  @Test
  public void testIsInvalidWhenSpecified() {
    testSpecifiedCondition(DefaultCondition.IS_INVALID, KEY, VALUE);
  }

  private void testSpecifiedCondition(DefaultCondition condition,
                                      String key, String value) {
    settings.when(condition)
            .thenDo(taskMap::put)
            .thenReturn(RETURN_VALUE);
    assertEquals(RETURN_VALUE, settings.apply(condition, key, value, new PropertiesException()));
    assertEquals(value, taskMap.get(key)); // Ensures the task was executed
    taskMap.clear();
  }

  @Test(expected = PropertiesException.class)
  public void testParseFailsWhenUnspecified() {
    settings.apply(DefaultCondition.PARSE_FAILS, KEY, VALUE_INVALID_TYPE, new PropertiesException());
  }

  @Test(expected = PropertiesException.class)
  public void testIsEmptyWhenUnspecified() {
    settings.apply(DefaultCondition.IS_EMPTY, KEY, VALUE_NULL, new PropertiesException());
  }

  @Test(expected = PropertiesException.class)
  public void testIsInvalidWhenUnspecified() {
    settings.apply(DefaultCondition.IS_INVALID, KEY, VALUE, new PropertiesException());
  }

  @Test
  public void testAllSet() {
    settings.when(DefaultCondition.values())
            .thenDo(taskMap::put)
            .thenReturn(RETURN_VALUE);
    PropertiesException e = new PropertiesException();
    assertEquals(RETURN_VALUE, settings.apply(DefaultCondition.IS_EMPTY, DefaultCondition.IS_EMPTY.toString(), VALUE_NULL, e));
    assertEquals(RETURN_VALUE, settings.apply(DefaultCondition.IS_INVALID, DefaultCondition.IS_INVALID.toString(), VALUE, e));
    assertEquals(RETURN_VALUE, settings.apply(DefaultCondition.PARSE_FAILS, DefaultCondition.PARSE_FAILS.toString(),
                                              VALUE_INVALID_TYPE, e));
    assertEquals(VALUE_NULL, taskMap.get(DefaultCondition.IS_EMPTY.toString()));
    assertEquals(VALUE, taskMap.get(DefaultCondition.IS_INVALID.toString()));
    assertEquals(VALUE_INVALID_TYPE, taskMap.get(DefaultCondition.PARSE_FAILS.toString()));
  }

  /**
   * Ensure the most recent setting is applied for each {@code DefaultCondition}.
   */
  @Test
  public void testOverwrite() {
    settings.when(DefaultCondition.IS_EMPTY)
            .thenDo((key, value) -> taskMap.put("wrongKey", "wrongValue"))
            .thenReturn(RETURN_VALUE-1)
            .when(DefaultCondition.IS_EMPTY)
            .thenDo(taskMap::put)
            .thenReturn(RETURN_VALUE);
    assertEquals(RETURN_VALUE, settings.apply(DefaultCondition.IS_EMPTY, KEY, VALUE_NULL,
                                              new PropertiesException()));
    assertEquals(VALUE_NULL, taskMap.get(KEY));
  }

}
