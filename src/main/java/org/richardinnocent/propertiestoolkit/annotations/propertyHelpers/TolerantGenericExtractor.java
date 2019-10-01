package org.richardinnocent.propertiestoolkit.annotations.propertyHelpers;

import org.richardinnocent.propertiestoolkit.DefaultCondition;
import org.richardinnocent.propertiestoolkit.DefaultSettings;

/**
 * This is an extension of the {@link GenericExtractor}, except that the triggering of any default
 * conditions will cause the field to be set to {@code null}, or the default primitive value for
 * that type, if appropriate.
 * @since 3.0.0
 */
public final class TolerantGenericExtractor extends GenericExtractor {

  @Override
  public DefaultSettings<Object> getDefaultSettings() {
    return new DefaultSettings<>().when(DefaultCondition.values())
                                  .thenReturn(null);
  }

}
