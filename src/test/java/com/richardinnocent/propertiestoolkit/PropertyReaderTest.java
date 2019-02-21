package com.richardinnocent.propertiestoolkit;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author RichardInnocent
 */
public class PropertyReaderTest {

  private Properties properties = new Properties();
  private PropertyReader reader = new PropertyReader(properties);

  private static final String BYTE_LABEL = "byte";
  private static final String BYTE_TEXT = "126";
  private static final Byte BYTE_VALUE = Byte.parseByte(BYTE_TEXT);

  private static final String SHORT_LABEL = "short";
  private static final String SHORT_TEXT = "1238";
  private static final Short SHORT_VALUE = Short.parseShort(SHORT_TEXT);

  private static final String INT_LABEL = "int";
  private static final String INT_TEXT = "12352";
  private static final Integer INT_VALUE = Integer.parseInt(INT_TEXT);

  private static final String LONG_LABEL = "long";
  private static final String LONG_TEXT = "1251225";
  private static final Long LONG_VALUE = Long.parseLong(LONG_TEXT);

  private static final String FLOAT_LABEL = "float";
  private static final String FLOAT_TEXT = "124.245";
  private static final Float FLOAT_VALUE = Float.parseFloat(FLOAT_TEXT);

  private static final String DOUBLE_LABEL = "double";
  private static final String DOUBLE_TEXT = "124.56235";
  private static final Double DOUBLE_VALUE = Double.parseDouble(DOUBLE_TEXT);

  private static final String BOOLEAN_LABEL = "boolean";
  private static final String BOOLEAN_TEXT = "true";
  private static final Boolean BOOLEAN_VALUE = Boolean.parseBoolean(BOOLEAN_TEXT);

  private static final String STRING_LABEL = "String";
  private static final String STRING_TEXT = "some text";
  private static final String STRING_VALUE = STRING_TEXT;

  @Before
  public void setUp() {
    properties.setProperty(BYTE_LABEL, BYTE_TEXT);
    properties.setProperty(SHORT_LABEL, SHORT_TEXT);
    properties.setProperty(INT_LABEL, INT_TEXT);
    properties.setProperty(LONG_LABEL, LONG_TEXT);
    properties.setProperty(FLOAT_LABEL, FLOAT_TEXT);
    properties.setProperty(DOUBLE_LABEL, DOUBLE_TEXT);
    properties.setProperty(BOOLEAN_LABEL, BOOLEAN_TEXT);
    properties.setProperty(STRING_LABEL, STRING_TEXT);
  }

  @Test
  public void testGetByte() {
    assertEquals(BYTE_VALUE, reader.getByte(BYTE_LABEL).get());
  }

  @Test
  public void testGetShort() {
    assertEquals(SHORT_VALUE, reader.getShort(SHORT_LABEL).get());
  }

  @Test
  public void testGetInt() {
    assertEquals(INT_VALUE, reader.getInt(INT_LABEL).get());
  }

  @Test
  public void testGetLong() {
    assertEquals(LONG_VALUE, reader.getLong(LONG_LABEL).get());
  }

  @Test
  public void testGetFloat() {
    assertEquals(FLOAT_VALUE, reader.getFloat(FLOAT_LABEL).get());
  }

  @Test
  public void testGetDouble() {
    assertEquals(DOUBLE_VALUE, reader.getDouble(DOUBLE_LABEL).get());
  }

  @Test
  public void testGetBoolean() {
    assertEquals(BOOLEAN_VALUE, reader.getBoolean(BOOLEAN_LABEL).get());
  }

  @Test
  public void testGetString() {
    assertEquals(STRING_VALUE, reader.getString(STRING_LABEL).get());
  }

  @Test
  public void testCustom() {
    String key = "custom";
    String value = "big StringBuilder";
    properties.put(key, value);
    StringBuilder returned = reader.getCustom(key, StringBuilder::new).get();
    assertEquals(value, returned.toString());
  }

}
