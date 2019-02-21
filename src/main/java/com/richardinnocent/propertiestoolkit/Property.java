package com.richardinnocent.propertiestoolkit;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Stores and processes the returned value from the {@code Properties} file. To get the value in
 * the appropriate, the {@link Property#get()} method should be called, which makes a converts
 * and validates the specified value, and throwing meaningful exceptions or returning appropriate
 * default values where necessary.
 * @param <T> The type that the property should be converted to.
 * @see Property#get()
 * @since 1.0.0
 * @author RichardInnocent
 */
public class Property<T> {

  private final String value;
  private final String key;
  private final Function<String, T> parser;
  private DefaultSettings<T> defaultSettings;
  private List<Predicate<T>> constraints = new LinkedList<>();

  /**
   * Creates a {@code Property} for the given key and value.
   * @param key The key name from the {@code Properties} file.
   * @param value The value for the given key.
   * @param parser The method of converting the raw {@code value} text from the {@code Properties}
   *   file, to the desired type.
   */
  Property(String key, String value, Function<String, T> parser) {
    this.key = key;
    this.value = value;
    this.parser = parser;
  }

  /**
   * Sets the default settings for this property. The default settings specify what should be
   * returned (and executed, if appropriate), if specific {@link DefaultCondition}s are encountered
   * during processing. If no default is set for that specific condition, a {@code
   * PropertiesException} will be thrown when calling {@link Property#get()}.
   * @param defaultSettings The default settings.
   * @return This {@code Property} object, for chaining.
   */
  @SuppressWarnings("WeakerAccess")
  public Property<T> withDefaultSettings(DefaultSettings<T> defaultSettings) {
    this.defaultSettings = defaultSettings;
    return this;
  }

  /**
   * Enforces that the returned object meets the given constraints. Repeated calls to this method
   * will add additional validation checks, not replacing the previously added checks.<br>
   * <br>
   * <b>When all validation checks pass</b>, the desired object will be returned as normal.<br>
   * <b>When any validations checks fail or a {@code RuntimeException} is thrown from the
   * method</b>, a {@link ValidationException} will be thrown from the {@link Property#get()}
   * method, unless a {@link DefaultSettings} object has been applied to this property which
   * specifies what to return in this scenario ({@link DefaultCondition#IS_INVALID}).
   * @param constraint The constraint that should be applied. Note that the {@code <T>} object that
   *   is passed to the predicate will never be {@code null}.
   * @return This {@code Property} object, for chaining.
   */
  @SuppressWarnings("WeakerAccess")
  public Property<T> addConstraint(Predicate<T> constraint) {
    if (constraint != null)
      this.constraints.add(constraint);
    return this;
  }

  /**
   * Converts the {@code Property} to the expected type, {@code <T>}. This processes in the
   * following manner:
   * <ol>
   *   <li><b>Check if {@code null} or empty</b>:<br>
   *     <b>Property is not {@code null} or empty</b>: Continue to step 2.<br>
   *     <b>Property is {@code null} or empty</b>: Do not attempt to parse this property. Check if
   *     a {@code defaultSettings} object has been applied which contains the appropriate behaviour
   *     for {@link DefaultCondition#IS_EMPTY}. If a setting is found, process as specified in the
   *     setting. If no such setting is found, a {@link MissingPropertyException} is
   *     thrown.</b></li>
   *   <li><b>Attempt to parse the property to the expected type</b><br>
   *     This parse is completed using the provided {@code Function<String, T>}.<br>
   *     <b>If the property is parsed successfully</b>, continue to step 3.<br>
   *     <b>If the property is not parsed successfully</b>, check if a {@code defaultSettings}
   *     object has been applied which contains the appropriate behaviour for {@link
   *     DefaultCondition#PARSE_FAILS}. If a setting is found, process as specified in the setting.
   *     If no such setting is found, an {@link InvalidTypeException} is thrown.</li>
   *   <li><b>Apply all constraint checks</b><br>
   *     <b>If all constraint checks pass</b>, return the processed value.<br>
   *     <b>If any constraint checks fail</b>, check if a {@code defaultSettings} object has been
   *     applied which contains the appropriate behaviour for {@link DefaultCondition#IS_INVALID}.
   *     If a setting is found, process as specified in the setting. If no such setting is found, a
   *     {@link ValidationException} is thrown.</li>
   * </ol>
   * @return The property, converted to the required type, or an appropriate default if there is a
   *   return value specified for the triggered {@link DefaultCondition}.
   * @throws MissingPropertyException Thrown if the property text is {@code null} or empty, and
   *   there is no {@code defaultSetting} specifying behaviour for {@link
   *   DefaultCondition#IS_EMPTY}.
   * @throws InvalidTypeException Thrown if the property text cannot be converted to the correct
   *   type, and there is no {@code defaultSetting} specifying behaviour for {@link
   *    DefaultCondition#PARSE_FAILS}.
   * @throws ValidationException Thrown if any of the constraint checks for this property fail,
   *   and there is no {@code defaultSetting} specifying behaviour for {@link
   *   DefaultCondition#IS_INVALID}.
   */
  public T get() throws MissingPropertyException, InvalidTypeException, ValidationException {
    if (value == null || value.isEmpty())
      return applyDefaultBehaviour(DefaultCondition.IS_EMPTY,
                                   MissingPropertyException.forProperty(key));

    T parsedValue;
    try {
      parsedValue = parse();
    } catch (InvalidTypeException e) {
      return applyDefaultBehaviour(DefaultCondition.PARSE_FAILS, e);
    }

    for (Predicate<T> constraint : constraints) {
      try {
        if (!constraint.test(parsedValue))
          return applyDefaultBehaviour(DefaultCondition.IS_INVALID,
                                       ValidationException.forProperty(key, value));
      } catch (RuntimeException e) {
        return applyDefaultBehaviour(DefaultCondition.IS_INVALID,
                                     ValidationException.forProperty(key, value, e));
      }
    }

    return parsedValue;
  }

  private T applyDefaultBehaviour(DefaultCondition condition, PropertiesException e)
      throws PropertiesException {
    if (defaultSettings == null)
      throw e;
    return defaultSettings.apply(condition, key, value, e);
  }

  private T parse() {
    try {
      return parser.apply(value);
    } catch (Exception e) {
      throw InvalidTypeException.forProperty(key, value, e);
    }
  }

  @Override
  public String toString() {
    return String.format("%s: %s", key, value);
  }

}
