package com.richardinnocent.propertiestoolkit;

import java.util.Properties;
import java.util.function.Function;

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

  public PropertyReader(Properties properties) {
    this.properties = properties;
  }

  public Property<Byte> getByte(String key) {
    return new Property<>(key, properties.getProperty(key), BYTE_PARSER);
  }

  public Property<Short> getShort(String key) {
    return new Property<>(key, properties.getProperty(key), SHORT_PARSER);
  }

  public Property<Integer> getInt(String key) {
    return new Property<>(key, properties.getProperty(key), INT_PARSER);
  }

  public Property<Long> getLong(String key) {
    return new Property<>(key, properties.getProperty(key), LONG_PARSER);
  }

  public Property<Float> getFloat(String key) {
    return new Property<>(key, properties.getProperty(key), FLOAT_PARSER);
  }

  public Property<Double> getDouble(String key) {
    return new Property<>(key, properties.getProperty(key), DOUBLE_PARSER);
  }

  public Property<Boolean> getBoolean(String key) {
    return new Property<>(key, properties.getProperty(key), BOOLEAN_PARSER);
  }

  public Property<String> getString(String key) {
    return new Property<>(key, properties.getProperty(key), STRING_PARSER);
  }

  public <T> Property<T> getCustom(String key, Function<String, T> parser) {
    return new Property<>(key, properties.getProperty(key), parser);
  }

}
