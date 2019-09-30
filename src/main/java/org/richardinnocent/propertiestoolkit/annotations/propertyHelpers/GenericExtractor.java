package org.richardinnocent.propertiestoolkit.annotations.propertyHelpers;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import org.richardinnocent.propertiestoolkit.annotations.InvalidAnnotationException;
import org.richardinnocent.propertiestoolkit.annotations.PropertyExtractor;

/**
 * The {@code GenericExtractor} makes a best attempt at instantiating the desired object type from
 * the text value in the config. At minimum, the following types are explicitly supported:
 * <ul>
 *   <li>{@code byte} (and {@code Byte}</li>
 *   <li>{@code short} (and {@code Short}</li>
 *   <li>{@code int} (and {@code Integer}</li>
 *   <li>{@code long} (and {@code Long}</li>
 *   <li>{@code float} (and {@code Float}</li>
 *   <li>{@code double} (and {@code Double}</li>
 *   <li>{@code boolean} (and {@code Boolean}</li>
 *   <li>{@code String}</li>
 * </ul>
 * For all other object types, a public constructor that consume a {@code String} is searched for.
 * If a constructor is found, the object type will attempt to be instantiated using that
 * constructor. Any exceptions thrown during this process will cause an {@link
 * IllegalArgumentException} to be thrown. If no constructor was found, an {@link
 * InvalidAnnotationException} is thrown, implying that the annotation configuration for this field
 * requires attention.
 */
public class GenericExtractor implements PropertyExtractor<Object> {

  private static final Function<String, Byte> BYTE_PARSER = Byte::valueOf;
  private static final Function<String, Short> SHORT_PARSER = Short::valueOf;
  private static final Function<String, Integer> INT_PARSER = Integer::valueOf;
  private static final Function<String, Long> LONG_PARSER = Long::valueOf;
  private static final Function<String, Float> FLOAT_PARSER = Float::valueOf;
  private static final Function<String, Double> DOUBLE_PARSER = Double::valueOf;
  private static final Function<String, Boolean> BOOLEAN_PARSER = Boolean::valueOf;
  private static final Function<String, String> STRING_PARSER = value -> value;

  /**
   * This should not be used.
   * @return {@code null}.
   */
  @Override
  public Function<String, Object> getExtractionMethod() {
    return null;
  }

  /**
   * Makes a best attempt at instantiating the desired object type from
   * the text value in the config. At minimum, the following types are explicitly supported:
   * <ul>
   *   <li>{@code byte} (and {@code Byte}</li>
   *   <li>{@code short} (and {@code Short}</li>
   *   <li>{@code int} (and {@code Integer}</li>
   *   <li>{@code long} (and {@code Long}</li>
   *   <li>{@code float} (and {@code Float}</li>
   *   <li>{@code double} (and {@code Double}</li>
   *   <li>{@code boolean} (and {@code Boolean}</li>
   *   <li>{@code String}</li>
   * </ul>
   * For all other object types, a public constructor that consume a {@code String} is searched for.
   * If a constructor is found, the object type will attempt to be instantiated using that
   * constructor. Any exceptions thrown during this process will cause an {@link
   * IllegalArgumentException} to be thrown. If no constructor was found, an {@link
   * InvalidAnnotationException} is thrown, implying that the annotation configuration for this field
   * requires attention.
   * @param type The desired type that should be returned.
   * @param <T> The desired type that should be returned.
   * @return An appropriate object, constructed from the input from the properties file.
   * @throws InvalidAnnotationException Thrown if the object type is not explicitly supported, and
   * contains no public constructor that consumes only a {@code String}.
   * @throws IllegalArgumentException Thrown if there are any problems encountered when
   * constructing the object, e.g.:
   * <ul>
   *   <li>A value is attempted to be parsed as a {@code long} but is non-numeric.</li>
   *   <li>An exception is thrown when entering the object type's {@code String} constructor.</li>
   * </ul>
   */
  @SuppressWarnings("unchecked")
  public <T> Function<String, T> getExtractionMethod(Class<T> type)
      throws InvalidAnnotationException, IllegalArgumentException {
    if (type == Byte.TYPE) {
      return (Function<String, T>) BYTE_PARSER;
    } else if (type == Short.TYPE) {
      return (Function<String, T>) SHORT_PARSER;
    } else if (type == Integer.TYPE) {
      return (Function<String, T>) INT_PARSER;
    } else if (type == Long.TYPE) {
      return (Function<String, T>) LONG_PARSER;
    } else if (type == Float.TYPE) {
      return (Function<String, T>) FLOAT_PARSER;
    } else if (type == Double.TYPE) {
      return (Function<String, T>) DOUBLE_PARSER;
    } else if (type == Boolean.TYPE) {
      return (Function<String, T>) BOOLEAN_PARSER;
    } else if (type == String.class) {
      return (Function<String, T>) STRING_PARSER;
    } else {
      return buildOtherType(type);
    }
  }

  private <T> Function<String, T> buildOtherType(Class<T> type)
      throws InvalidAnnotationException, IllegalArgumentException {
    try {
      Constructor<T> constructor = type.getConstructor(String.class);
      return text -> buildObjectFromTextUsingConstructor(text, constructor, type);
    } catch (NoSuchMethodException e) {
      throw new InvalidAnnotationException(
          "No valid constructor exists for object type " + type
              + ". Consider using a customer PropertyExtractor");
    }
  }

  private <T> T buildObjectFromTextUsingConstructor(String text,
                                                    Constructor<T> constructor,
                                                    Class<T> type)
      throws IllegalArgumentException {
    try {
      return constructor.newInstance(text);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Could not construct argument of type " + type + " from value " + text, e);
    }
  }

}
