package com.richardinnocent.propertiestoolkit;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Property<T> {

  private final String value;
  private final String key;
  private final Function<String, T> parser;
  private DefaultSettings<T> defaultSettings;
  private List<Predicate<T>> validations = new LinkedList<>();

  protected Property(String key, String value, Function<String, T> parser) {
    this.key = key;
    this.value = value;
    this.parser = parser;
  }

  public Property withDefaultSettings(DefaultSettings<T> defaultSettings) {
    this.defaultSettings = defaultSettings;
    return this;
  }

  public Property withValidation(Predicate<T> validation) {
    if (validation != null)
      this.validations.add(validation);
    return this;
  }

  public Property withValidations(Predicate<T>... validations) {
    return withValidations(Arrays.asList(validations));
  }

  public Property withValidations(Collection<Predicate<T>> validations) {
    validations.forEach(this::withValidation);
    return this;
  }

  public T get() throws PropertiesException {
    if (value == null || value.isEmpty())
      return applyDefaultBehaviour(DefaultCondition.IS_EMPTY,
                                   MissingPropertyException.forProperty(key));

    T parsedValue;
    try {
      parsedValue = parse();
    } catch (InvalidTypeException e) {
      return applyDefaultBehaviour(DefaultCondition.PARSE_FAILS, e);
    }

    for (Predicate<T> validation : validations) {
      if (!validation.test(parsedValue))
        return applyDefaultBehaviour(DefaultCondition.IS_INVALID,
                                     ValidationException.forProperty(key, value));
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
