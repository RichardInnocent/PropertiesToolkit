package org.richardinnocent.propertiestoolkit.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.function.Function;

import org.richardinnocent.propertiestoolkit.Property;
import org.richardinnocent.propertiestoolkit.PropertyReader;
import org.richardinnocent.propertiestoolkit.annotations.constraints.PropertyConstraint;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

/**
 * Represents an instance where some (or all) of the instance fields are generated from a single
 * properties instance. To use this, a class should extend this class, and mark the fields that
 * should be auto-filled with the appropriate {@link FromProperty} annotation.
 * @since 3.0.0
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
    Property property = reader.getCustom(key, extractionMethod)
                              .withDefaultSettings(extractor.getDefaultSettings());
    addConstraints(propertySettings, property, field);
    setField(field, property.get());
  }

  private <T> Function<String, T> getExtractionMethod(PropertyExtractor<T> extractor,
                                                      Class<T> fieldType) {
    return extractor instanceof GenericExtractor ?
        ((GenericExtractor) extractor).getExtractionMethod(fieldType) :
        extractor.getExtractionMethod();
  }

  private void addConstraints(FromProperty settings, Property property, Field field)
      throws InvalidAnnotationException {
    for (Class constraintClass : settings.constraints()) {
      safelyAddConstraint(constraintClass, property, field);
    }
  }

  @SuppressWarnings("unchecked")
  private void safelyAddConstraint(Class constraintClass, Property property, Field field) {
    checkClassIsAConstraint(constraintClass);
    PropertyConstraint constraintInstance = buildConstraintInstance(constraintClass);
    ensureConstraintTypeIsApplicableToField(constraintInstance, field);
    property.addConstraint(constraintInstance.getConstraint());
  }

  private void checkClassIsAConstraint(Class constraint) throws InvalidAnnotationException {
    if (!PropertyConstraint.class.isAssignableFrom(constraint)) {
      throw new InvalidAnnotationException(constraint.getName() + " does not extend " + PropertyConstraint.class.getName());
    }
  }

  private PropertyConstraint buildConstraintInstance(
      Class<? extends PropertyConstraint> constraintClass) throws InvalidAnnotationException {
    try {
      return constraintClass.newInstance();
    } catch (Exception e) {
      throw new InvalidAnnotationException(
          "The extractor class, " + constraintClass + ", cannot be instantiated. "
              + "Ensure the class is public and static.", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void ensureConstraintTypeIsApplicableToField(PropertyConstraint constraint, Field field) {
    Class type = getWrapperType(field.getType());
    if (!constraint.getType().isAssignableFrom(type)) {
      throw new InvalidAnnotationException(
          "The type of constraint " + constraint.getClass().getName() + " on field "
              + field.getName() + " (" + constraint.getType()
              + ") is not assignable from the field type (" + type.getName() + ")");
    }
  }

  private Class getWrapperType(Class type) throws InvalidAnnotationException {
    if (!type.isPrimitive()) {
      return type;
    } else if (type == Byte.TYPE) {
      return Byte.class;
    } else if (type == Short.TYPE) {
      return Short.class;
    } else if (type == Integer.TYPE) {
      return Integer.class;
    } else if (type == Long.TYPE) {
      return Long.class;
    } else if (type == Float.TYPE) {
      return Float.class;
    } else if (type == Double.TYPE) {
      return Double.class;
    } else if (type == Boolean.TYPE) {
      return Boolean.class;
    } else if (type == Character.TYPE) {
      return Character.class;
    }
    throw new InvalidAnnotationException("Primitive type " + type.getName() + " is not supported");
  }

  private void setField(Field field, Object value) {
    Class type = field.getType();
    try {
      field.set(this, value == null && type.isPrimitive() ? getPrimitivesNullEquivalent(type) : value);
    } catch (IllegalAccessException e) {
      throw new InvalidAnnotationException("Cannot set value of field " + field.getName(), e);
    }
  }

  private Object getPrimitivesNullEquivalent(Class type) {
    if (type == Byte.TYPE) {
      return (byte) 0;
    } else if (type == Short.TYPE) {
      return (short) 0;
    } else if (type == Integer.TYPE) {
      return 0;
    } else if (type == Long.TYPE) {
      return 0L;
    } else if (type == Float.TYPE) {
      return 0f;
    } else if (type == Double.TYPE) {
      return 0d;
    } else if (type == Boolean.TYPE) {
      return false;
    } else if (type == Character.TYPE) {
      return (char) 0;
    }
    return null;
  }

}
