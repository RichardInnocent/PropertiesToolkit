package com.richardinnocent.propertiestoolkit;

public class ValidationException extends PropertiesException {

  private ValidationException(String message) {
    super(message);
  }

  private ValidationException(String message, RuntimeException e) {
    super(message, e);
  }

  public static ValidationException forProperty(String key, String value) {
    return new ValidationException(getExceptionMessage(key, value));
  }

  public static ValidationException forProperty(String key, String value, RuntimeException e) {
    return new ValidationException(getExceptionMessage(key, value), e);
  }

  private static String getExceptionMessage(String key, String value) {
    return String.format("Key, %s, contains an invalid value, %s", key, value);
  }

}
