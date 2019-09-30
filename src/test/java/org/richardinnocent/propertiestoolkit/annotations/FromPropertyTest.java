package org.richardinnocent.propertiestoolkit.annotations;

import org.junit.Test;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

import static org.junit.Assert.assertEquals;

public class FromPropertyTest {

  @Test
  public void testDefaults() throws NoSuchFieldException {
    class Test {
      @FromProperty
      private String field;
    }
    FromProperty settings = Test.class.getDeclaredField("field").getAnnotation(FromProperty.class);
    assertEquals("", settings.key());
    assertEquals(GenericExtractor.class, settings.extractor());
  }

}
