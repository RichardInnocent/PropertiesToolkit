package com.richardinnocent.propertiestoolkit;

public class ValidationException extends PropertiesException {

  private ValidationException(String message) {
    super(message);
  }

  public static ValidationException forProperty(String key, String value) {
    return new ValidationException(
        String.format("Key, %s, contains an invalid value, %s", key, value));
  }

}
