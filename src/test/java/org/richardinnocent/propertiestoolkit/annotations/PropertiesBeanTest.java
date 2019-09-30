package org.richardinnocent.propertiestoolkit.annotations;

import java.util.Properties;
import java.util.function.Function;

import org.junit.BeforeClass;
import org.junit.Test;
import org.richardinnocent.propertiestoolkit.DefaultSettings;
import org.richardinnocent.propertiestoolkit.Property;
import org.richardinnocent.propertiestoolkit.PropertyReader;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unused", "unchecked"})
public class PropertiesBeanTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final Property<String> PROPERTY = mock(Property.class);

  @BeforeClass
  public static void setUp() {
    when(PROPERTY.get()).thenReturn(VALUE);
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
      @FromProperty(key = KEY, extractor = StringExtractor.class)
      public String field;
    }
    assertEquals(VALUE, new Test().field);
  }

  @Test
  public void testSettingPrivateField() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = KEY, extractor = StringExtractor.class)
      private String field;
    }
    assertEquals(VALUE, new Test().field);
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testSettingFinalField() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = KEY, extractor = StringExtractor.class)
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
      @FromProperty(key = KEY, extractor = BadFieldExtractor.class)
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
      @FromProperty(key = KEY, extractor = HiddenExtractor.class)
      private String field;
    }
    new Test();
  }

  @Test
  public void testDifferentExtractorIsRetrievedForGenericExtractor() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = KEY, extractor = TestGenericExtractor.class)
      private String field;
    }
    Test test = new Test();
    verify(test.reader, times(1)).getCustom(KEY, TestGenericExtractor.FUNCTION);
  }

  @Test
  public void testKeyDefaultsToTheNameOfTheField() {
    class Test extends TestPropertiesBean {
      @FromProperty
      private String key;
    }
    assertEquals(VALUE, new Test().key);
  }

  @Test(expected = InvalidAnnotationException.class)
  public void testUsingExtractorThatIsNotAPropertyExtractorThrowsException() {
    class Test extends TestPropertiesBean {
      @FromProperty(key = KEY, extractor = NotAnExtractor.class)
      private String field;
    }
    new Test();
  }

  @Test
  public void testPropertiesConstructorIsUsedAppropriately() {
    class Test extends PropertiesBean {
      @FromProperty(key = KEY, extractor = StringExtractor.class)
      private String field;

      public Test(Properties properties) {
        super(properties);
      }
    }
    Properties properties = new Properties();
    properties.setProperty(KEY, VALUE);
    assertEquals(VALUE, new Test(properties).field);
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
      when(reader.getCustom(eq(KEY), any(Function.class))).thenReturn(PROPERTY);
      return reader;
    }

  }

  private static class TestPropertiesBeanWithStaticField extends TestPropertiesBean {
    @FromProperty(key = KEY, extractor = StringExtractor.class)
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

  public static class NotAnExtractor {}

}
