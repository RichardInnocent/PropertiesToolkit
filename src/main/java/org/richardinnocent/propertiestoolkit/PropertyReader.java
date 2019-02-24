package org.richardinnocent.propertiestoolkit;

import java.util.Properties;
import java.util.function.Function;

/**
 * The {@code PropertyReader} is the instance that is used to convert properties from a populated
 * {@code Properties} object, into their desired formats. Presented below are several typical use
 * cases. For the sake of simplicity, assume that {@code properties} is a populated {@code
 * Properties} file, and that the following line has been executed:
 * <pre>PropertyReader reader = new PropertyReader(properties);</pre>
 *
 * <h3>Converting to an {@code Integer}</h3>
 * In this example, assume that the {@code properties} instance contains a key, "{@code intKey}",
 * , with a value that can successfully be parsed to an {@code Integer}.
 * <pre>
 *   Integer value = reader.getInt("intKey").get();
 * </pre>
 * It's possible that the value for {@code intKey} is <i>not</i> cannot be parsed to an {@code
 * Integer}. In this case, a {@link InvalidTypeException} will be thrown, unless some {@code
 * DefaultSettings} have been defined for this scenario.
 *
 * <h3>Imposing constraints</h3>
 * Constraints can be applied to the returned value, to ensure that it meets some criteria.
 * <pre>
 *   Integer value = reader.getInt("intKey").addConstraint(value -> value >= 0)
 *                                          .get();
 * </pre>
 * It has been imposed that {@code value >= 0}. If this constraint fails, a {@code
 * ValidationException} is thrown, unless some {@code DefaultSettings} have been defined for this
 * scenario.
 *
 * <h3>Exceptions</h3>
 * It's possible that some exceptions may be thrown when calling the {@link Property#get()} method,
 * so it's usually safest (although not required) to wrap property readings in a try-catch, such as
 * the following:
 * <pre>
 *   try {
 *     Integer value = reader.getInt("intKey").get();
 *   } catch(PropertiesException e) {
 *     // Handle error...
 *   }
 * </pre>
 * These exceptions can be suppressed, by providing some {@code DefaultSettings} that define what
 * value should be returned instead of throwing an exception.
 *
 * <h3>Default settings</h3>
 * A {@link DefaultSettings} instance can be used to define any tasks that should be run and a
 * default value when processing exceptions are encountered. For example:
 * <pre>
 *   properties.put("intKey", "notAnInt");
 *
 *   reader.getInt("intKey").get(); // Throws InvalidTypeException
 *
 *   reader.getInt("intKey")
 *         .withDefaultSettings(new DefaultSettings<Integer>()
 *                                 .when(DefaultCondition.PARSE_FAILS)
 *                                 .thenReturn(0))
 *         .get(); // 0
 * </pre>
 * See {@link DefaultCondition} for a full set of conditions.
 * @since 1.0.0
 * @author RichardInnocent
 */
@SuppressWarnings("WeakerAccess")
public class PropertyReader {

  private static final Function<String, Byte> BYTE_PARSER = Byte::valueOf;
  private static final Function<String, Short> SHORT_PARSER = Short::valueOf;
  private static final Function<String, Integer> INT_PARSER = Integer::valueOf;
  private static final Function<String, Long> LONG_PARSER = Long::valueOf;
  private static final Function<String, Float> FLOAT_PARSER = Float::valueOf;
  private static final Function<String, Double> DOUBLE_PARSER = Double::valueOf;
  private static final Function<String, Boolean> BOOLEAN_PARSER = Boolean::valueOf;
  private static final Function<String, String> STRING_PARSER = value -> value;

  private final Properties properties;

  /**
   * Initialises a {@code PropertyReader} to read from the given {@code Properties} file.
   * @param properties The object to read from.
   */
  public PropertyReader(Properties properties) {
    this.properties = properties;
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Byte} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Byte}, an
   * {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is called,
   * unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Byte}.
   * @see Property#get()
   */
  public Property<Byte> getByte(String key) {
    return new Property<>(key, properties.getProperty(key), BYTE_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Short} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Short}, an
   * {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is called,
   * unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Short}.
   * @see Property#get()
   */
  public Property<Short> getShort(String key) {
    return new Property<>(key, properties.getProperty(key), SHORT_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as an {@code Integer} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to an {@code Integer},
   * an {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is
   * called, unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for an {@code Integer}.
   * @see Property#get()
   */
  public Property<Integer> getInt(String key) {
    return new Property<>(key, properties.getProperty(key), INT_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Long} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Long}, an
   * {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is called,
   * unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Long}.
   * @see Property#get()
   */
  public Property<Long> getLong(String key) {
    return new Property<>(key, properties.getProperty(key), LONG_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Float} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Float}, an
   * {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is called,
   * unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Float}.
   * @see Property#get()
   */
  public Property<Float> getFloat(String key) {
    return new Property<>(key, properties.getProperty(key), FLOAT_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Double} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Double}, an
   * {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is called,
   * unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Double}.
   * @see Property#get()
   */
  public Property<Double> getDouble(String key) {
    return new Property<>(key, properties.getProperty(key), DOUBLE_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code Boolean} when the
   * {@link Property#get()} method is called. If the value cannot be parsed to a {@code Boolean},
   * an {@code InvalidTypeException} will be thrown when the {@link Property#get()} method is
   * called, unless suitable default behaviour has been defined.
   * @param key The property key.
   * @return A {@code Property} for a {@code Boolean}.
   * @see Property#get()
   */
  public Property<Boolean> getBoolean(String key) {
    return new Property<>(key, properties.getProperty(key), BOOLEAN_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as a {@code String} when the
   * {@link Property#get()} method is called. Note that this will always return the raw value from
   * the {@code Properties} file, provided that:
   * <ul>
   *   <li>The value is not {@code null} or empty ({@code ""}); and</li>
   *   <li>The value passes all constraints, if specified.</li>
   * </ul>
   * @param key The property key.
   * @return A {@code Property} for a {@code String}.
   * @see Property#get()
   */
  public Property<String> getString(String key) {
    return new Property<>(key, properties.getProperty(key), STRING_PARSER);
  }

  /**
   * Creates a {@code Property} that will attempt to read the value as whatever object type is
   * specified.
   * @param key The property key.
   * @param parser The {@code Function} that will convert the raw {@code String} from the {@code
   *   Properties} file to a value of the appropriate type. If {@code RuntimeException}s are thrown
   *   from the process, this will be caught during the {@link Property#get()} method, and treated
   *   as an {@code InvalidTypeException}. This exception will be thrown, unless suitable default
   *   behaviour for this scenario has been defined.
   * @param <T> The type to parse the value to.
   * @return A {@code Property} for the custom type.
   * @see Property#get()
   */
  public <T> Property<T> getCustom(String key, Function<String, T> parser) {
    return new Property<>(key, properties.getProperty(key), parser);
  }

}
