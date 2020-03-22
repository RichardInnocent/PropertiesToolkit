package org.richardinnocent.propertiestoolkit.annotations;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.richardinnocent.propertiestoolkit.DefaultSettings;
import org.richardinnocent.propertiestoolkit.Property;
import org.richardinnocent.propertiestoolkit.PropertyReader;
import org.richardinnocent.propertiestoolkit.annotations.constraints.PropertyConstraint;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unused", "unchecked"})
public class PropertiesBeanTest {

  private static final String STRING_KEY = "stringKey";
  private static final String BYTE_KEY = "byteKey";
  private static final String SHORT_KEY = "shortKey";
  private static final String INT_KEY = "intKey";
  private static final String LONG_KEY = "longKey";
  private static final String FLOAT_KEY = "floatKey";
  private static final String DOUBLE_KEY = "doubleKey";
  private static final String TRUE_KEY = "trueKey";
  private static final String FALSE_KEY = "falseKey";
  private static final String CHAR_KEY = "charKey";
  private static final String STRING_VALUE = "value";
  private static final byte BYTE_VALUE = (byte) 12;
  private static final short SHORT_VALUE = (short) 12;
  private static final int INT_VALUE = 12;
  private static final long LONG_VALUE = 12L;
  private static final float FLOAT_VALUE = 1.2f;
  private static final double DOUBLE_VALUE = 1.2d;
  private static final char CHAR_VALUE = 'c';
  private static final Property<String> STRING_PROPERTY = mock(Property.class, "stringProperty");
  private static final Property<Byte> BYTE_PROPERTY = mock(Property.class, "byteProperty");
  private static final Property<Short> SHORT_PROPERTY = mock(Property.class, "shortProperty");
  private static final Property<Integer> INTEGER_PROPERTY = mock(Property.class, "intProperty");
  private static final Property<Long> LONG_PROPERTY = mock(Property.class, "longProperty");
  private static final Property<Float> FLOAT_PROPERTY = mock(Property.class, "floatProperty");
  private static final Property<Double> DOUBLE_PROPERTY = mock(Property.class, "doubleProperty");
  private static final Property<Boolean> TRUE_PROPERTY = mock(Property.class, "trueProperty");
  private static final Property<Boolean> FALSE_PROPERTY = mock(Property.class, "falseProperty");
  private static final Property<Character> CHARACTER_PROPERTY =
      mock(Property.class, "charProperty");

  @BeforeClass
  public static void setUp() {
    setUpProperty(STRING_PROPERTY, STRING_VALUE);
    setUpProperty(BYTE_PROPERTY, BYTE_VALUE);
    setUpProperty(SHORT_PROPERTY, SHORT_VALUE);
    setUpProperty(INTEGER_PROPERTY, INT_VALUE);
    setUpProperty(LONG_PROPERTY, LONG_VALUE);
    setUpProperty(FLOAT_PROPERTY, FLOAT_VALUE);
    setUpProperty(DOUBLE_PROPERTY, DOUBLE_VALUE);
    setUpProperty(CHARACTER_PROPERTY, CHAR_VALUE);
    setUpProperty(TRUE_PROPERTY, true);
    setUpProperty(FALSE_PROPERTY, false);
  }

  private static <T> void setUpProperty(Property<T> property, T value) {
    when(property.withDefaultSettings(any())).thenReturn(property);
    when(property.get()).thenReturn(value);
  }

  @Test
  public void testFieldsWithoutAnnotationAreNotSet() {
    class Test extends TestPropertiesBean {
      private String field;
    }
    assertNull(new Test().field);
  }

