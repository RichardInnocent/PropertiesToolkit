package org.richardinnocent.propertiestoolkit.annotations;

import java.util.function.Function;

import org.richardinnocent.propertiestoolkit.DefaultSettings;

@FunctionalInterface
public interface PropertyExtractor<T> {

  Function<String, T> getExtractionMethod();

  default DefaultSettings<T> getDefaultSettings() {
    return null;
  }

}
