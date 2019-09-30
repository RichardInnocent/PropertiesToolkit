package org.richardinnocent.propertiestoolkit.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.function.Function;

import org.richardinnocent.propertiestoolkit.PropertyReader;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

/**
 * Represents an instance where some (or all) of the instance fields are generated from a single
 * properties instance. To use this, a class should extend this class, and mark the fields that
 * should be auto-filled with the appropriate {@link FromProperty} annotation.
 */
public abstract class PropertiesBean {

  /**
   * Populates all appropriate fields marked with {@link FromProperty}, with a value read from the
   * given properties instance.
   * @param properties The properties instance that values will be read from.
   */
  protected PropertiesBean(Properties properties) {
    this(new PropertyReader(properties));
  }

  /**
   * Populates all appropriate fields marked with {@link FromProperty}, with a value read using the
   * reader.
   * @param propertyReader The reader that will parse values from the properties file into instance
   * fields of the desired type.
   */
  protected PropertiesBean(PropertyReader propertyReader) {
    extractProperties(propertyReader);
  }

  private void extractProperties(PropertyReader propertyReader) {
    for (Field field : this.getClass().getDeclaredFields()) {
      FromProperty propertySettings = field.getAnnotation(FromProperty.class);

      if (propertySettings == null) {
        continue;
      }

      ensureFieldIsSettable(field);
      setField(field, propertySettings, propertyReader, field.getType());
    }
  }

  private void ensureFieldIsSettable(Field field) {
    int modifiers = field.getModifiers();
    if (Modifier.isFinal(modifiers)) {
      throw new InvalidAnnotationException("Field, " + field.getName() + ", is final");
    }

    if (Modifier.isStatic(modifiers)) {
      throw new InvalidAnnotationException(
          "Field, " + field.getName() + ", is static. Setting static fields is not supported");
    }
  }

  @SuppressWarnings("unchecked")
  private PropertyExtractor getPropertyExtractor(FromProperty settings)
      throws InvalidAnnotationException {
    if (PropertyExtractor.class.isAssignableFrom(settings.extractor())) {
      return initialiseExtractor(settings.extractor());
    } else {
      throw new InvalidAnnotationException(
          "The specified extractor, " + settings.extractor()
              + ", does not extend PropertyExtractor");
    }
  }

  private PropertyExtractor initialiseExtractor(Class<? extends PropertyExtractor> extractorClass)
      throws InvalidAnnotationException {
    try {
      return extractorClass.newInstance();
    } catch (Exception e) {
      throw new InvalidAnnotationException(
          "The extractor class, " + extractorClass + ", cannot be instantiated. "
              + "Ensure the class is public and static.", e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> void setField(Field field,
                            FromProperty propertySettings,
                            PropertyReader reader,
                            Class<T> fieldType) {
    PropertyExtractor<T> extractor = getPropertyExtractor(propertySettings);
    String key = propertySettings.key().isEmpty() ? field.getName() : propertySettings.key();
    Function<String, T> extractionMethod = getExtractionMethod(extractor, fieldType);
    field.setAccessible(true);
    setField(field, reader.getCustom(key, extractionMethod).get());
  }

  private <T> Function<String, T> getExtractionMethod(PropertyExtractor<T> extractor,
                                                      Class<T> fieldType) {
    return extractor instanceof GenericExtractor ?
        ((GenericExtractor) extractor).getExtractionMethod(fieldType) :
        extractor.getExtractionMethod();
  }

  private void setField(Field field, Object value) {
    try {
      field.set(this, value);
    } catch (IllegalAccessException e) {
      throw new InvalidAnnotationException("Cannot set value of field " + field.getName(), e);
    }
  }

}
