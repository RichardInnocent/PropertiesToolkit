package org.richardinnocent.propertiestoolkit.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;
import org.richardinnocent.propertiestoolkit.annotations.propertyHelpers.GenericExtractor;

/**
 * Indicates that the specified field should be set from the value specified in a properties file.
 * If this annotation is applied to any instance field within a {@link PropertiesBean} instance,
 * the value will be set at initialisation, depending on the way in which it is specified.
 * @see #key()
 * @see #extractor()
 * @see #constraints()
 * @see PropertiesBean
 * @since 3.0.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromProperty {

  /**
   * The key in the properties instance that should be used to determine the value. If this is left
   * blank, the name of the field is used.
   * @return The object key.
   */
  String key() default "";

  /**
   * The extractor that is used to transform the text value from the properties instance into the
   * desired object type. If this is left blank, the {@code GenericExtractor}
   * @return The extractor that maps a string to the desired object type.
   */
  Class<? extends PropertyExtractor<?>> extractor() default GenericExtractor.class;

  /**
   * The constraints that the parsed value must meet to be valid. Each class used in this method
   * must extend {@link
   * org.richardinnocent.propertiestoolkit.annotations.constraints.PropertyConstraint}.
   * @return The constraints that must be met for the value to be valid.
   */
  Class[] constraints() default {};

}
