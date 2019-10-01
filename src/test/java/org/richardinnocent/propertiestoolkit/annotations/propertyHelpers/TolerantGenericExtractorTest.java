package org.richardinnocent.propertiestoolkit.annotations.propertyHelpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class TolerantGenericExtractorTest {

  @Test
  public void testDefaultSettings() {
    assertNotNull(new TolerantGenericExtractor().getDefaultSettings());
  }

}
