package org.richardinnocent.propertiestoolkit.annotations.propertyHelpers;

import org.junit.Test;
import org.richardinnocent.propertiestoolkit.annotations.InvalidAnnotationException;

import static org.junit.Assert.*;

public class GenericExtractorTest {

  private static final GenericExtractor EXTRACTOR = new GenericExtractor();

  @Test
  public void testParsingBytePrimitive() {
    byte value = 4;
    assertEquals(value, (byte) extract(Byte.toString(value), Byte.TYPE));
  }

  @Test
  public void testParsingByteWrapper() {
    Byte value = 4;
    assertEquals(value, extract(value.toString(), Byte.class));
  }

  @Test
  public void testParsingShortPrimitive() {
    short value = 4;
    assertEquals(value, (short) extract(Short.toString(value), Short.TYPE));
  }

  @Test
  public void testParsingShortWrapper() {
    Short value = 4;
    assertEquals(value, extract(value.toString(), Short.class));
  }

  @Test
  public void testParsingIntPrimitive() {
    int value = 4;
    assertEquals(value, (int) extract(Integer.toString(value), Integer.TYPE));
  }

  @Test
  public void testParsingIntegerWrapper() {
    Integer value = 4;
    assertEquals(value, extract(value.toString(), Integer.class));
  }

  @Test
  public void testParsingLongPrimitive() {
    long value = 4L;
    assertEquals(value, (long) extract(Long.toString(value), Long.TYPE));
  }

  @Test
  public void testParsingLongWrapper() {
    Long value = 4L;
    assertEquals(value, extract(value.toString(), Long.class));
  }

  @Test
  public void testParsingFloatPrimitive() {
    float value = 4.3f;
    assertEquals(value, extract(Float.toString(value), Float.TYPE), 1e-5f);
  }

  @Test
  public void testParsingFloatWrapper() {
    Float value = 4.3f;
    assertEquals(value, extract(value.toString(), Float.class), 1e-5f);
  }

  @Test
  public void testParsingDoublePrimitive() {
    double value = 4.3d;
    assertEquals(value, extract(Double.toString(value), Double.TYPE), 1e-5);
  }

  @Test
  public void testParsingDoubleWrapper() {
    Double value = 4.3d;
    assertEquals(value, extract(value.toString(), Double.class), 1e-5);
  }

  @Test
  public void testParsingBooleanPrimitive() {
    assertTrue(extract(Boolean.toString(true), Boolean.TYPE));
    assertFalse(extract(Boolean.toString(false), Boolean.TYPE));
  }

  @Test
  public void testParsingBooleanWrapper() {
    assertTrue(extract(Boolean.toString(true), Boolean.class));
    assertFalse(extract(Boolean.toString(false), Boolean.class));
  }

  @Test
  public void testParsingString() {
    String value = "value";
    assertEquals(value, extract(value, String.class));
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testClassWithoutAppropriateConstructor() {
    class NoStringConstructorObject {}
    extract("value", NoStringConstructorObject.class);
  }

  @Test
  public void testClassWithAppropriateConstructor() {
    String value = "value";
    assertEquals(value, extract(value, StringConstructorObject.class).text);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testClassWithAppropriateConstructorThrowsRuntimeExceptionOnInstantiation() {
    extract("value", StringConstructorObjectThrowsRuntimeException.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testClassWithAppropriateConstructorThrowsCheckedExceptionOnInstantiation() {
    extract("value", StringConstructorObjectThrowsCheckedException.class);
  }

  private <T> T extract(String value, Class<T> valueType) {
    return (T) EXTRACTOR.getExtractionMethod(valueType).apply(value);
  }

  public static class StringConstructorObject {
    private final String text;
    public StringConstructorObject(String text) {
      this.text = text;
    }
  }

  public static class StringConstructorObjectThrowsRuntimeException {
    public StringConstructorObjectThrowsRuntimeException(String text) {
      throw new RuntimeException("Test exception");
    }
  }

  public static class StringConstructorObjectThrowsCheckedException {
    public StringConstructorObjectThrowsCheckedException(String text) throws Exception {
      throw new Exception("Test exception");
    }
  }

}