  @Test
  public void testSettingPublicField() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = StringExtractor.class)
      public String field;
    }
    assertEquals(STRING_VALUE, new Test().field);
  }

  @Test
  public void testSettingPrivateField() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = StringExtractor.class)
      private String field;
    }
    assertEquals(STRING_VALUE, new Test().field);
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testSettingFinalField() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = StringExtractor.class)
      private final String field = "finalValue";
    }
    new Test();
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testSettingStaticField() {
    new TestPropertiesBeanWithStaticField();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSettingFieldThatThrowsIllegalArgumentException() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = BadFieldExtractor.class)
      private BadField field;
    }
    new Test();
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testInvalidAnnotationExceptionThrownWhenExtractorIsNotAccessible() {
    class HiddenExtractor implements PropertyExtractor<String> {
      @Override
      public Function<String, String> getExtractionMethod() {
        return text -> text;
      }

      @Override
      public DefaultSettings<String> getDefaultSettings() {
        return null;
      }
    }

    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = HiddenExtractor.class)
      private String field;
    }
    new Test();
  }

  @Test
  public void testDifferentExtractorIsRetrievedForGenericExtractor() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = TestGenericExtractor.class)
      private String field;
    }
    Test test = new Test();
    verify(test.reader, times(1)).getCustom(STRING_KEY, TestGenericExtractor.FUNCTION);
  }

  @Test
  public void testKeyDefaultsToTheNameOfTheField() {
    class Test extends TestPropertiesBean {
      @FromProperty
      private String stringKey;
    }
    assertEquals(STRING_VALUE, new Test().stringKey);
  }

  @Test
  public void testPropertiesConstructorIsUsedAppropriately() {
    class Test extends PropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = StringExtractor.class)
      private String field;

      public Test(Properties properties) {
        super(properties);
      }
    }
    Properties properties = new Properties();
    properties.setProperty(STRING_KEY, STRING_VALUE);
    assertEquals(STRING_VALUE, new Test(properties).field);
  }

  @Test
  public void testBytePrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private byte field;
    }
    assertEquals((byte) 0, new Test().field);
    setUp();
  }

  @Test
  public void testShortPrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private short field;
    }
    assertEquals((short) 0, new Test().field);
    setUp();
  }

  @Test
  public void testIntPrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private int field;
    }
    assertEquals(0, new Test().field);
    setUp();
  }

  @Test
  public void testLongPrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private long field;
    }
    assertEquals(0L, new Test().field);
    setUp();
  }

  @Test
  public void testFloatPrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private float field;
    }
    assertEquals(0f, new Test().field, 1e-5);
    setUp();
  }

  @Test
  public void testDoublePrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private double field;
    }
    assertEquals(0d, new Test().field, 1e-5);
    setUp();
  }

  @Test
  public void testBooleanPrimitiveIsSetToFalseInsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private boolean field;
    }
    assertFalse(new Test().field);
    setUp();
  }

  @Test
  public void testCharPrimitiveIsSetTo0InsteadOfNull() {
    when(STRING_PROPERTY.get()).thenReturn(null);
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, extractor = AlwaysNullExtractor.class)
      private char field;
    }
    assertEquals((char) 0, new Test().field);
    setUp();
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testConstraintClassThatDoesNotExtendPropertyConstraintThrowsException() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, constraints = String.class)
      private String field;
    }
    new Test();
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testConstraintOfTheWrongTypeThrowsException() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, constraints = IntegerGreaterThanZeroConstraint.class)
      private String field;
    }
    new Test();
  }

  @Test
  public void testConstraintOfTheCorrectTypeIsAddedAsAppropriate() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, constraints = StringShorterThan30CharactersConstraint.class)
      private String field;
    }
    new Test();
    verify(STRING_PROPERTY, times(1)).addConstraint(StringShorterThan30CharactersConstraint.CONSTRAINT);
  }

  @Test
  public void testConstraintOfASupertypeIsAddedAsAppropriate() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, constraints = CharSequenceStartsWithLetterAConstraint.class)
      private String field;
    }
    new Test();
    verify(STRING_PROPERTY, times(1)).addConstraint(CharSequenceStartsWithLetterAConstraint.CONSTRAINT);
  }

  @Test
  public void testMultipleConstraintsAreAdded() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY,
          constraints = {
              StringLongerThan1CharacterConstraint.class,
              StringLongerThan1CharacterConstraint.class,
              StringLongerThan1CharacterConstraint.class
          })
      private String field;
    }
    new Test();
    verify(STRING_PROPERTY, times(3)).addConstraint(StringLongerThan1CharacterConstraint.CONSTRAINT);
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testExceptionIsThrownIsConstraintInstanceCannotBeCreated() {
    class InaccessibleConstraint extends PropertyConstraint<String> {
      private InaccessibleConstraint() {
        super(String.class);
      }

      @Override
      public Predicate<String> getConstraint() {
        return String::isEmpty;
      }
    }

    class Test extends TestPropertiesBean {
      @FromProperty(key = STRING_KEY, constraints = InaccessibleConstraint.class)
      private String field;
    }
    new Test();
  }

  @Test
  public void testSettingConstraintsOnBytePrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = BYTE_KEY, constraints = ByteGreaterThanZeroConstraint.class)
      private byte field;
    }
    new Test();
    verify(BYTE_PROPERTY, times(1)).addConstraint(ByteGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintsOnShortPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = SHORT_KEY, constraints = ShortGreaterThanZeroConstraint.class)
      private short field;
    }
    new Test();
    verify(SHORT_PROPERTY, times(1)).addConstraint(ShortGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintsOnIntPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = INT_KEY, constraints = IntegerGreaterThanZeroConstraint.class)
      private int field;
    }
    new Test();
    verify(INTEGER_PROPERTY, times(1)).addConstraint(IntegerGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintsOnLongPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = LONG_KEY, constraints = LongGreaterThanZeroConstraint.class)
      private long field;
    }
    new Test();
    verify(LONG_PROPERTY, times(1)).addConstraint(LongGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintsOnFloatPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = FLOAT_KEY, constraints = FloatGreaterThanZeroConstraint.class)
      private float field;
    }
    new Test();
    verify(FLOAT_PROPERTY, times(1)).addConstraint(FloatGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintOnDoublePrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = DOUBLE_KEY, constraints = DoubleGreaterThanZeroConstraint.class)
      private double field;
    }
    new Test();
    verify(DOUBLE_PROPERTY, times(1)).addConstraint(DoubleGreaterThanZeroConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintOnBooleanPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = TRUE_KEY, constraints = MustBeTrueConstraint.class)
      private boolean field;
    }
    new Test();
    verify(TRUE_PROPERTY, times(1)).addConstraint(MustBeTrueConstraint.CONSTRAINT);
  }

  @Test
  public void testSettingConstraintOnCharPrimitive() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = CHAR_KEY, constraints = CharacterGreaterThanZeroConstraint.class)
      private char field;
    }
    new Test();
    verify(CHARACTER_PROPERTY, times(1))
        .addConstraint(CharacterGreaterThanZeroConstraint.CONSTRAINT);
  }

  private static class TestPropertiesBean extends PropertiesBean {

    final PropertyReader reader;

    public TestPropertiesBean() {
      this(createReaderMock());
    }

    public TestPropertiesBean(PropertyReader reader) {
      super(reader);
      this.reader = reader;
    }

    private static PropertyReader createReaderMock() {
      PropertyReader reader = mock(PropertyReader.class);
      when(reader.getCustom(eq(STRING_KEY), any(Function.class))).thenReturn(STRING_PROPERTY);
      when(reader.getCustom(eq(BYTE_KEY), any(Function.class))).thenReturn(BYTE_PROPERTY);
      when(reader.getCustom(eq(SHORT_KEY), any(Function.class))).thenReturn(SHORT_PROPERTY);
      when(reader.getCustom(eq(INT_KEY), any(Function.class))).thenReturn(INTEGER_PROPERTY);
      when(reader.getCustom(eq(LONG_KEY), any(Function.class))).thenReturn(LONG_PROPERTY);
      when(reader.getCustom(eq(FLOAT_KEY), any(Function.class))).thenReturn(FLOAT_PROPERTY);
      when(reader.getCustom(eq(DOUBLE_KEY), any(Function.class))).thenReturn(DOUBLE_PROPERTY);
      when(reader.getCustom(eq(TRUE_KEY), any(Function.class))).thenReturn(TRUE_PROPERTY);
      when(reader.getCustom(eq(FALSE_KEY), any(Function.class))).thenReturn(FALSE_PROPERTY);
      when(reader.getCustom(eq(CHAR_KEY), any(Function.class))).thenReturn(CHARACTER_PROPERTY);
      return reader;
    }

  }

  private static class TestPropertiesBeanWithStaticField extends TestPropertiesBean {
    @FromProperty(key = STRING_KEY, extractor = StringExtractor.class)
    private static String field = "finalValue";
  }

  private static class BadField {
    public BadField(String input) {
      throw new IllegalArgumentException("Test exception");
    }
  }

  public static class BadFieldExtractor implements PropertyExtractor<BadField> {
    @Override
    public Function<String, BadField> getExtractionMethod() {
      return BadField::new;
    }
  }


  public static class StringExtractor implements PropertyExtractor<String> {
    @Override
    public Function<String, String> getExtractionMethod() {
      return text -> text;
    }

    @Override
    public DefaultSettings<String> getDefaultSettings() {
      return null;
    }
  }

  public static class TestGenericExtractor extends GenericExtractor {

    private static final Function<String, Object> FUNCTION = t -> t + "generified";

    @Override
    public Function<String, Object> getExtractionMethod() {
      return text -> text + " not generified";
    }

    public <T> Function<String, T> getExtractionMethod(Class<T> type) {
      return (Function<String, T>) FUNCTION;
    }
  }

  public static class AlwaysNullExtractor extends GenericExtractor {

    private static final Function<String, Object> FUNCTION = t -> null;

    @Override
    public Function<String, Object> getExtractionMethod() {
      return text -> text + " not generified";
    }

    public <T> Function<String, T> getExtractionMethod(Class<T> type) {
      return (Function<String, T>) FUNCTION;
    }
  }

  public static class StringShorterThan30CharactersConstraint extends PropertyConstraint<String> {
    private static final Predicate<String> CONSTRAINT = str -> str.length() < 30;

    public StringShorterThan30CharactersConstraint() {
      super(String.class);
    }

    @Override
    public Predicate<String> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class StringLongerThan1CharacterConstraint extends PropertyConstraint<String> {
    private static final Predicate<String> CONSTRAINT = str -> str.length() > 1;

    public StringLongerThan1CharacterConstraint() {
      super(String.class);
    }

    @Override
    public Predicate<String> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class CharSequenceStartsWithLetterAConstraint extends PropertyConstraint<CharSequence> {
    private static final Predicate<CharSequence> CONSTRAINT = charSeq -> charSeq.charAt(0) == 'a';

    public CharSequenceStartsWithLetterAConstraint() {
      super(CharSequence.class);
    }

    @Override
    public Predicate<CharSequence> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class ByteGreaterThanZeroConstraint extends PropertyConstraint<Byte> {
    private static final Predicate<Byte> CONSTRAINT = value -> value > (byte) 0;

    public ByteGreaterThanZeroConstraint() {
      super(Byte.class);
    }

    @Override
    public Predicate<Byte> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class ShortGreaterThanZeroConstraint extends PropertyConstraint<Short> {
    private static final Predicate<Short> CONSTRAINT = value -> value > (short) 0;

    public ShortGreaterThanZeroConstraint() {
      super(Short.class);
    }

    @Override
    public Predicate<Short> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class IntegerGreaterThanZeroConstraint extends PropertyConstraint<Integer> {
    private static final Predicate<Integer> CONSTRAINT = value -> value > 0;

    public IntegerGreaterThanZeroConstraint() {
      super(Integer.class);
    }

    @Override
    public Predicate<Integer> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class LongGreaterThanZeroConstraint extends PropertyConstraint<Long> {
    private static final Predicate<Long> CONSTRAINT = value -> value > 0L;

    public LongGreaterThanZeroConstraint() {
      super(Long.class);
    }

    @Override
    public Predicate<Long> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class FloatGreaterThanZeroConstraint extends PropertyConstraint<Float> {
    private static final Predicate<Float> CONSTRAINT = value -> value > 0f;

    public FloatGreaterThanZeroConstraint() {
      super(Float.class);
    }

    @Override
    public Predicate<Float> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class DoubleGreaterThanZeroConstraint extends PropertyConstraint<Double> {
    private static final Predicate<Double> CONSTRAINT = value -> value > 0;

    public DoubleGreaterThanZeroConstraint() {
      super(Double.class);
    }

    @Override
    public Predicate<Double> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class CharacterGreaterThanZeroConstraint extends PropertyConstraint<Character> {
    private static final Predicate<Character> CONSTRAINT = value -> value > 0;

    public CharacterGreaterThanZeroConstraint() {
      super(Character.class);
    }

    @Override
    public Predicate<Character> getConstraint() {
      return CONSTRAINT;
    }
  }

  public static class MustBeTrueConstraint extends PropertyConstraint<Boolean> {
    private static final Predicate<Boolean> CONSTRAINT = value -> value;

    public MustBeTrueConstraint() {
      super(Boolean.class);
    }

    @Override
    public Predicate<Boolean> getConstraint() {
      return CONSTRAINT;
    }
  }

}
